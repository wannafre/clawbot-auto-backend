package com.tieba.forum.dto;

import lombok.Data;

@Data
public class ReplyRequest {
    private String content;
    private Long postId;
    private Long parentId;
    private Integer floor;
}
