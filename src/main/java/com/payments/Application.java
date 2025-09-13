package com.payments;

import com.payments.controller.AdminController;
import com.payments.controller.ManagerController;
import com.payments.controller.PaymentController;
import com.payments.controller.ViewerController;
import com.payments.model.User;
import com.payments.service.UserService;
import com.payments.util.UI;

import java.util.Optional;
import java.util.Scanner;

public class Application {

    private final Scanner scanner;
    private final UserService userService;
    private final PaymentController paymentController;

    public Application() {
        this.scanner = new Scanner(System.in);
        this.userService = new UserService();
        this.paymentController = new PaymentController(userService);
    }

    public void run() {
        User currentUser = null;

        while (true) {
            UI.printHeader();

            if (currentUser == null) {
                currentUser = handleAuthenticationMenu();
            } else {
                currentUser = handleRoleBasedMenu(currentUser);
            }
        }
    }

    private User handleAuthenticationMenu() {
        UI.printLoginMenu();
        int choice;

        try {
            choice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            UI.printError("Invalid input. Please enter a number.");
            return null;
        }

        switch (choice) {
            case 1 -> {
                return handleSignup();
            }
            case 2 -> {
                return handleLogin();
            }
            case 0 -> {
                UI.printInfo("Exiting...");
                System.exit(0);
            }
            default -> UI.printError("Invalid choice.");
        }
        return null;
    }

    private User handleSignup() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User newUser = new User(null, username, fullName, email, null);
            User createdUser = userService.signup(newUser, password);
            UI.printSuccess("Signed up successfully: " + username);
            UI.printInfo("You have been assigned VIEWER role by default");
            return createdUser;
        } catch (RuntimeException e) {
            UI.printError("Signup failed: " + e.getMessage());
            return null;
        }
    }

    private User handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        Optional<User> userOpt = userService.login(username, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UI.printSuccess("Login successful!");
            UI.printWelcome(user);
            return user;
        } else {
            UI.printError("Login failed - Invalid credentials");
            return null;
        }
    }

    private User handleRoleBasedMenu(User currentUser) {
        UI.printWelcome(currentUser);
        UI.printSeparator();

        int choice;

        try {
            if (userService.hasRole(currentUser.getId(), "ADMIN")) {
                UI.printAdminMenu();
                choice = Integer.parseInt(scanner.nextLine());
                return AdminController.handleAdminMenu(choice, currentUser, userService, paymentController, scanner);

            } else if (userService.hasRole(currentUser.getId(), "FINANCE_MANAGER")) {
                UI.printFinanceManagerMenu();
                choice = Integer.parseInt(scanner.nextLine());
                return ManagerController.handleFinanceManagerMenu(choice, currentUser, userService, paymentController, scanner);

            } else {
                UI.printViewerMenu();
                choice = Integer.parseInt(scanner.nextLine());
                return ViewerController.handleViewerMenu(choice, currentUser, userService, paymentController, scanner);
            }
        } catch (NumberFormatException e) {
            UI.printError("Invalid input. Please enter a number.");
        }

        return currentUser;
    }
}
