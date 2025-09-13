package com.payments.model;
//new
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Report {
    private Long id;
    private ReportType reportType;
    private Integer year;
    private Integer month;
    private Integer quarter;
    private String filePath;
    private String status;
    private Long generatedBy;
    private BigDecimal totalIncoming;
    private BigDecimal totalOutgoing;
    private String meta; // JSON as String
    private OffsetDateTime createdAt;

    public Report() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public Integer getQuarter() { return quarter; }
    public void setQuarter(Integer quarter) { this.quarter = quarter; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(Long generatedBy) { this.generatedBy = generatedBy; }
    public BigDecimal getTotalIncoming() { return totalIncoming; }
    public void setTotalIncoming(BigDecimal totalIncoming) { this.totalIncoming = totalIncoming; }
    public BigDecimal getTotalOutgoing() { return totalOutgoing; }
    public void setTotalOutgoing(BigDecimal totalOutgoing) { this.totalOutgoing = totalOutgoing; }
    public String getMeta() { return meta; }
    public void setMeta(String meta) { this.meta = meta; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() { return "Report{id=" + id + ", type=" + reportType + ", status=" + status + "}"; }
}
