package com.example.attendanceapp.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String employeeNumber;
    private String password;
}
