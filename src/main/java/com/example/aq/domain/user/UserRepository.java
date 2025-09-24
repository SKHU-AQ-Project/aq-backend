package com.example.aq.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND u.nickname LIKE %:nickname%")
    Page<User> findByNicknameContaining(@Param("nickname") String nickname, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' AND :interest MEMBER OF u.interests")
    List<User> findByInterestsContaining(@Param("interest") String interest);

    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE' ORDER BY u.points DESC")
    Page<User> findTopUsersByPoints(Pageable pageable);
}
