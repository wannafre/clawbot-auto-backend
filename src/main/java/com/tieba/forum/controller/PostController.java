package com.tieba.forum.controller;

import com.tieba.forum.dto.*;
import com.tieba.forum.entity.*;
import com.tieba.forum.repository.*;
import com.tieba.forum.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class PostController {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private ForumRepository forumRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReplyRepository replyRepository;
    
    @GetMapping
    public ApiResponse<Page<PostSummaryDto>> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(postRepository.findPostSummaries(pageable));
    }
    
    @GetMapping("/summary")
    public ApiResponse<Page<PostSummaryDto>> listSummary(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(postRepository.findPostSummaries(pageable));
    }
    
    @GetMapping("/forum/{forumId}")
    public ApiResponse<Page<PostSummaryDto>> byForum(
        @PathVariable Long forumId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Forum forum = forumRepository.findById(forumId)
            .orElseThrow(() -> new RuntimeException("板块不存在"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(postRepository.findPostSummariesByForum(forum, pageable));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Post> get(@PathVariable Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new BusinessException("帖子不存在"));
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
        return ApiResponse.success(post);
    }
    
    @PostMapping
    public ApiResponse<Post> create(@RequestBody PostRequest req, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        User author = userRepository.findById(userId).orElse(null);
        if (author == null) {
            return ApiResponse.error("用户不存在");
        }
        
        Forum forum = forumRepository.findById(req.getForumId())
            .orElseThrow(() -> new RuntimeException("板块不存在"));
        
        Post post = new Post();
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setAuthor(author);
        post.setForum(forum);
        post.setViewCount(0);
        post.setReplyCount(0);
        post.setLikeCount(0);
        post.setIsTop(false);
        post.setIsGood(false);
        
        postRepository.save(post);
        
        forum.setPostCount(forum.getPostCount() + 1);
        forumRepository.save(forum);
        
        return ApiResponse.success(post);
    }
    
    @GetMapping("/{id}/replies")
    public ApiResponse<List<Reply>> replies(@PathVariable Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new BusinessException("帖子不存在"));
        return ApiResponse.success(replyRepository.findByPostOrderByFloorAsc(post));
    }
    
    // 编辑帖子
    @PutMapping("/{id}")
    public ApiResponse<Post> update(@PathVariable Long id, @RequestBody PostRequest req, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new BusinessException("帖子不存在"));
        
        // 只有作者可以编辑
        if (!post.getAuthor().getId().equals(userId)) {
            return ApiResponse.error(403, "无权限编辑此帖子");
        }
        
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        postRepository.save(post);
        
        return ApiResponse.success(post);
    }
    
    // 删除帖子（软删除）
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new BusinessException("帖子不存在"));
        
        // 只有作者或管理员可以删除
        if (!post.getAuthor().getId().equals(userId)) {
            return ApiResponse.error(403, "无权限删除此帖子");
        }
        
        post.setIsDeleted(true);
        post.setDeletedAt(java.time.LocalDateTime.now());
        postRepository.save(post);
        
        return ApiResponse.success(null);
    }
    
    // 搜索帖子
    @GetMapping("/search")
    public ApiResponse<Page<Post>> search(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(postRepository.searchPosts(keyword, pageable));
    }
    
    // 热门帖子
    @GetMapping("/hot")
    public ApiResponse<Page<Post>> hot(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewCount"));
        return ApiResponse.success(postRepository.findByIsDeletedFalseOrderByViewCountDesc(pageable));
    }
    
    // 用户的帖子
    @GetMapping("/user/{userId}")
    public ApiResponse<Page<Post>> byUser(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(postRepository.findByAuthorIdAndIsDeletedFalse(userId, pageable));
    }
    
    @PostMapping("/{id}/replies")
    public ApiResponse<Reply> reply(@PathVariable Long id, @RequestBody ReplyRequest req, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new BusinessException("帖子不存在"));
        
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException("用户不存在"));
        
        Reply reply = new Reply();
        reply.setContent(req.getContent());
        reply.setAuthor(author);
        reply.setPost(post);
        
        if (req.getParentId() != null) {
            Reply parent = replyRepository.findById(req.getParentId())
                .orElseThrow(() -> new BusinessException("父回复不存在"));
            reply.setParent(parent);
        }
        
        long floor = replyRepository.countByPost(post) + 1;
        reply.setFloor((int)floor);
        reply.setLikeCount(0);
        
        replyRepository.save(reply);
        
        post.setReplyCount(post.getReplyCount() + 1);
        postRepository.save(post);
        
        return ApiResponse.success(reply);
    }
}
