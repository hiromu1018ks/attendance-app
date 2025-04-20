package com.example.attendanceapp;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class AttendanceApplication {

    public AttendanceApplication(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
    }

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void printPasswordHash() {
        String raw = "password";
        String hash = passwordEncoder.encode(raw);
        System.out.println("🔐 正確なハッシュ値: " + hash);
    }
}
