package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserRegistrationDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserRegistrationDto registrationDto);

    UserDto updateUser(Long id, UserUpdateDto userDto);

    List<UserDto> findAllUsers();

    UserDto findUserById(Long id);

    UserDto findUserByEmail(String email);

    void deleteUser(Long id);

}
