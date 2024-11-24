package com.example.saas.todo.api.model;

import java.util.UUID;

public class Task {
    private String id;
    private String title;
    private String description;
    private String userId;
    private String companyId;

    // Constructor 
    public Task(String title, String description, String userId, String companyId) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.companyId = companyId;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    // Setters (for updates)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

}



