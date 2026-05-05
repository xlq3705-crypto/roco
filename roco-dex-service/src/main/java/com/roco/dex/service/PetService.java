package com.roco.dex.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.roco.dex.dto.PetDTO;
import com.roco.dex.entity.Pet;

public interface PetService {

    IPage<Pet> list(String attr, int page, int size);

    PetDTO getDetail(Long id);

    IPage<Pet> search(String keyword, int page, int size);
}
