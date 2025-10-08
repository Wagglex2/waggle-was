package com.wagglex2.waggle.domain.project.repository;

import com.wagglex2.waggle.domain.common.type.PositionParticipantInfo;
import com.wagglex2.waggle.domain.common.type.Skill;
import com.wagglex2.waggle.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // 1. Project + User
    @Query("SELECT p FROM Project p JOIN FETCH p.user u WHERE p.id = :id")
    Optional<Project> findByIdWithUser(@Param("id") Long id);

    // 2. Positions
    @Query("SELECT pos FROM Project p JOIN p.positions pos WHERE p.id = :id")
    Set<PositionParticipantInfo> findPositionsByProjectId(@Param("id") Long id);

    // 3. Skills
    @Query("SELECT s FROM Project p JOIN p.skills s WHERE p.id = :id")
    Set<Skill> findSkillsByProjectId(@Param("id") Long id);

    // 4. Grades
    @Query("SELECT g FROM Project p JOIN p.grades g WHERE p.id = :id")
    Set<Integer> findGradesByProjectId(@Param("id") Long id);


    @Modifying(clearAutomatically = true)
    @Query("update Project p set p.viewCount = p.viewCount + 1 where p.id = :id")
    int increaseViewCount(@Param("id") Long id);
}
