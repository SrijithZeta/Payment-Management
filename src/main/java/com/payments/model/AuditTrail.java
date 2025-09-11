package com.payments.model;

import java.time.LocalDateTime;

public class AuditTrail {
    private Long id;
    private String tableName;
    private Long recordId;
    private String action;
    private Long performedBy;
    private LocalDateTime performedAt;

    public AuditTrail() {}
    public AuditTrail(Long id, String tableName, Long recordId,
                      String action, Long performedBy, LocalDateTime performedAt) {
        this.id = id;
        this.tableName = tableName;
        this.recordId = recordId;
        this.action = action;
        this.performedBy = performedBy;
        this.performedAt = performedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(Long performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    @Override
    public String toString() {
        return "AuditTrail{" +
                "id=" + id +
                ", tableName='" + tableName + '\'' +
                ", recordId=" + recordId +
                ", action='" + action + '\'' +
                ", performedBy=" + performedBy +
                ", performedAt=" + performedAt +
                '}';
    }
}
