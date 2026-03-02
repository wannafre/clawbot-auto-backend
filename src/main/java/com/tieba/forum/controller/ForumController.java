package com.tieba.forum.controller;

import com.tieba.forum.dto.ApiResponse;
import com.tieba.forum.entity.Forum;
import com.tieba.forum.repository.ForumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/forums")
@CrossOrigin(origins = "*")
public class ForumController {
    
    @Autowired
    private ForumRepository forumRepository;
    
    @GetMapping
    public ApiResponse<List<Forum>> list() {
        return ApiResponse.success(forumRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Forum> get(@PathVariable Long id) {
        Forum forum = forumRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("板块不存在"));
        return ApiResponse.success(forum);
    }
    
    @PostMapping
    public ApiResponse<Forum> create(@RequestBody Forum forum) {
        if (forumRepository.existsByName(forum.getName())) {
            return ApiResponse.error("板块名称已存在");
        }
        forum.setPostCount(0);
        forum.setFollowCount(0);
        forumRepository.save(forum);
        return ApiResponse.success(forum);
    }
}
