package edu.ukma.projectmanagementsystem.service.security;

import edu.ukma.projectmanagementsystem.config.TokenData;
import edu.ukma.projectmanagementsystem.domain.entity.RefreshTokenEntity;
import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.service.security.dto.JwtResponseDto;
import edu.ukma.projectmanagementsystem.service.security.dto.LoginDto;
import edu.ukma.projectmanagementsystem.service.security.dto.RefreshTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public JwtResponseDto authenticate(LoginDto loginDto) {
        UserEntity user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtService.generateToken(new TokenData(user.getId(), UserRole.valueOf(user.getRole().getName())));
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user.getId());
        return new JwtResponseDto(user.getId(), token, refreshToken.getToken());
    }

    public JwtResponseDto refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        Long userId = refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
        String token = jwtService.generateToken(new TokenData(user.getId(), UserRole.valueOf(user.getRole().getName())));
        return new JwtResponseDto(userId, token, requestRefreshToken);
    }
}
