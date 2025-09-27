package com.wagglex2.waggle.domain.user.service.serviceImpl;

import com.wagglex2.waggle.common.error.ErrorCode;
import com.wagglex2.waggle.common.exception.BusinessException;
import com.wagglex2.waggle.domain.auth.dto.request.SignUpRequestDto;
import com.wagglex2.waggle.domain.user.entity.User;
import com.wagglex2.waggle.domain.user.repository.UserRepository;
import com.wagglex2.waggle.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
}
