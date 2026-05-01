package com.taskmanager.controller;

import com.taskmanager.dto.MessageResponse;
import com.taskmanager.dto.ProjectRequest;
import com.taskmanager.model.Project;
import com.taskmanager.model.User;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectRequest projectRequest) {
        Project project = new Project(projectRequest.getName(), projectRequest.getDescription());
        
        if (projectRequest.getMemberIds() != null) {
            Set<User> members = new HashSet<>(userRepository.findAllById(projectRequest.getMemberIds()));
            project.setMembers(members);
        }
        
        projectRepository.save(project);
        return ResponseEntity.ok(new MessageResponse("Project created successfully!"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Project not found."));
        return ResponseEntity.ok(project);
    }
}
