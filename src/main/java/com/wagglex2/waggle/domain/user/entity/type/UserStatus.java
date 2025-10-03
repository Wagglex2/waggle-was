package com.wagglex2.waggle.domain.user.entity.type;


/**
 * 사용자 상태를 나타내는 열거형(Enum)
 *
 * <p>회원 계정의 활성 여부 및 제재 상태를 관리한다.</p>
 *
 * <ul>
 *   <li>{@link #ACTIVE} : 정상적으로 활동 중인 상태</li>
 *   <li>{@link #WITHDRAWN} : 사용자가 자발적으로 탈퇴한 상태 (Soft Delete)</li>
 * </ul>
 *
 * <p><b>사용처:</b></p>
 * <ul>
 *   <li>{@code User} 엔티티의 {@code status} 필드</li>
 *   <li>회원 탈퇴/정지/차단 처리 로직</li>
 *   <li>조회 시 "탈퇴한 회원" 또는 "정지된 회원" 표시 처리</li>
 * </ul>
 */
public enum UserStatus {
    ACTIVE,
    WITHDRAWN
}
