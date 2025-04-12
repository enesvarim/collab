package com.collab.collab.service;

import com.collab.collab.dto.AuthResponse;
import com.collab.collab.dto.LoginRequest;
import com.collab.collab.dto.RegisterRequest;
import com.collab.collab.entity.User;
import com.collab.collab.repository.UserRepository;
import com.collab.collab.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Bu e-mail adresi ile zaten bir kaydınız bulunmaktadır.")
                    .build();
        }
        
        // Create new user
        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        
        userRepository.save(user);
        
        // Generate JWT token
        String token = jwtUtils.generateJwtToken(user.getEmail());
        
        return AuthResponse.builder()
                .success(true)
                .message("Registration successful")
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .token(token)
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            
            // User authenticated successfully, fetch user details
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate JWT token
            String token = jwtUtils.generateJwtToken(request.getEmail());
            
            return AuthResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .token(token)
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("Invalid email or password")
                    .build();
        }
    }
}
