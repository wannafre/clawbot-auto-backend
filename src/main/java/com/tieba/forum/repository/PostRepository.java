package com.tieba.forum.repository;

import com.tieba.forum.entity.Post;
import com.tieba.forum.entity.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByForum(Forum forum, Pageable pageable);
    Page<Post> findByOrderByCreatedAtDesc(Pageable pageable);
}
