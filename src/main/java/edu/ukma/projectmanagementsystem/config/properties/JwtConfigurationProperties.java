package edu.ukma.projectmanagementsystem.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfigurationProperties {
    private String secret;
    private Integer expirationTimeMinutes;
    private Integer refreshTokenExpirationDays;
}
