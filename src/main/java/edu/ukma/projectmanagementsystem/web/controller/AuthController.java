package edu.ukma.projectmanagementsystem.web.controller;

import edu.ukma.projectmanagementsystem.service.business.UserService;
import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserRegistrationDto;
import edu.ukma.projectmanagementsystem.service.security.AuthService;
import edu.ukma.projectmanagementsystem.service.security.dto.JwtResponseDto;
import edu.ukma.projectmanagementsystem.service.security.dto.LoginDto;
import edu.ukma.projectmanagementsystem.service.security.dto.RefreshTokenRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Auth management endpoints")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@RequestBody UserRegistrationDto registrationDto) {
        return userService.createUser(registrationDto);
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Logic user using username and password")
    @ApiResponse(responseCode = "200",
            content = {@Content(schema = @Schema(implementation = JwtResponseDto.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "401",
            content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")})
    public JwtResponseDto authorize(@RequestBody LoginDto dto) {
        return authService.authenticate(dto);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh authentication token using refresh token.")
    @ApiResponse(responseCode = "200",
            content = {@Content(schema = @Schema(implementation = JwtResponseDto.class), mediaType = "application/json")})
    @ApiResponse(responseCode = "403",
            content = {@Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")})
    public JwtResponseDto refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }
}
