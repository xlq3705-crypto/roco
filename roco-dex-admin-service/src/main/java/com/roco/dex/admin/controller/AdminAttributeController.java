package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.Attribute;
import com.roco.dex.admin.mapper.AttributeMapper;
import com.roco.dex.admin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin/attribute")
@RequiredArgsConstructor
public class AdminAttributeController {

    private final AttributeMapper attributeMapper;
    private final OperationLogService operationLogService;

    @GetMapping("/table")
    public Result<List<Attribute>> table() {
        List<Attribute> list = attributeMapper.selectList(new LambdaQueryWrapper<Attribute>()
                .orderByAsc(Attribute::getAttackAttr)
                .orderByAsc(Attribute::getDefenseAttr));
        return Result.success(list);
    }

    @PutMapping("/{id}")
    public Result<Attribute> update(@PathVariable Long id, @RequestBody Attribute attribute, HttpServletRequest request) {
        Attribute existing = attributeMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "属性克制关系不存在");
        }

        existing.setMultiplier(attribute.getMultiplier());
        attributeMapper.updateById(existing);

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "属性克制管理", "修改",
                id.toString(), "修改属性克制: " + existing.getAttackAttr() + " -> " + existing.getDefenseAttr()
                        + " = " + existing.getMultiplier(), getClientIp(request));

        return Result.success(existing);
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
