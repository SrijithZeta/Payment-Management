package com.payments.repository;

import com.payments.config.DatabaseConfig;
import com.payments.model.Role;
import com.payments.model.User;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public User save(User user) {
        System.out.println("Inside userRepoImpl : "+user);
        String sql = "INSERT INTO users(username, email, password_hash, full_name) VALUES (?, ?, ?, ?) RETURNING id, created_at";
        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setString(4, user.getFullName());
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                user.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user", e);
        }
        return user;
    }



    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                User user = mapUser(rs);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }



    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                User user = mapUser(rs);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }



    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try {
            Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }



//    @Override
//    public void update(User user) {
//        String sql = "UPDATE users SET email=?, full_name=?, is_active=? WHERE id=?";
//        try {
//            Connection connection = DatabaseConfig.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setString(1, user.getEmail());
//            preparedStatement.setString(2, user.getFullName());
//            preparedStatement.setBoolean(3, user.isActive());
//            preparedStatement.setLong(4, user.getId());
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }


    @Override
    public void delete(Long id) {
        String deleteRoleRequests = "DELETE FROM role_requests WHERE user_id=?";
        String deleteUser = "DELETE FROM users WHERE id=?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement ps1 = connection.prepareStatement(deleteRoleRequests);
                 PreparedStatement ps2 = connection.prepareStatement(deleteUser)) {

                ps1.setLong(1, id);
                ps1.executeUpdate();

                ps2.setLong(1, id);
                ps2.executeUpdate();

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setActive(rs.getBoolean("is_active"));
        return user;
    }
}
