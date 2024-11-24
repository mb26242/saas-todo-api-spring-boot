package com.example.saas.todo.api;

import com.example.saas.todo.api.model.Company;
import com.example.saas.todo.api.model.Task;
import com.example.saas.todo.api.model.User;
import com.example.saas.todo.api.model.Role;
import com.example.saas.todo.api.repository.TaskRepository;
import com.example.saas.todo.api.repository.UserRepository;
import com.example.saas.todo.api.repository.CompanyRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UnitTests {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private DataUtils dataUtils;

    private Company company1;
    private Company company2;
    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        dataUtils.emptyRepositories();

        // Create test companies
        company1 = companyRepository.createCompany("Company1");
        company2 = companyRepository.createCompany("Company2");

        // Create test users
        user1 = userRepository.createUserWithCompany("User1", company1, Role.STANDARD);
        user2 = userRepository.createUserWithCompany("User2", company2, Role.STANDARD);
    }

    @AfterEach
    void cleanup() {
        dataUtils.emptyRepositories();
    }

    @Test
    void testSaveAndRetrieveTask() {
        // Create a task for user1
        Task task = taskRepository.createTaskForUser(user1, "Test Task", "Test Description");

        // Retrieve and validate the task
        Optional<Task> retrievedTask = taskRepository.findById(task.getId());
        assertTrue(retrievedTask.isPresent());
        assertEquals("Test Task", retrievedTask.get().getTitle());
        assertEquals("Test Description", retrievedTask.get().getDescription());
        assertEquals(user1.getId(), retrievedTask.get().getUserId());
        assertEquals(company1.getId(), retrievedTask.get().getCompanyId());
    }

    @Test
    void testSaveMultipleTasks() {
        // Create multiple tasks for different users
        taskRepository.createTaskForUser(user1, "Task 1", "Description 1");
        taskRepository.createTaskForUser(user2, "Task 2", "Description 2");

        // Retrieve and validate tasks
        List<Task> tasks = taskRepository.findAll();
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(task -> task.getTitle().equals("Task 1")));
        assertTrue(tasks.stream().anyMatch(task -> task.getTitle().equals("Task 2")));
    }

    @Test
    void testUpdateTask() {
        // Create a task for user1
        Task task = taskRepository.createTaskForUser(user1, "Original Task", "Original Description");


        taskRepository.updateTask(task.getId(), "Updated Task", "Updated Description");

        // Validate the updated task
        Optional<Task> retrievedTask = taskRepository.findById(task.getId());

        assertTrue(retrievedTask.isPresent());
        assertEquals("Updated Task", retrievedTask.get().getTitle());
        assertEquals("Updated Description", retrievedTask.get().getDescription());
    }

    @Test
    void testDeleteTask() {
        // Create a task for user1
        Task task = taskRepository.createTaskForUser(user1, "Task to be deleted", "Description");

        // Delete the task
        taskRepository.deleteById(task.getId());

        // Ensure the task is deleted
        Optional<Task> deletedTask = taskRepository.findById(task.getId());
        assertFalse(deletedTask.isPresent());
    }

    @Test
    void testCountTasks() {
        // Create multiple tasks for different users
        taskRepository.createTaskForUser(user1, "Task 1", "Description 1");
        taskRepository.createTaskForUser(user2, "Task 2", "Description 2");

        // Validate the task count
        long count = taskRepository.count();
        assertEquals(2, count);
    }
}
