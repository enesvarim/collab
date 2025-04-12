package com.collab.collab.dto;

import com.collab.collab.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    private Long projectId;
    private String projectName;
    private ProjectResponse.UserDto assignedTo;
    private ProjectResponse.UserDto createdBy;
    
    public static TaskResponse fromTask(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .deadline(task.getDeadline())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .completedAt(task.getCompletedAt())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .assignedTo(ProjectResponse.UserDto.fromUser(task.getAssignedTo()))
                .createdBy(ProjectResponse.UserDto.fromUser(task.getCreatedBy()))
                .build();
    }
}
