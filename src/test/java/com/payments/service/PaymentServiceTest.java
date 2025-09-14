package com.payments.service;

import com.payments.dto.PaymentDTO;
import com.payments.dto.PaymentView;
import com.payments.model.Direction;
import com.payments.model.Payment;
import com.payments.model.AuditTrail;
import com.payments.repository.PaymentRepository;
import com.payments.repository.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @DisplayName("Create Payment - unAuthorized User")
    void testCreatePayment_Unauthorized() {
        Long userId = 2L;
        Payment payment = new Payment();

        when(userService.hasRole(userId, "FINANCE_MANAGER")).thenReturn(false);
        when(userService.hasRole(userId, "ADMIN")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.createPayment(userId, payment));

        assertEquals("User must be FINANCE_MANAGER or ADMIN to create payment", ex.getMessage());
        verify(paymentRepository, never()).save(any());
    }


    @Test
    @DisplayName("Should handle direction enum values correctly")
    void testDirectionEnum_Values() {
        // Act & Assert
        assertEquals("INCOMING", Direction.INCOMING.toString(), "INCOMING direction should be correct");
        assertEquals("OUTGOING", Direction.OUTGOING.toString(), "OUTGOING direction should be correct");

        // Test enum values exist
        Direction[] directions = Direction.values();
        assertEquals(2, directions.length, "Should have exactly 2 direction values");
        assertTrue(java.util.Arrays.asList(directions).contains(Direction.INCOMING));
        assertTrue(java.util.Arrays.asList(directions).contains(Direction.OUTGOING));
    }



    @Test
    @DisplayName("Update Payment Status - Payment Not Found")
    void testUpdatePaymentStatus_PaymentNotFound() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.updatePaymentStatus(1L, 99L, 2));

        assertEquals("Payment not found", ex.getMessage());
    }
}
