package com.tieba.forum.repository;

import com.tieba.forum.entity.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ForumRepository extends JpaRepository<Forum, Long> {
    Optional<Forum> findByName(String name);
    boolean existsByName(String name);
}
