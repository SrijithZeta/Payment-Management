package com.payments.repository;

import com.payments.config.DatabaseConfig;
import com.payments.model.UserRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//
//        save – Assigns a role to a user.
//
//        findRoleIdsByUser – Returns all role IDs for a given user.
//
//        findUserRole – Quickly checks if a specific role assignment exists.


public class UserRoleRepository {

    /** Assign a role to a user */
    public void save(UserRole ur) {
        String sql = "INSERT INTO user_roles(user_id, role_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ur.getUserId());
            ps.setLong(2, ur.getRoleId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning role to user", e);
        }
    }

    /** Fetch roles assigned to a particular user */
    public List<Long> findRoleIdsByUser(Long userId) {
        String sql = "SELECT role_id FROM user_roles WHERE user_id = ?";
        List<Long> roleIds = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                roleIds.add(rs.getLong("role_id"));
            }
            return roleIds;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding roles for user", e);
        }
    }

    /** Optional: Check if a user already has a specific role */
    public Optional<UserRole> findUserRole(Long userId, Long roleId) {
        String sql = "SELECT * FROM user_roles WHERE user_id = ? AND role_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                UserRole ur = new UserRole();
                ur.setUserId(rs.getLong("user_id"));
                ur.setRoleId(rs.getLong("role_id"));
                return Optional.of(ur);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user role", e);
        }
    }
}
