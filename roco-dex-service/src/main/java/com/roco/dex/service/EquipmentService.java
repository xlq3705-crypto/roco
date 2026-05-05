package com.roco.dex.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.roco.dex.entity.Equipment;

public interface EquipmentService {

    IPage<Equipment> list(String category, int page, int size);

    Equipment getDetail(Long id);

    IPage<Equipment> search(String keyword, int page, int size);
}
