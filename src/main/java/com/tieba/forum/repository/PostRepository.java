package com.tieba.forum.repository;

import com.tieba.forum.entity.Post;
import com.tieba.forum.entity.Forum;
import com.tieba.forum.dto.PostSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByForum(Forum forum, Pageable pageable);
    Page<Post> findByOrderByCreatedAtDesc(Pageable pageable);
    
    // 查询未删除的帖子
    Page<Post> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    Page<Post> findByForumAndIsDeletedFalse(Forum forum, Pageable pageable);
    
    // 搜索帖子
    @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);
    
    // 热门帖子（按浏览量）
    Page<Post> findByIsDeletedFalseOrderByViewCountDesc(Pageable pageable);
    
    // 用户发布的帖子
    Page<Post> findByAuthorIdAndIsDeletedFalse(Long userId, Pageable pageable);
    
    // 查询帖子列表摘要（不包含 content 大字段）
    @Query("SELECT new com.tieba.forum.dto.PostSummaryDto(" +
           "p.id, p.title, p.viewCount, p.replyCount, p.likeCount, p.isTop, p.isGood, p.createdAt, " +
           "p.author.id, p.author.username, " +
           "p.forum.id, p.forum.name) " +
           "FROM Post p WHERE p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<PostSummaryDto> findPostSummaries(Pageable pageable);
    
    // 查询板块帖子摘要
    @Query("SELECT new com.tieba.forum.dto.PostSummaryDto(" +
           "p.id, p.title, p.viewCount, p.replyCount, p.likeCount, p.isTop, p.isGood, p.createdAt, " +
           "p.author.id, p.author.username, " +
           "p.forum.id, p.forum.name) " +
           "FROM Post p WHERE p.forum = :forum AND p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<PostSummaryDto> findPostSummariesByForum(@Param("forum") Forum forum, Pageable pageable);
}
