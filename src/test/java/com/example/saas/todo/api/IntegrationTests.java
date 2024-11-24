package com.example.saas.todo.api;

import com.example.saas.todo.api.model.Company;
import com.example.saas.todo.api.model.Role;
import com.example.saas.todo.api.model.Task;
import com.example.saas.todo.api.model.User;
import com.example.saas.todo.api.repository.CompanyRepository;
import com.example.saas.todo.api.repository.TaskRepository;
import com.example.saas.todo.api.repository.UserRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DataUtils dataUtils;

    private User superAdminUser;
    private User companyAdminUser;
    private User standardUser;
    private User standardUser2;

    
    private Company superAdminCompany;
    private Company company;
    private Company company2;

    @BeforeEach
    void setup() {
        dataUtils.emptyRepositories();

        // Create a companies
        superAdminCompany = companyRepository.createCompany("SuperAdmin Company");
        company = companyRepository.createCompany("Test Company");
        company2 = companyRepository.createCompany("Test Company 2");

        // Create users
        superAdminUser = userRepository.createUserWithCompany("Super Admin", superAdminCompany, Role.SUPER_USER);
        companyAdminUser = userRepository.createUserWithCompany("Company Admin", company, Role.COMPANY_ADMIN);
        standardUser = userRepository.createUserWithCompany("Standard User", company, Role.STANDARD);
        standardUser2 = userRepository.createUserWithCompany("Standard User2", company2, Role.STANDARD);
    }

    @AfterEach
    void cleanup() {
        dataUtils.emptyRepositories();
    }

    @Test
    void testCreateTaskForStandardUser() throws Exception {
        String taskJson = """
            {
                "title": "Standard User Task",
                "description": "This is a task for the standard user."
            }
            """;

        mockMvc.perform(post("/user/" + standardUser.getId() + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Standard User Task"))
                .andExpect(jsonPath("$.description").value("This is a task for the standard user."));
    }

    @Test
    void testGetTasksForCompanyAdmin() throws Exception {
        // Add tasks for the company
        taskRepository.createTaskForUser(standardUser, "Task 1", "Description 1");
        taskRepository.createTaskForUser(companyAdminUser, "Task 2", "Description 2");

        mockMvc.perform(get("/user/" + companyAdminUser.getId() + "/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.title=='Task 1')]").exists())
                .andExpect(jsonPath("$[?(@.title=='Task 2')]").exists());
    }

    @Test
    void testSuperUserAccessToAllTasks() throws Exception {
        // Add tasks for multiple users
        taskRepository.createTaskForUser(standardUser, "Super Task 1", "Description 1");
        taskRepository.createTaskForUser(companyAdminUser, "Super Task 2", "Description 2");

        mockMvc.perform(get("/user/" + superAdminUser.getId() + "/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.title=='Super Task 1')]").exists())
                .andExpect(jsonPath("$[?(@.title=='Super Task 2')]").exists());
    }

    @Test
    void testCompanyAdminTaskCreation() throws Exception {
        String taskJson = """
            {
                "title": "Admin Task",
                "description": "Task created by company admin."
            }
            """;

        mockMvc.perform(post("/user/" + companyAdminUser.getId() + "/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Admin Task"))
                .andExpect(jsonPath("$.description").value("Task created by company admin."));
    }

    @Test
    void testUpdateTask() throws Exception {
        // Create a task
        Task task = taskRepository.createTaskForUser(standardUser, "Initial Task", "Initial Description");

        String updatedTaskJson = """
            {
                "title": "Updated Task Title",
                "description": "Updated Task Description"
            }
            """;

        mockMvc.perform(put("/user/" + standardUser.getId() + "/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedTaskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task Title"))
                .andExpect(jsonPath("$.description").value("Updated Task Description"));
    }

    @Test
    void testDeleteTaskByStandardUser() throws Exception {
        // Create a task for the standard user
        Task task = taskRepository.createTaskForUser(standardUser, "Task to Delete", "Task description");

        mockMvc.perform(delete("/user/" + standardUser.getId() + "/tasks/" + task.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testTaskAccessRestrictionOtherCompanyStandardUser() throws Exception {
        // Task created by other standard user
        Task task = taskRepository.createTaskForUser(companyAdminUser, "Restricted Task", "Restricted access description");

        mockMvc.perform(get("/user/" + standardUser2.getId() + "/tasks/" + task.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testTaskAccessRestrictionCompanyAdmin() throws Exception {
        // Task created by company admin
        Task task = taskRepository.createTaskForUser(companyAdminUser, "Restricted Task", "Restricted access description");

        mockMvc.perform(get("/user/" + standardUser.getId() + "/tasks/" + task.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testInvalidUserAccess() throws Exception {
        // Invalid user ID
        mockMvc.perform(get("/user/invalid-user-id/tasks"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEmptyTaskList() throws Exception {
        mockMvc.perform(get("/user/" + standardUser.getId() + "/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
