package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.domain.entity.RoleEntity;
import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import edu.ukma.projectmanagementsystem.domain.repository.RoleRepository;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.service.dto.UserDto;
import edu.ukma.projectmanagementsystem.service.dto.UserRegistrationDto;
import edu.ukma.projectmanagementsystem.service.mapper.UserMapper;
import edu.ukma.projectmanagementsystem.web.exception.EmailDuplicateException;
import edu.ukma.projectmanagementsystem.web.exception.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public UserDto createUser(UserRegistrationDto registrationDto) {
        log.info("Attempting to create new user");
        validateForDuplicateEmail(registrationDto.getEmail());
        UserEntity userEntity = mapper.mapWithEncodedPassword(registrationDto, passwordEncoder);
        RoleEntity roleEntity = getRoleOrElseThrow(registrationDto.getRole());
        userEntity.setRole(roleEntity);
        UserDto createdUser = mapper.toDto(userRepository.save(userEntity));
        log.info("Created new user with id = {}", createdUser.getId());
        return createdUser;
    }

    private RoleEntity getRoleOrElseThrow(UserRole role) {
        return roleRepository.findByName(role.name())
                .orElseThrow(() -> new NoSuchEntityException("Role not found"));
    }

    private void validateForDuplicateEmail(String email) {
        userRepository.findByEmail(email).ifPresent(this::throwEmailDuplicateException);
    }

    private void throwEmailDuplicateException(UserEntity user) {
        throw new EmailDuplicateException();
    }

    private UserEntity getUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("User not found"));
    }
}
