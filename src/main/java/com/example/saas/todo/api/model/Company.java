package com.example.saas.todo.api.model;

import java.util.UUID;

public class Company {
    private String id;
    private String name;

    public Company(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
