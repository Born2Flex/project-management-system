package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.service.dto.taskComment.TaskCommentCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.taskComment.TaskCommentDto;
import edu.ukma.projectmanagementsystem.service.dto.taskComment.TaskCommentUpdateDto;

import java.util.List;

public interface TaskCommentService {
    TaskCommentDto createTaskComment(TaskCommentCreateDto createDto);

    TaskCommentDto updateTaskComment(Long id, TaskCommentUpdateDto updateDto);

    TaskCommentDto findTaskCommentById(Long id);

    List<TaskCommentDto> findCommentsByTaskId(Long taskId);

    void deleteTaskComment(Long id);
}
