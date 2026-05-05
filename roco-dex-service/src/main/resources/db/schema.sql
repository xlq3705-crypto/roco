CREATE DATABASE IF NOT EXISTS roco_dex DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE roco_dex;

-- 宠物表
CREATE TABLE IF NOT EXISTS roc_pet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '宠物名',
    pet_no VARCHAR(20) COMMENT '编号',
    main_attr VARCHAR(20) COMMENT '主属性',
    sub_attr VARCHAR(20) COMMENT '副属性',
    image_url VARCHAR(255) COMMENT '图片地址',
    hp INT COMMENT '精力',
    attack INT COMMENT '攻击',
    defense INT COMMENT '防御',
    sp_attack INT COMMENT '魔攻',
    sp_defense INT COMMENT '魔防',
    speed INT COMMENT '速度',
    description TEXT COMMENT '简介',
    obtain_method TEXT COMMENT '获取方式',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '宠物表';

-- 技能表
CREATE TABLE IF NOT EXISTS roc_skill (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '技能名',
    attr VARCHAR(20) COMMENT '属性',
    type VARCHAR(10) COMMENT '类型: 物理/魔法/变化',
    power INT COMMENT '威力',
    pp INT COMMENT 'PP',
    accuracy INT COMMENT '命中率',
    description TEXT COMMENT '效果描述',
    image_url VARCHAR(255) COMMENT '图片地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '技能表';

-- 宠物-技能关联表
CREATE TABLE IF NOT EXISTS roc_pet_skill (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    skill_id BIGINT NOT NULL COMMENT '技能ID',
    learn_level INT DEFAULT 1 COMMENT '学习等级',
    INDEX idx_pet_id (pet_id),
    INDEX idx_skill_id (skill_id)
) COMMENT '宠物-技能关联表';

-- 道具表
CREATE TABLE IF NOT EXISTS roc_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '道具名',
    category VARCHAR(20) COMMENT '分类: 进化道具/活动道具/其他',
    image_url VARCHAR(255) COMMENT '图片地址',
    description TEXT COMMENT '描述',
    obtain_method TEXT COMMENT '获取方式',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '道具表';

-- 装备表
CREATE TABLE IF NOT EXISTS roc_equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '装备名',
    category VARCHAR(20) COMMENT '分类',
    image_url VARCHAR(255) COMMENT '图片地址',
    description TEXT COMMENT '描述',
    effect TEXT COMMENT '效果说明',
    obtain_method TEXT COMMENT '获取方式',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '装备表';

-- 属性克制表
CREATE TABLE IF NOT EXISTS roc_attribute (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    attack_attr VARCHAR(20) NOT NULL COMMENT '攻击属性',
    defense_attr VARCHAR(20) NOT NULL COMMENT '防御属性',
    multiplier DECIMAL(3,2) COMMENT '倍率: 0/0.5/1/2',
    INDEX idx_attack (attack_attr),
    INDEX idx_defense (defense_attr)
) COMMENT '属性克制表';

-- 进化链表
CREATE TABLE IF NOT EXISTS roc_evolution_chain (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chain_name VARCHAR(50) COMMENT '进化链名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '进化链表';

-- 进化详情表
CREATE TABLE IF NOT EXISTS roc_evolution_detail (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    chain_id BIGINT NOT NULL COMMENT '进化链ID',
    pet_id BIGINT NOT NULL COMMENT '宠物ID',
    stage INT COMMENT '阶段: 1/2/3',
    evolve_condition TEXT COMMENT '进化条件',
    INDEX idx_chain_id (chain_id)
) COMMENT '进化详情表';

-- 自然性格表
CREATE TABLE IF NOT EXISTS roc_nature (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL COMMENT '性格名',
    increase VARCHAR(20) COMMENT '增加属性',
    decrease VARCHAR(20) COMMENT '减少属性',
    description VARCHAR(100) COMMENT '描述'
) COMMENT '自然性格表';
