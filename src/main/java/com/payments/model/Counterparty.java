package com.payments.model;
//new
import java.time.OffsetDateTime;

public class Counterparty {
    private Long id;
    private String name;
    private String details; // store JSON as String for now
    private OffsetDateTime createdAt;

    public Counterparty() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() { return "Counterparty{id=" + id + ", name='" + name + "'}"; }
}
