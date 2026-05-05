package com.roco.dex.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.entity.Equipment;
import com.roco.dex.mapper.EquipmentMapper;
import com.roco.dex.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentMapper equipmentMapper;

    @Override
    @Cacheable(value = "equipmentList", key = "#category + ':' + #page + ':' + #size")
    public IPage<Equipment> list(String category, int page, int size) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(category)) {
            wrapper.eq(Equipment::getCategory, category);
        }
        wrapper.orderByDesc(Equipment::getId);
        return equipmentMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Cacheable(value = "equipmentDetail", key = "#id")
    public Equipment getDetail(Long id) {
        return equipmentMapper.selectById(id);
    }

    @Override
    public IPage<Equipment> search(String keyword, int page, int size) {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Equipment::getName, keyword)
                .or()
                .like(Equipment::getDescription, keyword);
        wrapper.orderByDesc(Equipment::getId);
        return equipmentMapper.selectPage(new Page<>(page, size), wrapper);
    }
}
