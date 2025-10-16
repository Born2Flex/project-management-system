package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.service.dto.task.TaskCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskFullDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskUpdateDto;

import java.util.List;

public interface TaskService {
    TaskFullDto createTaskForProject(Long projectId, TaskCreateDto createDto);

    List<TaskDto> findTasksByProjectId(Long projectId);

    TaskFullDto findTaskById(Long projectId, Long taskId);

    TaskFullDto updateTask(Long projectId, Long taskId, TaskUpdateDto updateDto);

    void deleteTask(Long projectId, Long taskId);

    TaskFullDto assignTaskToUser(Long taskId, Long userId);

    TaskFullDto unassignTaskFromUser(Long taskId);
}
