package com.wagglex2.waggle.domain.user.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.auth.dto.request.SignUpRequestDto;
import com.wagglex2.waggle.domain.user.dto.request.PasswordRequestDto;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.repository.UserRepository;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    public void changePassword(Long userId, PasswordRequestDto dto) {
        User user = findById(userId);

        if (!passwordEncoder.matches(dto.old(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        String encodedPassword = passwordEncoder.encode(dto.newPassword());
        user.changePassword(encodedPassword);

        log.info("비밀번호 변경 성공 : userId = {}", userId);
    }
}
