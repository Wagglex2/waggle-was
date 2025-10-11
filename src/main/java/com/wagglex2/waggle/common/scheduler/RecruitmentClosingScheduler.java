package com.wagglex2.waggle.common.scheduler;

import com.wagglex2.waggle.domain.common.service.RecruitmentService;
import com.wagglex2.waggle.domain.common.type.RecruitmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 모집 공고 마감 스케줄러
 *
 * <p>매일 0시 0분 0초에 실행되어, 마감일이 지난 공고를 자동으로
 * {@link RecruitmentStatus#CLOSED CLOSED(마감)} 상태로 변경</p>
 *
 * <p>실제 상태 변경 로직은 {@link RecruitmentService#closeExpiredRecruitments()}에서 수행</p>
 */
@Component
@RequiredArgsConstructor
public class RecruitmentClosingScheduler {

    private final RecruitmentService  recruitmentService;

    // 매일 자정(00:00:00)에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleRecruitmentClosing() {
        recruitmentService.closeExpiredRecruitments();
    }
}
