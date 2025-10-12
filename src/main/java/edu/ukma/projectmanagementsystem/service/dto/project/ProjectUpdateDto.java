package edu.ukma.projectmanagementsystem.service.dto.project;

import edu.ukma.projectmanagementsystem.domain.enumerated.ProjectStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectUpdateDto {

    String name;

    String description;

    ProjectStatus status;
}
