package com.payments.service;

import com.payments.exception.AuthenticationException;
import com.payments.model.User;
import com.payments.repository.UserRepository;
import com.payments.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        String rawPassword = "kk";
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        User user = new User();
        user.setUsername("kk");
        user.setPasswordHash(hashed);

        when(userRepository.findByUsername("kk")).thenReturn(Optional.of(user));

        Optional<User> loggedInUser = userService.login("kk", rawPassword);

        assertTrue(loggedInUser.isPresent());
        assertEquals("kk", loggedInUser.get().getUsername());
    }

    @Test
    void testLoginFailureWrongPassword() {
        String rawPassword = "mypassword";
        String wrongPassword = "wrongpass";
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        User user = new User();
        user.setUsername("kk");
        user.setPasswordHash(hashed);

        when(userRepository.findByUsername("kk")).thenReturn(Optional.of(user));

        Optional<User> loggedInUser = userService.login("kk", wrongPassword);

        assertFalse(loggedInUser.isPresent());
    }


    @Test
    @DisplayName("Should throw AuthenticationException when non-admin tries to assign role")
    void testAssignRole_NonAdmin_ThrowsAuthenticationException() {
        Long nonAdminUserId = 2L;
        Long targetUserId = 3L;
        String roleName = "FINANCE_MANAGER";

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> userService.assignRole(nonAdminUserId, targetUserId, roleName),
                "Should throw AuthenticationException for non-admin user"
        );

        assertTrue(exception.getMessage().contains("Only ADMIN can assign roles"));
    }

    @Test
    @DisplayName("Should throw AuthenticationException when non-admin tries to review role request")
    void testReviewRoleRequest_NonAdmin_ThrowsAuthenticationException() {
        Long nonAdminUserId = 2L;
        Long requestId = 1L;
        boolean approve = true;
        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> userService.reviewRoleRequest(nonAdminUserId, requestId, approve),
                "Should throw AuthenticationException for non-admin user"
        );
        assertTrue(exception.getMessage().contains("Only ADMIN can review role requests"));
    }


    @Test
    @DisplayName("Should return empty optional for invalid login credentials")
    void testLogin_InvalidCredentials_ReturnsEmpty() {
        String username = "nonexistent";
        String password = "wrongpassword";

        var result = userService.login(username, password);

        assertTrue(result.isEmpty(), "Should return empty optional for invalid credentials");
    }

}
