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

@Slf4j
@Service
public class JwtService {
    private final String secret;
    private final long expirationTimeInMinutes;
    private final ObjectMapper mapper;
    public static final TypeReference<Map<String, Object>> TOKEN_DATA_TYPE_REF = new TypeReference<>() {};

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-time-minutes}") long expirationTimeInMinutes,
                      ObjectMapper mapper) {
        this.secret = secret;
        this.expirationTimeInMinutes = expirationTimeInMinutes;
        this.mapper = mapper;
    }

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

    public TokenData parseToken(String token) {
        Claims claims = getClaims(token);
        return mapper.convertValue(claims, TokenData.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(TokenData tokenData) {
        Date issuedDateTime = new Date(System.currentTimeMillis());
        Date expirationDateTime = new Date(System.currentTimeMillis() + expirationTimeInMinutes * 60 * 1000);
        return Jwts.builder()
                .claims(mapper.convertValue(tokenData, TOKEN_DATA_TYPE_REF))
                .issuedAt(issuedDateTime)
                .expiration(expirationDateTime)
                .signWith(getSigningKey()).compact();
    }

    public SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}