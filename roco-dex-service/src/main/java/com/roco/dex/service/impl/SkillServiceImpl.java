package com.roco.dex.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.entity.Skill;
import com.roco.dex.mapper.SkillMapper;
import com.roco.dex.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillMapper skillMapper;

    @Override
    @Cacheable(value = "skillList", key = "#attr + ':' + #page + ':' + #size")
    public IPage<Skill> list(String attr, int page, int size) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(attr)) {
            wrapper.eq(Skill::getAttr, attr);
        }
        wrapper.orderByDesc(Skill::getId);
        return skillMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Cacheable(value = "skillDetail", key = "#id")
    public Skill getDetail(Long id) {
        return skillMapper.selectById(id);
    }

    @Override
    public IPage<Skill> search(String keyword, int page, int size) {
        LambdaQueryWrapper<Skill> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Skill::getName, keyword)
                .or()
                .like(Skill::getDescription, keyword);
        wrapper.orderByDesc(Skill::getId);
        return skillMapper.selectPage(new Page<>(page, size), wrapper);
    }
}
