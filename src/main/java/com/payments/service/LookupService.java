package com.payments.service;

import com.payments.config.DatabaseConfig;
import java.sql.*;

public class LookupService {

    public static String getStatusName(int statusId) {
        String sql = "SELECT name FROM statuses WHERE id = ?";
        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, statusId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException exception) {
            System.err.println("Error fetching counterparty name: " + exception.getMessage());
        }
        return String.valueOf(statusId);
    }



    public static String getCounterpartyName(Long counterpartyId) {
        if (counterpartyId == null) return "Unknown";
        String sql = "SELECT name FROM counterparties WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, counterpartyId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException exception) {
            System.err.println("Error fetching counterparty name: " + exception.getMessage());
        }
        return String.valueOf(counterpartyId);
    }



    public static String getBankAccountName(Long bankAccountId) {
        if (bankAccountId == null) return "Unknown";
        String sql = "SELECT account_number FROM bank_accounts WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bankAccountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("account_number");
            }
        } catch (SQLException exception) {
            System.err.println("Error fetching counterparty name: " + exception.getMessage());
        }
        return String.valueOf(bankAccountId);
    }



    public static String getUsername(Long userId) {
        if (userId == null) return "Unknown";
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException exception) {
            System.err.println("Error fetching counterparty name: " + exception.getMessage());
        }
        return String.valueOf(userId);
    }


}