# 洛克王国图鉴 - 后台管理系统设计文档

## 1. 概述

为洛克王国图鉴小程序构建配套的后台管理系统，实现游戏数据管理、用户管理、数据导入导出、数据统计与日志等功能。

## 2. 整体架构

```
roco-dex-admin (Vue3 + Element Plus)  ←→  roco-dex-admin-service (Spring Boot)  ←→  Supabase PostgreSQL
roco-dex-miniapp (uni-app)            ←→  roco-dex-service (Spring Boot)         ↗
```

- 两个后端服务连接同一个 Supabase PostgreSQL 数据库，各自独立部署
- 管理后台前端通过 Nginx 反向代理
- 管理后台 API 前缀: `/api/admin/*`

## 3. 技术选型

### 前端 `roco-dex-admin`

| 技术 | 用途 |
|------|------|
| Vue 3 + TypeScript | 框架 |
| Vite | 构建工具 |
| Element Plus | UI 组件库 |
| Vue Router | 路由 |
| Pinia | 状态管理 |
| Axios | HTTP 请求 |
| ECharts | 图表（仪表盘） |
| SheetJS (xlsx) | 数据导入导出 |

### 后端 `roco-dex-admin-service`

| 技术 | 用途 |
|------|------|
| Spring Boot 3.2 | 框架 |
| Java 17 | 语言 |
| MyBatis-Plus 3.5.5 | ORM |
| PostgreSQL (Supabase) | 数据库 |
| JWT (jjwt) | 认证 |
| BCrypt | 密码加密 |
| EasyExcel | 数据导入导出 |
| Caffeine | 缓存 |
| Knife4j | API 文档 |

## 4. 数据库设计

### 4.1 新增表

#### roc_admin（管理员表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGSERIAL PK | 主键 |
| username | VARCHAR(50) UNIQUE | 用户名 |
| password | VARCHAR(255) | BCrypt 加密密码 |
| nickname | VARCHAR(50) | 昵称 |
| role | VARCHAR(20) | 角色: super/normal |
| status | SMALLINT | 状态: 0禁用 1启用 |
| last_login_time | TIMESTAMPTZ | 最后登录时间 |
| create_time | TIMESTAMPTZ | 创建时间 |

#### roc_operation_log（操作日志表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGSERIAL PK | 主键 |
| admin_id | BIGINT | 管理员ID |
| admin_username | VARCHAR(50) | 管理员用户名 |
| module | VARCHAR(50) | 模块名 |
| action | VARCHAR(50) | 操作: create/update/delete/import/export |
| target_id | BIGINT | 操作目标ID |
| detail | TEXT | 操作详情 |
| ip | VARCHAR(50) | IP地址 |
| create_time | TIMESTAMPTZ | 创建时间 |

#### roc_login_log（登录日志表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGSERIAL PK | 主键 |
| admin_id | BIGINT | 管理员ID |
| username | VARCHAR(50) | 用户名 |
| ip | VARCHAR(50) | IP地址 |
| user_agent | VARCHAR(500) | 浏览器信息 |
| status | SMALLINT | 0失败 1成功 |
| create_time | TIMESTAMPTZ | 创建时间 |

### 4.2 复用现有表

roc_pet, roc_skill, roc_pet_skill, roc_item, roc_equipment, roc_attribute, roc_user

## 5. 页面结构

```
├── 登录页
├── 仪表盘（数据统计看板）
│   ├── 总览卡片（宠物/技能/道具/装备/用户数量）
│   ├── 近7天注册用户趋势图
│   └── 各属性宠物分布饼图
├── 游戏数据管理
│   ├── 宠物管理（表格 + 增删改查 + 技能关联管理）
│   ├── 技能管理（表格 + 增删改查）
│   ├── 道具管理（表格 + 增删改查）
│   ├── 装备管理（表格 + 增删改查）
│   └── 属性克制管理（表格 + 编辑倍率）
├── 用户管理
│   └── 用户列表（查看/禁用/删除）
├── 数据导入导出
│   ├── 导入（Excel/JSON 上传）
│   └── 导出（按模块导出 Excel）
├── 系统管理
│   ├── 管理员管理（增删改查，仅超级管理员）
│   ├── 登录日志
│   └── 操作日志
```

## 6. 权限模型

| 功能 | 超级管理员 (super) | 普通管理员 (normal) |
|------|:-:|:-:|
| 仪表盘 | ✅ | ✅ |
| 游戏数据 CRUD | ✅ | ✅ |
| 用户管理 | ✅ | ✅ |
| 数据导入导出 | ✅ | ✅ |
| 管理员管理 | ✅ | ❌ |
| 操作日志 | ✅ | ✅（仅自己） |
| 登录日志 | ✅ | ✅（仅自己） |

## 7. API 设计

### 7.1 认证

| Method | Endpoint | 说明 |
|--------|----------|------|
| POST | `/api/admin/auth/login` | 管理员登录 |
| POST | `/api/admin/auth/logout` | 退出登录 |
| GET | `/api/admin/auth/me` | 当前管理员信息 |

### 7.2 仪表盘

| Method | Endpoint | 说明 |
|--------|----------|------|
| GET | `/api/admin/dashboard/stats` | 统计数据 |
| GET | `/api/admin/dashboard/trend` | 近7天注册趋势 |
| GET | `/api/admin/dashboard/attr-dist` | 属性分布 |

### 7.3 游戏数据管理

宠物:
| Method | Endpoint | 说明 |
|--------|----------|------|
| GET | `/api/admin/pet/list` | 宠物列表（分页+筛选） |
| POST | `/api/admin/pet` | 新增宠物 |
| PUT | `/api/admin/pet/{id}` | 编辑宠物 |
| DELETE | `/api/admin/pet/{id}` | 删除宠物 |
| GET | `/api/admin/pet/{id}/skills` | 宠物关联技能 |
| POST | `/api/admin/pet/{id}/skills` | 设置宠物技能关联 |

技能、道具、装备同上结构。

属性克制:
| Method | Endpoint | 说明 |
|--------|----------|------|
| GET | `/api/admin/attribute/table` | 属性克制表 |
| PUT | `/api/admin/attribute/{id}` | 编辑克制倍率 |

### 7.4 用户管理

| Method | Endpoint | 说明 |
|--------|----------|------|
| GET | `/api/admin/user/list` | 用户列表 |
| PUT | `/api/admin/user/{id}/status` | 禁用/启用用户 |
| DELETE | `/api/admin/user/{id}` | 删除用户 |

### 7.5 数据导入导出

| Method | Endpoint | 说明 |
|--------|----------|------|
| POST | `/api/admin/import/{module}` | 批量导入 |
| GET | `/api/admin/export/{module}` | 批量导出 |

### 7.6 管理员管理（仅超级管理员）

| Method | Endpoint | 说明 |
|--------|----------|------|
| GET | `/api/admin/admin/list` | 管理员列表 |
| POST | `/api/admin/admin` | 新增管理员 |
| PUT | `/api/admin/admin/{id}` | 编辑管理员 |
| DELETE | `/api/admin/admin/{id}` | 删除管理员 |

### 7.7 日志

| Method | Endpoint | 说明 |
|--------|----------|------|
| GET | `/api/admin/log/login` | 登录日志 |
| GET | `/api/admin/log/operation` | 操作日志 |

## 8. 项目结构

```
roco/
├── roco-dex-miniapp/          # 小程序前端（已有）
├── roco-dex-service/          # 小程序后端（已有）
├── roco-dex-admin/            # 管理后台前端（新建）
│   ├── src/
│   │   ├── api/               # API 请求封装
│   │   ├── components/        # 公共组件
│   │   ├── layouts/           # 布局组件
│   │   ├── router/            # 路由
│   │   ├── stores/            # Pinia 状态
│   │   ├── views/             # 页面
│   │   │   ├── dashboard/
│   │   │   ├── pet/
│   │   │   ├── skill/
│   │   │   ├── item/
│   │   │   ├── equipment/
│   │   │   ├── attribute/
│   │   │   ├── user/
│   │   │   ├── import-export/
│   │   │   ├── admin/
│   │   │   ├── log/
│   │   │   └── login/
│   │   └── utils/
│   └── package.json
└── roco-dex-admin-service/    # 管理后台后端（新建）
    └── src/main/java/com/roco/dex/admin/
        ├── config/
        ├── controller/
        ├── service/
        ├── mapper/
        ├── entity/
        ├── dto/
        ├── interceptor/
        └── common/
```
