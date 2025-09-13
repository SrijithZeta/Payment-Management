package com.payments.service;

import com.payments.dto.PaymentView;
import com.payments.model.BankAccount;
import com.payments.model.Counterparty;
import com.payments.model.Payment;
import com.payments.model.AuditTrail;
import com.payments.repository.PaymentRepository;
import com.payments.repository.PaymentRepositoryImpl;
import com.payments.repository.AuditRepository;
import com.payments.util.PaymentLogWriter;

import java.util.List;

public class PaymentService {

    private final PaymentRepository paymentRepository = new PaymentRepositoryImpl();
    private final AuditRepository auditRepository = new AuditRepository();
    private final UserService userService;


    public PaymentService(UserService userService) {
        this.userService = userService;
    }



    public Payment createPayment(Long userId, Payment payment) {
        // only FINANCE_MANAGER or ADMIN roles allowed
        if (!userService.hasRole(userId, "FINANCE_MANAGER") && !userService.hasRole(userId, "ADMIN")) {
            throw new RuntimeException("User must be FINANCE_MANAGER or ADMIN to create payment");
        }

        Payment savedPayment = paymentRepository.save(payment);
        // this is to add into logs for evey payment created
        AuditTrail audit = new AuditTrail();
        audit.setTableName("payments");
        audit.setRecordId(savedPayment.getId());
        audit.setAction("PAYMENT_CREATED");
        audit.setPerformedBy(userId);
        audit.setDetails("{\"direction\":\"" + savedPayment.getDirection() +
                "\",\"amount\":\"" + savedPayment.getAmount() + "\"}");
        auditRepository.save(audit);

        PaymentLogWriter.logPaymentAsync(savedPayment, "PAYMENT_CREATED", userId, null, null);

        return savedPayment;
    }



    public List<PaymentView>  listAllPayments(){
        return paymentRepository.findAll();
    }



    public void updatePaymentStatus(Long userId, Long paymentId, int newStatusId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (!userService.hasRole(userId, "FINANCE_MANAGER") && !userService.hasRole(userId, "ADMIN")) {
            throw new RuntimeException("User must be FINANCE_MANAGER or ADMIN to update payment");
        }

        int oldStatusId = payment.getStatusId();
        payment.setStatusId(newStatusId);

        paymentRepository.updateStatus(paymentId, newStatusId);

        AuditTrail audit = new AuditTrail();
        audit.setTableName("payments");
        audit.setRecordId(payment.getId());
        audit.setAction("STATUS_CHANGE");
        audit.setPerformedBy(userId);
        audit.setDetails("{\"old_status\":" + oldStatusId + ", \"new_status\":" + newStatusId + "}");
        auditRepository.save(audit);

        PaymentLogWriter.logPaymentAsync(payment, "STATUS_CHANGE", userId,
                String.valueOf(oldStatusId), String.valueOf(newStatusId));
    }



    public List<Counterparty> listCounterparties() {
        String sql = "SELECT id, name, details, created_at FROM counterparties ORDER BY id";
        try {
            var connection = com.payments.config.DatabaseConfig.getConnection();
             var preparedStatement = connection.prepareStatement(sql);
             var resultSet = preparedStatement.executeQuery();
            List<Counterparty> list = new java.util.ArrayList<>();
            while (resultSet.next()) {
                Counterparty c = new Counterparty();
                c.setId(resultSet.getLong("id"));
                c.setName(resultSet.getString("name"));
                c.setDetails(resultSet.getString("details"));
                c.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
                list.add(c);
            }
            return list;

        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Error fetching counterparties", e);
        }
    }



    public List<BankAccount> listBankAccounts() {
        String sql = "SELECT id, bank_name, account_number, created_at FROM bank_accounts ORDER BY id";

        try (var connection = com.payments.config.DatabaseConfig.getConnection();
             var preparedStatement = connection.prepareStatement(sql);
             var resultSet = preparedStatement.executeQuery()) {

            List<BankAccount> list = new java.util.ArrayList<>();
            while (resultSet.next()) {
                BankAccount bank = new BankAccount();
                bank.setId(resultSet.getLong("id"));
                bank.setBankName(resultSet.getString("bank_name"));
                bank.setAccountNumber(resultSet.getString("account_number"));
                bank.setCreatedAt(resultSet.getObject("created_at", java.time.OffsetDateTime.class));
                list.add(bank);
            }
            return list;

        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Error fetching bank accounts", e);
        }
    }

}
