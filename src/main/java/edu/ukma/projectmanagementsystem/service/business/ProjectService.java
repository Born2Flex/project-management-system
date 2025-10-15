package edu.ukma.projectmanagementsystem.service.business;

import edu.ukma.projectmanagementsystem.service.dto.project.ProjectCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectDto;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectUpdateDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;

import java.util.List;

public interface ProjectService {
    ProjectDto createProject(ProjectCreateDto createDto);

    List<ProjectDto> findAllProjects();

    List<ProjectDto> findAllProjectsForCurrentUser();

    ProjectDto findProjectById(Long id);

    ProjectDto updateProject(Long id, ProjectUpdateDto updateDto);

    void deleteProject(Long id);

    List<UserDto> getProjectDevelopers(Long projectId);

    ProjectDto addDeveloperToProject(Long projectId, Long userId);

    void removeDeveloperFromProject(Long projectId, Long userId);
}
