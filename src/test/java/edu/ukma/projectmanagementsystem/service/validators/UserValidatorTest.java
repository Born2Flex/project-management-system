package edu.ukma.projectmanagementsystem.service.validators;

import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.web.exception.EmailDuplicateException;
import edu.ukma.projectmanagementsystem.web.exception.UsernameDuplicateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator userValidator;

    private UserEntity existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new UserEntity();
        existingUser.setId(2L);
        existingUser.setEmail("test@example.com");
        existingUser.setUsername("testuser");
    }

    @Nested
    @DisplayName("validateForDuplicateEmail tests")
    class ValidateForDuplicateEmailTests {

        @Test
        @DisplayName("Should not throw exception when email is not found")
        void validateForDuplicateEmail_whenEmailNotFound_shouldNotThrow() {
            when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
            assertDoesNotThrow(() -> userValidator.validateForDuplicateEmail(1L, "new@example.com"));
        }

        @Test
        @DisplayName("Should not throw exception when email belongs to the same user")
        void validateForDuplicateEmail_whenEmailBelongsToSameUser_shouldNotThrow() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
            assertDoesNotThrow(() -> userValidator.validateForDuplicateEmail(2L, "test@example.com"));
        }

        @Test
        @DisplayName("Should throw EmailDuplicateException when email belongs to another user")
        void validateForDuplicateEmail_whenEmailBelongsToAnotherUser_shouldThrowException() {
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));
            assertThrows(EmailDuplicateException.class, () -> userValidator.validateForDuplicateEmail(1L, "test@example.com"));
        }
    }

    @Nested
    @DisplayName("validateForDuplicateUsername tests")
    class ValidateForDuplicateUsernameTests {

        @Test
        @DisplayName("Should not throw exception when username is not found")
        void validateForDuplicateUsername_whenUsernameNotFound_shouldNotThrow() {
            when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
            assertDoesNotThrow(() -> userValidator.validateForDuplicateUsername(1L, "newuser"));
        }

        @Test
        @DisplayName("Should not throw exception when username belongs to the same user")
        void validateForDuplicateUsername_whenUsernameBelongsToSameUser_shouldNotThrow() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
            assertDoesNotThrow(() -> userValidator.validateForDuplicateUsername(2L, "testuser"));
        }

        @Test
        @DisplayName("Should throw UsernameDuplicateException when username belongs to another user")
        void validateForDuplicateUsername_whenUsernameBelongsToAnotherUser_shouldThrowException() {
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));
            assertThrows(UsernameDuplicateException.class, () -> userValidator.validateForDuplicateUsername(1L, "testuser"));
        }
    }
}
