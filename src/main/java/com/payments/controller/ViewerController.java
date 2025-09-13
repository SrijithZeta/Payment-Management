package com.payments.controller;

import com.payments.model.User;
import com.payments.service.UserService;
import com.payments.util.UI;

import java.util.Scanner;

public class ViewerController {
    public static User handleViewerMenu(int choice, User currentUser, UserService userService,
                                        PaymentController paymentController, Scanner scanner) {
        switch (choice) {
            case 1 -> paymentController.viewPayments();
            case 2 -> paymentController.viewReportHistory();
            case 3 -> userService.requestRole(currentUser.getId(), "FINANCE_MANAGER");
            case 4 -> userService.requestStatus(currentUser.getId());
            case 5 -> System.out.println("Your roles: " + currentUser.getUserRoles(currentUser.getId()));
            case 6 -> {
                UI.printInfo("Logged out: " + currentUser.getUsername());
                return null;
            }
        }
        return currentUser;
    }
}
