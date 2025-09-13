package com.payments.repository;

import com.payments.config.DatabaseConfig;
import com.payments.dto.PaymentView;
import com.payments.model.Direction;
import com.payments.model.Payment;

import java.math.BigDecimal;
import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentRepositoryImpl implements PaymentRepository {

    @Override
    public Payment save(Payment payment) {
        String sql = "INSERT INTO payments(direction, category_id, status_id, amount, currency, counterparty_id, bank_account_id, description, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, created_at";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, payment.getDirection().name());
            preparedStatement.setInt(2, payment.getCategoryId());
            preparedStatement.setInt(3, payment.getStatusId());
            preparedStatement.setBigDecimal(4, payment.getAmount());
            preparedStatement.setString(5, payment.getCurrency());
            preparedStatement.setLong(6, payment.getCounterpartyId());
            preparedStatement.setLong(7, payment.getBankAccountId());
            preparedStatement.setString(8, payment.getDescription());
            preparedStatement.setLong(9, payment.getCreatedBy());

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                payment.setId(rs.getLong("id"));
                payment.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving payment", e);
        }
        return payment;
    }



    @Override
    public Optional<Payment> findById(Long id) {
        String sql = "SELECT * FROM payments WHERE id=?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return Optional.of(mapPayment(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }



    @Override
    public List<PaymentView> findAll() {
        List<PaymentView> list = new ArrayList<>();
        String sql = "SELECT p.id, p.direction, c.name AS category_name, s.name AS status_name, " +
                "p.amount, p.currency, cp.name AS counterparty_name, ba.account_number AS bank_account, " +
                "p.description, p.reference, u.username AS created_by, p.created_at " +
                "FROM payments p " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "LEFT JOIN statuses s ON p.status_id = s.id " +
                "LEFT JOIN counterparties cp ON p.counterparty_id = cp.id " +
                "LEFT JOIN bank_accounts ba ON p.bank_account_id = ba.id " +
                "LEFT JOIN users u ON p.created_by = u.id " +
                "ORDER BY p.created_at DESC";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                PaymentView pv = new PaymentView();
                pv.setId(rs.getLong("id"));
                pv.setDirection(rs.getString("direction"));
                pv.setCategoryName(rs.getString("category_name"));
                pv.setStatusName(rs.getString("status_name"));
                pv.setAmount(rs.getBigDecimal("amount"));
                pv.setCurrency(rs.getString("currency"));
                pv.setCounterpartyName(rs.getString("counterparty_name"));
                pv.setBankAccount(rs.getString("bank_account"));
                pv.setDescription(rs.getString("description"));
                pv.setReference(rs.getString("reference"));
                pv.setCreatedBy(rs.getString("created_by"));
                pv.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));

                list.add(pv);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching payments", e);
        }
        return list;
    }



    @Override
    public void updateStatus(Long paymentId, Integer statusId) {
        String sql = "UPDATE payments SET status_id=?, updated_at=NOW() WHERE id=?";
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, statusId);
            preparedStatement.setLong(2, paymentId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating payment status", e);
        }
    }



    private Payment mapPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getLong("id"));
        payment.setDirection(Direction.valueOf(rs.getString("direction")));
        payment.setCategoryId(rs.getInt("category_id"));
        payment.setStatusId(rs.getInt("status_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setCurrency(rs.getString("currency"));
        payment.setCounterpartyId(rs.getLong("counterparty_id"));
        payment.setBankAccountId(rs.getLong("bank_account_id"));
        payment.setDescription(rs.getString("description"));
        payment.setCreatedBy(rs.getLong("created_by"));
        payment.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
        payment.setUpdatedAt(rs.getObject("updated_at", OffsetDateTime.class));

        Timestamp createdTs = rs.getTimestamp("created_at");
        payment.setCreatedAt(createdTs != null ? createdTs.toInstant().atOffset(ZoneOffset.UTC) : null);

        Timestamp updatedTs = rs.getTimestamp("updated_at");
        payment.setUpdatedAt(updatedTs != null ? updatedTs.toInstant().atOffset(ZoneOffset.UTC) : null);


        return payment;
    }


}
