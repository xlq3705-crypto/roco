package com.roco.dex.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.JwtUtil;
import com.roco.dex.admin.dto.AdminLoginDTO;
import com.roco.dex.admin.dto.AdminVO;
import com.roco.dex.admin.entity.Admin;
import com.roco.dex.admin.entity.LoginLog;
import com.roco.dex.admin.mapper.AdminMapper;
import com.roco.dex.admin.mapper.LoginLogMapper;
import com.roco.dex.admin.service.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminMapper adminMapper;
    private final LoginLogMapper loginLogMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AdminVO login(AdminLoginDTO dto, String ip, String userAgent) {
        // Find admin by username
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, dto.getUsername()));

        // Record login log
        LoginLog loginLog = new LoginLog();
        loginLog.setUsername(dto.getUsername());
        loginLog.setIp(ip);
        loginLog.setUserAgent(userAgent);
        loginLog.setCreateTime(OffsetDateTime.now());

        if (admin == null) {
            loginLog.setStatus(0);
            loginLogMapper.insert(loginLog);
            throw new RuntimeException("用户名或密码错误");
        }

        // Verify password
        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            loginLog.setAdminId(admin.getId());
            loginLog.setStatus(0);
            loginLogMapper.insert(loginLog);
            throw new RuntimeException("用户名或密码错误");
        }

        // Check status
        if (admin.getStatus() != null && admin.getStatus() != 1) {
            loginLog.setAdminId(admin.getId());
            loginLog.setStatus(0);
            loginLogMapper.insert(loginLog);
            throw new RuntimeException("账号已被禁用");
        }

        // Update last login time
        admin.setLastLoginTime(OffsetDateTime.now());
        adminMapper.updateById(admin);

        // Record successful login
        loginLog.setAdminId(admin.getId());
        loginLog.setStatus(1);
        loginLogMapper.insert(loginLog);

        // Generate JWT token
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), admin.getRole());

        // Build VO
        AdminVO vo = new AdminVO();
        vo.setId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setNickname(admin.getNickname());
        vo.setRole(admin.getRole());
        vo.setStatus(admin.getStatus());
        vo.setLastLoginTime(admin.getLastLoginTime());
        vo.setToken(token);
        return vo;
    }

    @Override
    public AdminVO getCurrentAdmin() {
        AdminContext.AdminInfo adminInfo = AdminContext.get();
        if (adminInfo == null) {
            throw new RuntimeException("未登录");
        }

        Admin admin = adminMapper.selectById(adminInfo.getId());
        if (admin == null) {
            throw new RuntimeException("管理员不存在");
        }

        AdminVO vo = new AdminVO();
        vo.setId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setNickname(admin.getNickname());
        vo.setRole(admin.getRole());
        vo.setStatus(admin.getStatus());
        vo.setLastLoginTime(admin.getLastLoginTime());
        return vo;
    }
}
