package edu.ukma.projectmanagementsystem.service.dto.task;

import edu.ukma.projectmanagementsystem.domain.enumerated.TaskPriority;
import edu.ukma.projectmanagementsystem.domain.enumerated.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TaskDto {
    private Long id;

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDateTime dueDateTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
