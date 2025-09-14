package com.payments.model;
import java.time.OffsetDateTime;

public class AuditTrail {
    private Long id;
    private String tableName;
    private Long recordId;
    private String action;
    private Long performedBy;
    private String details; // store JSON as String for now
    private OffsetDateTime performedAt;

    public AuditTrail() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }
    public Long getRecordId() { return recordId; }
    public void setRecordId(Long recordId) { this.recordId = recordId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Long getPerformedBy() { return performedBy; }
    public void setPerformedBy(Long performedBy) { this.performedBy = performedBy; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public OffsetDateTime getPerformedAt() { return performedAt; }
    public void setPerformedAt(OffsetDateTime performedAt) { this.performedAt = performedAt; }

    @Override
    public String toString() { return "AuditTrail{id=" + id + ", action='" + action + "'}"; }
}
