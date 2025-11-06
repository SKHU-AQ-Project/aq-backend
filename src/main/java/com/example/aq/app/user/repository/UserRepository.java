package com.example.aq.app.user.repository;

import com.example.aq.app.user.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @EntityGraph(attributePaths = {"interests"})
    Optional<User> findById(Long id);
    
    @EntityGraph(attributePaths = {"interests"})
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    
    @EntityGraph(attributePaths = {"interests"})
    @Query("SELECT u FROM User u WHERE u.nickname = :nickname")
    Optional<User> findByNickname(@Param("nickname") String nickname);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.enabled = true")
    boolean existsByEmailAndEnabled(@Param("email") String email);
    
    boolean existsByNickname(String nickname);
    
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.nickname = :nickname AND u.enabled = true")
    boolean existsByNicknameAndEnabled(@Param("nickname") String nickname);
    
    @EntityGraph(attributePaths = {"interests"})
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    java.util.List<User> findAllActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    Long countActiveUsers();
    
    @EntityGraph(attributePaths = {"interests"})
    @Query("SELECT u FROM User u WHERE u.enabled = true ORDER BY u.points DESC")
    java.util.List<User> findTopUsersByPoints();
}
