package com.wagglex2.waggle.domain.common.dto.response;

import com.wagglex2.waggle.domain.common.type.ParticipantInfo;

public record ParticipantInfoResponseDto(
        int maxParticipants,
        int currParticipants
) {
    public static ParticipantInfoResponseDto fromParticipantInfo(ParticipantInfo info) {
        return new ParticipantInfoResponseDto(info.getMaxParticipants(), info.getCurrParticipants());
    }
}
