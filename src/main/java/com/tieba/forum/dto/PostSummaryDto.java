package com.tieba.forum.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSummaryDto {
    private Long id;
    private String title;
    private Integer viewCount;
    private Integer replyCount;
    private Integer likeCount;
    private Boolean isTop;
    private Boolean isGood;
    private LocalDateTime createdAt;
    private Long authorId;
    private String authorName;
    private Long forumId;
    private String forumName;
}
