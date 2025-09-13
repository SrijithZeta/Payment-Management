package com.payments.dto;

import java.math.BigDecimal;
import java.util.Map;

public class ReportDTO {
    private String period;
    private BigDecimal totalIncoming;
    private BigDecimal totalOutgoing;
    private Map<String, BigDecimal> categoryTotals;
    private String filePath;

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public BigDecimal getTotalIncoming() { return totalIncoming; }
    public void setTotalIncoming(BigDecimal totalIncoming) { this.totalIncoming = totalIncoming; }
    public BigDecimal getTotalOutgoing() { return totalOutgoing; }
    public void setTotalOutgoing(BigDecimal totalOutgoing) { this.totalOutgoing = totalOutgoing; }
    public Map<String, BigDecimal> getCategoryTotals() { return categoryTotals; }
    public void setCategoryTotals(Map<String, BigDecimal> categoryTotals) { this.categoryTotals = categoryTotals; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}