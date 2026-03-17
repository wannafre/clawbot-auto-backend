package com.tieba.forum.repository;

import com.tieba.forum.entity.Reply;
import com.tieba.forum.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    List<Reply> findByPostOrderByFloorAsc(Post post);
    long countByPost(Post post);
    
    // 查询未删除的回复
    List<Reply> findByPostAndIsDeletedFalseOrderByFloorAsc(Post post);
}
