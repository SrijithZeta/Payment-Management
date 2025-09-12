package com.payments.repository;

import com.payments.model.Payment;
import java.util.List;
import java.util.Optional;

public interface IPaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<Payment> findByStatus(Long statusId);
    List<Payment> findAll();
}
