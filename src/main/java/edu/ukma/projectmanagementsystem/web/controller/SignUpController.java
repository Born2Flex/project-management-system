package edu.ukma.projectmanagementsystem.web.controller;

import edu.ukma.projectmanagementsystem.service.business.UserService;
import edu.ukma.projectmanagementsystem.service.dto.UserDto;
import edu.ukma.projectmanagementsystem.service.dto.UserRegistrationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignUpController {
    private final UserService userService;

    @PostMapping("/sign-up")
    public UserDto registerUser(@RequestBody UserRegistrationDto registrationDto) {
        return userService.registerUser(registrationDto);
    }
}
