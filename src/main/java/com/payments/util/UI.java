package com.payments.util;

import com.payments.model.User;
import com.payments.service.UserService;
import java.util.List;

public class UI{

    public static void printHeader() {
        System.out.println("\n=== PAYMENT MANAGEMENT SYSTEM ===");
    }

    public static void printWelcome(User user) {
        System.out.println("\nWelcome, " + user.getFullName() + " (" + user.getUsername() + ")");

    }

    public static void printLoginMenu() {
        System.out.println("\nPlease select an option:");
        System.out.println("1. Sign Up (New User)");
        System.out.println("2. Login");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    public static void printAdminMenu() {
        System.out.println("\n--- ADMIN MENU ---");
        System.out.println("1. Create Payment");
        System.out.println("2. Update Payment Status");
        System.out.println("3. View All Payments");
        System.out.println("4. View Pending Role Requests");
        System.out.println("5. Approve/Reject Role Requests");
        System.out.println("6. Generate Monthly Report");
        System.out.println("7. Generate Quarterly Report");
        System.out.println("8. View Report History");
        System.out.println("9. Check My Roles");
        System.out.println("10. Delete User Account");
        System.out.println("11. Logout");
        System.out.print("Choose: ");
    }

    public static void printFinanceManagerMenu() {
        System.out.println("\n--- FINANCE MANAGER MENU ---");
        System.out.println("1. Create Payment");
        System.out.println("2. Update Payment Status");
        System.out.println("3. View All Payments");
        System.out.println("4. Generate Monthly Report");
        System.out.println("5. Generate Quarterly Report");
        System.out.println("6. View Report History");
        System.out.println("7. Check My Roles");
        System.out.println("8. Logout");
        System.out.print("Choose: ");
    }

    public static void printViewerMenu() {
        System.out.println("\n--- VIEWER MENU ---");
        System.out.println("1. View All Payments");
        System.out.println("2. View Report History");
        System.out.println("3. Request Finance Manager Role");
        System.out.println("4. Check Role Request Status");
        System.out.println("5. Check My Roles");
        System.out.println("6. Logout");
        System.out.print("Choose: ");
    }

    public static void printSuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }

    public static void printError(String message) {
        System.out.println("ERROR: " + message);
    }

    public static void printWarning(String message) {
        System.out.println("WARNING: " + message);
    }

    public static void printInfo(String message) {
        System.out.println("INFO: " + message);
    }

    public static void printSeparator() {
        System.out.println("-------------------------------------------------------------");
    }

    private static String getUserRolesDisplay(User user) {
        try {
            List<String> roles = user.getUserRoles(user.getId());
            return String.join(", ", roles);
        } catch (Exception e) {
            return "VIEWER";  // gvivng viewer as a default role
        }
    }

}