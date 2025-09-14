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

    public Report generateMonthlyReport(int year, int month, Long generatedBy) {
        YearMonth ym = YearMonth.of(year, month);
        return generateReport(
                Date.valueOf(ym.atDay(1)),
                Date.valueOf(ym.atEndOfMonth()),
                "monthly_report_" + year + "_" + month + ".txt",
                ReportType.MONTHLY,
                year,
                month,
                null,
                generatedBy
        );
    }

    public Report generateQuarterlyReport(int year, int quarter, Long generatedBy) {
        int startMonth = (quarter - 1) * 3 + 1;
        int endMonth = startMonth + 2;
        return generateReport(
                Date.valueOf(YearMonth.of(year, startMonth).atDay(1)),
                Date.valueOf(YearMonth.of(year, endMonth).atEndOfMonth()),
                "quarterly_report_" + year + "_Q" + quarter + ".txt",
                ReportType.QUARTERLY,
                year,
                null,
                quarter,
                generatedBy
        );
    }

    private Report generateReport(Date start, Date end, String filename,
                                  ReportType type, int year, Integer month, Integer quarter, Long generatedBy) {

        List<PaymentView> payments = getPaymentsBetween(start, end);

        BigDecimal totalIncoming = BigDecimal.ZERO;
        BigDecimal totalOutgoing = BigDecimal.ZERO;

        for (PaymentView pv : payments) {
            if ("INCOMING".equalsIgnoreCase(pv.getDirection())) {
                totalIncoming = totalIncoming.add(pv.getAmount());
            } else if ("OUTGOING".equalsIgnoreCase(pv.getDirection())) {
                totalOutgoing = totalOutgoing.add(pv.getAmount());
            }
        }

        String folder = type == ReportType.MONTHLY ? "MonthlyReports" : "QuarterlyReports";
        String fullPath = folder + "/" + filename;

        writeToTextFile(payments, fullPath);

        Report report = new Report();
        report.setReportType(type);
        report.setYear(year);
        report.setMonth(month);
        report.setQuarter(quarter);
        report.setFilePath(fullPath);
        report.setStatus("COMPLETED");
        report.setGeneratedBy(generatedBy);
        report.setTotalIncoming(totalIncoming);
        report.setTotalOutgoing(totalOutgoing);
        report.setCreatedAt(OffsetDateTime.now());

        saveReportMetadata(report);
        return report;
    }


    private void writeToTextFile(List<PaymentView> payments, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (PaymentView pv : payments) {
                writer.write("ID: " + pv.getId() + "\n");
                writer.write("Direction: " + pv.getDirection() + "\n");
                writer.write("Category: " + pv.getCategoryName() + "\n");
                writer.write("Status: " + pv.getStatusName() + "\n");
                writer.write("Amount: " + pv.getAmount() + " " + pv.getCurrency() + "\n");
                writer.write("Counterparty: " + pv.getCounterpartyName() + "\n");
                writer.write("Bank Account: " + pv.getBankAccount() + "\n");
                writer.write("Description: " + pv.getDescription() + "\n");
                writer.write("Created By: " + pv.getCreatedBy() + "\n");
                writer.write("Created At: " + pv.getCreatedAt() + "\n");
                writer.write("--------------------------------------------------\n");
            }
            logger.info("Exported report to: {}", filename);
        } catch (IOException e) {
            logger.error("Failed to write report", e);
            throw new RuntimeException("Error writing report to file", e);
        }
    }

    private List<PaymentView> getPaymentsBetween(Date start, Date end) {
        List<PaymentView> payments = new ArrayList<>();
        String sql = """
            SELECT p.id, p.direction, c.name AS category_name, s.name AS status_name,
                   p.amount, p.currency, cp.name AS counterparty_name,
                   ba.account_number AS bank_account, p.description,
                   u.username AS created_by, p.created_at
            FROM payments p
            LEFT JOIN categories c ON p.category_id = c.id
            LEFT JOIN statuses s ON p.status_id = s.id
            LEFT JOIN counterparties cp ON p.counterparty_id = cp.id
            LEFT JOIN bank_accounts ba ON p.bank_account_id = ba.id
            LEFT JOIN users u ON p.created_by = u.id
            WHERE p.created_at BETWEEN ? AND ?
            ORDER BY p.created_at
        """;

        try {
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setDate(1, start);
                stmt.setDate(2, end);
                try (ResultSet rs = stmt.executeQuery()) {
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
                    payments.add(pv);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching payment data", e);
            throw new RuntimeException("Error loading payment data", e);
        }

        return payments;
    }

    private void saveReportMetadata(Report report) {
        String sql = """
            INSERT INTO reports (report_type, year, month, quarter, file_path, status,
                                 generated_by, total_incoming, total_outgoing, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (
                Connection connection = DatabaseConfig.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, report.getReportType().name());
            stmt.setInt(2, report.getYear());
            stmt.setObject(3, report.getMonth());
            stmt.setObject(4, report.getQuarter());
            stmt.setString(5, report.getFilePath());
            stmt.setString(6, report.getStatus());
            stmt.setLong(7, report.getGeneratedBy());
            stmt.setBigDecimal(8, report.getTotalIncoming());
            stmt.setBigDecimal(9, report.getTotalOutgoing());
            stmt.setTimestamp(10, Timestamp.from(report.getCreatedAt().toInstant()));

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new RuntimeException("Saving report failed");

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    report.setId(keys.getLong(1));
                }
            }

            logger.info("Saved report metadata: ID = {}", report.getId());
        } catch (SQLException e) {
            logger.error("Error saving report metadata", e);
            throw new RuntimeException("Failed to save report metadata", e);
        }
    }

    public List<Report> getReportHistory() {
        List<Report> reports = new ArrayList<>();
        String sql = """
            SELECT r.*, u.username as generated_by_name
            FROM reports r
            LEFT JOIN users u ON r.generated_by = u.id
            ORDER BY r.created_at DESC
        """;

        try (
                Connection conn = DatabaseConfig.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Report r = new Report();
                r.setId(rs.getLong("id"));
                r.setReportType(ReportType.valueOf(rs.getString("report_type")));
                r.setYear(rs.getInt("year"));
                r.setMonth(rs.getObject("month", Integer.class));
                r.setQuarter(rs.getObject("quarter", Integer.class));
                r.setFilePath(rs.getString("file_path"));
                r.setStatus(rs.getString("status"));
                r.setGeneratedBy(rs.getLong("generated_by"));
                r.setTotalIncoming(rs.getBigDecimal("total_incoming"));
                r.setTotalOutgoing(rs.getBigDecimal("total_outgoing"));
                r.setCreatedAt(rs.getTimestamp("created_at").toInstant().atOffset(java.time.ZoneOffset.UTC));
                reports.add(r);
            }

        } catch (SQLException e) {
            logger.error("Error fetching report history", e);
            throw new RuntimeException("Error fetching report history", e);
        }

        return reports;
    }
}
