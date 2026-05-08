package com.roco.dex.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@TableName("roc_item")
public class Item {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String category;
    private String imageUrl;
    private String description;
    private String obtainMethod;
    private OffsetDateTime createTime;
}
