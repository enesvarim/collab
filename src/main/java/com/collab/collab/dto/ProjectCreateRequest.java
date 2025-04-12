package com.collab.collab.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectCreateRequest {
    
    @NotBlank(message = "Proje adı gereklidir")
    private String name;
    
    @NotBlank(message = "Proje konusu gereklidir")
    private String subject;
    
    private LocalDate deadline;
    
    private List<String> memberEmails; // Davet edilecek kullanıcıların email adresleri
}
