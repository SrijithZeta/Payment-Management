package com.payments.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Payment {
    private Long id;
    private Direction direction;
    private Integer categoryId;
    private Integer statusId;
    private BigDecimal amount;
    private String currency;
    private Long counterpartyId;
    private Long bankAccountId;
    private String description;
    private String reference;
    private Long createdBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Payment() {}

    public Payment(Object o, String direction, int categoryId, int i, BigDecimal amount, String currency, long counterpartyId, long bankAccountId, String description, Long id, Object o1, Object o2) {
    }

    public Payment(Long id, Direction direction, int categoryId, int statusId,
                   BigDecimal amount, String currency, long counterpartyId,
                   long bankAccountId, String description, Long createdBy) {
        this.id = id;
        this.direction = direction;
        this.categoryId = categoryId;
        this.statusId = statusId;
        this.amount = amount;
        this.currency = currency;
        this.counterpartyId = counterpartyId;
        this.bankAccountId = bankAccountId;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = OffsetDateTime.now();
    }


    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getStatusId() { return statusId; }
    public void setStatusId(Integer statusId) { this.statusId = statusId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Long getCounterpartyId() { return counterpartyId; }
    public void setCounterpartyId(Long counterpartyId) { this.counterpartyId = counterpartyId; }
    public Long getBankAccountId() { return bankAccountId; }
    public void setBankAccountId(Long bankAccountId) { this.bankAccountId = bankAccountId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", direction=" + direction +
                ", categoryId=" + categoryId +
                ", statusId=" + statusId +
                ", amount=" + amount +
                " " + currency +
                ", counterpartyId=" + counterpartyId +
                ", bankAccountId=" + bankAccountId +
                ", description='" + description + '\'' +
                ", createdBy=" + createdBy +
                ", createdAt=" + createdAt +
                '}';
    }

}
