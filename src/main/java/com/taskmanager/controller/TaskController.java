package com.taskmanager.controller;

import com.taskmanager.dto.MessageResponse;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.model.Project;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public List<Task> getAllTasks() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return taskRepository.findAll();
        } else {
            User user = userRepository.findById(userDetails.getId()).get();
            return taskRepository.findByAssignedTo(user);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        Project project = projectRepository.findById(taskRequest.getProjectId())
                .orElseThrow(() -> new RuntimeException("Error: Project not found."));
        
        User assignedTo = userRepository.findById(taskRequest.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        Task task = new Task(taskRequest.getTitle(), taskRequest.getDescription(), 
                             taskRequest.getDueDate(), project, assignedTo);
        
        taskRepository.save(task);
        return ResponseEntity.ok(new MessageResponse("Task created successfully!"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest taskRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Task not found."));
        
        Project project = projectRepository.findById(taskRequest.getProjectId())
                .orElseThrow(() -> new RuntimeException("Error: Project not found."));
        
        User assignedTo = userRepository.findById(taskRequest.getAssignedToId())
                .orElseThrow(() -> new RuntimeException("Error: User not found."));

        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setDueDate(taskRequest.getDueDate());
        task.setProject(project);
        task.setAssignedTo(assignedTo);
        
        taskRepository.save(task);
        return ResponseEntity.ok(new MessageResponse("Task updated successfully!"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Task deleted successfully!"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> statusRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Error: Task not found."));
        
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!task.getAssignedTo().getId().equals(userDetails.getId()) && 
            userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(403).body(new MessageResponse("Error: You can only update your own tasks!"));
        }

        task.setStatus(Task.TaskStatus.valueOf(statusRequest.get("status")));
        taskRepository.save(task);
        return ResponseEntity.ok(new MessageResponse("Task status updated successfully!"));
    }
}
