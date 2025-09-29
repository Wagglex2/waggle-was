package com.wagglex2.waggle.domain.common.dto.request;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.type.Period;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PeriodRequestDto(
        @NotNull(message = "시작일이 누락되었습니다.")
        LocalDate startDate,

        @NotNull(message = "종료일이 누락되었습니다.")
        @FutureOrPresent(message = "종료일은 오늘 이후여야 합니다.")
        LocalDate endDate
) {
    public PeriodRequestDto {
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(
                    ErrorCode.INVALID_DATE_RANGE,
                    "종료일은 시작일 이후여야 합니다."
            );
        }
    }

    public static Period toPeriod(PeriodRequestDto dto) {
        return new Period(dto.startDate, dto.endDate);
    }
}
