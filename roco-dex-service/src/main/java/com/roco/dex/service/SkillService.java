package com.roco.dex.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.roco.dex.entity.Skill;

public interface SkillService {

    IPage<Skill> list(String attr, int page, int size);

    Skill getDetail(Long id);

    IPage<Skill> search(String keyword, int page, int size);
}
