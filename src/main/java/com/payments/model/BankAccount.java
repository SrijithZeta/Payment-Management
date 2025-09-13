package com.payments.model;
//new
import java.time.OffsetDateTime;

public class BankAccount {
    private Long id;
    private String accountNumber;
    private String bankName;
    private String currency; //
    private Long ownerCounterpartyId;
    private OffsetDateTime createdAt;

    public BankAccount() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public Long getOwnerCounterpartyId() { return ownerCounterpartyId; }
    public void setOwnerCounterpartyId(Long ownerCounterpartyId) { this.ownerCounterpartyId = ownerCounterpartyId; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() { return "BankAccount{id=" + id + ", accountNumber='" + accountNumber + "'}"; }
}
