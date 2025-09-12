package com.payments.service;

import com.payments.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class AuditService {

    public void record(String tableName, Long recordId, String action, Long performedBy) {
        String sql = "INSERT INTO audit_trails(table_name, record_id, action, performed_by, performed_at) " +
                "VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tableName);
            ps.setLong(2, recordId);
            ps.setString(3, action);
            ps.setLong(4, performedBy);
            ps.setObject(5, LocalDateTime.now());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to record audit", e);
        }
    }
}
