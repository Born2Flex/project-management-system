package edu.ukma.projectmanagementsystem.service.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AssignTaskRequest {
    private Long assigneeId;
}
