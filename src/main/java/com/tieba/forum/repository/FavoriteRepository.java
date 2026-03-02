package com.tieba.forum.repository;

import com.tieba.forum.entity.Favorite;
import com.tieba.forum.entity.Post;
import com.tieba.forum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndPost(User user, Post post);
    List<Favorite> findByUser(User user);
    long countByPost(Post post);
    void deleteByUserAndPost(User user, Post post);
}
