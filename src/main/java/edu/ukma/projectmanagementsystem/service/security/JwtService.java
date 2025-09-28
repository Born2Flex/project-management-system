package edu.ukma.projectmanagementsystem.service.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ukma.projectmanagementsystem.config.TokenData;
import edu.ukma.projectmanagementsystem.web.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * Service responsible for handling operations related to JWT tokens.
 * <p>
 * This service provides methods to generate, validate, and parse JWT tokens,
 * as well as extract claims and manage token expiration.
 *
 * @author Danylo Shlapak
 * @version 1.1
 * @since 1.1
 * @see ObjectMapper
 * @see Jwts
 * @see Claims
 */
@Slf4j
@Service
public class JwtService {
    private final String secret;
    private final long expirationTime;
    private final ObjectMapper mapper;
    public static final TypeReference<Map<String, Object>> TOKEN_DATA_TYPE_REF = new TypeReference<>() {};

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-time-minutes}") long expirationTime,
                      ObjectMapper mapper) {
        this.secret = secret;
        this.expirationTime = expirationTime;
        this.mapper = mapper;
    }

    /**
     * Checks if a JWT token has expired.
     * <p>
     * Example:
     * <pre>
     * {@code
     * LoginDto loginDto = new LoginDto("email@example.com", "password");
     * TokenDto token = authService.authenticate(loginDto);
     * }
     * </pre>
     * @param token the JWT token to validate.
     * @return {@code true} if the token has expired, {@code false} otherwise.
     * @throws InvalidTokenException if the token is invalid.
     */
    public boolean isExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token", e);
            throw new InvalidTokenException("Invalid JWT token");
        }
    }

    /**
     * Parses a JWT token and converts its claims into a {@link TokenData} object.
     *
     * @param token the JWT token to parse.
     * @return a {@link TokenData} object containing the token's claims.
     */
    public TokenData parseToken(String token) {
        Claims claims = getClaims(token);
        return mapper.convertValue(claims, TokenData.class);
    }

    /**
     * Extracts the claims from a JWT token.
     *
     * @param token the JWT token to parse.
     * @return a {@link Claims} object containing the token's payload.
     * @throws InvalidTokenException if the token is invalid.
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Generates a JWT token based on the provided {@link TokenData}.
     *
     * @param tokenData the data to include in the token claims.
     * @return a signed JWT token.
     */
    public String generateToken(TokenData tokenData) {
        Date issuedDateTime = new Date(System.currentTimeMillis());
        Date expirationDateTime = new Date(System.currentTimeMillis() + expirationTime * 60 * 1000);
        return Jwts.builder()
                .claims(mapper.convertValue(tokenData, TOKEN_DATA_TYPE_REF))
                .issuedAt(issuedDateTime)
                .expiration(expirationDateTime)
                .signWith(getSigningKey()).compact();
    }

    /**
     * Retrieves the signing key used for JWT token generation and validation.
     *
     * @return the signing {@link SecretKey}.
     */
    public SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}