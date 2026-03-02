package com.tieba.forum.controller;

import com.tieba.forum.dto.ApiResponse;
import com.tieba.forum.entity.CheckIn;
import com.tieba.forum.entity.User;
import com.tieba.forum.repository.CheckInRepository;
import com.tieba.forum.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/checkin")
@CrossOrigin(origins = "*")
public class CheckInController {
    
    @Autowired
    private CheckInRepository checkInRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 签到
     */
    @PostMapping
    public ApiResponse<Map<String, Object>> checkIn(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        LocalDate today = LocalDate.now();
        
        // 检查今天是否已签到
        if (checkInRepository.findByUserIdAndCheckInDate(userId, today).isPresent()) {
            Map<String, Object> data = new HashMap<>();
            data.put("alreadyCheckedIn", true);
            data.put("message", "今天已经签到过了哦～");
            return ApiResponse.success(data);
        }
        
        // 计算连续签到天数
        int continuousDays = 1;
        var lastCheckIn = checkInRepository.findTopByUserIdOrderByCheckInDateDesc(userId);
        if (lastCheckIn.isPresent()) {
            LocalDate lastDate = lastCheckIn.get().getCheckInDate();
            if (lastDate.equals(today.minusDays(1))) {
                continuousDays = lastCheckIn.get().getContinuousDays() + 1;
            }
        }
        
        // 创建签到记录
        CheckIn checkIn = new CheckIn();
        checkIn.setUser(user);
        checkIn.setCheckInDate(today);
        checkIn.setContinuousDays(continuousDays);
        checkIn.setTotalExp(10 + continuousDays); // 连续签到奖励更多
        
        checkInRepository.save(checkIn);
        
        // 更新用户经验
        user.setExp(user.getExp() + checkIn.getTotalExp());
        // 升级逻辑
        int newLevel = (int) Math.sqrt(user.getExp() / 100) + 1;
        if (newLevel > user.getLevel()) {
            user.setLevel(newLevel);
        }
        userRepository.save(user);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("exp", checkIn.getTotalExp());
        data.put("continuousDays", continuousDays);
        data.put("level", user.getLevel());
        data.put("message", continuousDays > 7 ? "🔥 连续签到 " + continuousDays + " 天！太厉害了！" : "签到成功！+" + checkIn.getTotalExp() + " 经验");
        
        return ApiResponse.success(data);
    }
    
    /**
     * 获取签到状态
     */
    @GetMapping("/status")
    public ApiResponse<Map<String, Object>> getStatus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        LocalDate today = LocalDate.now();
        boolean checkedInToday = checkInRepository.findByUserIdAndCheckInDate(userId, today).isPresent();
        
        var lastCheckIn = checkInRepository.findTopByUserIdOrderByCheckInDateDesc(userId);
        int continuousDays = lastCheckIn.map(CheckIn::getContinuousDays).orElse(0);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Map<String, Object> data = new HashMap<>();
        data.put("checkedInToday", checkedInToday);
        data.put("continuousDays", continuousDays);
        data.put("level", user.getLevel());
        data.put("exp", user.getExp());
        
        return ApiResponse.success(data);
    }
    
    /**
     * 获取签到记录
     */
    @GetMapping("/history")
    public ApiResponse<List<CheckIn>> getHistory(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ApiResponse.error(401, "请先登录");
        }
        
        var history = checkInRepository.findByUserIdOrderByCheckInDateDesc(userId);
        return ApiResponse.success(history);
    }
}
