package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.domain.entity.RoleEntity;
import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import edu.ukma.projectmanagementsystem.domain.repository.RoleRepository;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.service.validators.UserValidator;
import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserRegistrationDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserUpdateDto;
import edu.ukma.projectmanagementsystem.service.mapper.UserMapper;
import edu.ukma.projectmanagementsystem.web.exception.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final UserValidator userValidator;

    @Override
    public UserDto createUser(UserRegistrationDto registrationDto) {
        log.info("Attempting to create new user");
        userValidator.validateForDuplicateEmail(-1L, registrationDto.getEmail());
        UserEntity userEntity = mapper.mapWithEncodedPassword(registrationDto, passwordEncoder);
        RoleEntity roleEntity = getRoleOrElseThrow(registrationDto.getRole());
        userEntity.setRole(roleEntity);
        UserDto createdUser = mapper.toDto(userRepository.save(userEntity));
        log.info("Created new user with id = {}", createdUser.getId());
        return createdUser;
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateDto updateDto) {
        log.info("Attempting to update user with ID: {}", id);
        userValidator.validateForDuplicateEmail(id, updateDto.getEmail());
        userValidator.validateForDuplicateUsername(id, updateDto.getUsername());
        UserEntity userEntity = getUserByIdOrElseThrow(id);
        UserEntity updatedEntity = mapper.updateEntity(userEntity, updateDto);
        UserDto updatedUser = mapper.toDto(updatedEntity);
        log.info("User with id: {} updated successfully", updatedUser.getId());
        return updatedUser;
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserDto> userDtos = mapper.toDto(userEntities);
        log.info("Retrieved {} users from the database", userDtos.size());
        return userDtos;
    }

    @Override
    public UserDto findUserById(Long id) {
        UserEntity userEntity = getUserByIdOrElseThrow(id);
        UserDto userDto = mapper.toDto(userEntity);
        log.info("Retrieved user with id: {}", id);
        return userDto;
    }

    @Override
    public UserDto findUserByEmail(String email) {
        UserEntity userEntity = getUserByEmailOrElseThrow(email);
        UserDto userDto = mapper.toDto(userEntity);
        log.info("Retrieved user by email: {}", email);
        return userDto;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Attempting to delete user with id: {}", id);
        getUserByIdOrElseThrow(id);
        userRepository.deleteById(id);
        log.info("User with id: {} deleted successfully", id);
    }

    @Override
    public List<UserDto> findUserByEmailOrUsername(String searchTerm) {
        log.info("Attempting to find users by email or username: {}", searchTerm);
        List<UserEntity> userEntities = userRepository.findUsersByEmailOrUsername(searchTerm);
        List<UserDto> userDtos = mapper.toDto(userEntities);
        log.info("Retrieved {} users from the database", userDtos.size());
        return userDtos;
    }

    private RoleEntity getRoleOrElseThrow(UserRole role) {
        return roleRepository.findByName(role.name()).orElseThrow(() -> new NoSuchEntityException("Role not found"));
    }

    private UserEntity getUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchEntityException("User not found"));
    }

    private UserEntity getUserByEmailOrElseThrow(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NoSuchEntityException("User not found"));
    }
}
