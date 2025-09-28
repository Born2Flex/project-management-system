package edu.ukma.projectmanagementsystem.service.dto;

import edu.ukma.projectmanagementsystem.domain.enumerated.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
}
