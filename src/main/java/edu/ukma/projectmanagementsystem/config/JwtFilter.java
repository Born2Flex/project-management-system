package edu.ukma.projectmanagementsystem.config;

import edu.ukma.projectmanagementsystem.service.security.JwtService;
import edu.ukma.projectmanagementsystem.web.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";
    private final JwtService jwtService;

    private final HandlerExceptionResolver exceptionResolver;

    public JwtFilter(JwtService jwtService,
                     @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.jwtService = jwtService;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("Processing request on URI: {}", request.getRequestURI());
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(BEARER_PREFIX.length());
        try {
            TokenData tokenData = jwtService.parseToken(token);
            Set<GrantedAuthority> authorities = Set.of(tokenData.getRole());
            SecurityContextHolder.getContext()
                    .setAuthentication(new UsernamePasswordAuthenticationToken(tokenData, null, authorities));
        } catch (MalformedJwtException e) {
            exceptionResolver.resolveException(request, response, null, new InvalidTokenException("Invalid token"));
            return;
        } catch (ExpiredJwtException e) {
            exceptionResolver.resolveException(request, response, null, new InvalidTokenException("Token is expired"));
            return;
        }
        filterChain.doFilter(request, response);
    }
}