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
    public ApiResponse<Page<Post>> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(postRepository.findByOrderByCreatedAtDesc(pageable));
    }
    
    @GetMapping("/forum/{forumId}")
    public ApiResponse<Page<Post>> byForum(
        @PathVariable Long forumId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        Forum forum = forumRepository.findById(forumId)
            .orElseThrow(() -> new RuntimeException("板块不存在"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ApiResponse.success(postRepository.findByForum(forum, pageable));
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
