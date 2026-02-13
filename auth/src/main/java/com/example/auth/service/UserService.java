package com.example.auth.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import com.example.auth.dto.AdminDTO;

import com.example.auth.dto.ManagerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.entity.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.util.EmailUtil;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailUtil emailUtil;

    private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);

    // 1. REGISTER
    public String register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);
        return "User registered successfully";
    }

    // 2. LOGIN (SAFE VERSION)
    public User login(LoginRequest request) {
        System.out.println("Attempting login for: " + request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Handle if Password is null in DB
        String dbPass = user.getPassword();
        if (dbPass == null || !encoder.matches(request.getPassword(), dbPass)) {
            throw new RuntimeException("Invalid credentials");
        }

        // Handle if Role is null in DB
        String dbRole = user.getRole();
        if (dbRole == null) {
            dbRole = "manager"; 
        }

        if (!dbRole.equalsIgnoreCase(request.getRole())) {
            throw new RuntimeException("Access Denied: You are not authorized as " + request.getRole());
        }

        return user;
    }

    // 3. FORGOT PASSWORD (UPDATED: CONSOLE ONLY)
    public String forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        // Generate 6-digit OTP
        String otp = String.valueOf(new Random().nextInt(999999 - 100000) + 100000);
        
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);

        // --- PRINT OTP TO CONSOLE (Workaround for Firewall Block) ---
        System.out.println("\n");
        System.out.println("********************************************");
        System.out.println("* OTP FOR " + email + " : " + otp + "  *");
        System.out.println("********************************************");
        System.out.println("\n");

        // ❌ COMMENTED OUT TO PREVENT HANGING/TIMEOUT
        // emailUtil.sendOtpEmail(email, otp); 

        return "OTP generated (Check Console)";
    }

    // 4. VERIFY OTP
    public String verifyOtp(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpGeneratedTime() != null && 
            Duration.between(user.getOtpGeneratedTime(), LocalDateTime.now()).getSeconds() > (5 * 60)) {
            throw new RuntimeException("OTP has expired. Please regenerate.");
        }

        user.setPassword(newPassword); 
        user.setOtp(null);
        userRepository.save(user);

        return "Password reset successfully";
    }
    public List<ManagerDTO> getManagerDetails(){
        List<User> user=userRepository.findAll();
        List<ManagerDTO> m=new ArrayList<>();
        for(User u:user)
        {
            if(u.getRole() != null && u.getRole().equals("manager"))
            {
                m.add(new ManagerDTO(u.getId(),u.getEmail(),u.getName()));
            }
        }
        return m;
    }
    public List<AdminDTO> getAdminDetails(){
        List<User> user=userRepository.findAll();
        List<AdminDTO> m=new ArrayList<>();
        for(User u:user)
        {
            if(u.getRole() != null && u.getRole().equals("admin"))
            {
                m.add(new AdminDTO(u.getId(),u.getEmail(),u.getName()));
            }
        }
        return m;
    }
}