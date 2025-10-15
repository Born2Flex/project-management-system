package edu.ukma.projectmanagementsystem.service.security;

import edu.ukma.projectmanagementsystem.config.AuthenticationFacade;
import edu.ukma.projectmanagementsystem.config.TokenData;
import edu.ukma.projectmanagementsystem.domain.entity.RefreshTokenEntity;
import edu.ukma.projectmanagementsystem.service.business.UserService;
import edu.ukma.projectmanagementsystem.service.dto.security.JwtResponseDto;
import edu.ukma.projectmanagementsystem.service.dto.security.LoginDto;
import edu.ukma.projectmanagementsystem.service.dto.security.RefreshTokenRequest;
import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationFacade authenticationFacade;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UsernamePasswordAuthenticator usernamePasswordAuthenticator;
    private final UserService userService;

    public JwtResponseDto authenticate(LoginDto loginDto) {
        usernamePasswordAuthenticator.authenticateUser(loginDto);
        UserDto user = userService.findUserByEmail(loginDto.getEmail());
        String token = jwtService.generateToken(new TokenData(user.getId(), user.getRole()));
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new JwtResponseDto(user.getId(), token, refreshToken.getToken());
    }

    public JwtResponseDto refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        Long userId = refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        UserDto user = userService.findUserById(userId);
        String token = jwtService.generateToken(new TokenData(user.getId(), user.getRole()));
        return new JwtResponseDto(userId, token, requestRefreshToken);
    }

    public void logoutCurrentUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)){
            TokenData tokenData = (TokenData) authentication.getPrincipal();
            refreshTokenService.deleteByUserId(tokenData.getId());
        }
    }
}
