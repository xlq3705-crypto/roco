package com.roco.dex.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("roc_attribute")
public class Attribute {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String attackAttr;
    private String defenseAttr;
    private BigDecimal multiplier;
}
