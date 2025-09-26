package com.wagglex2.waggle.domain.assignment.repository;

import com.wagglex2.waggle.domain.assignment.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}
