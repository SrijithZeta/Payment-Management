package com.payments.service;

import com.payments.model.Payment;

import java.math.BigDecimal;
import java.util.List;

public interface IPaymentService {

    Payment createPayment(Long userId, Payment payment);

    Payment updatePaymentStatus(Long userId, Long paymentId, Long newStatusId);

    List<Payment> getAllPayments();

    List<Payment> getPaymentsByStatus(Long statusId);

}
