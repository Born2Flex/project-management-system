package edu.ukma.projectmanagementsystem.service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ukma.projectmanagementsystem.config.TokenData;
import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import edu.ukma.projectmanagementsystem.web.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String secret = "YS12ZXJ5LWxvbmctYW5kLXN1cGVyLXNlY3JldC1rZXktZm9yLXRlc3RpbmctcHVycG9zZXM=";
    private TokenData tokenData;

    @BeforeEach
    void setUp() {
        long expirationTimeInMinutes = 60;
        jwtService = new JwtService(secret, expirationTimeInMinutes, objectMapper);
        tokenData = new TokenData(1L, UserRole.DEVELOPER);
    }

    @Nested
    @DisplayName("Token Generation and Parsing")
    class GenerationAndParsingTests {
        @Test
        @DisplayName("Should correctly extract claims from a valid token")
        void getClaims_whenTokenIsValid_shouldReturnClaims() {
            String token = jwtService.generateToken(tokenData);
            Claims claims = jwtService.getClaims(token);
            assertThat(claims.get("id", Long.class)).isEqualTo(tokenData.getId());
            assertThat(claims.get("role", String.class)).isEqualTo(tokenData.getRole().name());
        }
    }

    @Nested
    @DisplayName("Token Expiration Logic")
    class ExpirationTests {

        @Test
        @DisplayName("isExpired should return false for a valid, non-expired token")
        void isExpired_whenTokenIsNotExpired_shouldReturnFalse() {
            String token = jwtService.generateToken(tokenData);
            assertThat(jwtService.isExpired(token)).isFalse();
        }

        @Test
        @DisplayName("isExpired should return true for an expired token")
        void isExpired_whenTokenIsExpired_shouldReturnTrue() throws InterruptedException {
            JwtService expiredService = new JwtService(secret, 0, objectMapper);
            String expiredToken = expiredService.generateToken(tokenData);
            Thread.sleep(10);
            assertTrue(expiredService.isExpired(expiredToken));
        }

        @Test
        @DisplayName("getClaims should throw ExpiredJwtException for an expired token")
        void getClaims_whenTokenIsExpired_shouldThrowException() throws InterruptedException {
            JwtService expiredService = new JwtService(secret, 0, objectMapper);
            String expiredToken = expiredService.generateToken(tokenData);
            Thread.sleep(10);
            assertThrows(ExpiredJwtException.class, () -> expiredService.getClaims(expiredToken));
        }
    }

    @Nested
    @DisplayName("Invalid Token Handling")
    class InvalidTokenTests {

        @Test
        @DisplayName("parseToken should throw JwtException for a malformed token")
        void parseToken_whenTokenIsMalformed_shouldThrowException() {
            String malformedToken = "invalid.token.string";
            assertThrows(JwtException.class, () -> jwtService.parseToken(malformedToken));
        }

        @Test
        @DisplayName("isExpired should throw InvalidTokenException for a malformed token")
        void isExpired_whenTokenIsMalformed_shouldThrowException() {
            String malformedToken = "invalid.token.string";
            assertThrows(InvalidTokenException.class, () -> jwtService.isExpired(malformedToken));
        }

        @Test
        @DisplayName("getClaims should throw JwtException for a token with an invalid signature")
        void getClaims_whenSignatureIsInvalid_shouldThrowException() {
            String token = jwtService.generateToken(tokenData);
            String tamperedToken = token.substring(0, token.length() - 1) + "X";
            assertThrows(JwtException.class, () -> jwtService.getClaims(tamperedToken));
        }
    }
}
