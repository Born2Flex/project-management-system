package edu.ukma.projectmanagementsystem.service.dto.security;

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
}
