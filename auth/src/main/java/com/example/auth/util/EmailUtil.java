package com.example.auth.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {

    // @Autowired
    // private JavaMailSender javaMailSender;

    public void sendOtpEmail(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("PetroManage - Password Reset OTP");
        message.setText("Hello,\n\nYour verification code is: " + otp + "\n\nThis code is valid for 5 minutes.");
        // javaMailSender.send(message);

    }
}