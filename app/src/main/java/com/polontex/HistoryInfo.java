package com.polontex;

public class HistoryInfo {
    private String name, email, details;

    public HistoryInfo(String name, String email, String details) {
        this.name = name;
        this.email = email;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "HistoryInfo{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
