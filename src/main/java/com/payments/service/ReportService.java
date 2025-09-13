package com.payments.service;

import com.payments.config.DatabaseConfig;
import com.payments.dto.PaymentView;
import com.payments.model.Report;
import com.payments.model.ReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    public List<PaymentView> getPaymentsBetween(Date start, Date end) {
        List<PaymentView> list = new ArrayList<>();
        String sql = "SELECT p.id, p.direction, c.name AS category_name, s.name AS status_name, " +
                "p.amount, p.currency, cp.name AS counterparty_name, ba.account_number AS bank_account, " +
                "p.description, u.username AS created_by, p.created_at " +
                "FROM payments p " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "LEFT JOIN statuses s ON p.status_id = s.id " +
                "LEFT JOIN counterparties cp ON p.counterparty_id = cp.id " +
                "LEFT JOIN bank_accounts ba ON p.bank_account_id = ba.id " +
                "LEFT JOIN users u ON p.created_by = u.id " +
                "WHERE p.created_at BETWEEN ? AND ? " +
                "ORDER BY p.created_at";

        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setDate(1, start);
            preparedStatement.setDate(2, end);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                PaymentView pv = new PaymentView();
                pv.setId(rs.getLong("id"));
                pv.setDirection(rs.getString("direction"));
                pv.setCategoryName(rs.getString("category_name"));
                pv.setStatusName(rs.getString("status_name"));
                pv.setAmount(rs.getBigDecimal("amount"));
                pv.setCurrency(rs.getString("currency"));
                pv.setCounterpartyName(rs.getString("counterparty_name"));
                pv.setBankAccount(rs.getString("bank_account"));
                pv.setDescription(rs.getString("description"));
                pv.setCreatedBy(rs.getString("created_by"));
                pv.setCreatedAt(rs.getTimestamp("created_at").toInstant().atOffset(java.time.ZoneOffset.UTC));
                list.add(pv);
            }
        } catch (SQLException e) {
            logger.error("Error fetching report data", e);
            throw new RuntimeException("Error fetching report data", e);
        }
        return list;
    }

    public void exportReport(List<PaymentView> payments, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("ID,Direction,Category,Status,Amount,Currency,Counterparty,Bank,Description,CreatedBy,CreatedAt\n");
            for (PaymentView pv : payments) {
                writer.write(pv.getId() + "," +
                        pv.getDirection() + "," +
                        pv.getCategoryName() + "," +
                        pv.getStatusName() + "," +
                        pv.getAmount() + "," +
                        pv.getCurrency() + "," +
                        pv.getCounterpartyName() + "," +
                        pv.getBankAccount() + "," +
                        pv.getDescription() + "," +
                        pv.getCreatedBy() + "," +
                        pv.getCreatedAt() + "\n");
            }
            logger.info("Report exported: {}", filename);
        } catch (IOException e) {
            logger.error("Error writing report file", e);
            throw new RuntimeException("Error writing report file", e);
        }
    }

    public Report generateMonthlyReport(int year, int month, Long generatedBy) {
        logger.info("Generating monthly report for {}/{}", year, month);

        YearMonth ym = YearMonth.of(year, month);
        Date start = Date.valueOf(ym.atDay(1));
        Date end = Date.valueOf(ym.atEndOfMonth());

        List<PaymentView> payments = getPaymentsBetween(start, end);
        String filename = "monthly_report_" + year + "_" + month + ".csv";

        // Calculate totals
        BigDecimal totalIncoming = BigDecimal.ZERO;
        BigDecimal totalOutgoing = BigDecimal.ZERO;

        for (PaymentView pv : payments) {
            if ("INCOMING".equals(pv.getDirection())) {
                totalIncoming = totalIncoming.add(pv.getAmount());
            } else if ("OUTGOING".equals(pv.getDirection())) {
                totalOutgoing = totalOutgoing.add(pv.getAmount());
            }
        }

        exportReport(payments, filename);

        // Save report metadata to database
        Report report = new Report();
        report.setReportType(ReportType.MONTHLY);
        report.setYear(year);
        report.setMonth(month);
        report.setFilePath(filename);
        report.setStatus("COMPLETED");
        report.setGeneratedBy(generatedBy);
        report.setTotalIncoming(totalIncoming);
        report.setTotalOutgoing(totalOutgoing);
        report.setCreatedAt(OffsetDateTime.now());

        saveReportMetadata(report);

        logger.info("Monthly report generated: {} records", payments.size());
        return report;
    }

    public Report generateQuarterlyReport(int year, int quarter, Long generatedBy) {
        logger.info("Generating quarterly report for Q{}/{}", quarter, year);

        int startMonth = (quarter - 1) * 3 + 1;
        int endMonth = startMonth + 2;
        Date start = Date.valueOf(YearMonth.of(year, startMonth).atDay(1));
        Date end = Date.valueOf(YearMonth.of(year, endMonth).atEndOfMonth());

        List<PaymentView> payments = getPaymentsBetween(start, end);
        String filename = "quarterly_report_" + year + "_Q" + quarter + ".csv";

        // Calculate totals
        BigDecimal totalIncoming = BigDecimal.ZERO;
        BigDecimal totalOutgoing = BigDecimal.ZERO;

        for (PaymentView pv : payments) {
            if ("INCOMING".equals(pv.getDirection())) {
                totalIncoming = totalIncoming.add(pv.getAmount());
            } else if ("OUTGOING".equals(pv.getDirection())) {
                totalOutgoing = totalOutgoing.add(pv.getAmount());
            }
        }

        exportReport(payments, filename);

        // Save report metadata to database
        Report report = new Report();
        report.setReportType(ReportType.QUARTERLY);
        report.setYear(year);
        report.setQuarter(quarter);
        report.setFilePath(filename);
        report.setStatus("COMPLETED");
        report.setGeneratedBy(generatedBy);
        report.setTotalIncoming(totalIncoming);
        report.setTotalOutgoing(totalOutgoing);
        report.setCreatedAt(OffsetDateTime.now());

        saveReportMetadata(report);

        logger.info("Quarterly report generated: {} records", payments.size());
        return report;
    }

    private void validateInput(int year, int month, int quarter) {
        if (year < 2020 || year > 2030) {
            throw new RuntimeException("Invalid year");
        }
        if (month < 1 || month > 12) {
            throw new RuntimeException("Invalid month");
        }
        if (quarter < 1 || quarter > 4) {
            throw new RuntimeException("Invalid quarter");
        }
    }

    private void saveReportMetadata(Report report) {
        String sql = "INSERT INTO reports (report_type, year, month, quarter, file_path, status, " +
                "generated_by, total_incoming, total_outgoing, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, report.getReportType().name());
            preparedStatement.setInt(2, report.getYear());
            preparedStatement.setObject(3, report.getMonth());
            preparedStatement.setObject(4, report.getQuarter());
            preparedStatement.setString(5, report.getFilePath());
            preparedStatement.setString(6, report.getStatus());
            preparedStatement.setLong(7, report.getGeneratedBy());
            preparedStatement.setBigDecimal(8, report.getTotalIncoming());
            preparedStatement.setBigDecimal(9, report.getTotalOutgoing());
            preparedStatement.setTimestamp(10, Timestamp.from(report.getCreatedAt().toInstant()));

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Failed to save report metadata");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    report.setId(generatedKeys.getLong(1));
                }
            }

            logger.info("Report metadata saved with ID: {}", report.getId());

        } catch (SQLException e) {
            logger.error("Error saving report metadata", e);
            throw new RuntimeException("Error saving report metadata", e);
        }
    }

    public List<Report> getReportHistory() {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT r.*, u.username as generated_by_name " +
                "FROM reports r " +
                "LEFT JOIN users u ON r.generated_by = u.id " +
                "ORDER BY r.created_at DESC";

        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Report report = new Report();
                report.setId(rs.getLong("id"));
                report.setReportType(ReportType.valueOf(rs.getString("report_type")));
                report.setYear(rs.getInt("year"));
                report.setMonth(rs.getObject("month", Integer.class));
                report.setQuarter(rs.getObject("quarter", Integer.class));
                report.setFilePath(rs.getString("file_path"));
                report.setStatus(rs.getString("status"));
                report.setGeneratedBy(rs.getLong("generated_by"));
                report.setTotalIncoming(rs.getBigDecimal("total_incoming"));
                report.setTotalOutgoing(rs.getBigDecimal("total_outgoing"));
                report.setCreatedAt(rs.getTimestamp("created_at").toInstant().atOffset(java.time.ZoneOffset.UTC));
                reports.add(report);
            }

        } catch (SQLException e) {
            logger.error("Error fetching report history", e);
            throw new RuntimeException("Error fetching report history", e);
        }

        return reports;
    }
}
