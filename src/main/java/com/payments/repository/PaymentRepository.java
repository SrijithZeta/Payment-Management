package com.payments.repository;
//new
import com.payments.dto.PaymentView;
import com.payments.model.Payment;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<PaymentView> findAll();
    void updateStatus(Long paymentId, Integer statusId);
}
