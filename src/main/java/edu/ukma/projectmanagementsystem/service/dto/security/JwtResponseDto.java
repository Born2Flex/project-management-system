package edu.ukma.projectmanagementsystem.service.dto.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDto {
    private Long userId;
    private String accessToken;
    private String refreshToken;
}