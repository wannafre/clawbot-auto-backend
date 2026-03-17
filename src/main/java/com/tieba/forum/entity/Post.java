package com.tieba.forum.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "posts")
public class Post {
    
    public Post() {}
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forum forum;
    
    @Column(nullable = false)
    private Integer viewCount = 0;
    
    @Column(nullable = false)
    private Integer replyCount = 0;
    
    @Column(nullable = false)
    private Integer likeCount = 0;
    
    @Column(nullable = false)
    private Boolean isTop = false;
    
    @Column(nullable = false)
    private Boolean isGood = false;
    
    @Column(nullable = false)
    private Boolean isDeleted = false;
    
    private LocalDateTime deletedAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
