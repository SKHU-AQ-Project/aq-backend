package com.example.aq.domain.interaction;

import com.example.aq.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Page<Bookmark> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<Bookmark> findByUserAndTypeOrderByCreatedAtDesc(User user, BookmarkType type, Pageable pageable);

    Optional<Bookmark> findByUserAndTypeAndTargetId(User user, BookmarkType type, Long targetId);

    boolean existsByUserAndTypeAndTargetId(User user, BookmarkType type, Long targetId);
}
