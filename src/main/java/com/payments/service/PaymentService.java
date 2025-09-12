// PaymentService.java
package com.payments.service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.payments.model.Payment;
import com.payments.model.PaymentDirection;
import com.payments.repository.PaymentRepository;
import com.payments.dto.ReportDTO;
import com.payments.util.ThreadPoolManager;

import java.util.List;
import java.util.concurrent.Future;

public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ThreadPoolManager threadPoolManager;

    public PaymentService() {
        this.paymentRepository = new PaymentRepository();
        this.threadPoolManager = ThreadPoolManager.getInstance();
    }

    public Payment createPayment(Long userId, Payment payment) {
        // Add validation and business logic here
        payment.setCreatedBy(userId);
        return paymentRepository.save(payment);
    }

    public Payment updatePaymentStatus(Long userId, Long paymentId, Long statusId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatusId(statusId);
        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Future<ReportDTO> generateMonthlyReport(Long userId, int year, int month) {
        return threadPoolManager.getReportExecutor().submit(() -> {
            ReportDTO report = new ReportDTO();
            String fileName = String.format("reports/%d-%02d-monthly.csv", year, month);

            // Get all payments for the month
            List<Payment> payments = paymentRepository.findByYearAndMonth(year, month);

            // Aggregate totals
            BigDecimal totalIncoming = BigDecimal.ZERO;
            BigDecimal totalOutgoing = BigDecimal.ZERO;
            Map<String, BigDecimal> categoryTotals = new HashMap<>();

            for (Payment payment : payments) {
                if (payment.getDirection() == PaymentDirection.INCOMING) {
                    totalIncoming = totalIncoming.add(payment.getAmount());
                } else {
                    totalOutgoing = totalOutgoing.add(payment.getAmount());
                }

                // Aggregate by category

//
//                String category = payment.getCategory();
//                categoryTotals.merge(category, payment.getAmount(), BigDecimal::add);
            }

            // Generate CSV content
            StringBuilder csv = new StringBuilder();
            csv.append("Direction,Amount,Currency,Category,Status,Date\n");

            for (Payment payment : payments) {
                csv.append(String.format("%s,%s,%s,%s,%s,%s\n",
                        payment.getDirection(),
                        payment.getAmount(),
                        payment.getCurrency(),
//                        payment.getCategory(),
                        payment.getStatusId(),
                        payment.getCreatedAt()
                ));
            }

            // Write to file
            Files.write(Paths.get(fileName), csv.toString().getBytes());

            // Create summary
            String summary = String.format(
                    "Total incoming: %.2f%nTotal outgoing: %.2f%nBy category:%n%s",
                    totalIncoming,
                    totalOutgoing,
                    categoryTotals.entrySet().stream()
                            .map(e -> String.format("- %s: %.2f", e.getKey(), e.getValue()))
                            .collect(Collectors.joining("\n"))
            );

            report.setReportPath(fileName);
            report.setSummary(summary);
            return report;
        });
    }

    public Future<ReportDTO> generateQuarterlyReport(Long userId, int year, int quarter) {
        return threadPoolManager.getReportExecutor().submit(() -> {
            ReportDTO report = new ReportDTO();
            String fileName = String.format("reports/%d-Q%d-quarterly.csv", year, quarter);

            // Calculate quarter months
            int startMonth = (quarter - 1) * 3 + 1;
            int endMonth = startMonth + 2;

            // Get all payments for the quarter
            List<Payment> payments = paymentRepository.findByYearAndMonthRange(year, startMonth, endMonth);

            // Aggregate totals
            BigDecimal totalIncoming = BigDecimal.ZERO;
            BigDecimal totalOutgoing = BigDecimal.ZERO;
            Map<String, BigDecimal> categoryTotals = new HashMap<>();
            Map<Integer, BigDecimal> monthlyTotals = new HashMap<>();

            for (Payment payment : payments) {
                if (payment.getDirection() == PaymentDirection.INCOMING) {
                    totalIncoming = totalIncoming.add(payment.getAmount());
                } else {
                    totalOutgoing = totalOutgoing.add(payment.getAmount());
                }

                // Aggregate by category and month

//
//                String category = payment.getCategory();
//                categoryTotals.merge(category, payment.getAmount(), BigDecimal::add);

                LocalDate date = payment.getCreatedAt().toLocalDate();
                monthlyTotals.merge(date.getMonthValue(), payment.getAmount(), BigDecimal::add);
            }

            // Generate CSV content
            StringBuilder csv = new StringBuilder();
            csv.append("Direction,Amount,Currency,Category,Status,Date\n");

            for (Payment payment : payments) {
                csv.append(String.format("%s,%s,%s,%s,%s,%s\n",
                        payment.getDirection(),
                        payment.getAmount(),
                        payment.getCurrency(),
//                        payment.getCategory(),
                        payment.getStatusId(),
                        payment.getCreatedAt()
                ));
            }

            // Write to file
            Files.write(Paths.get(fileName), csv.toString().getBytes());

            // Create summary
            String summary = String.format(
                    "Quarter %d, %d%nTotal incoming: %.2f%nTotal outgoing: %.2f%n%n" +
                            "By category:%n%s%n%nMonthly breakdown:%n%s",
                    quarter,
                    year,
                    totalIncoming,
                    totalOutgoing,
                    categoryTotals.entrySet().stream()
                            .map(e -> String.format("- %s: %.2f", e.getKey(), e.getValue()))
                            .collect(Collectors.joining("\n")),
                    monthlyTotals.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .map(e -> String.format("- Month %d: %.2f", e.getKey(), e.getValue()))
                            .collect(Collectors.joining("\n"))
            );

            report.setReportPath(fileName);
            report.setSummary(summary);
            return report;
        });
    }
}