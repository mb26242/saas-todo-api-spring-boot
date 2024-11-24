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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ConcurrencyTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private TaskRepository taskRepository;


    @Autowired
    private DataUtils dataUtils;

    private User user; 
    private Company company; 

    @BeforeEach
    void setup() {
        dataUtils.emptyRepositories();

        company = companyRepository.createCompany("TestCompany");
        user = userRepository.createUserWithCompany("testUser", company, Role.STANDARD)
        ;
    }

    @AfterEach
    void cleanup() {
        dataUtils.emptyRepositories();
    }


    @Test
    void testConcurrentTaskCreation() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int index = i;
            executor.execute(() -> {
                try {
                    taskRepository.createTaskForUser(
                        user,
                        "Task " + index,
                        "Description " + index
                    );
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // Wait for all threads to finish
        executor.shutdown();

        long count = taskRepository.count();
        assertEquals(threadCount, count, "All tasks should be created without issues.");
    }

    @Test
    void testConcurrentTaskUpdates() throws InterruptedException {
        Task task = taskRepository.createTaskForUser(user, "Initial Task", "Initial Description"); 

        int threadCount = 5;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            int index = i;
            executor.execute(() -> {
                try {
                    taskRepository.updateTask(
                        task.getId(),
                        "Updated Title " + index,
                        task.getDescription()
                    );
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();

        String expectedTitle = "Updated Title " + (threadCount - 1);
        assertEquals(expectedTitle, updatedTask.getTitle(), "The final title should be the last one set.");
    }
}
