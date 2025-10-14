package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.domain.entity.RoleEntity;
import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import edu.ukma.projectmanagementsystem.domain.repository.RoleRepository;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserRegistrationDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserUpdateDto;
import edu.ukma.projectmanagementsystem.service.mapper.UserMapper;
import edu.ukma.projectmanagementsystem.web.exception.EmailDuplicateException;
import edu.ukma.projectmanagementsystem.web.exception.NoSuchEntityException;
import edu.ukma.projectmanagementsystem.web.exception.UsernameDuplicateException;
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

    @Override
    public UserDto createUser(UserRegistrationDto registrationDto) {
        log.info("Attempting to create new user");
        validateForDuplicateEmail(-1L, registrationDto.getEmail());
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
        validateForDuplicateEmail(id, updateDto.getEmail());
        validateForDuplicateUsername(id, updateDto.getEmail());
        UserEntity userEntity = getUserByIdOrElseThrow(id);
        UserEntity updatedEntity = mapper.updateEntity(userEntity, updateDto);
        UserDto updatedUser = mapper.toDto(updatedEntity);
        log.info("User with id: {} updated successfully", updatedUser.getId());
        return updatedUser;
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
        log.info("Retrieved {} users from the database", users.size());
        return users;
    }

    @Override
    public UserDto findUserById(Long id) {
        UserDto user = mapper.toDto(getUserByIdOrElseThrow(id));
        log.info("Retrieved user with id: {}", id);
        return user;
    }

    @Override
    public UserDto findUserByEmail(String email) {
        UserDto user = mapper.toDto(userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchEntityException("User not found")));
        log.info("Retrieved user by email: {}", email);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Attempting to delete user with id: {}", id);
        userRepository.deleteById(id);
        log.info("User with id: {} deleted successfully", id);
    }

    private RoleEntity getRoleOrElseThrow(UserRole role) {
        return roleRepository.findByName(role.name())
                .orElseThrow(() -> new NoSuchEntityException("Role not found"));
    }

    private void validateForDuplicateEmail(Long id, String email) {
        userRepository.findByEmail(email)
                .filter(user -> !user.getId().equals(id))
                .ifPresent(this::throwEmailDuplicateException);
    }


    private void validateForDuplicateUsername(Long id, String email) {
        userRepository.findByUsername(email)
                .filter(user -> !user.getId().equals(id))
                .ifPresent(this::throwUsernameDuplicateException);
    }

    private void throwEmailDuplicateException(UserEntity user) {
        throw new EmailDuplicateException();
    }

    private void throwUsernameDuplicateException(UserEntity user) {
        throw new UsernameDuplicateException();
    }

    private UserEntity getUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("User not found"));
    }
}
