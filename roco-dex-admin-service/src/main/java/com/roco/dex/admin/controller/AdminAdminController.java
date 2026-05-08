package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.Admin;
import com.roco.dex.admin.mapper.AdminMapper;
import com.roco.dex.admin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/admin/admin")
@RequiredArgsConstructor
public class AdminAdminController {

    private final AdminMapper adminMapper;
    private final OperationLogService operationLogService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/list")
    public Result<PageResult<Admin>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        // Check super role
        AdminContext.AdminInfo currentAdmin = AdminContext.get();
        if (!"super".equals(currentAdmin.getRole())) {
            return Result.error(403, "权限不足");
        }

        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Admin::getUsername, keyword)
                    .or()
                    .like(Admin::getNickname, keyword);
        }
        wrapper.orderByDesc(Admin::getId);
        Page<Admin> result = adminMapper.selectPage(new Page<>(page, size), wrapper);

        // Clear passwords
        for (Admin admin : result.getRecords()) {
            admin.setPassword(null);
        }

        return Result.success(PageResult.of(result));
    }

    @PostMapping
    public Result<Admin> create(@RequestBody Admin admin, HttpServletRequest request) {
        // Check super role
        AdminContext.AdminInfo currentAdmin = AdminContext.get();
        if (!"super".equals(currentAdmin.getRole())) {
            return Result.error(403, "权限不足");
        }

        // Check username uniqueness
        Long count = adminMapper.selectCount(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, admin.getUsername()));
        if (count > 0) {
            return Result.error(400, "用户名已存在");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        admin.setStatus(1);
        admin.setCreateTime(OffsetDateTime.now());
        adminMapper.insert(admin);

        // Log operation
        operationLogService.log(currentAdmin.getId(), currentAdmin.getUsername(), "管理员管理", "新增",
                admin.getId().toString(), "新增管理员: " + admin.getUsername(), getClientIp(request));

        admin.setPassword(null);
        return Result.success(admin);
    }

    @PutMapping("/{id}")
    public Result<Admin> update(@PathVariable Long id, @RequestBody Admin admin, HttpServletRequest request) {
        // Check super role
        AdminContext.AdminInfo currentAdmin = AdminContext.get();
        if (!"super".equals(currentAdmin.getRole())) {
            return Result.error(403, "权限不足");
        }

        Admin existing = adminMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "管理员不存在");
        }

        if (StringUtils.hasText(admin.getNickname())) {
            existing.setNickname(admin.getNickname());
        }
        if (StringUtils.hasText(admin.getRole())) {
            existing.setRole(admin.getRole());
        }
        if (admin.getStatus() != null) {
            existing.setStatus(admin.getStatus());
        }
        if (StringUtils.hasText(admin.getPassword())) {
            existing.setPassword(passwordEncoder.encode(admin.getPassword()));
        }

        adminMapper.updateById(existing);

        // Log operation
        operationLogService.log(currentAdmin.getId(), currentAdmin.getUsername(), "管理员管理", "修改",
                id.toString(), "修改管理员: " + existing.getUsername(), getClientIp(request));

        existing.setPassword(null);
        return Result.success(existing);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        // Check super role
        AdminContext.AdminInfo currentAdmin = AdminContext.get();
        if (!"super".equals(currentAdmin.getRole())) {
            return Result.error(403, "权限不足");
        }

        // Cannot delete self
        if (currentAdmin.getId().equals(id)) {
            return Result.error(400, "不能删除自己");
        }

        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            return Result.error(404, "管理员不存在");
        }

        adminMapper.deleteById(id);

        // Log operation
        operationLogService.log(currentAdmin.getId(), currentAdmin.getUsername(), "管理员管理", "删除",
                id.toString(), "删除管理员: " + admin.getUsername(), getClientIp(request));

        return Result.success();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
