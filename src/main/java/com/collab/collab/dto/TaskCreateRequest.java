package com.collab.collab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreateRequest {
    
    @NotBlank(message = "Görev başlığı gereklidir")
    private String title;
    
    private String description;
    
    @NotNull(message = "Proje ID'si gereklidir")
    private Long projectId;
    
    @NotNull(message = "Atanacak kullanıcı ID'si gereklidir")
    private Long assignedToId;
    
    private LocalDate deadline;
}
