package com.payments.controller;

import com.payments.model.RoleRequest;
import com.payments.model.User;
import com.payments.service.UserService;
import com.payments.util.UI;

import java.util.List;
import java.util.Scanner;

public class AdminController {

    public static User handleAdminMenu(int choice, User currentUser, UserService userService,
                                        PaymentController paymentController, Scanner scanner) {
        switch (choice) {
            case 1 -> paymentController.createPayment(currentUser);
            case 2 -> paymentController.updatePaymentStatus(currentUser);
            case 3 -> paymentController.viewPayments();
            case 4 -> {
                List<RoleRequest> requests = userService.listPendingRoleRequests();
                if (requests.isEmpty()) {
                    UI.printInfo("No pending requests.");
                } else {
                    requests.forEach(System.out::println);
                }
            }
            case 5 -> {
                List<com.payments.model.RoleRequest> requests = userService.listPendingRoleRequests();
                if (requests.isEmpty()) {
                    UI.printInfo("No pending requests.");
                } else {
                    requests.forEach(r -> System.out.println("Request ID: " + r.getId() + ", User ID: " + r.getUserId() + ", Role: " + r.getRoleName()));
                    System.out.print("Enter request ID to review: ");
                    long requestId = Long.parseLong(scanner.nextLine());
                    System.out.print("Approve? (yes/no): ");
                    boolean approve = scanner.nextLine().equalsIgnoreCase("yes");
                    userService.reviewRoleRequest(currentUser.getId(), requestId, approve);
                }
            }
            case 6 -> paymentController.generateMonthlyReport(currentUser);
            case 7 -> paymentController.generateQuarterlyReport(currentUser);
            case 8 -> paymentController.viewReportHistory();
            case 9 -> System.out.println("Your roles: " + currentUser.getUserRoles(currentUser.getId()));
            case 10 -> {
                UI.printInfo("Logged out: " + currentUser.getUsername());
                return null;
            }
        }
        return currentUser;
    }
}
