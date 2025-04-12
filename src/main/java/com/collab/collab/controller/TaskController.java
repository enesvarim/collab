package com.collab.collab.controller;

import com.collab.collab.dto.TaskCreateRequest;
import com.collab.collab.dto.TaskResponse;
import com.collab.collab.entity.Task;
import com.collab.collab.service.TaskService;
import com.collab.collab.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        TaskResponse task = taskService.createTask(request, email);
        return ResponseEntity.ok(task);
    }
    
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskResponse>> getProjectTasks(@PathVariable Long projectId) {
        String email = SecurityUtils.getCurrentUserEmail();
        List<TaskResponse> tasks = taskService.getProjectTasks(projectId, email);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/user")
    public ResponseEntity<List<TaskResponse>> getUserTasks() {
        String email = SecurityUtils.getCurrentUserEmail();
        List<TaskResponse> tasks = taskService.getUserTasks(email);
        return ResponseEntity.ok(tasks);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        String email = SecurityUtils.getCurrentUserEmail();
        Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
        TaskResponse task = taskService.updateTaskStatus(id, taskStatus, email);
        return ResponseEntity.ok(task);
    }
}
