package edu.ukma.projectmanagementsystem.service.validators;

import edu.ukma.projectmanagementsystem.domain.repository.ProjectRepository;
import edu.ukma.projectmanagementsystem.web.exception.ProjectNameDuplicateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {
    private final ProjectRepository projectRepository;

    public void validateForDuplicateName(Long userId, String projectName) {
        projectRepository.findByNameAndDeveloperId(projectName, userId)
                .ifPresent(project -> {
                    throw new ProjectNameDuplicateException();
                });
    }
}
