package edu.ukma.projectmanagementsystem.domain.repository;

import edu.ukma.projectmanagementsystem.domain.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    @Query("SELECT p FROM ProjectEntity p JOIN p.developers d WHERE d.id = :userId")
    List<ProjectEntity> findAllProjectsForCurrentUser(@Param("userId") Long userId);

    @Query("SELECT p FROM ProjectEntity p JOIN p.developers d WHERE d.id = :userId AND p.name = :projectName")
    Optional<ProjectEntity> findByNameAndDeveloperId(@Param("projectName") String projectName, @Param("userId") Long userId);
}

