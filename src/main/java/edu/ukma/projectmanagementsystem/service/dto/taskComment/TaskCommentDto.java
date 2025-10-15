package edu.ukma.projectmanagementsystem.service.dto.taskComment;

import edu.ukma.projectmanagementsystem.service.dto.user.UserDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TaskCommentDto {
    private Long id;

    private String text;

    private UserDto author;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
