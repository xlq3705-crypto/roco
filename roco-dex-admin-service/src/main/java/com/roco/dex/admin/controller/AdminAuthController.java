package com.roco.dex.admin.controller;

import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.dto.AdminLoginDTO;
import com.roco.dex.admin.dto.AdminVO;
import com.roco.dex.admin.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public Result<AdminVO> login(@RequestBody AdminLoginDTO dto, HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        AdminVO vo = adminAuthService.login(dto, ip, userAgent);
        return Result.success(vo);
    }

    @GetMapping("/me")
    public Result<AdminVO> me() {
        AdminVO vo = adminAuthService.getCurrentAdmin();
        return Result.success(vo);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
