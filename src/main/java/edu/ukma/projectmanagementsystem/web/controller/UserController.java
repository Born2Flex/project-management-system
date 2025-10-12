package edu.ukma.projectmanagementsystem.web.controller;

import edu.ukma.projectmanagementsystem.service.business.UserService;
import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserUpdateDto;
import edu.ukma.projectmanagementsystem.web.handler.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PutMapping("/{id}")
    @Operation(summary = "Update user data")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json")})
    @ApiResponse(responseCode = "400", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    public UserDto updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateDto dto) {
        return service.updateUser(id, dto);
    }

    @GetMapping
    @Operation(summary = "Get information about all users")
    @ApiResponse(responseCode = "200", content = {@Content(array = @ArraySchema(
            schema = @Schema(implementation = UserDto.class)), mediaType = "application/json")})
    public List<UserDto> getAllUsers() {
        return service.findAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get information about user by id")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json")})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    public UserDto getUserById(@PathVariable Long id) {
        return service.findUserById(id);
    }

    @GetMapping("/by-email")
    @Operation(summary = "Get information about user by email")
    @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserDto.class),
            mediaType = "application/json")})
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    public UserDto getUserByEmail(@RequestParam String email) {
        return service.findUserByEmail(email);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by id")
    @ApiResponse(responseCode = "204")
    @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
    }
}
