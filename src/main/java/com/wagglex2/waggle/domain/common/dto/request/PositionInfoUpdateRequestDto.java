package com.wagglex2.waggle.domain.common.dto.request;

import com.wagglex2.waggle.domain.common.type.PositionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record PositionInfoUpdateRequestDto(
        @NotNull(message = "포지션이 누락되었습니다.")
        PositionType position,

        @NotNull
        @Valid
        ParticipantInfoUpdateRequestDto participantInfo
) { }
