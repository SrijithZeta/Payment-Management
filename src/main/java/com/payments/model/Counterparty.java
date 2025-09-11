package com.payments.model;

public class Counterparty {
    private Long id;
    private String type; // CLIENT, VENDOR, EMPLOYEE
    private String name;
    private String contactEmail;

    public Counterparty() {}
    public Counterparty(Long id, String type, String name, String contactEmail) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.contactEmail = contactEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @Override
    public String toString() {
        return "Counterparty{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", contactEmail='" + contactEmail + '\'' +
                '}';
    }
}
