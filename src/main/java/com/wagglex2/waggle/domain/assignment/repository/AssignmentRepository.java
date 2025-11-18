package com.wagglex2.waggle.domain.assignment.repository;

import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>, AssignmentRepositoryCustom {

    @EntityGraph(
            attributePaths = {"user", "grades"},
            type = EntityGraph.EntityGraphType.LOAD
    )
    Optional<Assignment> findWithAllById(Long assignmentId);

    @Modifying(clearAutomatically = true)
    @Query("update Assignment a set a.viewCount = a.viewCount + 1 where a.id = :assignmentId")
    int increaseViewCount(@Param("assignmentId") Long assignmentId);
}
