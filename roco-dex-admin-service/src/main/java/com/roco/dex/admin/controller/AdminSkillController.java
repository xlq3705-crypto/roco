package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.Skill;
import com.roco.dex.admin.mapper.SkillMapper;
import com.roco.dex.admin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/admin/skill")
@RequiredArgsConstructor
public class AdminSkillController {

    private final SkillMapper skillMapper;
    private final OperationLogService operationLogService;

    @GetMapping("/list")
    public Result<PageResult<Skill>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Skill::getName, keyword)
                    .or()
                    .like(Skill::getAttr, keyword);
        }
        wrapper.orderByDesc(Skill::getId);
        Page<Skill> result = skillMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(PageResult.of(result));
    }

    @GetMapping("/{id}")
    public Result<Skill> getById(@PathVariable Long id) {
        Skill skill = skillMapper.selectById(id);
        return Result.success(skill);
    }

    @PostMapping
    public Result<Skill> create(@RequestBody Skill skill, HttpServletRequest request) {
        skill.setCreateTime(OffsetDateTime.now());
        skillMapper.insert(skill);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "技能管理", "新增",
                skill.getId().toString(), "新增技能: " + skill.getName(), getClientIp(request));

        return Result.success(skill);
    }

    @PutMapping("/{id}")
    public Result<Skill> update(@PathVariable Long id, @RequestBody Skill skill, HttpServletRequest request) {
        Skill existing = skillMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "技能不存在");
        }

        skill.setId(id);
        skillMapper.updateById(skill);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "技能管理", "修改",
                id.toString(), "修改技能: " + skill.getName(), getClientIp(request));

        return Result.success(skill);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Skill skill = skillMapper.selectById(id);
        if (skill == null) {
            return Result.error(404, "技能不存在");
        }

        skillMapper.deleteById(id);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "技能管理", "删除",
                id.toString(), "删除技能: " + skill.getName(), getClientIp(request));

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
