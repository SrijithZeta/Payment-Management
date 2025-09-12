package com.payments.controller;

import com.payments.exception.*;
import com.payments.model.User;
import com.payments.service.IUserService;
import com.payments.service.UserService;

import java.util.Optional;

public class UserController {

    private final IUserService userService = new UserService();

    public User register(String username, String email, String fullName, String rawPassword) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setFullName(fullName);

        try {
            User user = userService.register(u, rawPassword);
            System.out.println("User registered successfully! ID=" + user.getId());
            return user;
        } catch (DuplicateUserException ex) {
            System.err.println("Registration failed: Username already exists.");
            throw ex;
        } catch (DuplicateEmailException ex) {
            System.err.println("Registration failed: Email already exists.");
            throw ex;
        } catch (RuntimeException ex) {
            System.err.println("Registration failed: " + ex.getMessage());
            throw ex;
        }
    }

    public Optional<User> login(String username, String password) {
        try {
            return userService.login(username, password);
        } catch (AuthenticationException e) {
            System.err.println("Login failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    public void assignRole(Long adminId, Long targetUserId, Long roleId) {
        try {
            userService.assignRole(targetUserId, roleId);
            System.out.println("Role assigned successfully to userId=" + targetUserId);
        } catch (RuntimeException e) {
            System.err.println("Failed to assign role: " + e.getMessage());
            throw e;
        }
    }
}
