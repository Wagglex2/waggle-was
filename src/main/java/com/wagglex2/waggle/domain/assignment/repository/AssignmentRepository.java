package com.wagglex2.waggle.domain.assignment.repository;

import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Assignment a set a.viewCount = a.viewCount + 1 where a.id = :assignmentId")
    int increaseViewCount(@Param("assignmentId") Long assignmentId);
}
