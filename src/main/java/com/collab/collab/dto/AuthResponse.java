package com.collab.collab.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private boolean success;
    private String firstName;
    private String lastName;
    private String email;
    private String token; // JWT token eklenecek
}
