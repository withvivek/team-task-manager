package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class ProjectRequest {
    @NotBlank
    private String name;
    private String description;
    private Set<Long> memberIds;
}
