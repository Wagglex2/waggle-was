package com.wagglex2.waggle.domain.project.repository;

import com.wagglex2.waggle.domain.project.entity.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {

    @EntityGraph(
            attributePaths = {"user", "positions", "skills", "grades"},
            type = EntityGraph.EntityGraphType.LOAD
    )
    Optional<Project> findWithAllById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Project p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    int increaseViewCount(@Param("id") Long id);
}
