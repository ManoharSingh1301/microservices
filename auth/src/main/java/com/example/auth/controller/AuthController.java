package com.example.auth.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping; // Imports POST, PUT, etc.
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.OtpVerificationRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.service.UserService;

@RestController
@RequestMapping("/auth")

public class AuthController {

    @Autowired
    private UserService userService;

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
            
            // Return simple data (No full User object to avoid crashes)
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login Successful");
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
    public ResponseEntity<?> getManagerDetails() {
        return ResponseEntity.ok("Manager Details Retrieved Successfully");
    }
    @GetMapping("/getadmindetails")
    public ResponseEntity<?> getAdminDetails() {
        return ResponseEntity.ok("Admin Details Retrieved Successfully");
    }
    @GetMapping("/details?role={role}")
    public ResponseEntity<?> getDetailsByRole(@RequestBody Map<String, String> payload) {
        String role = payload.get("role");
        if ("manager".equalsIgnoreCase(role)) {
            return ResponseEntity.ok("Manager Details Retrieved Successfully");
        } else if ("admin".equalsIgnoreCase(role)) {
            return ResponseEntity.ok("Admin Details Retrieved Successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid Role");
        }
    }
}