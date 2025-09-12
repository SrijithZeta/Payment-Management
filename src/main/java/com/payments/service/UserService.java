package com.payments.service;

import com.payments.config.DatabaseConfig;
import com.payments.exception.AuthenticationException;
import com.payments.exception.DuplicateEmailException;
import com.payments.exception.DuplicateUserException;
import com.payments.model.Role;
import com.payments.model.User;
import com.payments.model.UserRole;
import com.payments.repository.RoleRepository;
import com.payments.repository.UserRepository;
import com.payments.repository.UserRoleRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService implements IUserService {

    private final UserRepository userRepo = new UserRepository();
    private final RoleRepository roleRepo = new RoleRepository();
    private final UserRoleRepository userRoleRepo = new UserRoleRepository();

    @Override
    public User register(User user, String rawPassword) {
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
        user.setPasswordHash(hashed);

        try {
            User saved = userRepo.save(user);
            // Assign default role VIEWER
            roleRepo.findByName("VIEWER").ifPresent(role ->
                    userRoleRepo.save(new UserRole(saved.getId(), role.getId())));
            return saved;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof org.postgresql.util.PSQLException psqlEx) {
                String msg = psqlEx.getServerErrorMessage().getDetail();
                if (msg != null) {
                    if (msg.contains("users_username_key")) throw new DuplicateUserException("Username exists");
                    else if (msg.contains("users_email_key")) throw new DuplicateEmailException("Email exists");
                }
            }
            throw new RuntimeException("Failed to register user", e);
        }
    }

    @Override
    public Optional<User> login(String username, String rawPassword) {
        Optional<User> userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty() || !BCrypt.checkpw(rawPassword, userOpt.get().getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password");
        }
        return userOpt;
    }

    @Override
    public void assignRole(Long userId, Long roleId) {
        String sql = "INSERT INTO user_roles(user_id, role_id) VALUES(?,?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error assigning role", e);
        }
    }

    // New RBAC helpers
    public List<Role> getRoles(Long userId) {
        List<Long> roleIds = userRoleRepo.findRoleIdsByUser(userId);
        return roleIds.stream()
                .map(roleRepo::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public boolean hasRole(Long userId, String roleName) {
        return getRoles(userId).stream().anyMatch(r -> r.getName().equalsIgnoreCase(roleName));
    }
}
