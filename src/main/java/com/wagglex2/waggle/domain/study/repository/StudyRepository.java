package com.wagglex2.waggle.domain.study.repository;

import com.wagglex2.waggle.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {
    @Modifying(clearAutomatically = true)
    @Query("update Study a set a.viewCount = a.viewCount + 1 where a.id = :studyId")
    int increaseViewCount(@Param("studyId") Long studyId);
}