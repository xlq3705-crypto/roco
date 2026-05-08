package com.roco.dex.admin.controller;

import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.Admin;
import com.roco.dex.admin.mapper.AdminMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/init")
@RequiredArgsConstructor
public class InitController {

    private final JdbcTemplate jdbcTemplate;
    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/setup")
    public Result<String> setup() {
        // Create tables
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS roc_admin (
                id BIGSERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                nickname VARCHAR(50),
                role VARCHAR(20) NOT NULL DEFAULT 'normal',
                status SMALLINT NOT NULL DEFAULT 1,
                last_login_time TIMESTAMPTZ,
                create_time TIMESTAMPTZ DEFAULT NOW()
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS roc_operation_log (
                id BIGSERIAL PRIMARY KEY,
                admin_id BIGINT,
                admin_username VARCHAR(50),
                module VARCHAR(50),
                action VARCHAR(50),
                target_id VARCHAR(50),
                detail TEXT,
                ip VARCHAR(50),
                create_time TIMESTAMPTZ DEFAULT NOW()
            )
        """);

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS roc_login_log (
                id BIGSERIAL PRIMARY KEY,
                admin_id BIGINT,
                username VARCHAR(50),
                ip VARCHAR(50),
                user_agent VARCHAR(500),
                status SMALLINT NOT NULL DEFAULT 1,
                create_time TIMESTAMPTZ DEFAULT NOW()
            )
        """);

        // Add status column to roc_user if not exists
        try {
            jdbcTemplate.execute("ALTER TABLE roc_user ADD COLUMN IF NOT EXISTS status SMALLINT NOT NULL DEFAULT 1");
        } catch (Exception e) {
            // Column may already exist, ignore
        }

        // Insert default super admin if not exists
        Admin existing = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, "admin"));
        if (existing == null) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("超级管理员");
            admin.setRole("super");
            admin.setStatus(1);
            adminMapper.insert(admin);
        }

        return Result.success("初始化成功，默认管理员: admin / admin123");
    }
}
