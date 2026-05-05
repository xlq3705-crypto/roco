package com.roco.dex.dto;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String token;
}
