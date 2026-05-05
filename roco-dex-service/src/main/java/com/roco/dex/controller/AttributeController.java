package com.roco.dex.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roco.dex.common.Result;
import com.roco.dex.entity.Attribute;
import com.roco.dex.mapper.AttributeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "属性克制")
@RestController
@RequestMapping("/api/attribute")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeMapper attributeMapper;

    @Operation(summary = "属性克制表")
    @GetMapping("/table")
    public Result<List<Attribute>> table() {
        return Result.success(attributeMapper.selectList(null));
    }

    @Operation(summary = "初始化属性克制数据")
    @PostMapping("/init-data")
    public Result<String> initData() {
        // 先清空已有数据
        attributeMapper.delete(new LambdaQueryWrapper<>());

        String[][] data = {
            {"火","草","2.0"},{"火","冰","2.0"},{"火","虫","2.0"},{"火","机械","2.0"},
            {"火","水","0.5"},{"火","火","0.5"},{"火","土","0.5"},{"火","龙","0.5"},
            {"水","火","2.0"},{"水","土","2.0"},
            {"水","水","0.5"},{"水","草","0.5"},{"水","龙","0.5"},
            {"草","水","2.0"},{"草","土","2.0"},
            {"草","火","0.5"},{"草","草","0.5"},{"草","翼","0.5"},{"草","虫","0.5"},{"草","龙","0.5"},{"草","机械","0.5"},
            {"电","水","2.0"},{"电","翼","2.0"},
            {"电","草","0.5"},{"电","电","0.5"},{"电","土","0.0"},{"电","龙","0.5"},
            {"冰","草","2.0"},{"冰","土","2.0"},{"冰","翼","2.0"},{"冰","龙","2.0"},
            {"冰","火","0.5"},{"冰","水","0.5"},{"冰","冰","0.5"},{"冰","机械","0.5"},
            {"土","火","2.0"},{"土","电","2.0"},{"土","机械","2.0"},
            {"土","水","0.5"},{"土","草","0.5"},{"土","翼","0.0"},
            {"翼","草","2.0"},{"翼","虫","2.0"},
            {"翼","电","0.5"},{"翼","冰","0.5"},{"翼","土","0.0"},
            {"萌","萌","0.5"},{"萌","机械","0.5"},{"萌","幽灵","0.0"},
            {"虫","草","2.0"},{"虫","萌","2.0"},{"虫","恶魔","2.0"},
            {"虫","火","0.5"},{"虫","翼","0.5"},{"虫","机械","0.5"},
            {"幽灵","萌","2.0"},{"幽灵","幽灵","2.0"},
            {"幽灵","普通","0.0"},{"幽灵","恶魔","0.5"},
            {"龙","龙","2.0"},{"龙","机械","0.5"},
            {"恶魔","萌","2.0"},{"恶魔","幽灵","2.0"},
            {"恶魔","恶魔","0.5"},{"恶魔","机械","0.5"},
            {"机械","冰","2.0"},{"机械","萌","0.5"},
            {"机械","火","0.5"},{"机械","水","0.5"},{"机械","电","0.5"},{"机械","机械","0.5"},
            {"光","幽灵","2.0"},{"光","恶魔","2.0"},{"光","光","0.5"},
            {"普通","幽灵","0.0"}
        };

        for (String[] row : data) {
            Attribute attr = new Attribute();
            attr.setAttackAttr(row[0]);
            attr.setDefenseAttr(row[1]);
            attr.setMultiplier(new BigDecimal(row[2]));
            attributeMapper.insert(attr);
        }

        return Result.success("初始化成功，共插入" + data.length + "条数据");
    }
}
