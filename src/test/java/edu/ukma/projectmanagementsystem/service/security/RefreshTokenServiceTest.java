package edu.ukma.projectmanagementsystem.service.security;

import edu.ukma.projectmanagementsystem.domain.entity.RefreshTokenEntity;
import edu.ukma.projectmanagementsystem.domain.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpirationDays", 30L);
    }

    @Nested
    @DisplayName("createRefreshToken tests")
    class CreateRefreshTokenTests {

        @Test
        @DisplayName("Should delete old tokens and create a new one")
        void createRefreshToken_shouldCreateNewToken() {
            Long userId = 1L;
            RefreshTokenEntity refreshToken = new RefreshTokenEntity(userId, "some-token", LocalDateTime.now().plusDays(30));
            when(refreshTokenRepository.save(any(RefreshTokenEntity.class))).thenReturn(refreshToken);

            RefreshTokenEntity result = refreshTokenService.createRefreshToken(userId);

            verify(refreshTokenRepository, times(1)).deleteByUserId(userId);
            ArgumentCaptor<RefreshTokenEntity> captor = ArgumentCaptor.forClass(RefreshTokenEntity.class);
            verify(refreshTokenRepository, times(1)).save(captor.capture());

            RefreshTokenEntity savedEntity = captor.getValue();
            assertThat(savedEntity.getUserId()).isEqualTo(userId);
            assertThat(savedEntity.getToken()).isNotNull();
            assertThat(savedEntity.getExpirationDate()).isAfter(LocalDateTime.now().plusDays(29));

            assertThat(result).isEqualTo(refreshToken);
        }
    }

    @Nested
    @DisplayName("verifyExpiration tests")
    class VerifyExpirationTests {

        @Test
        @DisplayName("Should return token if not expired")
        void verifyExpiration_whenNotExpired_shouldReturnToken() {
            RefreshTokenEntity token = new RefreshTokenEntity(1L, "token", LocalDateTime.now().plusDays(1));
            RefreshTokenEntity result = refreshTokenService.verifyExpiration(token);
            assertThat(result).isEqualTo(token);
            verify(refreshTokenRepository, never()).delete(any());
        }

        @Test
        @DisplayName("Should throw exception and delete token if expired")
        void verifyExpiration_whenExpired_shouldThrowExceptionAndDeleteToken() {
            RefreshTokenEntity token = new RefreshTokenEntity(1L, "token", LocalDateTime.now().minusDays(1));
            assertThrows(ResponseStatusException.class, () -> refreshTokenService.verifyExpiration(token));
            verify(refreshTokenRepository, times(1)).delete(token);
        }
    }

    @Nested
    @DisplayName("findByToken tests")
    class FindByTokenTests {

        @Test
        @DisplayName("Should return Optional of token when found")
        void findByToken_whenFound_shouldReturnOptionalOfToken() {
            String tokenValue = "test-token";
            RefreshTokenEntity token = new RefreshTokenEntity(1L, tokenValue, LocalDateTime.now());
            when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.of(token));
            Optional<RefreshTokenEntity> result = refreshTokenService.findByToken(tokenValue);
            assertThat(result).isPresent().contains(token);
        }

        @Test
        @DisplayName("Should return empty Optional when not found")
        void findByToken_whenNotFound_shouldReturnEmptyOptional() {
            String tokenValue = "non-existent-token";
            when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(Optional.empty());
            Optional<RefreshTokenEntity> result = refreshTokenService.findByToken(tokenValue);
            assertThat(result).isNotPresent();
        }
    }

    @Nested
    @DisplayName("deleteByUserId tests")
    class DeleteByUserIdTests {

        @Test
        @DisplayName("Should call repository to delete by user ID")
        void deleteByUserId_shouldCallRepository() {
            Long userId = 1L;
            refreshTokenService.deleteByUserId(userId);
            verify(refreshTokenRepository).deleteByUserId(userId);
        }
    }
}
