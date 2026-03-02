package com.tieba.forum.controller;

import com.tieba.forum.dto.*;
import com.tieba.forum.entity.User;
import com.tieba.forum.entity.Captcha;
import com.tieba.forum.repository.UserRepository;
import com.tieba.forum.repository.CaptchaRepository;
import com.tieba.forum.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CaptchaRepository captchaRepository;
    
    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, Object> req) {
        String username = (String) req.get("username");
        String password = (String) req.get("password");
        String captchaToken = (String) req.get("captchaToken");
        
        // 验证验证码
        if (captchaToken != null) {
            Captcha captcha = captchaRepository.findByToken(captchaToken)
                .orElseThrow(() -> new RuntimeException("验证码不存在"));
            if (!captcha.getVerified() || captcha.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
                return ApiResponse.error("验证码无效或已过期");
            }
        }
        
        if (username == null || username.trim().isEmpty()) {
            return ApiResponse.error("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            return ApiResponse.error("密码至少 6 位");
        }
        
        if (userRepository.existsByUsername(username)) {
            return ApiResponse.error("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(passwordEncoder.encode(password));
        user.setAvatar("/avatars/default.png");
        user.setSignature("这个人很懒，什么都没写");
        
        userRepository.save(user);
        
        // 自动生成登录 token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("avatar", user.getAvatar());
        data.put("level", user.getLevel());
        data.put("token", token);
        
        return ApiResponse.success(data);
    }
    
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ApiResponse.error("密码错误");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("avatar", user.getAvatar());
        data.put("level", user.getLevel());
        data.put("token", token);
        
        return ApiResponse.success(data);
    }
    
    @GetMapping("/info/{id}")
    public ApiResponse<User> userInfo(@PathVariable Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPassword(null);
        return ApiResponse.success(user);
    }
}
