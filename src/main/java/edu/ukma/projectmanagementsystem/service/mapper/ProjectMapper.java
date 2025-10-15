package edu.ukma.projectmanagementsystem.service.mapper;

import edu.ukma.projectmanagementsystem.domain.entity.ProjectEntity;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectDto;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = ComponentModel.SPRING)
public interface ProjectMapper {
    ProjectDto toDto(ProjectEntity entity);

    List<ProjectDto> toDto(List<ProjectEntity> entity);

    ProjectEntity toEntity(ProjectCreateDto createDto);

    ProjectEntity toEntity(ProjectDto dto);

    ProjectEntity updateEntity(@MappingTarget ProjectEntity entity, ProjectUpdateDto updateDto);
}