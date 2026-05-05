package com.roco.dex.controller;

import com.roco.dex.common.PageResult;
import com.roco.dex.common.Result;
import com.roco.dex.entity.Skill;
import com.roco.dex.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "技能管理")
@RestController
@RequestMapping("/api/skill")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @Operation(summary = "技能列表")
    @GetMapping("/list")
    public Result<PageResult<Skill>> list(
            @Parameter(description = "属性筛选") @RequestParam(required = false) String attr,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(skillService.list(attr, page, size)));
    }

    @Operation(summary = "技能详情")
    @GetMapping("/{id}")
    public Result<Skill> detail(@PathVariable Long id) {
        Skill skill = skillService.getDetail(id);
        if (skill == null) {
            return Result.error(404, "技能不存在");
        }
        return Result.success(skill);
    }

    @Operation(summary = "搜索技能")
    @GetMapping("/search")
    public Result<PageResult<Skill>> search(
            @Parameter(description = "关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(skillService.search(keyword, page, size)));
    }
}
