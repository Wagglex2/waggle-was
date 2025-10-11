package com.wagglex2.waggle.domain.common.service.serviceImpl;

import com.wagglex2.waggle.domain.common.entity.BaseRecruitment;
import com.wagglex2.waggle.domain.common.repository.RecruitmentRepository;
import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentServiceImpl implements RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;

    @Transactional
    @Override
    public void closeExpiredRecruitments() {
        // 현재 시각을 가져와서 자정 시각으로 설정 (00:00:00)
        LocalDateTime baseTime = LocalDateTime.now()
                                         .withHour(0)
                                         .withMinute(0)
                                         .withSecond(0);

        // 조건에 해당하는 공고 조회
        List<BaseRecruitment> targets = recruitmentRepository.findByStatusIsAndDeadlineBefore(
                RecruitmentStatus.RECRUITING,
                baseTime
        );

        // 상태 변경
        targets.forEach(BaseRecruitment::changeStatusByDeadline);

        log.info(
                "[모집 공고 마감 처리] 시각: {} | 마감 상태로 변경된 공고 수: {}",
                baseTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                targets.size()
        );
    }
}
