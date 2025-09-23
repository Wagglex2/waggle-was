package com.wagglex2.waggle.domain.common.type;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public record Period(
        @Column(name = "start_date", nullable = false)
        LocalDate startDate,

        @Column(name = "end_date", nullable = false)
        LocalDate endDate
) { }
