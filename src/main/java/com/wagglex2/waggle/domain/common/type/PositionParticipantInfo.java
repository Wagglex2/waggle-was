package com.wagglex2.waggle.domain.common.type;

import com.wagglex2.waggle.domain.project.entity.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

/**
 * 포지션 별 모집 인원 현황을 나타내는 VO 클래스
 * <p>
 * <ul>
 *   <li>position: 포지션</li>
 *   <li>maxParticipants: 최대 모집 인원</li>
 *   <li>currParticipants: 현재 모집된 인원</li>
 * </ul>
 * <p>
 * {@link PositionType} 정보가 필요한 {@link Project}에서 사용된다.
 *
 * @see ParticipantInfo
 * @author 오재민
 */
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Getter
@Embeddable
public class PositionParticipantInfo {
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private final PositionType position;

    private ParticipantInfo participantInfo;
}
