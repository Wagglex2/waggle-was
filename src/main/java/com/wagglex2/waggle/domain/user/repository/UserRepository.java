package com.wagglex2.waggle.domain.user.repository;

import com.wagglex2.waggle.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByNickname(String nickname);

    /**
     * 주어진 userId에 해당하는 User 엔티티를 조회하면서
     * skills 컬렉션을 Fetch Join으로 함께 가져온다.
     *
     * @param id 조회할 User의 식별자
     * @return id에 해당하는 User와 그 User의 skills 컬렉션을 포함한 Optional
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.skills WHERE u.id = :id")
    Optional<User> findByIdWithSkills(@Param("id") Long id);
}
