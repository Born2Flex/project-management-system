package edu.ukma.projectmanagementsystem.service.security;

import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.service.dto.security.LoginDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernamePasswordAuthenticatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsernamePasswordAuthenticator authenticator;

    private LoginDto loginDto;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto("test@example.com", "password123");

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");
    }

    @Nested
    @DisplayName("authenticateUser tests")
    class AuthenticateUserTests {

        @Test
        @DisplayName("Should authenticate successfully when credentials are valid")
        void authenticateUser_whenCredentialsAreValid_shouldNotThrowException() {
            when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(userEntity));
            when(passwordEncoder.matches(loginDto.getPassword(), userEntity.getPassword())).thenReturn(true);

            assertDoesNotThrow(() -> authenticator.authenticateUser(loginDto));
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when email is not found")
        void authenticateUser_whenEmailNotFound_shouldThrowBadCredentialsException() {
            when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

            assertThrows(BadCredentialsException.class, () -> authenticator.authenticateUser(loginDto));
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when password does not match")
        void authenticateUser_whenPasswordIsIncorrect_shouldThrowBadCredentialsException() {
            when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(userEntity));
            when(passwordEncoder.matches(loginDto.getPassword(), userEntity.getPassword())).thenReturn(false);

            assertThrows(BadCredentialsException.class, () -> authenticator.authenticateUser(loginDto));
        }
    }
}
