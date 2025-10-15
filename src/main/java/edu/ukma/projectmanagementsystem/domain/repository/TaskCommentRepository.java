package edu.ukma.projectmanagementsystem.domain.repository;

import edu.ukma.projectmanagementsystem.domain.entity.TaskCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long> {
    boolean existsByIdAndAuthorId(Long id, Long authorId);
    List<TaskCommentEntity> findByTaskId(Long taskId);
}