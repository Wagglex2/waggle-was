package com.wagglex2.waggle.domain.common.dto.request;

import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import com.wagglex2.waggle.domain.common.type.PositionParticipantInfo;
import com.wagglex2.waggle.domain.common.type.PositionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PositionInfoCreationRequestDto(
        @NotNull(message = "포지션이 누락되었습니다.")
        PositionType position,

        @Min(value = 1, message = "모집 인원수는 1 이상이어야 합니다.")
        int maxParticipants
) {
    public static PositionParticipantInfo to(PositionInfoCreationRequestDto dto) {
        return new PositionParticipantInfo(
                dto.position(),
                new ParticipantInfo(dto.maxParticipants())
        );
    }
}
