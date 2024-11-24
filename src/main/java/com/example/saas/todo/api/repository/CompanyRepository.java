package com.example.saas.todo.api.repository;

import com.example.saas.todo.api.model.Company;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;



@Repository
public class CompanyRepository {
    private final Map<String, Company> companies = new ConcurrentHashMap<>();

    private Company save(Company company) {
        companies.put(company.getId(), company);
        return company;
    }

    public Company createCompany(String companyName) {
        Company company = new Company(companyName);
        return save(company);
    }

    public void deleteById(String id) {
        companies.remove(id);
    }

    public void deleteAll() {
        companies.clear();
    }

    public List<Company> findAll() {
        return List.copyOf(companies.values());
    }

    public Company findById(String id) {
        return companies.get(id);
    }

    public Optional<Company> findByName(String name) {
        return companies.values().stream()
            .filter(company -> company.getName().equals(name))
            .findFirst();
    }

    public long count() {
        return companies.size();
    }
}
