package com.roco.dex.admin.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AdminVO {

    private Long id;
    private String username;
    private String nickname;
    private String role;
    private Integer status;
    private OffsetDateTime lastLoginTime;
    private String token;
}
