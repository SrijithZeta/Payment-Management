package com.payments.controller;

import com.payments.config.DatabaseConfig;
import com.payments.dto.PaymentView;
import com.payments.model.*;
import com.payments.repository.PaymentRepositoryImpl;
import com.payments.service.PaymentService;
import com.payments.service.ReportService;
import com.payments.service.UserService;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PaymentController {

    private final UserService userService;
    private final PaymentService paymentService;
    private final Scanner scanner = new Scanner(System.in);
    private final ReportService reportService = new ReportService();

    // Constructor
    public PaymentController(UserService userService) {
        this.userService = userService;
        this.paymentService = new PaymentService(userService);
    }

    public void createPayment(User user) {
        if (!userService.hasRole(user.getId(), "FINANCE_MANAGER") &&
                !userService.hasRole(user.getId(), "ADMIN")) {
            System.out.println("Permission denied: must be FINANCE_MANAGER or ADMIN");
            return;
        }

        System.out.print("Enter direction (INCOMING/OUTGOING): ");
        String dirInput = scanner.nextLine().toUpperCase();
        Direction direction;
        try {
            direction = Direction.valueOf(dirInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid direction. Defaulting to OUTGOING.");
            direction = Direction.OUTGOING;
        }

        System.out.println("Category Options: 1=Salary, 2=Vendor Payment, 3=Client Invoice");
        int categoryId;
        while (true) {
            System.out.print("Enter category id: ");
            categoryId = Integer.parseInt(scanner.nextLine());
            if (categoryId >= 1 && categoryId <= 3) break;
            System.out.println("Invalid category. Choose 1, 2, or 3.");
        }

        System.out.print("Enter amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        System.out.print("Enter currency (e.g., INR, USD): ");
        String currency = scanner.nextLine().toUpperCase();

        List<Counterparty> counterparties = paymentService.listCounterparties();
        System.out.println("Existing Counterparties:");
        counterparties.forEach(c -> System.out.println(c.getId() + " = " + c.getName()));

        System.out.print("Enter counterparty id (or 0 to add new): ");
        long counterpartyId = Long.parseLong(scanner.nextLine());

        if (counterpartyId == 0) {
            System.out.print("Enter new counterparty name: ");
            String newCounterparty = scanner.nextLine();

            try (Connection conn = DatabaseConfig.getConnection()) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO counterparties(name, details, created_at) VALUES (?, ?::jsonb, now()) RETURNING id"
                );
                ps.setString(1, newCounterparty);
                ps.setString(2, "{}"); // empty JSON object for details
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    counterpartyId = rs.getLong("id");
                    System.out.println("Added new counterparty with ID: " + counterpartyId);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error adding new counterparty", e);
            }
        }

        List<BankAccount> bankAccounts = paymentService.listBankAccounts();
        System.out.println("Bank Account Options:");
        bankAccounts.forEach(b -> System.out.println("id : "+b.getId() + " =>  " + b.getBankName() + " -  ACC No : " + b.getAccountNumber()));
        System.out.print("Enter bank account id: ");
        long bankAccountId = Long.parseLong(scanner.nextLine());

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        Payment payment = new Payment(
                null,
                direction,
                categoryId,
                1, // statusId default = 1 (e.g., PENDING)
                amount,
                currency,
                counterpartyId,
                bankAccountId,
                description,
                user.getId()
        );

        Payment saved;
        try {
            saved = paymentService.createPayment(user.getId(), payment);
            System.out.println("Saved Payment: " + saved);
        } catch (RuntimeException e) {
            System.out.println("Error saving payment: " + e.getMessage());
        }
    }



    public void updatePaymentStatus(User user) {
        viewPayments();

        System.out.print("Enter payment ID: ");
        long paymentId = Long.parseLong(scanner.nextLine());

        System.out.println("Status Options: 1=Pending, 2=Processing, 3=Completed");

        int statusId;
        while (true) {
            System.out.print("Enter status id: ");
            statusId = Integer.parseInt(scanner.nextLine());
            if (statusId >= 1 && statusId <= 3) break;
            System.out.println("Invalid status. Choose 1, 2, or 3.");
        }

        paymentService.updatePaymentStatus(user.getId(), paymentId, statusId);
        System.out.println("✅ Updated Payment ID " + paymentId + " to status " + statusId);
    }


    public void viewPayments() {
        PaymentRepositoryImpl paymentRepository = new PaymentRepositoryImpl();
        List<PaymentView> payments = paymentRepository.findAll();
        if (payments.isEmpty()) {
            System.out.println("No payments found.");
        } else {
            payments.forEach(System.out::println);
        }
    }



    public void generateMonthlyReport(User currentUser) {
        try {
            System.out.print("Enter year (e.g., 2025): ");
            int year = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter month (1-12): ");
            int month = Integer.parseInt(scanner.nextLine());

            Report report = reportService.generateMonthlyReport(year, month, currentUser.getId());
            System.out.println("Monthly report generated successfully!");
            System.out.println("Report ID: " + report.getId());
            System.out.println("File: " + report.getFilePath());
            System.out.println("Total Incoming: " + report.getTotalIncoming());
            System.out.println("Total Outgoing: " + report.getTotalOutgoing());
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            System.err.println("Error generating monthly report: " + e.getMessage());
        }
    }

    public void generateQuarterlyReport(User currentUser) {
        try {
            System.out.print("Enter year (e.g., 2025): ");
            int year = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter quarter (1-4): ");
            int quarter = Integer.parseInt(scanner.nextLine());

            Report report = reportService.generateQuarterlyReport(year, quarter, currentUser.getId());
            System.out.println("Quarterly report generated successfully!");
            System.out.println("Report ID: " + report.getId());
            System.out.println("File: " + report.getFilePath());
            System.out.println("Total Incoming: " + report.getTotalIncoming());
            System.out.println("Total Outgoing: " + report.getTotalOutgoing());
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Please enter valid numbers.");
        } catch (Exception e) {
            System.err.println("Error generating quarterly report: " + e.getMessage());
        }
    }

    public void viewReportHistory() {
        try {
            List<Report> reports = reportService.getReportHistory();
            if (reports.isEmpty()) {
                System.out.println("No reports found.");
                return;
            }

            System.out.println("\nReport History:");
            System.out.println("ID | Type | Period | Status | Incoming | Outgoing | Generated At");
            System.out.println("---|------|--------|--------|----------|----------|-------------");

            for (Report report : reports) {
                String period = report.getReportType() == ReportType.MONTHLY
                        ? report.getYear() + "/" + String.format("%02d", report.getMonth())
                        : report.getYear() + "/Q" + report.getQuarter();

                System.out.printf("%-3d| %-6s| %-7s| %-7s| %-9s| %-9s| %s%n",
                        report.getId(),
                        report.getReportType(),
                        period,
                        report.getStatus(),
                        report.getTotalIncoming(),
                        report.getTotalOutgoing(),
                        report.getCreatedAt().toLocalDateTime()
                );
            }
        } catch (Exception e) {
            System.err.println("Error fetching report history: " + e.getMessage());
        }
    }

}
