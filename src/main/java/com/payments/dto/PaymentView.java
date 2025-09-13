package com.payments.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class PaymentView {
    private Long id;
    private String direction;
    private String categoryName;
    private String statusName;
    private BigDecimal amount;
    private String currency;
    private String counterpartyName;
    private String bankAccount;
    private String description;
    private String reference;
    private String createdBy;
    private OffsetDateTime createdAt;

    @Override
    public String toString() {
        return String.format("Payment ID #%d [%s] %s %s %s\n - Category: %s, Status: %s\n - Counterparty: %s, Account: %s\n - Desc: %s\n - Created by: %s at %s",
                id, direction, amount, currency, (reference != null ? "Ref: " + reference : ""),
                categoryName, statusName,
                counterpartyName, bankAccount,
                description,
                createdBy, createdAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCounterpartyName() {
        return counterpartyName;
    }

    public void setCounterpartyName(String counterpartyName) {
        this.counterpartyName = counterpartyName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

