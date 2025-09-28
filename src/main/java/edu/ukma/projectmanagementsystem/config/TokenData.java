package edu.ukma.projectmanagementsystem.config;

import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenData {
    private Long id;
    private UserRole role;
}