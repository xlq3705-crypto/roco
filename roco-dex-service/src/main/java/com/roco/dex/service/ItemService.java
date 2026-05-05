package com.roco.dex.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.roco.dex.entity.Item;

public interface ItemService {

    IPage<Item> list(String category, int page, int size);

    Item getDetail(Long id);

    IPage<Item> search(String keyword, int page, int size);
}
