package com.example.saas.todo.api.repository;

import com.example.saas.todo.api.model.Task;
import com.example.saas.todo.api.model.User;
import com.example.saas.todo.api.exception.TaskNotFoundException;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class TaskRepository {
    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    private Task save(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Task createTaskForUser(User user, String title, String description) {
        Task task = new Task(title, description, user.getId(), user.getCompanyId());
        return save(task);
    }

    public Task updateTask(String taskId, String newTitle, String newDescription) {
        Optional<Task> taskOpt = findById(taskId);
        
        if (taskOpt.isEmpty()) {
            throw new TaskNotFoundException("Task with ID " + taskId + " not found");
        }
    
        Task task = taskOpt.get();
        task.setTitle(newTitle);
        task.setDescription(newDescription);
        return save(task);
    }

    public List<Task> findTasksForUser(User user) {

        switch (user.getRole()) {
            case STANDARD:

                // Standard users - their own tasks
                return tasks.values().stream()
                            .filter(task -> task.getUserId().equals(user.getId()))
                            .collect(Collectors.toList());

            case COMPANY_ADMIN:
                // Company admins - all tasks in their company
                return tasks.values().stream()
                            .filter(task -> task.getCompanyId().equals(user.getCompanyId()))
                            .collect(Collectors.toList());

            case SUPER_USER:
                // Super users - all tasks
                return List.copyOf(tasks.values());

            default:
                // For any unknown role, ret an empty list
                return List.of();
        }
    }

    public List<Task> findAll() {
        return List.copyOf(tasks.values());
    }

    public Optional<Task> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public void deleteById(String id) {
        tasks.remove(id);
    }

    public void deleteAll() {
        tasks.clear();
    }

    public long count() {
        return tasks.size();
    }

}


