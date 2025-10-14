package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.service.dto.task.TaskCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskUpdateDto;

import java.util.List;

public interface TaskService {
    TaskDto createTaskForProject(Long projectId, TaskCreateDto createDto);

    List<TaskDto> findTasksByProjectId(Long projectId);

    TaskDto findTaskById(Long projectId, Long taskId);

    TaskDto updateTask(Long projectId, Long taskId, TaskUpdateDto updateDto);

    void deleteTask(Long projectId, Long taskId);

    TaskDto assignTaskToUser(Long taskId, Long userId);

    TaskDto unassignTaskFromUser(Long taskId);
}
