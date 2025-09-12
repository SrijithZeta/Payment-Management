package com.payments.service;

import com.payments.exception.AuthenticationException;
import com.payments.model.User;

import java.util.Optional;

public interface IUserService {
    User register(User user, String rawPassword);
    Optional<User> login(String username, String rawPassword) throws AuthenticationException;
    void assignRole(Long userId, Long roleId);
}
