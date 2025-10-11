package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.service.dto.UserDto;
import edu.ukma.projectmanagementsystem.service.dto.UserRegistrationDto;

public interface UserService {
    UserDto registerUser(UserRegistrationDto registrationDto);
}
