package com.roco.dex.controller;

import com.roco.dex.common.JwtUtil;
import com.roco.dex.common.Result;
import com.roco.dex.dto.ForgotPasswordDTO;
import com.roco.dex.dto.LoginDTO;
import com.roco.dex.dto.RegisterDTO;
import com.roco.dex.dto.UserVO;
import com.roco.dex.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户认证")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO dto) {
        try {
            return Result.success(userService.register(dto));
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody LoginDTO dto) {
        try {
            return Result.success(userService.login(dto));
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserVO> me(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || !jwtUtil.isTokenValid(token)) {
            return Result.error(401, "未登录");
        }
        Long userId = jwtUtil.getUserId(token);
        try {
            return Result.success(userService.getUserInfo(userId));
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Operation(summary = "找回密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody ForgotPasswordDTO dto) {
        try {
            userService.resetPassword(dto);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}
