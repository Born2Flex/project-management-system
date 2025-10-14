package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.domain.entity.ProjectEntity;
import edu.ukma.projectmanagementsystem.domain.entity.TaskEntity;
import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.repository.ProjectRepository;
import edu.ukma.projectmanagementsystem.domain.repository.TaskRepository;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskUpdateDto;
import edu.ukma.projectmanagementsystem.service.mapper.TaskMapper;
import edu.ukma.projectmanagementsystem.web.exception.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskDto createTaskForProject(Long projectId, TaskCreateDto createDto) {
        log.info("Attempting to create task for project ID: {}", projectId);
        TaskEntity taskEntity = taskMapper.toEntity(createDto);
        ProjectEntity project = findProjectByIdOrElseThrow(projectId);
        taskEntity.setProject(project);
        LocalDateTime now = LocalDateTime.now();
        taskEntity.setCreatedAt(now);
        taskEntity.setUpdatedAt(now);
        TaskEntity savedTask = taskRepository.save(taskEntity);
        log.info("Successfully created task with ID: {} for project ID: {}", savedTask.getId(), projectId);
        return taskMapper.toDto(savedTask);
    }

    @Override
    public List<TaskDto> findTasksByProjectId(Long projectId) {
        log.info("Fetching all tasks for project ID: {}", projectId);
        validateProjectExists(projectId);
        List<TaskEntity> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    public TaskDto findTaskById(Long projectId, Long taskId) {
        log.info("Fetching task with ID: {}", taskId);
        validateProjectExists(projectId);
        TaskEntity taskEntity = findTaskByIdOrElseThrow(taskId);
        return taskMapper.toDto(taskEntity);
    }

    @Override
    public TaskDto updateTask(Long projectId, Long taskId, TaskUpdateDto updateDto) {
        log.info("Attempting to update task with ID: {}", taskId);
        validateProjectExists(projectId);
        TaskEntity existingTask = findTaskByIdOrElseThrow(taskId);
        taskMapper.updateEntity(existingTask, updateDto);
        existingTask.setUpdatedAt(LocalDateTime.now());
        TaskEntity updatedTask = taskRepository.save(existingTask);
        log.info("Successfully updated task with ID: {}", taskId);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    public void deleteTask(Long projectId, Long taskId) {
        log.info("Attempting to delete task with ID: {}", taskId);
        validateProjectExists(projectId);
        taskRepository.deleteById(taskId);
        log.info("Successfully deleted task with ID: {}", taskId);
    }

    @Override
    public TaskDto assignTaskToUser(Long taskId, Long userId) {
        log.info("Attempting to assign task {} to user {}", taskId, userId);
        TaskEntity task = findTaskEntityById(taskId);
        UserEntity user = findUserEntityById(userId);

        if (!task.getProject().getDevelopers().contains(user)) {
            throw new IllegalStateException("Cannot assign task to a user who is not a developer on the project.");
        }

        task.setAssignee(user);
        task.setUpdatedAt(LocalDateTime.now());
        TaskEntity updatedTask = taskRepository.save(task);
        log.info("Successfully assigned task {} to user {}", taskId, userId);
        return taskMapper.toDto(updatedTask);
    }

    @Override
    public TaskDto unassignTaskFromUser(Long taskId) {
        log.info("Attempting to unassign user from task {}", taskId);
        TaskEntity task = findTaskEntityById(taskId);
        task.setAssignee(null);
        task.setUpdatedAt(LocalDateTime.now());
        TaskEntity updatedTask = taskRepository.save(task);
        log.info("Successfully unassigned user from task {}", taskId);
        return taskMapper.toDto(updatedTask);
    }

    private TaskEntity findTaskEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("Task not found with ID: " + id));
    }

    private UserEntity findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("User not found with ID: " + id));
    }

    private void validateProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NoSuchEntityException("Project not found with ID: " + projectId);
        }
    }

    private ProjectEntity findProjectByIdOrElseThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchEntityException("Project not found with ID: " + projectId));
    }

    private TaskEntity findTaskByIdOrElseThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchEntityException("Task not found with ID: " + taskId));
    }
}
