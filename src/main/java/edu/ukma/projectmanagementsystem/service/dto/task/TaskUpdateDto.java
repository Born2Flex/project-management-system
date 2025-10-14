package edu.ukma.projectmanagementsystem.service.dto.task;

import edu.ukma.projectmanagementsystem.domain.enumerated.TaskPriority;
import edu.ukma.projectmanagementsystem.domain.enumerated.TaskStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TaskUpdateDto {
    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDateTime dueDateTime;

//    TODO projectId, assigneeId???
//    maybe we should also add some project info
//    we definitely should add some info about the user which is handing the task
//    (in case if it's present)

}
