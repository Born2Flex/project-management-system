package edu.ukma.projectmanagementsystem.service.mapper;

import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserRegistrationDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(source = "role", target = "role", ignore = true)
    UserEntity toEntity(UserRegistrationDto registrationDto);

    @Mapping(source = "role.name", target = "role")
    UserDto toDto(UserEntity entity);

    default UserEntity mapWithEncodedPassword(UserRegistrationDto registrationDto, PasswordEncoder passwordEncoder) {
        UserEntity userEntity = toEntity(registrationDto);
        userEntity.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        return userEntity;
    }

    UserEntity updateEntity(@MappingTarget UserEntity entity, UserUpdateDto updateDto);

    List<UserDto> toDtoList(Set<UserEntity> developers);
}
