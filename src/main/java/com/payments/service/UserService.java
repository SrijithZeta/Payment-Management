package com.payments.service;

import com.payments.config.DatabaseConfig;
import com.payments.model.RoleRequest;
import com.payments.model.User;
import com.payments.repository.UserRepository;
import com.payments.repository.UserRepositoryImpl;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository = new UserRepositoryImpl();



    public User signup(User user, String plainPassword) {
        user.setPasswordHash(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
        System.out.println("Inside UserService : "+user);
        User savedUser = userRepository.save(user);
        assignDefaultViewerRole(savedUser.getId());
        return savedUser;
    }



    private void assignDefaultViewerRole(Long userId) {
        String sql = "INSERT INTO user_roles(user_id, role_id) SELECT ?, id FROM roles WHERE name='VIEWER'";
        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning default role", e);
        }
    }



    public Optional<User> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }



    public boolean hasRole(Long userId, String roleName) {
        String sql = "SELECT 1 FROM user_roles ur JOIN roles r ON ur.role_id=r.id WHERE ur.user_id=? AND r.name=?";
        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, roleName);
            return preparedStatement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public List<String> getUserRoles(Long userId) {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT r.name FROM user_roles ur JOIN roles r ON ur.role_id = r.id WHERE ur.user_id=?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                roles.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user roles", e);
        }
        return roles;
    }




    public void assignRole(Long adminId, Long userId, String roleName) {
        if (!hasRole(adminId, "ADMIN")) {
            throw new RuntimeException("Only ADMIN can assign roles");
        }

        // Check if user already has role
        if (hasRole(userId, roleName)) {
            System.out.println("User ID " + userId + " already has role " + roleName);
            return; // nothing to do
        }

        String sql = "INSERT INTO user_roles(user_id, role_id) SELECT ?, id FROM roles WHERE name=?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, roleName);
            preparedStatement.executeUpdate();
            System.out.println("Role " + roleName + " assigned to user ID: " + userId);
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning role", e);
        }
    }




    public void requestRole(Long userId, String roleName) {
        if (hasRole(userId, "FINANCE_MANAGER")) {
            System.out.println("You already have the role: " + roleName);
            return;
        }
        String checkSql = "SELECT id, status FROM role_requests WHERE user_id = ? AND role_name = ?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
            checkPs.setLong(1, userId);
            checkPs.setString(2, roleName);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                Long requestId = rs.getLong("id");
                String currentStatus = rs.getString("status");

                if ("PENDING".equals(currentStatus)) {
                    System.out.println("You already have a pending request for this role.");
                    return;
                } else if ("APPROVED".equals(currentStatus)) {
                    System.out.println("You already have this role approved. Nothing to request.");
                    return;
                } else if ("REJECTED".equals(currentStatus)) {
                    // update old rejected request back to PENDING
                    String updateSql = "UPDATE role_requests SET status='PENDING' WHERE id=?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
                        preparedStatement.setLong(1, requestId);
                        preparedStatement.executeUpdate();
                    }
                    System.out.println("Your rejected request has been re-submitted for review.");
                    return;
                }
            } else {
                // no request exists → insert new one
                String sql = "INSERT INTO role_requests(user_id, role_name, status) VALUES (?, ?, 'PENDING')";
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setLong(1, userId);
                    ps.setString(2, roleName);
                    ps.executeUpdate();
                }
                System.out.println("Role request submitted. Wait for admin approval.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking/saving role request", e);
        }
    }


    // check ststus of role requet
    public void requestStatus(Long userId) {
        String checkSql = "SELECT status FROM role_requests WHERE user_id = ?";
        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(checkSql);
            preparedStatement.setLong(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                System.out.println("Role request Status: " + rs.getString("status"));
            } else {
                System.out.println("No role request found for this user.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking existing requests", e);
        }
    }



    // Admin lists pending requests
    public List<RoleRequest> listPendingRoleRequests() {
        List<RoleRequest> requests = new ArrayList<>();
        String sql = "SELECT id, user_id, role_name, status FROM role_requests WHERE status = 'PENDING'";
        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RoleRequest req = new RoleRequest();
                req.setId(rs.getLong("id"));
                req.setUserId(rs.getLong("user_id"));
                req.setRoleName(rs.getString("role_name"));
                req.setStatus(rs.getString("status"));
                requests.add(req);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching role requests", e);
        }
        return requests;
    }



    // Admin approves/rejects role request
    public void reviewRoleRequest(Long adminId, Long requestId, boolean approve) {
        if (!hasRole(adminId, "ADMIN")) {
            throw new RuntimeException("Only ADMIN can review role requests");
        }

        String selectSql = "SELECT user_id, role_name FROM role_requests WHERE id=?";
        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement ps = connection.prepareStatement(selectSql);
            ps.setLong(1, requestId);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) throw new RuntimeException("Request not found");

            Long userId = rs.getLong("user_id");
            String roleName = rs.getString("role_name");

            String status = approve ? "APPROVED" : "REJECTED";
            String updateSql = "UPDATE role_requests SET status=? WHERE id=?";
            try (PreparedStatement ps2 = connection.prepareStatement(updateSql)) {
                ps2.setString(1, status);
                ps2.setLong(2, requestId);
                ps2.executeUpdate();
            }

            if (approve) {
                assignRole(adminId, userId, roleName);
                System.out.println("Role " + roleName + " assigned to user ID: " + userId);
            } else {
                System.out.println("Role request rejected.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<User> listAllUsers() {
        return userRepository.findAll();
    }
}
