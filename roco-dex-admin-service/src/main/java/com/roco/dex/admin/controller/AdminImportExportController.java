package com.roco.dex.admin.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.*;
import com.roco.dex.admin.mapper.*;
import com.roco.dex.admin.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminImportExportController {

    private final PetMapper petMapper;
    private final SkillMapper skillMapper;
    private final ItemMapper itemMapper;
    private final EquipmentMapper equipmentMapper;
    private final PetSkillMapper petSkillMapper;
    private final OperationLogService operationLogService;

    @GetMapping("/export/{module}")
    public void export(@PathVariable String module, HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode(module + "_export", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        switch (module) {
            case "pet" -> {
                List<Pet> list = petMapper.selectList(null);
                EasyExcel.write(response.getOutputStream(), Pet.class).sheet("宠物").doWrite(list);
            }
            case "skill" -> {
                List<Skill> list = skillMapper.selectList(null);
                EasyExcel.write(response.getOutputStream(), Skill.class).sheet("技能").doWrite(list);
            }
            case "item" -> {
                List<Item> list = itemMapper.selectList(null);
                EasyExcel.write(response.getOutputStream(), Item.class).sheet("道具").doWrite(list);
            }
            case "equipment" -> {
                List<Equipment> list = equipmentMapper.selectList(null);
                EasyExcel.write(response.getOutputStream(), Equipment.class).sheet("装备").doWrite(list);
            }
            default -> {
                response.setStatus(400);
                response.getWriter().write("{\"code\":400,\"message\":\"不支持的模块\"}");
                return;
            }
        }

        AdminContext.AdminInfo admin = AdminContext.get();
        operationLogService.log(admin.getId(), admin.getUsername(), "导入导出", "导出",
                module, "导出模块: " + module, getClientIp(request));
    }

    @PostMapping("/import/{module}")
    @Transactional
    public Result<Integer> importData(@PathVariable String module, @RequestParam("file") MultipartFile file,
                                       HttpServletRequest request) {
        try {
            int count = 0;
            switch (module) {
                case "pet" -> {
                    List<Pet> list = new ArrayList<>();
                    EasyExcel.read(file.getInputStream(), Pet.class, new ReadListener<Pet>() {
                        @Override public void invoke(Pet data, AnalysisContext context) { list.add(data); }
                        @Override public void doAfterAllAnalysed(AnalysisContext context) {}
                    }).sheet().doRead();
                    for (Pet pet : list) {
                        pet.setId(null);
                        if (pet.getCreateTime() == null) pet.setCreateTime(OffsetDateTime.now());
                        petMapper.insert(pet);
                    }
                    count = list.size();
                }
                case "skill" -> {
                    List<Skill> list = new ArrayList<>();
                    EasyExcel.read(file.getInputStream(), Skill.class, new ReadListener<Skill>() {
                        @Override public void invoke(Skill data, AnalysisContext context) { list.add(data); }
                        @Override public void doAfterAllAnalysed(AnalysisContext context) {}
                    }).sheet().doRead();
                    for (Skill skill : list) {
                        skill.setId(null);
                        if (skill.getCreateTime() == null) skill.setCreateTime(OffsetDateTime.now());
                        skillMapper.insert(skill);
                    }
                    count = list.size();
                }
                case "item" -> {
                    List<Item> list = new ArrayList<>();
                    EasyExcel.read(file.getInputStream(), Item.class, new ReadListener<Item>() {
                        @Override public void invoke(Item data, AnalysisContext context) { list.add(data); }
                        @Override public void doAfterAllAnalysed(AnalysisContext context) {}
                    }).sheet().doRead();
                    for (Item item : list) {
                        item.setId(null);
                        if (item.getCreateTime() == null) item.setCreateTime(OffsetDateTime.now());
                        itemMapper.insert(item);
                    }
                    count = list.size();
                }
                case "equipment" -> {
                    List<Equipment> list = new ArrayList<>();
                    EasyExcel.read(file.getInputStream(), Equipment.class, new ReadListener<Equipment>() {
                        @Override public void invoke(Equipment data, AnalysisContext context) { list.add(data); }
                        @Override public void doAfterAllAnalysed(AnalysisContext context) {}
                    }).sheet().doRead();
                    for (Equipment equipment : list) {
                        equipment.setId(null);
                        if (equipment.getCreateTime() == null) equipment.setCreateTime(OffsetDateTime.now());
                        equipmentMapper.insert(equipment);
                    }
                    count = list.size();
                }
                default -> {
                    return Result.error(400, "不支持的模块");
                }
            }

            AdminContext.AdminInfo admin = AdminContext.get();
            operationLogService.log(admin.getId(), admin.getUsername(), "导入导出", "导入",
                    module, "导入模块: " + module + ", 数量: " + count, getClientIp(request));

            return Result.success(count);
        } catch (IOException e) {
            log.error("导入失败", e);
            return Result.error(500, "导入失败: " + e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
