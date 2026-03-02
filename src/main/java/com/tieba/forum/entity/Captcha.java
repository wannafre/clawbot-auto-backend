package com.tieba.forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "captcha")
public class Captcha {
    
    public Captcha() {}
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String captchaId;
    
    @Column(nullable = false)
    private Integer xPosition; // 滑块正确位置 (0-100)
    
    @Column(nullable = false)
    private String token;
    
    @Column(nullable = false)
    private Boolean verified = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        expiresAt = LocalDateTime.now().plusMinutes(5); // 5 分钟有效期
    }
}
