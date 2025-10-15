package edu.ukma.projectmanagementsystem.service.mapper;

import edu.ukma.projectmanagementsystem.domain.entity.TaskEntity;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = ComponentModel.SPRING, uses = TaskCommentMapper.class)
public interface TaskMapper {
    TaskDto toDto(TaskEntity entity);
    TaskEntity toEntity(TaskCreateDto createDto);
    TaskEntity updateEntity(@MappingTarget TaskEntity entity, TaskUpdateDto updateDto);
}