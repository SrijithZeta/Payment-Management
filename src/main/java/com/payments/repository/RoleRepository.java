package com.payments.repository;

import com.payments.config.DatabaseConfig;
import com.payments.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//save(Role) – Inserts a new role and returns it with generated ID.
//
//        findById / findByName – Fetch role details.
//
//        findAll – List all roles.

public class RoleRepository {

    public Role save(Role role) {
        String sql = "INSERT INTO roles(name) VALUES (?) RETURNING id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, role.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) role.setId(rs.getLong("id"));
            return role;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving role", e);
        }
    }

    public Optional<Role> findById(Long id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding role", e);
        }
    }

    public Optional<Role> findByName(String name) {
        String sql = "SELECT * FROM roles WHERE name = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding role by name", e);
        }
    }

    public List<Role> findAll() {
        String sql = "SELECT * FROM roles";
        List<Role> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all roles", e);
        }
    }

    private Role mapRow(ResultSet rs) throws SQLException {
        Role r = new Role();
        r.setId(rs.getLong("id"));
        r.setName(rs.getString("name"));
        return r;
    }
}
