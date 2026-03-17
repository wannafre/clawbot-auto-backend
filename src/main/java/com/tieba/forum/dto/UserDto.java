package com.tieba.forum.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String avatar;
    private Integer level;
    private Integer exp;
    private LocalDateTime createdAt;
}
