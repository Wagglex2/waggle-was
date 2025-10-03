package com.wagglex2.waggle.domain.user.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.auth.dto.request.SignUpRequestDto;
import com.wagglex2.waggle.domain.user.dto.request.PasswordRequestDto;
import com.wagglex2.waggle.domain.user.dto.request.UserUpdateRequestDto;
import com.wagglex2.waggle.domain.user.dto.response.UserResponseDto;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.entity.type.UserStatus;
import com.wagglex2.waggle.domain.user.repository.UserRepository;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public User findByIdWithSkills(Long id) {
        return userRepository.findByIdWithSkills(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 회원가입을 처리한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *     <li>아이디(username) 중복 검사</li>
     *     <li>이메일(email) 중복 검사</li>
     *     <li>닉네임(nickname) 중복 검사</li>
     *     <li>DTO를 User 엔티티로 변환 (비밀번호 암호화 포함)</li>
     *     <li>User 저장 및 생성된 식별자(ID) 반환</li>
     * </ol>
     *
     * @param dto 회원가입 요청 DTO
     * @return 생성된 User의 식별자(ID)
     * @throws BusinessException
     *         <ul>
     *             <li>{@link ErrorCode#DUPLICATED_USERNAME} : 이미 존재하는 아이디</li>
     *             <li>{@link ErrorCode#DUPLICATED_EMAIL} : 이미 등록된 이메일</li>
     *             <li>{@link ErrorCode#DUPLICATED_NICKNAME} : 이미 사용 중인 닉네임</li>
     *         </ul>
     */
    @Override
    @Transactional
    public Long signUp(SignUpRequestDto dto) {
        if (existsByUsername(dto.username())) {
            throw new BusinessException(ErrorCode.DUPLICATED_USERNAME);
        }

        if (existsByEmail(dto.email())) {
            throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);
        }

        if (existsByNickname(dto.nickname())) {
            throw new BusinessException(ErrorCode.DUPLICATED_NICKNAME);
        }

        User user = dto.toEntity(passwordEncoder);
        return userRepository.save(user).getId();
    }

    /**
     * 사용자 비밀번호를 변경한다.
     *
     * <p>처리 순서:</p>
     * <ol>
     *   <li>userId로 사용자를 조회한다.</li>
     *   <li>입력된 기존 비밀번호(dto.old())가 DB에 저장된 비밀번호와 일치하는지 검증한다.</li>
     *   <li>검증 실패 시 PASSWORD_NOT_MATCH 예외를 발생시킨다.</li>
     *   <li>새로운 비밀번호(dto.newPassword())를 암호화(encode)한다.</li>
     *   <li>엔티티(User)의 changePassword 메서드를 호출하여 암호화된 비밀번호로 교체한다.</li>
     *   <li>비밀번호 변경 성공 로그를 남긴다.</li>
     * </ol>
     *
     * @param userId 비밀번호를 변경할 사용자 ID
     * @param dto 비밀번호 변경 요청 DTO (old, newPassword, passwordConfirm 포함)
     * @throws BusinessException PASSWORD_NOT_MATCH (기존 비밀번호 불일치 시)
     */
    @Override
    @Transactional
    @PreAuthorize("#userId == authentication.principal.userId")
    public void changePassword(Long userId, PasswordRequestDto dto) {
        User user = findById(userId);

        if (!passwordEncoder.matches(dto.old(), user.getPassword())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }

        String encodedPassword = passwordEncoder.encode(dto.newPassword());
        user.changePassword(encodedPassword);

        log.info("비밀번호 변경 성공 : userId = {}", userId);
    }

    /**
     * 주어진 userId를 기반으로 사용자 정보를 조회하고 DTO로 변환한다.
     *
     * <p>처리 흐름:</p>
     * <ol>
     *   <li>{@code findByIdWithSkills(userId)} 호출 → User 엔티티 + skills 컬렉션을 Fetch Join으로 로딩</li>
     *   <li>조회된 User 엔티티를 {@code UserResponseDto.from(user)}로 변환</li>
     *   <li>정상 조회 시 info 로그 출력</li>
     * </ol>
     *
     * @param userId 조회할 사용자의 식별자
     * @return UserResponseDto 변환 객체
     */
    @Override
    @PreAuthorize("#userId == authentication.principal.userId")
    public UserResponseDto getUserInfo(Long userId) {
        User user = findByIdWithSkills(userId);

        log.info("회원정보 불러오기 성공 : userId = {}", userId);
        return UserResponseDto.from(user);
    }

    /**
     * 사용자 프로필 정보를 업데이트한다.
     *
     * <p><b>처리 흐름:</b></p>
     * <ol>
     *   <li>userId를 기반으로 사용자 엔티티 조회 (skills 연관관계 포함)</li>
     *   <li>수정 요청 DTO({@link UserUpdateRequestDto})의 각 필드를 검사</li>
     *   <li>null이 아니거나 유효한 값이 존재하는 경우에만 해당 엔티티 필드 업데이트</li>
     *   <li>skills는 전체 덮어쓰기(Replace) 방식으로 처리</li>
     *   <li>업데이트 완료 후 엔티티를 {@link UserResponseDto}로 변환하여 반환</li>
     * </ol>
     *
     * <p><b>트랜잭션 동작:</b></p>
     * <ul>
     *   <li>{@code @Transactional} 적용으로 메서드 실행 중 발생한 모든 변경 사항은 하나의 트랜잭션으로 처리</li>
     *   <li>예외 발생 시 롤백되어 데이터 일관성을 보장</li>
     * </ul>
     *
     * @param userId 수정 대상 사용자 ID
     * @param dto    수정 요청 DTO ({@link UserUpdateRequestDto})
     * @return 최신 사용자 정보를 담은 {@link UserResponseDto}
     */
    @Override
    @Transactional
    @PreAuthorize("#userId == authentication.principal.userId")
    public UserResponseDto updateUserInfo(Long userId, UserUpdateRequestDto dto) {
        User user = findByIdWithSkills(userId);

        if (StringUtils.hasText(dto.nickname())) {
            boolean duplicated = existsByNickname(dto.nickname());
            if (duplicated && !user.getNickname().equals(dto.nickname())) {
                throw new BusinessException(ErrorCode.DUPLICATED_NICKNAME);
            }
            user.updateNickname(dto.nickname());
        }

        if (dto.grade() != null) {
            user.updateGrade(dto.grade());
        }

        if (dto.position() != null) {
            user.updatePosition(dto.position());
        }

        if (dto.skills() != null) {
            user.updateSkills(dto.skills());
        }

        if (StringUtils.hasText(dto.shortIntro())) {
            user.updateShortIntro(dto.shortIntro());
        }

        log.info("회원정보 수정 성공 : userId = {}", userId);

        return UserResponseDto.from(user);
    }

    /**
     * 회원 탈퇴 서비스 로직
     *
     * <p><b>처리 순서:</b></p>
     * <ol>
     *   <li>userId 기준으로 사용자 엔티티 조회</li>
     *   <li>입력받은 비밀번호(rawPassword)와 저장된 비밀번호 해시 비교</li>
     *   <li>이미 탈퇴된 회원인지 상태 검증</li>
     *   <li>탈퇴 처리 (Soft Delete: {@link UserStatus#WITHDRAWN})</li>
     *   <li>Redis에 저장된 Refresh Token 제거 (재로그인 차단)</li>
     *   <li>로그 출력</li>
     * </ol>
     *
     * @param userId      탈퇴할 사용자 ID
     * @param rawPassword 클라이언트에서 전달한 평문 비밀번호
     * @throws BusinessException 비밀번호 불일치, 이미 탈퇴된 회원인 경우
     */
    @Override
    @Transactional
    @PreAuthorize("#userId == authentication.principal.userId")
    public void withdraw(Long userId, String rawPassword) {
        User user = findById(userId);

        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new BusinessException(ErrorCode.ALREADY_WITHDRAWN_USER);
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.MISMATCHED_PASSWORD);
        }

        user.withdraw();

        String redisKey = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.delete(redisKey);

        log.info("회원 탈퇴 성공 : userId = {}", userId);
    }
}
