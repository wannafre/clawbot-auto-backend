package com.tieba.forum.repository;

import com.tieba.forum.entity.PostLike;
import com.tieba.forum.entity.Post;
import com.tieba.forum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserAndPost(User user, Post post);
    List<PostLike> findByUser(User user);
    long countByPost(Post post);
    void deleteByUserAndPost(User user, Post post);
}
