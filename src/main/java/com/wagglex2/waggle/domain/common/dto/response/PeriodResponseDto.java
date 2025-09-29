package com.wagglex2.waggle.domain.common.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wagglex2.waggle.domain.common.type.Period;

import java.time.LocalDate;

public record PeriodResponseDto(
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate
) {
    public static PeriodResponseDto from(Period period) {
        return new PeriodResponseDto(period.startDate(), period.endDate());
    }
}
