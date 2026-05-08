package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.*;
import com.roco.dex.admin.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final PetMapper petMapper;
    private final SkillMapper skillMapper;
    private final ItemMapper itemMapper;
    private final EquipmentMapper equipmentMapper;
    private final UserMapper userMapper;
    private final AttributeMapper attributeMapper;

    @GetMapping("/stats")
    public Result<Map<String, Long>> stats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("petCount", petMapper.selectCount(null));
        stats.put("skillCount", skillMapper.selectCount(null));
        stats.put("itemCount", itemMapper.selectCount(null));
        stats.put("equipmentCount", equipmentMapper.selectCount(null));
        stats.put("userCount", userMapper.selectCount(null));
        return Result.success(stats);
    }

    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            OffsetDateTime startOfDay = date.atStartOfDay().atOffset(ZoneOffset.UTC);
            OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .ge(User::getCreateTime, startOfDay)
                            .lt(User::getCreateTime, endOfDay));

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.toString());
            item.put("count", count);
            trend.add(item);
        }

        return Result.success(trend);
    }

    @GetMapping("/attr-dist")
    public Result<List<Map<String, Object>>> attrDist() {
        List<Map<String, Object>> dist = new ArrayList<>();

        // Get all pets and count by mainAttr
        List<Pet> pets = petMapper.selectList(null);
        Map<String, Long> attrCount = new HashMap<>();
        for (Pet pet : pets) {
            String attr = pet.getMainAttr();
            if (attr != null && !attr.isEmpty()) {
                attrCount.merge(attr, 1L, Long::sum);
            }
        }

        for (Map.Entry<String, Long> entry : attrCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("attr", entry.getKey());
            item.put("count", entry.getValue());
            dist.add(item);
        }

        // Sort by count descending
        dist.sort((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")));

        return Result.success(dist);
    }
}
