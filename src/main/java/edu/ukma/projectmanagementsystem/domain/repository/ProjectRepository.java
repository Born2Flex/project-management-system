package edu.ukma.projectmanagementsystem.domain.repository;

import edu.ukma.projectmanagementsystem.domain.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {
}
