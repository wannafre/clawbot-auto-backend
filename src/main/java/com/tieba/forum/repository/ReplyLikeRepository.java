package com.tieba.forum.repository;

import com.tieba.forum.entity.ReplyLike;
import com.tieba.forum.entity.User;
import com.tieba.forum.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, Long> {
    Optional<ReplyLike> findByUserAndReply(User user, Reply reply);
    List<ReplyLike> findByReply(Reply reply);
    long countByReply(Reply reply);
}
