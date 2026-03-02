package com.tieba.forum.controller;

import com.tieba.forum.dto.ApiResponse;
import com.tieba.forum.entity.*;
import com.tieba.forum.repository.*;
import com.tieba.forum.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/interact")
@CrossOrigin(origins = "*")
public class InteractionController {
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 点赞/取消点赞
     */
    @PostMapping("/post/{postId}/like")
    public ApiResponse<Map<String, Object>> toggleLike(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("用户不存在"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException("帖子不存在"));
        
        var existing = postLikeRepository.findByUserAndPost(user, post);
        Map<String, Object> data = new HashMap<>();
        
        if (existing.isPresent()) {
            // 取消点赞
            postLikeRepository.delete(existing.get());
            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            data.put("liked", false);
        } else {
            // 点赞
            PostLike like = new PostLike();
            like.setUser(user);
            like.setPost(post);
            postLikeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            data.put("liked", true);
        }
        
        postRepository.save(post);
        data.put("likeCount", post.getLikeCount());
        
        return ApiResponse.success(data);
    }
    
    /**
     * 收藏/取消收藏
     */
    @PostMapping("/post/{postId}/favorite")
    public ApiResponse<Map<String, Object>> toggleFavorite(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("用户不存在"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException("帖子不存在"));
        
        var existing = favoriteRepository.findByUserAndPost(user, post);
        Map<String, Object> data = new HashMap<>();
        
        if (existing.isPresent()) {
            // 取消收藏
            favoriteRepository.delete(existing.get());
            data.put("favorited", false);
        } else {
            // 收藏
            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setPost(post);
            favoriteRepository.save(favorite);
            data.put("favorited", true);
        }
        
        return ApiResponse.success(data);
    }
    
    /**
     * 获取用户的收藏列表
     */
    @GetMapping("/favorites")
    public ApiResponse<?> getUserFavorites(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("用户不存在"));
        var favorites = favoriteRepository.findByUser(user);
        return ApiResponse.success(favorites);
    }
}
