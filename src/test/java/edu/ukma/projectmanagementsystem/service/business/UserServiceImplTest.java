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
import edu.ukma.projectmanagementsystem.service.validators.UserValidator;
import edu.ukma.projectmanagementsystem.web.exception.NoSuchEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserServiceImpl class.
 * Uses Mockito to mock dependencies and isolate the service logic.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper mapper;
    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDto registrationDto;
    private UserEntity userEntity;
    private UserDto userDto;
    private RoleEntity roleEntity;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("john.doe");
        registrationDto.setName("John Doe");
        registrationDto.setEmail("john.doe@example.com");
        registrationDto.setPassword("password123");
        registrationDto.setRole(UserRole.DEVELOPER);

        roleEntity = new RoleEntity(1L, UserRole.DEVELOPER.name());
        userEntity = new UserEntity(1L, "john.doe", "John Doe", "john.doe@example.com", "encodedPassword", roleEntity, Set.of());
        userDto = new UserDto(1L, "john.doe", "John Doe", "john.doe@example.com", UserRole.DEVELOPER);
    }

    @Nested
    @DisplayName("createUser tests")
    class CreateUserTests {

        @Test
        @DisplayName("Should create user successfully when data is valid")
        void createUser_whenValidData_shouldReturnCreatedUserDto() {
            doNothing().when(userValidator).validateForDuplicateEmail(anyLong(), anyString());
            when(roleRepository.findByName(UserRole.DEVELOPER.name())).thenReturn(Optional.of(roleEntity));
            when(mapper.mapWithEncodedPassword(any(UserRegistrationDto.class), any(PasswordEncoder.class))).thenReturn(userEntity);
            when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
            when(mapper.toDto(any(UserEntity.class))).thenReturn(userDto);

            UserDto result = userService.createUser(registrationDto);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(userDto.getId());
            assertThat(result.getEmail()).isEqualTo(userDto.getEmail());

            verify(userValidator, times(1)).validateForDuplicateEmail(-1L, registrationDto.getEmail());
            verify(roleRepository, times(1)).findByName(UserRole.DEVELOPER.name());
            verify(userRepository, times(1)).save(userEntity);
        }

        @Test
        @DisplayName("Should throw NoSuchEntityException when role does not exist")
        void createUser_whenRoleNotFound_shouldThrowNoSuchEntityException() {
            doNothing().when(userValidator).validateForDuplicateEmail(anyLong(), anyString());
            when(roleRepository.findByName(UserRole.DEVELOPER.name())).thenReturn(Optional.empty());

            assertThrows(NoSuchEntityException.class, () -> userService.createUser(registrationDto));
            verify(userRepository, never()).save(any(UserEntity.class));
        }
    }

    @Nested
    @DisplayName("updateUser tests")
    class UpdateUserTests {
        @Test
        @DisplayName("Should update user successfully when user exists")
        void updateUser_whenUserExists_shouldReturnUpdatedDto() {
            Long userId = 1L;
            UserUpdateDto updateDto = new UserUpdateDto();
            updateDto.setUsername("jane.doe");
            updateDto.setName("Jane Doe");
            updateDto.setEmail("jane.doe@example.com");
            updateDto.setRole(UserRole.PROJECT_MANAGER);

            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            when(mapper.updateEntity(any(UserEntity.class), any(UserUpdateDto.class))).thenReturn(userEntity);
            when(mapper.toDto(any(UserEntity.class))).thenReturn(userDto);

            UserDto result = userService.updateUser(userId, updateDto);

            assertThat(result).isNotNull();
            verify(userValidator, times(1)).validateForDuplicateEmail(userId, updateDto.getEmail());
            verify(userValidator, times(1)).validateForDuplicateUsername(userId, updateDto.getUsername());
            verify(mapper, times(1)).updateEntity(userEntity, updateDto);
        }

        @Test
        @DisplayName("Should throw NoSuchEntityException when user to update is not found")
        void updateUser_whenUserNotFound_shouldThrowNoSuchEntityException() {
            Long userId = 99L;
            UserUpdateDto updateDto = new UserUpdateDto();
            updateDto.setUsername("jane.doe");
            updateDto.setName("Jane Doe");
            updateDto.setEmail("jane.doe@example.com");
            updateDto.setRole(UserRole.PROJECT_MANAGER);

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(NoSuchEntityException.class, () -> userService.updateUser(userId, updateDto));
            verify(mapper, never()).updateEntity(any(), any());
        }
    }

    @Nested
    @DisplayName("findUser tests")
    class FindUserTests {

        @Test
        @DisplayName("findAllUsers should return a list of users")
        void findAllUsers_shouldReturnListOfUserDtos() {
            when(userRepository.findAll()).thenReturn(List.of(userEntity));
            when(mapper.toDto(List.of(userEntity))).thenReturn(List.of(userDto));

            List<UserDto> result = userService.findAllUsers();

            assertThat(result).isNotNull().hasSize(1);
            assertThat(result.getFirst().getEmail()).isEqualTo(userDto.getEmail());
        }

        @Test
        @DisplayName("findAllUsers should return empty list when no users exist")
        void findAllUsers_whenNoUsers_shouldReturnEmptyList() {
            when(userRepository.findAll()).thenReturn(Collections.emptyList());
            when(mapper.toDto(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<UserDto> result = userService.findAllUsers();

            assertThat(result).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("findUserById should return user when found")
        void findUserById_whenUserExists_shouldReturnUserDto() {
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            when(mapper.toDto(userEntity)).thenReturn(userDto);

            UserDto result = userService.findUserById(userId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(userId);
        }

        @Test
        @DisplayName("findUserById should throw exception when not found")
        void findUserById_whenUserNotExists_shouldThrowException() {
            Long userId = 99L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(NoSuchEntityException.class, () -> userService.findUserById(userId));
        }
    }

    @Nested
    @DisplayName("deleteUser tests")
    class DeleteUserTests {

        @Test
        @DisplayName("Should delete user when user exists")
        void deleteUser_whenUserExists_shouldCompleteSuccessfully() {
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            doNothing().when(userRepository).deleteById(userId);

            assertDoesNotThrow(() -> userService.deleteUser(userId));
            verify(userRepository, times(1)).deleteById(userId);
        }

        @Test
        @DisplayName("Should throw NoSuchEntityException when user to delete is not found")
        void deleteUser_whenUserNotFound_shouldThrowException() {
            Long userId = 99L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(NoSuchEntityException.class, () -> userService.deleteUser(userId));
            verify(userRepository, never()).deleteById(anyLong());
        }
    }
}

