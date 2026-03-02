package com.tieba.forum.controller;

import com.tieba.forum.dto.ApiResponse;
import com.tieba.forum.entity.Captcha;
import com.tieba.forum.repository.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/captcha")
@CrossOrigin(origins = "*")
public class CaptchaController {
    
    @Autowired
    private CaptchaRepository captchaRepository;
    
    /**
     * 生成滑块验证码
     */
    @GetMapping("/generate")
    public ApiResponse<Map<String, Object>> generate() {
        Captcha captcha = new Captcha();
        captcha.setCaptchaId(UUID.randomUUID().toString());
        captcha.setXPosition((int) (Math.random() * 80) + 10); // 10-90 随机位置
        captcha.setToken(UUID.randomUUID().toString());
        captcha.setVerified(false);
        
        captchaRepository.save(captcha);
        
        Map<String, Object> data = new HashMap<>();
        data.put("captchaId", captcha.getCaptchaId());
        data.put("token", captcha.getToken());
        // 不返回正确位置，只返回给前端生成图片用
        data.put("bgImage", "https://picsum.photos/300/150?random=" + System.currentTimeMillis());
        data.put("sliderSize", 40); // 滑块大小
        
        return ApiResponse.success(data);
    }
    
    /**
     * 验证滑块
     */
    @PostMapping("/verify")
    public ApiResponse<Map<String, Object>> verify(@RequestBody Map<String, Object> req) {
        String captchaId = (String) req.get("captchaId");
        Integer xPosition = (Integer) req.get("xPosition");
        
        Captcha captcha = captchaRepository.findByCaptchaId(captchaId)
            .orElseThrow(() -> new RuntimeException("验证码不存在"));
        
        // 检查是否过期
        if (captcha.getExpiresAt().isBefore(java.time.LocalDateTime.now())) {
            Map<String, Object> data = new HashMap<>();
            data.put("success", false);
            data.put("message", "验证码已过期");
            return ApiResponse.success(data);
        }
        
        // 验证位置（允许±5 像素误差）
        boolean success = Math.abs(captcha.getXPosition() - xPosition) <= 5;
        
        if (success) {
            captcha.setVerified(true);
            captchaRepository.save(captcha);
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "验证通过" : "验证失败，请重试");
        data.put("token", success ? captcha.getToken() : null);
        
        return ApiResponse.success(data);
    }
}
