package edu.ukma.projectmanagementsystem.service.mapper;

import edu.ukma.projectmanagementsystem.domain.entity.TaskCommentEntity;
import edu.ukma.projectmanagementsystem.service.dto.taskComment.TaskCommentCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.taskComment.TaskCommentDto;
import edu.ukma.projectmanagementsystem.service.dto.taskComment.TaskCommentUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = UserMapper.class)
public interface TaskCommentMapper {
    TaskCommentDto toDto(TaskCommentEntity entity);
    @Mapping(target = "task", ignore = true)
    TaskCommentEntity toEntity(TaskCommentCreateDto createDto);
    TaskCommentEntity updateEntity(@MappingTarget TaskCommentEntity entity, TaskCommentUpdateDto updateDto);
}
