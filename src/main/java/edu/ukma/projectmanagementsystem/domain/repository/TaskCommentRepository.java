package edu.ukma.projectmanagementsystem.domain.repository;

import edu.ukma.projectmanagementsystem.domain.entity.TaskCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCommentRepository extends JpaRepository<TaskCommentEntity, Long> {
    boolean existsByIdAndAuthorId(Long id, Long authorId);
}