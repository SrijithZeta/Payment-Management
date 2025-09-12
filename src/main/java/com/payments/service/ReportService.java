package com.payments.service;

import com.payments.model.Payment;
import com.payments.repository.PaymentRepository;
import com.payments.util.ThreadPoolManager;
import com.payments.dto.ReportDTO;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ReportService {

    private final PaymentRepository paymentRepo = new PaymentRepository();

    /** Generate monthly report asynchronously */
    public Future<ReportDTO> generateMonthlyReportAsync(Long userId, int year, int month) {
        Callable<ReportDTO> task = () -> generateMonthlyReport(userId, year, month);
        return ThreadPoolManager.getInstance().getReportExecutor().submit(task);
    }

    /** Generate quarterly report asynchronously */
    public Future<ReportDTO> generateQuarterlyReportAsync(Long userId, int year, int quarter) {
        Callable<ReportDTO> task = () -> generateQuarterlyReport(userId, year, quarter);
        return ThreadPoolManager.getInstance().getReportExecutor().submit(task);
    }

    /** Generate monthly report synchronously */
    public ReportDTO generateMonthlyReport(Long userId, int year, int month) throws IOException {
        List<Payment> allPayments = paymentRepo.findAll();
        List<Payment> filtered = filterByMonth(allPayments, year, month);

        BigDecimal totalIncoming = BigDecimal.ZERO;
        BigDecimal totalOutgoing = BigDecimal.ZERO;
        Map<String, BigDecimal> categorySummary = new HashMap<>();

        for (Payment p : filtered) {
            if (p.getDirection().name().equalsIgnoreCase("INCOMING")) {
                totalIncoming = totalIncoming.add(p.getAmount());
            } else {
                totalOutgoing = totalOutgoing.add(p.getAmount());
            }
            categorySummary.merge("Category-" + p.getCategoryId(), p.getAmount(), BigDecimal::add);
        }

        String filePath = writeCsv(filtered, year + "-" + month + "-monthly.csv");
//        return new ReportDTO(filePath, totalIncoming, totalOutgoing, categorySummary);
        return  new ReportDTO();
    }


    /** Generate quarterly report synchronously */
    public ReportDTO generateQuarterlyReport(Long userId, int year, int quarter) throws IOException {
        List<Payment> allPayments = paymentRepo.findAll();
        List<Payment> filtered = filterByQuarter(allPayments, year, quarter);

        BigDecimal totalIncoming = BigDecimal.ZERO;
        BigDecimal totalOutgoing = BigDecimal.ZERO;
        Map<String, BigDecimal> categorySummary = new HashMap<>();

        for (Payment p : filtered) {
            if (p.getDirection().name().equalsIgnoreCase("INCOMING")) {
                totalIncoming = totalIncoming.add(p.getAmount());
            } else {
                totalOutgoing = totalOutgoing.add(p.getAmount());
            }
            categorySummary.merge("Category-" + p.getCategoryId(), p.getAmount(), BigDecimal::add);
        }

        String filePath = writeCsv(filtered, year + "-Q" + quarter + "-quarterly.csv");
//        return new ReportDTO(filePath, totalIncoming, totalOutgoing, categorySummary);
        return  new ReportDTO();
    }

    /** CSV writer */
    private String writeCsv(List<Payment> payments, String fileName) throws IOException {
        String folder = "reports/";
        try (FileWriter fw = new FileWriter(folder + fileName)) {
            fw.write("ID,Direction,Amount,Currency,Status,Counterparty,BankAccount,CreatedBy,CreatedAt\n");
            for (Payment p : payments) {
                fw.write(String.format("%d,%s,%s,%s,%d,%d,%d,%d,%s\n",
                        p.getId(), p.getDirection(), p.getAmount(), p.getCurrency(),
                        p.getStatusId(), p.getCounterpartyId(), p.getBankAccountId(),
                        p.getCreatedBy(), p.getCreatedAt()
                ));
            }
        }
        return folder + fileName;
    }

    /** Filter payments by month */
    private List<Payment> filterByMonth(List<Payment> all, int year, int month) {
        List<Payment> filtered = new ArrayList<>();
        for (Payment p : all) {
            if (p.getCreatedAt().getYear() == year && p.getCreatedAt().getMonthValue() == month) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    /** Filter payments by quarter */
    private List<Payment> filterByQuarter(List<Payment> all, int year, int quarter) {
        List<Payment> filtered = new ArrayList<>();
        int startMonth = (quarter - 1) * 3 + 1;
        int endMonth = startMonth + 2;
        for (Payment p : all) {
            int m = p.getCreatedAt().getMonthValue();
            if (p.getCreatedAt().getYear() == year && m >= startMonth && m <= endMonth) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    /** Fetch audit logs for CLI view */
    public List<String> getAuditLogs(String entity) {
        // Example placeholder, connect to AuditService or repository
        List<String> logs = new ArrayList<>();
        logs.add("audit_id | table_name | record_id | action | performed_by | performed_at");
        logs.add("24 | " + entity + " | 7 | STATUS_CHANGE | 2 | 2025-09-12T14:03:12");
        logs.add("16 | " + entity + " | 7 | CREATE | 2 | 2025-09-12T14:02:50");
        return logs;
    }
}
