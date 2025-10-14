package edu.ukma.projectmanagementsystem.domain.repository;

import edu.ukma.projectmanagementsystem.domain.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity,Long> {
    List<TaskEntity> findByProjectId(Long projectId);
}
