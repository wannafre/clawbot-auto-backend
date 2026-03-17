package com.tieba.forum.controller;

import com.tieba.forum.dto.ApiResponse;
import com.tieba.forum.dto.UserDto;
import com.tieba.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 搜索用户
     */
    @GetMapping("/users")
    public ApiResponse<List<UserDto>> searchUsers(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return ApiResponse.error("搜索关键词不能为空");
        }
        
        if (keyword.length() > 50) {
            return ApiResponse.error("搜索关键词过长");
        }
        
        List<UserDto> users = userRepository.searchUserDtos(keyword.trim());
        return ApiResponse.success(users);
    }
}
