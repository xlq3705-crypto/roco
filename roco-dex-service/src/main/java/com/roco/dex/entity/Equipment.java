package com.roco.dex.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("roc_equipment")
public class Equipment {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private String imageUrl;
    private String description;
    private String effect;
    private String obtainMethod;
    private LocalDateTime createTime;
}
