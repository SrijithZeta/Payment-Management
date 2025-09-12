package com.payments.controller;

import com.payments.model.Role;
import com.payments.model.User;
import com.payments.service.ReportService;
import com.payments.service.AuditService;
import com.payments.dto.ReportDTO;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Future;

public class ReportController {

    private final User loggedInUser;
    private final List<Role> userRoles;
    private final ReportService reportService = new ReportService();
    private final AuditService auditService = new AuditService();
    private final Scanner scanner = new Scanner(System.in);

    public ReportController(User user, List<Role> roles) {
        this.loggedInUser = user;
        this.userRoles = roles;
    }

    private boolean hasRole(String roleName) {
        return userRoles.stream().anyMatch(r -> r.getName().equalsIgnoreCase(roleName));
    }

    public void generateReport() {
        if (!hasRole("ADMIN") && !hasRole("FINANCE_MANAGER")) {
            System.out.println("Unauthorized. Only ADMIN or FINANCE_MANAGER can generate reports.");
            return;
        }

        try {
            System.out.println("Generate type: 1) Monthly 2) Quarterly");
            System.out.print("> ");
            int type = Integer.parseInt(scanner.nextLine());

            if (type == 1) {
                System.out.print("Enter year (YYYY): ");
                int year = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter month (1-12): ");
                int month = Integer.parseInt(scanner.nextLine());

                Future<ReportDTO> future = reportService.generateMonthlyReportAsync(loggedInUser.getId(), year, month);
                System.out.println("Report generation started in background.");
                ReportDTO report = future.get(); // waits for completion
                displayReport(report);

            } else if (type == 2) {
                System.out.print("Enter year (YYYY): ");
                int year = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter quarter (1-4): ");
                int quarter = Integer.parseInt(scanner.nextLine());

                Future<ReportDTO> future = reportService.generateQuarterlyReportAsync(loggedInUser.getId(), year, quarter);
                System.out.println("Report generation started in background.");
                ReportDTO report = future.get();
                displayReport(report);
            } else {
                System.out.println("Invalid choice.");
            }

            auditService.record("reports", null, "GENERATE_REPORT", loggedInUser.getId());

        } catch (Exception e) {
            System.err.println("Failed to generate report: " + e.getMessage());
        }
    }

    private void displayReport(ReportDTO report) {
//        System.out.println("Report complete: " + report.getFilePath());
//        System.out.println("Total incoming: " + report.getTotalIncoming());
//        System.out.println("Total outgoing: " + report.getTotalOutgoing());
//        System.out.println("By category:");
//        report.getCategorySummary().forEach((cat, amount) -> System.out.println(" - " + cat + ": " + amount));
    }

    public void viewAuditLogs() {
        System.out.print("Enter entity name (payments/users): ");
        String entity = scanner.nextLine();
        reportService.getAuditLogs(entity).forEach(System.out::println);
    }
}
