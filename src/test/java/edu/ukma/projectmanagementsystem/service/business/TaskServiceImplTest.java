package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.domain.entity.ProjectEntity;
import edu.ukma.projectmanagementsystem.domain.entity.TaskEntity;
import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.enumerated.TaskPriority;
import edu.ukma.projectmanagementsystem.domain.enumerated.TaskStatus;
import edu.ukma.projectmanagementsystem.domain.repository.ProjectRepository;
import edu.ukma.projectmanagementsystem.domain.repository.TaskRepository;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskUpdateDto;
import edu.ukma.projectmanagementsystem.service.mapper.TaskMapper;
import edu.ukma.projectmanagementsystem.web.exception.NoSuchEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    private final Long projectId = 1L;
    private final Long taskId = 1L;
    private final Long userId = 1L;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskMapper taskMapper;
    @InjectMocks
    private TaskServiceImpl taskService;
    private ProjectEntity projectEntity;
    private UserEntity userEntity;
    private TaskEntity taskEntity;
    private TaskDto taskDto;
    private TaskCreateDto taskCreateDto;
    private TaskUpdateDto taskUpdateDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(userId);

        projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        projectEntity.setDevelopers(new HashSet<>(Set.of(userEntity)));

        taskCreateDto = new TaskCreateDto();
        taskCreateDto.setTitle("New Task");
        taskCreateDto.setDescription("Task Description");
        taskCreateDto.setStatus(TaskStatus.OPEN);
        taskCreateDto.setPriority(TaskPriority.MEDIUM);

        taskUpdateDto = new TaskUpdateDto();
        taskUpdateDto.setTitle("Updated Task");
        taskUpdateDto.setDescription("Updated Description");
        taskUpdateDto.setStatus(TaskStatus.IN_PROGRESS);
        taskUpdateDto.setPriority(TaskPriority.HIGH);

        taskEntity = new TaskEntity();
        taskEntity.setId(taskId);
        taskEntity.setTitle("New Task");
        taskEntity.setProject(projectEntity);

        taskDto = new TaskDto();
        taskDto.setId(taskId);
        taskDto.setTitle("New Task");
    }

    @Nested
    @DisplayName("createTaskForProject tests")
    class CreateTaskForProjectTests {
        @Test
        @DisplayName("Should create task successfully when project exists")
        void createTaskForProject_whenProjectExists_shouldReturnTaskDto() {
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
            when(taskMapper.toEntity(any(TaskCreateDto.class))).thenReturn(taskEntity);
            when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
            when(taskMapper.toDto(any(TaskEntity.class))).thenReturn(taskDto);

            TaskDto result = taskService.createTaskForProject(projectId, taskCreateDto);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(taskId);
            verify(taskRepository, times(1)).save(any(TaskEntity.class));
        }

        @Test
        @DisplayName("Should throw NoSuchEntityException when project does not exist")
        void createTaskForProject_whenProjectNotFound_shouldThrowException() {
            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            assertThrows(NoSuchEntityException.class, () -> taskService.createTaskForProject(projectId, taskCreateDto));
            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("findTasksByProjectId tests")
    class FindTasksByProjectIdTests {
        @Test
        @DisplayName("Should return list of tasks when project exists")
        void findTasksByProjectId_whenProjectExists_shouldReturnTaskDtoList() {
            when(projectRepository.existsById(projectId)).thenReturn(true);
            when(taskRepository.findByProjectId(projectId)).thenReturn(List.of(taskEntity));
            when(taskMapper.toDto(any(TaskEntity.class))).thenReturn(taskDto);

            List<TaskDto> result = taskService.findTasksByProjectId(projectId);

            assertThat(result).isNotNull().hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(taskId);
        }

        @Test
        @DisplayName("Should return empty list when project has no tasks")
        void findTasksByProjectId_whenNoTasks_shouldReturnEmptyList() {
            when(projectRepository.existsById(projectId)).thenReturn(true);
            when(taskRepository.findByProjectId(projectId)).thenReturn(Collections.emptyList());

            List<TaskDto> result = taskService.findTasksByProjectId(projectId);

            assertThat(result).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Should throw NoSuchEntityException when project does not exist")
        void findTasksByProjectId_whenProjectNotFound_shouldThrowException() {
            when(projectRepository.existsById(projectId)).thenReturn(false);

            assertThrows(NoSuchEntityException.class, () -> taskService.findTasksByProjectId(projectId));
            verify(taskRepository, never()).findByProjectId(anyLong());
        }
    }

    @Nested
    @DisplayName("updateTask tests")
    class UpdateTaskTests {
        @Test
        @DisplayName("Should update task successfully")
        void updateTask_whenTaskExists_shouldReturnUpdatedDto() {
            when(projectRepository.existsById(projectId)).thenReturn(true);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
            when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
            when(taskMapper.toDto(any(TaskEntity.class))).thenReturn(taskDto);

            TaskDto result = taskService.updateTask(projectId, taskId, taskUpdateDto);

            assertThat(result).isNotNull();
            verify(taskRepository, times(1)).save(taskEntity);
            verify(taskMapper, times(1)).updateEntity(taskEntity, taskUpdateDto);
        }
    }

    @Nested
    @DisplayName("deleteTask tests")
    class DeleteTaskTests {
        @Test
        @DisplayName("Should delete task successfully")
        void deleteTask_whenTaskExists_shouldComplete() {
            when(projectRepository.existsById(projectId)).thenReturn(true);
            doNothing().when(taskRepository).deleteById(taskId);

            assertDoesNotThrow(() -> taskService.deleteTask(projectId, taskId));
            verify(taskRepository, times(1)).deleteById(taskId);
        }
    }

    @Nested
    @DisplayName("assignTaskToUser tests")
    class AssignTaskToUserTests {
        @Test
        @DisplayName("Should assign task when user is a project developer")
        void assignTaskToUser_whenUserIsDeveloper_shouldSucceed() {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
            when(taskMapper.toDto(any(TaskEntity.class))).thenReturn(taskDto);

            TaskDto result = taskService.assignTaskToUser(taskId, userId);

            assertThat(result).isNotNull();
            verify(taskRepository, times(1)).save(taskEntity);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when user is not a project developer")
        void assignTaskToUser_whenUserIsNotDeveloper_shouldThrowException() {
            UserEntity nonDeveloper = new UserEntity();
            nonDeveloper.setId(99L);
            projectEntity.setDevelopers(Collections.emptySet());
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
            when(userRepository.findById(99L)).thenReturn(Optional.of(nonDeveloper));

            assertThrows(IllegalStateException.class, () -> taskService.assignTaskToUser(taskId, 99L));
            verify(taskRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("unassignTaskFromUser tests")
    class UnassignTaskFromUserTests {
        @Test
        @DisplayName("Should unassign task successfully")
        void unassignTaskFromUser_whenTaskExists_shouldSucceed() {
            taskEntity.setAssignee(userEntity);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
            when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);
            when(taskMapper.toDto(any(TaskEntity.class))).thenReturn(taskDto);

            taskService.unassignTaskFromUser(taskId);

            assertThat(taskEntity.getAssignee()).isNull();
            verify(taskRepository, times(1)).save(taskEntity);
        }
    }
}
