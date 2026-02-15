package com.example.auth.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.auth.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Imports POST, PUT, etc.
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.entity.User;
import com.example.auth.service.UserService;
import com.example.auth.util.JwtUtil;

@RestController
@RequestMapping("/auth")

public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(userService.register(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            
            // Generate JWT token
            String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getName()
            );
            
            // Return token and user data
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login Successful");
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("name", user.getName());
            response.put("role", user.getRole());
            response.put("email", user.getEmail());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        try {
            return ResponseEntity.ok(userService.forgotPassword(payload.get("email")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody OtpVerificationRequest request) {
        try {
            return ResponseEntity.ok(userService.verifyOtp(request.getEmail(), request.getOtp(), request.getNewPassword()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/getmanagerdetails")
    public List<ManagerDTO> getManagerDetails() {
        return userService.getManagerDetails();
    }
    @GetMapping("/getadmindetails")
    public List<AdminDTO> getAdminDetails() {
        return userService.getAdminDetails();
    }
    
    @GetMapping("/details")
    public ResponseEntity<?> getDetailsByRole(@RequestParam String role) {
        System.out.println("Fetching details for role: " + role);
        
        if (role == null || role.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Role parameter is required");
        }
        
        if (role.equalsIgnoreCase("manager")) {
            return ResponseEntity.ok(getManagerDetails());
        } else if (role.equalsIgnoreCase("admin")) {
            return ResponseEntity.ok(getAdminDetails());
        } else {
            return ResponseEntity.badRequest().body("Invalid role. Use 'manager' or 'admin'");
        }
    }
}