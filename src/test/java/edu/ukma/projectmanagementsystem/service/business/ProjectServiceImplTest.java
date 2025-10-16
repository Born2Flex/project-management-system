package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.config.AuthenticationFacade;
import edu.ukma.projectmanagementsystem.config.TokenData;
import edu.ukma.projectmanagementsystem.domain.entity.ProjectEntity;
import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.enumerated.ProjectStatus;
import edu.ukma.projectmanagementsystem.domain.repository.ProjectRepository;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectDto;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectUpdateDto;
import edu.ukma.projectmanagementsystem.service.mapper.ProjectMapper;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
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
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private UserEntity currentUser;
    private ProjectEntity projectEntity;
    private ProjectCreateDto createDto;
    private ProjectUpdateDto updateDto;
    private ProjectDto projectDto;

    private final Long projectId = 1L;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        TokenData tokenData = new TokenData(userId, null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(tokenData, null);

        currentUser = new UserEntity();
        currentUser.setId(userId);

        createDto = new ProjectCreateDto();
        createDto.setName("New Project");
        createDto.setDescription("A test project");
        createDto.setStatus(ProjectStatus.ACTIVE);

        updateDto = new ProjectUpdateDto();
        updateDto.setName("Updated Project");
        updateDto.setDescription("An updated project");
        updateDto.setStatus(ProjectStatus.COMPLETED);

        projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        projectEntity.setName("New Project");
        projectEntity.setDevelopers(new HashSet<>());

        projectDto = new ProjectDto();
        projectDto.setId(projectId);
        projectDto.setName("New Project");

        when(authenticationFacade.getAuthentication()).thenReturn(authentication);
    }

    @Nested
    @DisplayName("createProject tests")
    class CreateProjectTests {
        @Test
        @DisplayName("Should create project successfully")
        void createProject_shouldSucceed() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
            when(projectMapper.toEntity(any(ProjectCreateDto.class))).thenReturn(projectEntity);
            when(projectRepository.save(any(ProjectEntity.class))).thenReturn(projectEntity);
            when(projectMapper.toDto(any(ProjectEntity.class))).thenReturn(projectDto);

            ProjectDto result = projectService.createProject(createDto);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(projectId);
            assertThat(projectEntity.getDevelopers()).contains(currentUser);
            verify(projectRepository, times(1)).save(projectEntity);
        }
    }

    @Nested
    @DisplayName("findAllProjectsForCurrentUser tests")
    class FindAllProjectsForCurrentUserTests {
        @Test
        @DisplayName("Should return projects for the current user")
        void findAllProjectsForCurrentUser_shouldReturnProjectList() {
            when(projectRepository.findAllProjectsForCurrentUser(userId)).thenReturn(List.of(projectEntity));
            when(projectMapper.toDto(anyList())).thenReturn(List.of(projectDto));

            List<ProjectDto> result = projectService.findAllProjectsForCurrentUser();

            assertThat(result).isNotNull().hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(projectId);
            verify(projectRepository, times(1)).findAllProjectsForCurrentUser(userId);
        }
    }

    @Nested
    @DisplayName("updateProject tests")
    class UpdateProjectTests {
        @Test
        @DisplayName("Should update project successfully")
        void updateProject_whenProjectExists_shouldSucceed() {
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
            when(projectRepository.save(any(ProjectEntity.class))).thenReturn(projectEntity);
            when(projectMapper.toDto(any(ProjectEntity.class))).thenReturn(projectDto);

            ProjectDto result = projectService.updateProject(projectId, updateDto);

            assertThat(result).isNotNull();
            verify(projectRepository, times(1)).save(projectEntity);
            verify(projectMapper, times(1)).updateEntity(projectEntity, updateDto);
        }

        @Test
        @DisplayName("Should throw NoSuchEntityException when project not found")
        void updateProject_whenProjectNotFound_shouldThrowException() {
            when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

            assertThrows(NoSuchEntityException.class, () -> projectService.updateProject(projectId, updateDto));
            verify(projectRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteProject tests")
    class DeleteProjectTests {
        @Test
        @DisplayName("Should delete project successfully")
        void deleteProject_whenProjectExists_shouldSucceed() {
            when(projectRepository.existsById(projectId)).thenReturn(true);
            doNothing().when(projectRepository).deleteById(projectId);

            assertDoesNotThrow(() -> projectService.deleteProject(projectId));
            verify(projectRepository, times(1)).deleteById(projectId);
        }

        @Test
        @DisplayName("Should throw NoSuchEntityException when project not found")
        void deleteProject_whenProjectNotFound_shouldThrowException() {
            when(projectRepository.existsById(projectId)).thenReturn(false);

            assertThrows(NoSuchEntityException.class, () -> projectService.deleteProject(projectId));
            verify(projectRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("Developer management tests")
    class DeveloperManagementTests {
        private final Long developerId = 2L;
        private UserEntity developer;

        @BeforeEach
        void developerSetUp() {
            developer = new UserEntity();
            developer.setId(developerId);
        }

        @Test
        @DisplayName("Should add developer to project")
        void addDeveloperToProject_shouldSucceed() {
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
            when(userRepository.findById(developerId)).thenReturn(Optional.of(developer));
            when(projectRepository.save(any(ProjectEntity.class))).thenReturn(projectEntity);
            when(projectMapper.toDto(any(ProjectEntity.class))).thenReturn(projectDto);

            ProjectDto result = projectService.addDeveloperToProject(projectId, developerId);

            assertThat(result).isNotNull();
            assertThat(projectEntity.getDevelopers()).contains(developer);
            verify(projectRepository, times(1)).save(projectEntity);
        }

        @Test
        @DisplayName("Should remove developer from project")
        void removeDeveloperFromProject_shouldSucceed() {
            projectEntity.getDevelopers().add(developer);
            when(projectRepository.findById(projectId)).thenReturn(Optional.of(projectEntity));
            when(userRepository.findById(developerId)).thenReturn(Optional.of(developer));
            when(projectRepository.save(any(ProjectEntity.class))).thenReturn(projectEntity);

            assertDoesNotThrow(() -> projectService.removeDeveloperFromProject(projectId, developerId));

            assertThat(projectEntity.getDevelopers()).doesNotContain(developer);
            verify(projectRepository, times(1)).save(projectEntity);
        }
    }
}
