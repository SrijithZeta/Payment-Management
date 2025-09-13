package com.payments.model;

public class RoleRequest {
    private Long id;
    private Long userId;
    private String roleName;
    private String status; // PENDING, APPROVED, REJECTED

    public RoleRequest() {}

    public RoleRequest(Long id, Long userId, String roleName, String status) {
        this.id = id;
        this.userId = userId;
        this.roleName = roleName;
        this.status = status;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "RoleRequest{" +
                "id=" + id +
                ", userId=" + userId +
                ", roleName='" + roleName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
