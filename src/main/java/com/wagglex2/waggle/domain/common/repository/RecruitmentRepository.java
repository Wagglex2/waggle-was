package com.wagglex2.waggle.domain.common.repository;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecruitmentRepository extends JpaRepository<BaseRecruitment, Long> {

    /**
     * 상태가 RECRUITING(모집 중)인 공고 중, 마감일이 기준 시각 이전인 공고를 조회
     *
     * @param status 조회할 공고 상태 ({@link RecruitmentStatus#RECRUITING})
     * @param baseTime 기준 시각
     * @return 조건에 맞는 BaseRecruitment 타입의 모집 공고 리스트
     * (반환 리스트의 실제 객체는 BaseRecruitment의 서브클래스 인스턴스)
     */
    // 조회한 엔티티를 다른 트랜잭션에서 수정하지 못하도록 잠금
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    List<BaseRecruitment> findByStatusIsAndDeadlineBefore(RecruitmentStatus status, LocalDateTime baseTime);
}
