package com.payments.dto;

import com.payments.model.Direction;
import java.math.BigDecimal;

public class PaymentDTO {
    private Long id;
    private Direction direction;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String category;

    public PaymentDTO() {}

    public PaymentDTO(Long id, Direction direction, BigDecimal amount, String currency, String status, String category) {
        this.id = id; this.direction = direction; this.amount = amount; this.currency = currency;
        this.status = status; this.category = category;
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
