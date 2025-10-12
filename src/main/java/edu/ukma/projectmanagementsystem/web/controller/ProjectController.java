package edu.ukma.projectmanagementsystem.web.controller;

import edu.ukma.projectmanagementsystem.service.business.ProjectService;
import edu.ukma.projectmanagementsystem.service.business.TaskService;
import edu.ukma.projectmanagementsystem.service.dto.AssignTaskRequest;
import edu.ukma.projectmanagementsystem.service.dto.AddDeveloperRequest;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectDto;
import edu.ukma.projectmanagementsystem.service.dto.project.ProjectUpdateDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskCreateDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskDto;
import edu.ukma.projectmanagementsystem.service.dto.task.TaskUpdateDto;
import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import edu.ukma.projectmanagementsystem.web.handler.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new project")
    @ApiResponse(responseCode = "201", description = "Project created successfully", content = @Content(schema = @Schema(implementation = ProjectDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createProject(@RequestBody @Valid ProjectCreateDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @GetMapping
    @Operation(summary = "Get all projects")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProjectDto.class))))
    public List<ProjectDto> getAllProjects() {
        return projectService.findAllProjects();
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get a project by its ID")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProjectDto.class)))
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ProjectDto getProjectById(@PathVariable Long projectId) {
        return projectService.findProjectById(projectId);
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "Update an existing project")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProjectDto.class)))
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ProjectDto updateProject(@PathVariable Long projectId, @RequestBody @Valid ProjectUpdateDto projectDto) {
        return projectService.updateProject(projectId, projectDto);
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "Delete a project by its ID")
    @ApiResponse(responseCode = "204", description = "Project deleted successfully")
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
    }

    @PostMapping("/{projectId}/tasks")
    @Operation(summary = "Create a new task within a project")
    @ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(schema = @Schema(implementation = TaskDto.class)))
    @ApiResponse(responseCode = "404", description = "Project or Assignee not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTaskInProject(@PathVariable Long projectId, @RequestBody @Valid TaskCreateDto taskDto) {
        return taskService.createTaskForProject(projectId, taskDto);
    }

    @GetMapping("/{projectId}/tasks")
    @Operation(summary = "Get all tasks for a specific project")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskDto.class))))
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public List<TaskDto> getTasksForProject(@PathVariable Long projectId) {
        return taskService.findTasksByProjectId(projectId);
    }

    @GetMapping("/{projectId}/tasks/{taskId}")
    @Operation(summary = "Get a specific task within a project by its ID")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TaskDto.class)))
    @ApiResponse(responseCode = "404", description = "Project or Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public TaskDto getTaskById(@PathVariable Long projectId, @PathVariable Long taskId) {
        return taskService.findTaskById(projectId, taskId);
    }

    @PutMapping("/{projectId}/tasks/{taskId}")
    @Operation(summary = "Update a specific task within a project")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TaskDto.class)))
    @ApiResponse(responseCode = "404", description = "Project or Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public TaskDto updateTaskInProject(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody @Valid TaskUpdateDto taskDto) {
        return taskService.updateTask(projectId, taskId, taskDto);
    }

    @DeleteMapping("/{projectId}/tasks/{taskId}")
    @Operation(summary = "Delete a specific task within a project")
    @ApiResponse(responseCode = "204", description = "Task deleted successfully")
    @ApiResponse(responseCode = "404", description = "Project or Task not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskInProject(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.deleteTask(projectId, taskId);
    }

    @PatchMapping("/{projectId}/tasks/{taskId}/assign")
    @Operation(summary = "Assign a task to a user")
    public TaskDto assignTask(@PathVariable Long projectId, @PathVariable Long taskId, @RequestBody @Valid AssignTaskRequest request) {
        projectService.findProjectById(projectId);
        return taskService.assignTaskToUser(taskId, request.getAssigneeId());
    }

    @PatchMapping("/{projectId}/tasks/{taskId}/unassign")
    @Operation(summary = "Unassign a user from a task")
    public TaskDto unassignTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        projectService.findProjectById(projectId); // Ensures project exists
        return taskService.unassignTaskFromUser(taskId);
    }

    @GetMapping("/{projectId}/developers")
    @Operation(summary = "Get all developers for a project")
    @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
    public List<UserDto> getProjectDevelopers(@PathVariable Long projectId) {
        return projectService.getProjectDevelopers(projectId);
    }

    @PostMapping("/{projectId}/developers")
    @Operation(summary = "Add a developer to a project")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ProjectDto.class)))
    public ProjectDto addDeveloperToProject(@PathVariable Long projectId, @RequestBody @Valid AddDeveloperRequest request) {
        return projectService.addDeveloperToProject(projectId, request.getDeveloperId());
    }

    @DeleteMapping("/{projectId}/developers/{developerId}")
    @Operation(summary = "Remove a developer from a project")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeDeveloperFromProject(@PathVariable Long projectId, @PathVariable Long developerId) {
        projectService.removeDeveloperFromProject(projectId, developerId);
    }
}
