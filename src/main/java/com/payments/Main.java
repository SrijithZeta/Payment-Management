package com.payments;

import com.payments.model.*;
import com.payments.service.PaymentService;
import com.payments.service.UserService;
import com.payments.controller.PaymentController;
import com.payments.exception.AuthenticationException;
import com.payments.exception.DuplicateEmailException;
import com.payments.exception.DuplicateUserException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static PaymentController paymentController;

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            System.out.println("\n=== Payments Management System ===");
            System.out.println("1) Signup");
            System.out.println("2) Login");
            System.out.println("3) Exit");
            System.out.print("> ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> signupFlow();
                case "2" -> loginFlow();
                case "3" -> running = false;
                default -> System.out.println("Invalid choice!");
            }
        }

        System.out.println("Goodbye!");
        System.exit(0);
    }

    private static void signupFlow() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter full name: ");
            String fullName = scanner.nextLine();

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setFullName(fullName);

            User saved = userService.register(newUser, password);
            System.out.println("User created with ID: " + saved.getId());
            System.out.println("NOTE: Request role assignment from ADMIN to gain management rights.");

        } catch (DuplicateUserException | DuplicateEmailException e) {
            System.err.println("Signup failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void loginFlow() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            Optional<User> userOpt = userService.login(username, password);
            if (userOpt.isPresent()) {
                User loggedInUser = userOpt.get();
                System.out.println("Login successful. Welcome " + loggedInUser.getFullName() +
                        " (ID=" + loggedInUser.getId() + ")");
                System.out.println("Your roles: " + userService.getRoles(loggedInUser.getId()));

                paymentController = new PaymentController(loggedInUser, userService);
                mainMenu(loggedInUser);

            }
        } catch (AuthenticationException e) {
            System.err.println("Login failed: " + e.getMessage());
        }
    }

    private static void mainMenu(User loggedInUser) {
        boolean menuRunning = true;

        while (menuRunning) {
            System.out.println("\nMenu:");
            System.out.println("1) Create Payment");
            System.out.println("2) Update Payment Status");
            System.out.println("3) View Payments");
            System.out.println("4) Logout");
            System.out.println("5) Generate Report");
            System.out.print("> ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> createPaymentFlow(loggedInUser);
                case "2" -> updatePaymentStatusFlow(loggedInUser);
                case "3" -> viewPaymentsFlow();
                case "4" -> menuRunning = false;
                case "5" -> generateReportFlow(loggedInUser);
                default -> System.out.println("Invalid choice!");
            }
        }
        System.out.println("Logged out.");
    }

    private static void createPaymentFlow(User user) {
        if (!userService.hasRole(user.getId(), "FINANCE_MANAGER") &&
                !userService.hasRole(user.getId(), "ADMIN")) {
            System.err.println("You are not authorized to create payments.");
            return;
        }

        try {
            System.out.print("Enter direction (INCOMING/OUTGOING): ");
            String dir = scanner.nextLine();
            System.out.print("Enter category id: ");
            Long catId = Long.parseLong(scanner.nextLine());
            System.out.print("Enter amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());
            System.out.print("Enter currency (ISO3): ");
            String currency = scanner.nextLine();
            System.out.print("Enter counterparty id: ");
            Long counterpartyId = Long.parseLong(scanner.nextLine());
            System.out.print("Enter bank account id: ");
            Long bankAccountId = Long.parseLong(scanner.nextLine());
            System.out.print("Enter description: ");
            String desc = scanner.nextLine();

            Payment p = new Payment();
            p.setDirection(PaymentDirection.valueOf(dir.toUpperCase()));
            p.setCategoryId(catId);
            p.setAmount(amount);
            p.setCurrency(currency);
            p.setCounterpartyId(counterpartyId);
            p.setBankAccountId(bankAccountId);
            p.setDescription(desc);
            p.setCreatedBy(user.getId());
            p.setStatusId(1L); // PENDING

            Payment saved = paymentController.createPayment(user.getId(), p);
            System.out.println("Payment created: id=" + saved.getId() +
                    ", direction=" + saved.getDirection() +
                    ", amount=" + saved.getAmount() +
                    " " + saved.getCurrency() +
                    ", status=PENDING");

        } catch (Exception e) {
            System.err.println("Error creating payment: " + e.getMessage());
        }
    }


    private static void generateReportFlow(User user) {
        if (!userService.hasRole(user.getId(), "FINANCE_MANAGER") &&
                !userService.hasRole(user.getId(), "ADMIN")) {
            System.err.println("You are not authorized to generate reports.");
            return;
        }

        try {
            System.out.println("Generate type: 1) Monthly 2) Quarterly");
            System.out.print("> ");
            String typeChoice = scanner.nextLine();

            if ("1".equals(typeChoice)) {
                System.out.print("Enter year (YYYY): ");
                int year = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter month (1-12): ");
                int month = Integer.parseInt(scanner.nextLine());

                var futureReport = paymentController.generateMonthlyReport(user.getId(), year, month);
                System.out.println("Report generation started in background (task id: " + futureReport.hashCode() + ")");
                System.out.println("You can continue. When ready, select 'View Reports'.");

            } else if ("2".equals(typeChoice)) {
                System.out.print("Enter year (YYYY): ");
                int year = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter quarter (1-4): ");
                int quarter = Integer.parseInt(scanner.nextLine());

                var futureReport = paymentController.generateQuarterlyReport(user.getId(), year, quarter);
                System.out.println("Report generation started in background (task id: " + futureReport.hashCode() + ")");
                System.out.println("You can continue. When ready, select 'View Reports'.");

            } else {
                System.out.println("Invalid choice.");
            }

        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
        }
    }



    private static void updatePaymentStatusFlow(User user) {
        if (!userService.hasRole(user.getId(), "FINANCE_MANAGER") &&
                !userService.hasRole(user.getId(), "ADMIN")) {
            System.err.println("You are not authorized to update payment status.");
            return;
        }

        try {
            System.out.print("Enter payment ID: ");
            Long paymentId = Long.parseLong(scanner.nextLine());
            System.out.print("Enter new status id: ");
            Long statusId = Long.parseLong(scanner.nextLine());

            Payment updated = paymentController.updatePaymentStatus(user.getId(), paymentId, statusId);
            System.out.println("Status updated: id=" + updated.getId() +
                    ", new_status=" + updated.getStatusId());

        } catch (Exception e) {
            System.err.println("Error updating payment: " + e.getMessage());
        }
    }

    private static void viewPaymentsFlow() {
        System.out.println("Listing payments:");
        paymentController.getAllPayments().forEach(p -> System.out.println(
                "ID=" + p.getId() +
                        " | Direction=" + p.getDirection() +
                        " | Amount=" + p.getAmount() +
                        " | Currency=" + p.getCurrency() +
                        " | Status=" + p.getStatusId() +
                        " | CreatedBy=" + p.getCreatedBy()
        ));
    }
}
