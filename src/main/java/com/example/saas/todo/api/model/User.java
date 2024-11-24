package com.example.saas.todo.api.model;

import java.util.UUID;

public class User {
    private String id;
    private String name;
    private String companyId;
    private Role role;

    public User(String name, String companyId, Role role) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.companyId = companyId;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCompanyId() {
        return companyId;
    }

    public Role  getRole() {
        return role;
    }
    
    public String getRoleAsString() {
        return role.toString();
    }


}
