// PaymentController.java
package com.payments.controller;

import com.payments.model.Payment;
import com.payments.model.User;
import com.payments.service.PaymentService;
import com.payments.service.UserService;
import com.payments.dto.ReportDTO;

import java.util.List;
import java.util.concurrent.Future;

public class PaymentController {
    private final User currentUser;
    private final UserService userService;
    private final PaymentService paymentService;

    public PaymentController(User user, UserService userService) {
        this.currentUser = user;
        this.userService = userService;
        this.paymentService = new PaymentService();
    }

    public Payment createPayment(Long userId, Payment payment) {
        return paymentService.createPayment(userId, payment);
    }

    public Payment updatePaymentStatus(Long userId, Long paymentId, Long statusId) {
        return paymentService.updatePaymentStatus(userId, paymentId, statusId);
    }

    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    public Future<ReportDTO> generateMonthlyReport(Long userId, int year, int month) {
        return paymentService.generateMonthlyReport(userId, year, month);
    }

    public Future<ReportDTO> generateQuarterlyReport(Long userId, int year, int quarter) {
        return paymentService.generateQuarterlyReport(userId, year, quarter);
    }
}