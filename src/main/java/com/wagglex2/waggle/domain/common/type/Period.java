package com.wagglex2.waggle.domain.common.type;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Embeddable
public record Period(
        @NotNull(message = "시작일이 누락되었습니다.")
        @Column(name = "start_date", nullable = false)
        LocalDate startDate,

        @NotNull(message = "종료일이 누락되었습니다.")
        @Column(name = "end_date", nullable = false)
        LocalDate endDate
) {
    public void validate() {
        if (endDate.isBefore(LocalDate.now())) {
            throw new BusinessException(
                    ErrorCode.INVALID_DATE_RANGE,
                    "종료일은 오늘 이후여야 합니다."
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new BusinessException(
                    ErrorCode.INVALID_DATE_RANGE,
                    "종료일은 시작일 이후여야 합니다."
            );
        }
    }
}
