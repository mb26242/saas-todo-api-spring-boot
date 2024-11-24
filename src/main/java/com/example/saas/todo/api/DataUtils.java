package com.example.saas.todo.api;

import com.example.saas.todo.api.model.Company;
import com.example.saas.todo.api.model.Role;

import com.example.saas.todo.api.repository.CompanyRepository;
import com.example.saas.todo.api.repository.TaskRepository;
import com.example.saas.todo.api.repository.UserRepository;


import org.springframework.stereotype.Component;

@Component
public class DataUtils { //implements CommandLineRunner 

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    public DataUtils(TaskRepository taskRepository, UserRepository userRepository, CompanyRepository companyRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    
    public void run() {
        // Company names
        String[] companyNames = {"Company1", "Company2", "Company3"};
    
        // Create and save companies
        for (String companyName : companyNames) {
            companyRepository.createCompany(companyName);
        }
    
        // Fetch saved companies
        Company company1 = companyRepository.findByName("Company1").orElseThrow();
        Company company2 = companyRepository.findByName("Company2").orElseThrow();
        Company company3 = companyRepository.findByName("Company3").orElseThrow();
    
        // Define users for each company 
        createUsersForCompany(company1, "company1");
        createUsersForCompany(company2, "company2");
        createUsersForCompany(company3, "company3");
    
        
    
        // Create tasks for each user 
        userRepository.findAll().forEach(user -> {
            for (int i = 1; i <= 5; i++) {
                taskRepository.createTaskForUser(
                    user, 
                    "Task " + i + " for " + user.getName(),
                    "This is task " + i + " assigned to " + user.getName()
                );
            }
        });

        String superAdminCompanyName = "SuperAdmins";
        Company superAdminCompany = companyRepository.createCompany(superAdminCompanyName);
    
        Role superAdminRole = Role.SUPER_USER;
        userRepository.createUserWithCompany("super-admin", superAdminCompany, superAdminRole);
    
        System.out.println("Data initialization complete: Sample companies, users, and tasks have been created.");
    }

    private void createUsersForCompany(Company company, String companyNameSuffix) {
        String[] userNames = {"user1", "user2", "user3"};
    
        Role userRole = Role.STANDARD;
        Role companyAdminRole = Role.COMPANY_ADMIN;
    
        for (String userName : userNames) {
            userRepository.createUserWithCompany(
                userName + "-" + companyNameSuffix,
                company,
                userRole
            );
        }
    
        String companyAdminUsername = "CompanyAdmin";
        userRepository.createUserWithCompany(
            companyAdminUsername + "-" + companyNameSuffix,
            company,
            companyAdminRole
        );
    }

    public boolean initializeDataIfEmpty() {
        if (companyRepository.count() > 0 || userRepository.count() > 0 || taskRepository.count() > 0) {
            System.out.println("Data already exists. Skipping initialization.");
            return false;
        }

        
        try {
            run();
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error during data initialization", e);
        }
    }

    public void emptyRepositories() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        companyRepository.deleteAll();

        System.out.println("Repositories have been emptied.");
    }

}
