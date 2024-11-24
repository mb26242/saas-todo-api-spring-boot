package com.example.saas.todo.api.repository;

import com.example.saas.todo.api.model.Company;
import com.example.saas.todo.api.model.User;
import com.example.saas.todo.api.model.Role;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;

@Repository
public class UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    private User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public User createUserWithCompany(String userName, Company company, Role role) {
    
        User user = new User(userName, company.getId(), role);
        return save(user);
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id)); 
    }

    public User findByName(String name) {
        return users.values().stream().filter(user -> user.getName().equals(name)).findFirst().orElse(null);
    }
    
    public List<User> findAll() {
        return new ArrayList<>(users.values()); // List of all users
    }

    public long count() {
        return users.size();
    }

    public void deleteById(String id) {
        users.remove(id);
    }

    public void deleteAll() {
        users.clear();
    }

}

