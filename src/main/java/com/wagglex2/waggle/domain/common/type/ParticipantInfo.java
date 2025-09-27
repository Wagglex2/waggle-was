package com.wagglex2.waggle.domain.common.type;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 모집 인원 현황을 나타내는 VO 클래스
 * <p>
 * <ul>
 *   <li>maxParticipants: 최대 모집 인원</li>
 *   <li>currParticipants: 현재 모집된 인원</li>
 * </ul>
 * <p>
 * {@link PositionType} 정보가 필요 없는 Assignment와 Study에서 사용된다.
 *
 * @see PositionParticipantInfo
 * @author 오재민
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class ParticipantInfo {
    @Min(value = 1, message = "모집 인원수는 1 이상이어야 합니다.")
    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @Min(value = 0, message = "현재 인원수는 0 이상이어야 합니다.")
    @Column(name = "curr_participants", nullable = false)
    private int currParticipants = 0;

    public ParticipantInfo(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}
