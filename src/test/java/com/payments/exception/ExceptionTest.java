package com.payments.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

public class ExceptionTest {

    @Test
    @DisplayName("AppException should inherit from RuntimeException")
    void testAppException_Inheritance() {
        // Arrange
        String message = "Test error message";
        Throwable cause = new RuntimeException("Root cause");

        // Act
        AppException exception1 = new AppException(message);
        AppException exception2 = new AppException(message, cause);

        // Assert
        Assertions.assertTrue(exception1 instanceof RuntimeException, "AppException should be instance of RuntimeException");
        Assertions.assertTrue(exception2 instanceof RuntimeException, "AppException should be instance of RuntimeException");
        Assertions.assertEquals(message, exception1.getMessage(), "Message should match");
        Assertions.assertEquals(message, exception2.getMessage(), "Message should match");
        Assertions.assertEquals(cause, exception2.getCause(), "Cause should match");
    }

    @Test
    @DisplayName("AuthenticationException should inherit from AppException")
    void testAuthenticationException_Inheritance() {
        // Arrange
        String message = "Authentication failed";

        // Act
        AuthenticationException exception = new AuthenticationException(message);

        // Assert
        Assertions.assertTrue(exception instanceof AppException, "AuthenticationException should be instance of AppException");
        Assertions.assertTrue(exception instanceof RuntimeException, "AuthenticationException should be instance of RuntimeException");
        Assertions.assertEquals(message, exception.getMessage(), "Message should match");
    }

    @Test
    @DisplayName("DataAccessException should inherit from AppException")
    void testDataAccessException_Inheritance() {
        // Arrange
        String message = "Database connection failed";

        // Act
        DataAccessException exception = new DataAccessException(message);

        // Assert
        Assertions.assertTrue(exception instanceof AppException, "DataAccessException should be instance of AppException");
        Assertions.assertTrue(exception instanceof RuntimeException, "DataAccessException should be instance of RuntimeException");
        Assertions.assertEquals(message, exception.getMessage(), "Message should match");
    }

    @Test
    @DisplayName("DuplicateEmailException should inherit from AppException")
    void testDuplicateEmailException_Inheritance() {
        // Arrange
        String message = "Email already exists";

        // Act
        DuplicateEmailException exception = new DuplicateEmailException(message);

        // Assert
        Assertions.assertTrue(exception instanceof AppException, "DuplicateEmailException should be instance of AppException");
        Assertions.assertTrue(exception instanceof RuntimeException, "DuplicateEmailException should be instance of RuntimeException");
        Assertions.assertEquals(message, exception.getMessage(), "Message should match");
    }


    @Test
    @DisplayName("Exception hierarchy should be properly structured")
    void testExceptionHierarchy() {
        // Arrange
        String message = "Test message";

        // Act
        AppException appException = new AppException(message);
        AuthenticationException authException = new AuthenticationException(message);
        DataAccessException dataException = new DataAccessException(message);
        DuplicateEmailException emailException = new DuplicateEmailException(message);

        // Assert
        Assertions.assertTrue(authException instanceof AppException, "AuthenticationException should extend AppException");
        Assertions.assertTrue(dataException instanceof AppException, "DataAccessException should extend AppException");
        Assertions.assertTrue(emailException instanceof AppException, "DuplicateEmailException should extend AppException");

        Assertions.assertTrue(appException instanceof RuntimeException, "AppException should extend RuntimeException");
        Assertions.assertTrue(authException instanceof RuntimeException, "AuthenticationException should extend RuntimeException");
        Assertions.assertTrue(dataException instanceof RuntimeException, "DataAccessException should extend RuntimeException");
        Assertions.assertTrue(emailException instanceof RuntimeException, "DuplicateEmailException should extend RuntimeException");
    }
}