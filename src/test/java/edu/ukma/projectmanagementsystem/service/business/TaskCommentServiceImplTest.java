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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskCommentServiceImplTest {

    private final Long commentId = 1L;
    private final Long taskId = 1L;
    private final Long userId = 1L;
    @Mock
    private TaskCommentRepository taskCommentRepository;
    @Mock
    private TaskCommentMapper taskCommentMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private AuthenticationFacade authenticationFacade;
    @InjectMocks
    private TaskCommentServiceImpl taskCommentService;
    private UserEntity currentUser;
    private TaskEntity taskEntity;
    private TaskCommentEntity taskCommentEntity;
    private TaskCommentCreateDto createDto;
    private TaskCommentUpdateDto updateDto;
    private TaskCommentDto taskCommentDto;
    private TokenData tokenData;

    @BeforeEach
    void setUp() {
        tokenData = new TokenData(userId, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(tokenData, null);

        currentUser = new UserEntity();
        currentUser.setId(userId);

        taskEntity = new TaskEntity();
        taskEntity.setId(taskId);

        createDto = new TaskCommentCreateDto();
        createDto.setTask(taskId);
        createDto.setText("New comment");

        updateDto = new TaskCommentUpdateDto();
        updateDto.setText("Updated comment text");

        taskCommentEntity = new TaskCommentEntity();
        taskCommentEntity.setId(commentId);
        taskCommentEntity.setAuthor(currentUser);
        taskCommentEntity.setTask(taskEntity);
        taskCommentEntity.setText("New comment");

        taskCommentDto = new TaskCommentDto();
        taskCommentDto.setId(commentId);
        taskCommentDto.setText("New comment");

        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
    }

    @Nested
    @DisplayName("createTaskComment tests")
    class CreateTaskCommentTests {
        @Test
        @DisplayName("Should create comment successfully")
        void createTaskComment_whenDataIsValid_shouldReturnDto() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));
            when(taskCommentMapper.toEntity(any(TaskCommentCreateDto.class))).thenReturn(taskCommentEntity);
            when(taskCommentRepository.save(any(TaskCommentEntity.class))).thenReturn(taskCommentEntity);
            when(taskCommentMapper.toDto(any(TaskCommentEntity.class))).thenReturn(taskCommentDto);

            TaskCommentDto result = taskCommentService.createTaskComment(createDto);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(commentId);
            verify(taskCommentRepository, times(1)).save(any(TaskCommentEntity.class));
        }
    }

    @Nested
    @DisplayName("updateTaskComment tests")
    class UpdateTaskCommentTests {
        @Test
        @DisplayName("Should throw NoSuchEntityException when comment not found")
        void updateTaskComment_whenCommentNotFound_shouldThrowNoSuchEntityException() {
            when(taskCommentRepository.findById(commentId)).thenReturn(Optional.empty());

            assertThrows(NoSuchEntityException.class, () -> taskCommentService.updateTaskComment(commentId, updateDto));
            verify(taskCommentRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteTaskComment tests")
    class DeleteTaskCommentTests {
        @Test
        @DisplayName("Should delete comment when user is the author")
        void deleteTaskComment_whenUserIsAuthor_shouldSucceed() {
            when(taskCommentRepository.existsById(commentId)).thenReturn(true);
            when(taskCommentRepository.existsByIdAndAuthorId(commentId, userId)).thenReturn(true);
            doNothing().when(taskCommentRepository).deleteById(commentId);

            assertDoesNotThrow(() -> taskCommentService.deleteTaskComment(commentId));
            verify(taskCommentRepository, times(1)).deleteById(commentId);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when user is not the author")
        void deleteTaskComment_whenUserIsNotAuthor_shouldThrowAccessDeniedException() {
            when(taskCommentRepository.existsById(commentId)).thenReturn(true);
            when(taskCommentRepository.existsByIdAndAuthorId(commentId, userId)).thenReturn(false);

            assertThrows(AccessDeniedException.class, () -> taskCommentService.deleteTaskComment(commentId));
            verify(taskCommentRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Should throw NoSuchEntityException when comment does not exist")
        void deleteTaskComment_whenCommentNotFound_shouldThrowNoSuchEntityException() {
            when(taskCommentRepository.existsById(commentId)).thenReturn(false);

            assertThrows(NoSuchEntityException.class, () -> taskCommentService.deleteTaskComment(commentId));
            verify(taskCommentRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("findCommentsByTaskId tests")
    class FindCommentsByTaskIdTests {
        @Test
        @DisplayName("Should return list of comments for a task")
        void findCommentsByTaskId_whenCommentsExist_shouldReturnDtoList() {
            when(taskCommentRepository.findByTaskId(taskId)).thenReturn(List.of(taskCommentEntity));
            when(taskCommentMapper.toDto(any(TaskCommentEntity.class))).thenReturn(taskCommentDto);

            List<TaskCommentDto> result = taskCommentService.findCommentsByTaskId(taskId);

            assertThat(result).isNotNull().hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(commentId);
        }
    }
}
