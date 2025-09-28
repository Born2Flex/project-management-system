package edu.ukma.projectmanagementsystem.service.mapper;

import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.service.dto.UserDto;
import edu.ukma.projectmanagementsystem.service.dto.UserRegistrationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(source = "role", target = "role", ignore = true)
    UserEntity toEntity(UserRegistrationDto registrationDto);

    @Mapping(source = "role.name", target = "role")
    UserDto toDto(UserEntity userEntity);

    default UserEntity mapWithEncodedPassword(UserRegistrationDto dto, PasswordEncoder passwordEncoder) {
        UserEntity userEntity = toEntity(dto);
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userEntity;
    }
}
