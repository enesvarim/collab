package com.collab.collab.controller;

import com.collab.collab.dto.ProjectCreateRequest;
import com.collab.collab.dto.ProjectResponse;
import com.collab.collab.service.ProjectService;
import com.collab.collab.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        ProjectResponse project = projectService.createProject(request, email);
        return ResponseEntity.ok(project);
    }

    @PostMapping("/{projectId}/admins/{userId}")
    public ResponseEntity<ProjectResponse> addAdmin(@PathVariable Long projectId, @PathVariable Long userId) {
        String email = SecurityUtils.getCurrentUserEmail();
        ProjectResponse project = projectService.addAdmin(projectId, userId, email);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{projectId}/admins/{userId}")
    public ResponseEntity<ProjectResponse> removeAdmin(@PathVariable Long projectId, @PathVariable Long userId) {
        String email = SecurityUtils.getCurrentUserEmail();
        ProjectResponse project = projectService.removeAdmin(projectId, userId, email);
        return ResponseEntity.ok(project);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getUserProjects() {
        String email = SecurityUtils.getCurrentUserEmail();
        List<ProjectResponse> projects = projectService.getUserProjects(email);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id) {
        String email = SecurityUtils.getCurrentUserEmail();
        ProjectResponse project = projectService.getProjectById(id, email);
        return ResponseEntity.ok(project);
    }
}
