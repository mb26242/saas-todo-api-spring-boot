package com.example.saas.todo.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.saas.todo.api.DataUtils;

import com.example.saas.todo.api.model.User;
import com.example.saas.todo.api.model.Company;

import com.example.saas.todo.api.repository.UserRepository;
import com.example.saas.todo.api.repository.CompanyRepository;

import java.util.List;

@RestController
@RequestMapping("/")
public class UtilityController {

    private final DataUtils dataUtils;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public UtilityController(DataUtils dataUtils, UserRepository userRepository, CompanyRepository companyRepository) {
        this.dataUtils = dataUtils;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping("/populate")
    public ResponseEntity<String> populateData() {

        boolean initialization = dataUtils.initializeDataIfEmpty();

        if (initialization) {
            return ResponseEntity.ok("Population successful.");
        } else {
            return ResponseEntity.ok("Repositories already populated.");
        }
    }

    @GetMapping("/emptyRepositories")
    public ResponseEntity<String> emptyRepositories() {
        dataUtils.emptyRepositories();
        return ResponseEntity.ok("Repositories successfully emptied.");
    }

    @GetMapping("/")
    public ResponseEntity<String> getApiDocumentation() {
        return ResponseEntity.ok("API documentation is available at `/apidoc.html`.");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(companyRepository.findAll());
    }
}
