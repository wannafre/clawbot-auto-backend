package com.tieba.forum.dto;

import lombok.Data;

@Data
public class PostRequest {
    private String title;
    private String content;
    private Long forumId;
}
