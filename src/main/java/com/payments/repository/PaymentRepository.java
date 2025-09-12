package com.payments.repository;

import com.payments.config.DatabaseConfig;
import com.payments.exception.DataAccessException;
import com.payments.model.Payment;
import com.payments.model.PaymentDirection;

import java.sql.*;
import java.util.*;

public class PaymentRepository implements IPaymentRepository {

    @Override
    public Payment save(Payment payment) {
        String sql = "INSERT INTO payments(direction,category_id,status_id,amount,currency,"
                + "counterparty_id,bank_account_id,description,created_by) "
                + "VALUES (?::payment_direction,?,?,?,?,?,?,?,?) RETURNING id";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, payment.getDirection().name());
            ps.setLong(2, payment.getCategoryId());
            ps.setLong(3, payment.getStatusId());
            ps.setBigDecimal(4, payment.getAmount());
            ps.setString(5, payment.getCurrency());
            ps.setLong(6, payment.getCounterpartyId());
            ps.setLong(7, payment.getBankAccountId());
            ps.setString(8, payment.getDescription());
            ps.setLong(9, payment.getCreatedBy());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) payment.setId(rs.getLong("id"));
            return payment;
        } catch (SQLException e) {
            throw new DataAccessException("Error saving payment: " + e.getMessage());
        }
    }

    @Override
    public Optional<Payment> findById(Long id) {
        String sql = "SELECT * FROM payments WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new DataAccessException("Error finding payment by id: " + e.getMessage());
        }
    }

    @Override
    public List<Payment> findByStatus(Long statusId) {
        String sql = "SELECT * FROM payments WHERE status_id = ?";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, statusId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding payments by status: " + e.getMessage());
        }
    }

    @Override
    public List<Payment> findAll() {
        String sql = "SELECT * FROM payments";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all payments: " + e.getMessage());
        }
    }

    public List<Payment> findByYearAndMonth(int year, int month) {
        String sql = "SELECT * FROM payments WHERE EXTRACT(YEAR FROM created_at) = ? AND EXTRACT(MONTH FROM created_at) = ?";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding payments by year and month: " + e.getMessage());
        }
    }

    public List<Payment> findByYearAndMonthRange(int year, int startMonth, int endMonth) {
        String sql = "SELECT * FROM payments WHERE EXTRACT(YEAR FROM created_at) = ? " +
                "AND EXTRACT(MONTH FROM created_at) BETWEEN ? AND ?";
        List<Payment> list = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, startMonth);
            ps.setInt(3, endMonth);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException("Error finding payments by year and month range: " + e.getMessage());
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getLong("id"));
        p.setDirection(PaymentDirection.valueOf(rs.getString("direction")));
        p.setCategoryId(rs.getLong("category_id"));
        p.setStatusId(rs.getLong("status_id"));
        p.setAmount(rs.getBigDecimal("amount"));
        p.setCurrency(rs.getString("currency"));
        p.setCounterpartyId(rs.getLong("counterparty_id"));
        p.setBankAccountId(rs.getLong("bank_account_id"));
        p.setDescription(rs.getString("description"));
        p.setCreatedBy(rs.getLong("created_by"));
        p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return p;
    }
}