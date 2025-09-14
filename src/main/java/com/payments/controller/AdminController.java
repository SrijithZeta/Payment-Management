package com.payments.controller;

import com.payments.model.RoleRequest;
import com.payments.model.User;
import com.payments.repository.UserRepositoryImpl;
import com.payments.service.UserService;
import com.payments.util.UI;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                List<RoleRequest> requests = userService.listPendingRoleRequests();
                if (requests.isEmpty()) {
                    UI.printInfo("No pending requests.");
                } else {
                    UserRepositoryImpl userRepository = new UserRepositoryImpl();
                    Map<Long, User> usersById = userRepository.findAll().stream()
                            .collect(Collectors.toMap(User::getId, Function.identity()));

                    requests.forEach(r -> {
                        User user = usersById.get(r.getUserId());
                        String fullName = (user != null) ? user.getFullName() : "Unknown User";
                        System.out.println("Request ID: " + r.getId() +
                                ", User ID: " + r.getUserId() +
                                ", Name: " + fullName +
                                ", Requested Role: " + r.getRoleName());
                    });

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
            case 10->{
                UserRepositoryImpl userRepository = new UserRepositoryImpl();
                List<User> users = userRepository.findAll();
                users.forEach(u -> System.out.println("User ID: " + u.getId()+" Name : "+u.getFullName() +" "+ u.getUserRoles(u.getId())));
                System.out.print("Enter user ID to delete: ");
                long userId = Long.parseLong(scanner.nextLine());
                if(userId == currentUser.getId()){
                    UI.printError("You cannot delete your own account.");
                } else if (userRepository.findById(userId)==null) {
                    UI.printError("User ID not found.");
                } else {
                    userRepository.delete(userId);
                    UI.printInfo("User ID " + userId + " deleted.");
                }
            }
            case 11 -> {
                UI.printInfo("Logged out: " + currentUser.getUsername());
                return null;
            }
        }
        return currentUser;
    }
}
