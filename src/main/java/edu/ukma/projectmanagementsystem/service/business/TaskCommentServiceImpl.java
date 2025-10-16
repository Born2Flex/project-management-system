package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.config.AuthenticationFacade;
import edu.ukma.projectmanagementsystem.config.TokenData;
import edu.ukma.projectmanagementsystem.domain.entity.TaskCommentEntity;
import edu.ukma.projectmanagementsystem.domain.entity.TaskEntity;
import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.repository.TaskCommentRepository;
import edu.ukma.projectmanagementsystem.domain.repository.TaskRepository;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.service.dto.taskComment.TaskCommentCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.taskComment.TaskCommentDto;
import edu.ukma.projectmanagementsystem.service.dto.taskComment.TaskCommentUpdateDto;
import edu.ukma.projectmanagementsystem.service.mapper.TaskCommentMapper;
import edu.ukma.projectmanagementsystem.web.exception.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class TaskCommentServiceImpl implements TaskCommentService {
    private final TaskCommentRepository taskCommentRepository;
    private final TaskCommentMapper taskCommentMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final AuthenticationFacade authenticationFacade;

    @Override
    public TaskCommentDto createTaskComment(TaskCommentCreateDto createDto) {
        TaskCommentEntity entity = taskCommentMapper.toEntity(createDto);
        TokenData tokenData = (TokenData) authenticationFacade.getAuthentication().getPrincipal();
        UserEntity currentUser = findUserEntityById(tokenData.getId());
        TaskEntity task = findTaskEntityById(createDto.getTask());
        entity.setAuthor(currentUser);
        entity.setTask(task);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        TaskCommentEntity savedEntity = taskCommentRepository.save(entity);
        log.info("Successfully created comment with ID: {}", savedEntity.getId());
        return taskCommentMapper.toDto(savedEntity);
    }

    @Override
    public TaskCommentDto findTaskCommentById(Long id) {
        log.info("Fetching task comment with ID: {}", id);
        return taskCommentRepository.findById(id)
                .map(taskCommentMapper::toDto)
                .orElseThrow(() -> new NoSuchEntityException("Task comment not found with ID: " + id));
    }

    @Override
    public TaskCommentDto updateTaskComment(Long id, TaskCommentUpdateDto updateDto) {
        log.info("Attempting to update comment with ID: {}", id);
        TaskCommentEntity existingEntity = taskCommentRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("Project not found with ID: " + id));
        TokenData tokenData = (TokenData) authenticationFacade.getAuthentication().getPrincipal();
        if (!existingEntity.getAuthor().getId().equals(tokenData.getId())) {
            throw new AccessDeniedException("You are not allowed to update this comment");
        }
        taskCommentMapper.updateEntity(existingEntity, updateDto);
        LocalDateTime now = LocalDateTime.now();
        existingEntity.setUpdatedAt(now);
        TaskCommentEntity updatedEntity = taskCommentRepository.save(existingEntity);
        log.info("Successfully updated project with ID: {}", id);
        return taskCommentMapper.toDto(updatedEntity);
    }

    @Override
    public List<TaskCommentDto> findCommentsByTaskId(Long taskId) {
        log.info("Fetching all comments for task ID: {}", taskId);
        List<TaskCommentEntity> comments = taskCommentRepository.findByTaskId(taskId);
        return comments.stream()
                .map(taskCommentMapper::toDto)
                .toList();
    }

    @Override
    public void deleteTaskComment(Long id) {
        log.info("Attempting to delete task comment with ID: {}", id);
        if (!taskCommentRepository.existsById(id)) {
            throw new NoSuchEntityException("Project not found with ID: " + id);
        }
        TokenData tokenData = (TokenData) authenticationFacade.getAuthentication().getPrincipal();
        if (!taskCommentRepository.existsByIdAndAuthorId(id, tokenData.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this comment");
        }
        taskCommentRepository.deleteById(id);
        log.info("Successfully deleted task comment with ID: {}", id);
    }

    private UserEntity findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("User not found with ID: " + id));
    }

    private TaskEntity findTaskEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("Task not found with ID: " + id));
    }
}
