package edu.ukma.projectmanagementsystem.service.dto.taskComment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskCommentCreateDto {
    private String text;

    private Long task;
}
