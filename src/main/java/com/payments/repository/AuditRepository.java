package com.payments.repository;

import com.payments.config.DatabaseConfig;
import com.payments.model.AuditTrail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditRepository {

    // Save audit to DB
    public void save(AuditTrail audit) {
        String sql = "INSERT INTO audit_trails(table_name, record_id, action, performed_by, details) " +
                "VALUES (?, ?, ?, ?, ?::jsonb)"; // << cast string to jsonb

        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, audit.getTableName());
            preparedStatement.setObject(2, audit.getRecordId());
            preparedStatement.setString(3, audit.getAction());
            preparedStatement.setObject(4, audit.getPerformedBy());
            preparedStatement.setString(5, audit.getDetails()); // JSON string

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error saving audit trail", e);
        }
    }
}
