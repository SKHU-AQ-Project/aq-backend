package com.example.aq.app.user.repository;

import com.example.aq.app.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByNickname(String nickname);
    
    boolean existsByEmail(String email);
    
    boolean existsByNickname(String nickname);
    
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    java.util.List<User> findAllActiveUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    Long countActiveUsers();
    
    @Query("SELECT u FROM User u WHERE u.enabled = true ORDER BY u.points DESC")
    java.util.List<User> findTopUsersByPoints();
}
