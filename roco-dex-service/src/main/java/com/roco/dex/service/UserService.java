package com.roco.dex.service;

import com.roco.dex.dto.ForgotPasswordDTO;
import com.roco.dex.dto.LoginDTO;
import com.roco.dex.dto.RegisterDTO;
import com.roco.dex.dto.UserVO;

public interface UserService {

    UserVO register(RegisterDTO dto);

    UserVO login(LoginDTO dto);

    UserVO getUserInfo(Long userId);

    void resetPassword(ForgotPasswordDTO dto);
}
