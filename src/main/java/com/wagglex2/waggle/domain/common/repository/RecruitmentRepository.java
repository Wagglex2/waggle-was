package com.wagglex2.waggle.domain.common.repository;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RecruitmentRepository extends JpaRepository<BaseRecruitment, Long> {

    @Query("""
        SELECT r FROM BaseRecruitment r
        WHERE r.id = :id
        AND r.status != com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CANCELED
    """)
    Optional<BaseRecruitment> findByIdNotCanceled(@Param("id") Long id);

    /**
     * 마감일(deadline)이 기준 시각(baseTime) 이전인 공고들의 상태를 CLOSED로 변경
     * <p>
     * 기준 시각은 자정(00:00)으로 설정
     *
     * @param baseTime 기준 시각
     * @return 업데이트된 공고 건수
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE BaseRecruitment r
        SET r.status = com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CLOSED
        WHERE r.deadline < :baseTime
        AND r.status != com.wagglex2.waggle.domain.common.type.RecruitmentStatus.CANCELED
    """)
    int closeExpiredRecruitments(@Param("baseTime") LocalDateTime baseTime);
}
