package com.wagglex2.waggle.domain.common.type;

import com.wagglex2.waggle.domain.common.dto.request.PositionInfoCreationRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;


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
 * @see PositionInfoCreationRequestDto
 * @author 오재민
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Getter
@Embeddable
public class ParticipantInfo {
    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @Column(name = "curr_participants", nullable = false)
    private int currParticipants = 0;

    public ParticipantInfo(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }
}
