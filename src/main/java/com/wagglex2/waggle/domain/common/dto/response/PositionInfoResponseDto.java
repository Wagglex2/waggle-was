package com.wagglex2.waggle.domain.common.dto.response;

import com.wagglex2.waggle.domain.common.type.PositionParticipantInfo;
import com.wagglex2.waggle.domain.common.type.PositionType;

public record PositionInfoResponseDto(
        PositionType position,
        ParticipantInfoResponseDto participantInfo
) {
    public static PositionInfoResponseDto from(PositionParticipantInfo info) {
        return new PositionInfoResponseDto(
                info.getPosition(),
                ParticipantInfoResponseDto.from(info.getParticipantInfo())
        );
    }
}
