package com.wagglex2.waggle.domain.project.repository;

import com.wagglex2.waggle.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Project p set p.viewCount = p.viewCount + 1 where p.id = :projectId")
    void increaseViewCount(@Param("projectId") Long projectId);
}
