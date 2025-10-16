package edu.ukma.projectmanagementsystem.service.security;

import edu.ukma.projectmanagementsystem.config.AuthenticationFacade;
import edu.ukma.projectmanagementsystem.config.TokenData;
import edu.ukma.projectmanagementsystem.domain.entity.RefreshTokenEntity;
import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import edu.ukma.projectmanagementsystem.service.business.UserService;
import edu.ukma.projectmanagementsystem.service.dto.security.JwtResponseDto;
import edu.ukma.projectmanagementsystem.service.dto.security.LoginDto;
import edu.ukma.projectmanagementsystem.service.dto.security.RefreshTokenRequest;
import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UsernamePasswordAuthenticator usernamePasswordAuthenticator;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setRole(UserRole.DEVELOPER);
    }

    @Nested
    @DisplayName("authenticate tests")
    class AuthenticateTests {
        @Test
        @DisplayName("Should return JWT response on successful authentication")
        void authenticate_whenSuccessful_shouldReturnJwtResponse() {
            LoginDto loginDto = new LoginDto("test@example.com", "password");
            RefreshTokenEntity refreshToken = new RefreshTokenEntity(1L, "refresh-token", LocalDateTime.now());

            doNothing().when(usernamePasswordAuthenticator).authenticateUser(loginDto);
            when(userService.findUserByEmail(loginDto.getEmail())).thenReturn(userDto);
            when(jwtService.generateToken(any(TokenData.class))).thenReturn("jwt-token");
            when(refreshTokenService.createRefreshToken(userDto.getId())).thenReturn(refreshToken);

            JwtResponseDto response = authService.authenticate(loginDto);

            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(userDto.getId());
            assertThat(response.getAccessToken()).isEqualTo("jwt-token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
            verify(usernamePasswordAuthenticator).authenticateUser(loginDto);
        }

        @Test
        @DisplayName("Should throw BadCredentialsException when authentication fails")
        void authenticate_whenFails_shouldThrowBadCredentialsException() {
            LoginDto loginDto = new LoginDto("test@example.com", "wrong-password");
            doThrow(new BadCredentialsException("Invalid credentials")).when(usernamePasswordAuthenticator).authenticateUser(loginDto);

            assertThrows(BadCredentialsException.class, () -> authService.authenticate(loginDto));
            verify(userService, never()).findUserByEmail(anyString());
        }
    }

    @Nested
    @DisplayName("refreshToken tests")
    class RefreshTokenTests {
        @Test
        @DisplayName("Should return new JWT response when refresh token is valid")
        void refreshToken_whenValid_shouldReturnNewJwt() {
            RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
            RefreshTokenEntity refreshToken = new RefreshTokenEntity(1L, "valid-refresh-token", LocalDateTime.now().plusDays(1));

            when(refreshTokenService.findByToken(request.getRefreshToken())).thenReturn(Optional.of(refreshToken));
            when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
            when(userService.findUserById(refreshToken.getUserId())).thenReturn(userDto);
            when(jwtService.generateToken(any(TokenData.class))).thenReturn("new-jwt-token");

            JwtResponseDto response = authService.refreshToken(request);

            assertThat(response).isNotNull();
            assertThat(response.getUserId()).isEqualTo(userDto.getId());
            assertThat(response.getAccessToken()).isEqualTo("new-jwt-token");
            assertThat(response.getRefreshToken()).isEqualTo("valid-refresh-token");
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when token is not found")
        void refreshToken_whenTokenNotFound_shouldThrowException() {
            RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
            when(refreshTokenService.findByToken(request.getRefreshToken())).thenReturn(Optional.empty());
            assertThrows(ResponseStatusException.class, () -> authService.refreshToken(request));
        }
    }

    @Nested
    @DisplayName("logoutCurrentUser tests")
    class LogoutTests {
        @Test
        @DisplayName("Should delete refresh token for authenticated user")
        void logoutCurrentUser_whenAuthenticated_shouldDeleteToken() {
            TokenData tokenData = new TokenData(1L, UserRole.DEVELOPER);
            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(tokenData);
            when(authenticationFacade.getAuthentication()).thenReturn(authentication);

            authService.logoutCurrentUser();

            verify(refreshTokenService).deleteByUserId(tokenData.getId());
        }

        @Test
        @DisplayName("Should not do anything for anonymous user")
        void logoutCurrentUser_whenAnonymous_shouldDoNothing() {
            Authentication anonymousAuth = new AnonymousAuthenticationToken("key", "anonymousUser",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
            when(authenticationFacade.getAuthentication()).thenReturn(anonymousAuth);

            authService.logoutCurrentUser();
            verify(refreshTokenService, never()).deleteByUserId(anyLong());
        }
    }
}
