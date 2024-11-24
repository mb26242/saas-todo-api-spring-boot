package com.example.saas.todo.api.controller;

import com.example.saas.todo.api.model.Task;
import com.example.saas.todo.api.model.User;

import com.example.saas.todo.api.repository.TaskRepository;
import com.example.saas.todo.api.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/user/{userId}/tasks")
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }




    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@PathVariable String userId) {

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 for missing user
        }

        User user = userOpt.get();

        List<Task> tasks = taskRepository.findTasksForUser(user);

        return ResponseEntity.ok(tasks);
    }


    @PostMapping
    public ResponseEntity<Task> createTask(
            @PathVariable String userId, 
            @RequestBody Task taskRequest) {

        // Check if the user exists
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 for missing user
        }
    
        User user = userOpt.get();
    
        Task createdTask = taskRepository.createTaskForUser(user, taskRequest.getTitle(), taskRequest.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask); // 201 Created
    }


    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable String userId, @PathVariable String taskId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        User user = userOpt.get();

        List<Task> userTasks = taskRepository.findTasksForUser(user);

        Optional<Task> taskOpt = userTasks.stream()
                                      .filter(task -> task.getId().equals(taskId))
                                      .findFirst();
       
        if (taskOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 for missing task
        }

        Task task = taskOpt.get();

        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable String userId, 
            @PathVariable String taskId, 
            @RequestBody Task taskRequest) {

        // Check if the user exists
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 for missing user
        }

        User user = userOpt.get();
    
        // Check if the task exists and belongs to the user
        Optional<Task> taskOpt = taskRepository.findTasksForUser(user).stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst();

        if (taskOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 for missing task
        }

        String fetchedTaskId = taskOpt.get().getId();

        Task updatedTask = taskRepository.updateTask(fetchedTaskId, taskRequest.getTitle(), taskRequest.getDescription());
        return ResponseEntity.ok(updatedTask);


    }


    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable String userId, 
            @PathVariable String taskId) {

        // Check if the user exists
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 for missing user
        }
    
        User user = userOpt.get();
    
        // Check if the task exists and belongs to the user
        Optional<Task> taskOpt = taskRepository.findTasksForUser(user).stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst();
    
        if (taskOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 for missing task
        }

        String fetchedTaskId = taskOpt.get().getId();
    
        // Delete the task
        taskRepository.deleteById(fetchedTaskId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
    

}

