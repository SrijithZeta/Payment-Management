package com.payments.controller;

import com.payments.model.User;
import com.payments.service.UserService;
import com.payments.util.UI;

import java.util.Scanner;

public class ManagerController {

    public static User handleFinanceManagerMenu(int choice, User currentUser, UserService userService,
                                                PaymentController paymentController, Scanner scanner) {
        switch (choice) {
            case 1 -> paymentController.createPayment(currentUser);
            case 2 -> paymentController.updatePaymentStatus(currentUser);
            case 3 -> paymentController.viewPayments();
            case 4 -> paymentController.generateMonthlyReport(currentUser);
            case 5 -> paymentController.generateQuarterlyReport(currentUser);
            case 6 -> paymentController.viewReportHistory();
            case 7 -> System.out.println("Your roles: " + currentUser.getUserRoles(currentUser.getId()));
            case 8 -> {
                UI.printInfo("Logged out: " + currentUser.getUsername());
                return null;
            }
        }
        return currentUser;
    }
}
