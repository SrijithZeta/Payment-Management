// ReportDTO.java
package com.payments.dto;

public class ReportDTO {
    private String reportPath;
    private String summary;

    // Getters and setters
    public String getReportPath() {
        return reportPath;
    }

    public void setReportPath(String reportPath) {
        this.reportPath = reportPath;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}