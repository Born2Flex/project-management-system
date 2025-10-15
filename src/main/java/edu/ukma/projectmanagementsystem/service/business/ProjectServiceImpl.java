package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.config.AuthenticationFacade;
import edu.ukma.projectmanagementsystem.config.TokenData;
import edu.ukma.projectmanagementsystem.domain.entity.ProjectEntity;
import edu.ukma.projectmanagementsystem.domain.entity.UserEntity;
import edu.ukma.projectmanagementsystem.domain.repository.ProjectRepository;
import edu.ukma.projectmanagementsystem.domain.repository.UserRepository;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectDto;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectUpdateDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import edu.ukma.projectmanagementsystem.service.mapper.ProjectMapper;
import edu.ukma.projectmanagementsystem.service.mapper.UserMapper;
import edu.ukma.projectmanagementsystem.web.exception.NoSuchEntityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationFacade authenticationFacade;

    @Override
    public ProjectDto createProject(ProjectCreateDto createDto) {
        log.info("Attempting to create a new project with name: {}", createDto.getName());
        ProjectEntity entity = projectMapper.toEntity(createDto);
        TokenData tokenData = (TokenData) authenticationFacade.getAuthentication().getPrincipal();
        UserEntity currentUser = findUserEntityById(tokenData.getId());
        entity.getDevelopers().add(currentUser);
        ProjectEntity savedEntity = projectRepository.save(entity);
        log.info("Successfully created project with ID: {}", savedEntity.getId());
        return projectMapper.toDto(savedEntity);
    }

    @Override
    public List<ProjectDto> findAllProjects() {
        log.info("Fetching all projects");
        return projectRepository.findAll().stream()
                .map(projectMapper::toDto)
                .toList();
    }

    @Override
    public ProjectDto findProjectById(Long id) {
        log.info("Fetching project with ID: {}", id);
        return projectRepository.findById(id)
                .map(projectMapper::toDto)
                .orElseThrow(() -> new NoSuchEntityException("Project not found with ID: " + id));
    }

    @Override
    public ProjectDto updateProject(Long id, ProjectUpdateDto updateDto) {
        log.info("Attempting to update project with ID: {}", id);
        ProjectEntity existingEntity = projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("Project not found with ID: " + id));
        projectMapper.updateEntity(existingEntity, updateDto);
        ProjectEntity updatedEntity = projectRepository.save(existingEntity);
        log.info("Successfully updated project with ID: {}", id);
        return projectMapper.toDto(updatedEntity);
    }

    @Override
    public void deleteProject(Long id) {
        log.info("Attempting to delete project with ID: {}", id);
        if (!projectRepository.existsById(id)) {
            throw new NoSuchEntityException("Project not found with ID: " + id);
        }
        projectRepository.deleteById(id);
        log.info("Successfully deleted project with ID: {}", id);
    }

    @Override
    public List<UserDto> getProjectDevelopers(Long projectId) {
        log.info("Fetching developers for project ID: {}", projectId);
        ProjectEntity project = findProjectEntityById(projectId);
        return userMapper.toDtoList(project.getDevelopers());
    }

    @Override
    public ProjectDto addDeveloperToProject(Long projectId, Long userId) {
        log.info("Adding developer {} to project {}", userId, projectId);
        ProjectEntity project = findProjectEntityById(projectId);
        UserEntity user = findUserEntityById(userId);
        project.getDevelopers().add(user);
        ProjectEntity updatedProject = projectRepository.save(project);
        return projectMapper.toDto(updatedProject);
    }

    @Override
    public void removeDeveloperFromProject(Long projectId, Long userId) {
        log.info("Removing developer {} from project {}", userId, projectId);
        ProjectEntity project = findProjectEntityById(projectId);
        UserEntity user = findUserEntityById(userId);
        project.getDevelopers().remove(user);
        projectRepository.save(project);
    }

    private ProjectEntity findProjectEntityById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("Project not found with ID: " + id));
    }

    private UserEntity findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchEntityException("User not found with ID: " + id));
    }
}
