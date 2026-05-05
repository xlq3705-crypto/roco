package com.roco.dex.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.entity.Item;
import com.roco.dex.mapper.ItemMapper;
import com.roco.dex.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;

    @Override
    @Cacheable(value = "itemList", key = "#category + ':' + #page + ':' + #size")
    public IPage<Item> list(String category, int page, int size) {
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(category)) {
            wrapper.eq(Item::getCategory, category);
        }
        wrapper.orderByDesc(Item::getId);
        return itemMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Cacheable(value = "itemDetail", key = "#id")
    public Item getDetail(Long id) {
        return itemMapper.selectById(id);
    }

    @Override
    public IPage<Item> search(String keyword, int page, int size) {
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Item::getName, keyword)
                .or()
                .like(Item::getDescription, keyword);
        wrapper.orderByDesc(Item::getId);
        return itemMapper.selectPage(new Page<>(page, size), wrapper);
    }
}
