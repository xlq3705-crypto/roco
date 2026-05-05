package com.roco.dex.dto;

import lombok.Data;

@Data
public class ForgotPasswordDTO {
    private String username;
    private String nickname;
    private String newPassword;
}
