package com.wagglex2.waggle.domain.common.dto.request;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.common.type.ParticipantInfo;
import jakarta.validation.constraints.Min;

public record ParticipantInfoUpdateRequestDto(
        @Min(value = 1, message = "모집 인원수는 1 이상이어야 합니다.")
        int maxParticipants,

        @Min(value = 0, message = "현재 인원수는 0 이상이어야 합니다.")
        int currParticipants
) {
    public ParticipantInfoUpdateRequestDto {
        if (maxParticipants < currParticipants) {
            throw new BusinessException(ErrorCode.MAX_PARTICIPANTS_EXCEEDED);
        }
    }

    public static ParticipantInfo toParticipantInfo(ParticipantInfoUpdateRequestDto dto) {
        return new ParticipantInfo(dto.maxParticipants(), dto.currParticipants());
    }
}
