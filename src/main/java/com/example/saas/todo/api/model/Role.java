package com.example.saas.todo.api.model;

import java.util.Optional;

public enum Role {
    STANDARD("Standard"),
    COMPANY_ADMIN("Company-Admin"),
    SUPER_USER("Super-User");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return role;
    }

    // For dynamic user - input cases, not used in this iteration of the system
    
    public static Optional<Role> fromString(String roleString) {
        if (roleString == null || roleString.isEmpty()) {
            return Optional.empty();  // Eempty Optional if the input is null or empty
        }
        

        for (Role role : Role.values()) {
            if (role.getRole().equalsIgnoreCase(roleString)) {
                return Optional.of(role);  // Matching Role as an Optional
            }
        }

        return Optional.empty();  // Empty Optional if no match is found
    }

    // Default if input invalid
    public static Role fromStringOrDefault(String roleString) {
        return fromString(roleString).orElse(STANDARD);  // Default to STANDARD
    }

}
