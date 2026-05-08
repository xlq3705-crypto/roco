# 洛克王国图鉴 - 后台管理系统实施计划

> **For agentic workers:** Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为洛克王国图鉴小程序构建配套的后台管理系统，包含独立的 Spring Boot 后端和 Vue3 + Element Plus 前端。

**Architecture:** 新建 `roco-dex-admin-service`（Spring Boot 后端，端口8081）和 `roco-dex-admin`（Vue3 前端）两个独立项目，连接同一个 Supabase PostgreSQL 数据库。后端通过 JWT 拦截器实现管理员认证和角色鉴权。

**Tech Stack:** Spring Boot 3.2, Java 17, MyBatis-Plus 3.5.5, PostgreSQL, JWT, Vue 3, TypeScript, Element Plus, Vite, Pinia, ECharts

---

## Phase 1: 后端基础搭建

### Task 1: 创建后端项目骨架

**Files:**
- Create: `roco-dex-admin-service/pom.xml`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/AdminApplication.java`
- Create: `roco-dex-admin-service/src/main/resources/application.yml`

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
    </parent>
    <groupId>com.roco</groupId>
    <artifactId>roco-dex-admin-service</artifactId>
    <version>1.0.0</version>
    <name>roco-dex-admin-service</name>
    <description>洛克王国图鉴后台管理系统</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <jjwt.version>0.12.5</jjwt.version>
        <knife4j.version>4.4.0</knife4j.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-crypto</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.github.xiaoymin</groupId>
            <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
            <version>${knife4j.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>easyexcel</artifactId>
            <version>3.3.4</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建 application.yml**

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://db.qirlzzpmasmhkbezacro.supabase.co:5432/postgres?sslmode=require
    username: postgres
    password: 3CEp7gUHMNLss30b
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 3
      minimum-idle: 0
      connection-timeout: 30000
      idle-timeout: 30000
      max-lifetime: 120000
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=500,expireAfterWrite=30m

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto

jwt:
  secret: roco-dex-admin-secret-key-must-be-at-least-256-bits-long-for-hs256
  expiration: 86400000

springdoc:
  swagger-ui:
    path: /doc.html
  api-docs:
    path: /v3/api-docs

knife4j:
  enable: true
  setting:
    language: zh_cn
```

- [ ] **Step 3: 创建主启动类**

创建 `roco-dex-admin-service/src/main/java/com/roco/dex/admin/AdminApplication.java`:

```java
package com.roco.dex.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@MapperScan("com.roco.dex.admin.mapper")
@EnableCaching
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
```

- [ ] **Step 4: 验证项目能编译**

Run: `cd roco-dex-admin-service && mvn compile`
Expected: BUILD SUCCESS

---

### Task 2: 创建公共类（Result、PageResult、JwtUtil）

**Files:**
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/common/Result.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/common/PageResult.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/common/JwtUtil.java`

- [ ] **Step 1: 创建 Result.java**

```java
package com.roco.dex.admin.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
```

- [ ] **Step 2: 创建 PageResult.java**

```java
package com.roco.dex.admin.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long page;
    private long size;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPage(page.getCurrent());
        result.setSize(page.getSize());
        return result;
    }
}
```

- [ ] **Step 3: 创建 JwtUtil.java**

```java
package com.roco.dex.admin.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Long adminId, String username, String role) {
        return Jwts.builder()
                .subject(String.valueOf(adminId))
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getAdminId(String token) {
        return Long.valueOf(parseToken(token).getSubject());
    }

    public String getUsername(String token) {
        return parseToken(token).get("username", String.class);
    }

    public String getRole(String token) {
        return parseToken(token).get("role", String.class);
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add roco-dex-admin-service/
git commit -m "feat(admin): 创建后端项目骨架和公共类"
```

---

### Task 3: 创建配置类

**Files:**
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/config/MyBatisPlusConfig.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/config/CorsConfig.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/config/WebConfig.java`

- [ ] **Step 1: 创建 MyBatisPlusConfig.java**

```java
package com.roco.dex.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }
}
```

- [ ] **Step 2: 创建 CorsConfig.java**

```java
package com.roco.dex.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

- [ ] **Step 3: 创建 WebConfig.java（注册拦截器）**

```java
package com.roco.dex.admin.config;

import com.roco.dex.admin.interceptor.AdminAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/auth/login");
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add roco-dex-admin-service/src/main/java/com/roco/dex/admin/config/
git commit -m "feat(admin): 添加 MyBatis-Plus、CORS、拦截器配置"
```

---

### Task 4: 创建实体类

**Files:**
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/entity/Admin.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/entity/OperationLog.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/entity/LoginLog.java`

- [ ] **Step 1: 创建 Admin.java**

```java
package com.roco.dex.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@TableName("roc_admin")
public class Admin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String role;
    private Integer status;
    private OffsetDateTime lastLoginTime;
    private OffsetDateTime createTime;
}
```

- [ ] **Step 2: 创建 OperationLog.java**

```java
package com.roco.dex.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@TableName("roc_operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adminId;
    private String adminUsername;
    private String module;
    private String action;
    private Long targetId;
    private String detail;
    private String ip;
    private OffsetDateTime createTime;
}
```

- [ ] **Step 3: 创建 LoginLog.java**

```java
package com.roco.dex.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@TableName("roc_login_log")
public class LoginLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long adminId;
    private String username;
    private String ip;
    private String userAgent;
    private Integer status;
    private OffsetDateTime createTime;
}
```

- [ ] **Step 4: 创建数据库表（执行 SQL）**

```sql
CREATE TABLE IF NOT EXISTS roc_admin (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'normal',
    status SMALLINT NOT NULL DEFAULT 1,
    last_login_time TIMESTAMPTZ,
    create_time TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS roc_operation_log (
    id BIGSERIAL PRIMARY KEY,
    admin_id BIGINT,
    admin_username VARCHAR(50),
    module VARCHAR(50),
    action VARCHAR(50),
    target_id BIGINT,
    detail TEXT,
    ip VARCHAR(50),
    create_time TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS roc_login_log (
    id BIGSERIAL PRIMARY KEY,
    admin_id BIGINT,
    username VARCHAR(50),
    ip VARCHAR(50),
    user_agent VARCHAR(500),
    status SMALLINT NOT NULL DEFAULT 1,
    create_time TIMESTAMPTZ DEFAULT NOW()
);

-- 插入默认超级管理员 (密码: admin123, BCrypt加密)
INSERT INTO roc_admin (username, password, nickname, role, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', 'super', 1)
ON CONFLICT (username) DO NOTHING;
```

- [ ] **Step 5: Commit**

```bash
git add roco-dex-admin-service/src/main/java/com/roco/dex/admin/entity/
git commit -m "feat(admin): 创建管理员和日志实体类"
```

---

### Task 5: 创建 Mapper 接口

**Files:**
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/mapper/AdminMapper.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/mapper/OperationLogMapper.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/mapper/LoginLogMapper.java`

- [ ] **Step 1: 创建三个 Mapper**

```java
package com.roco.dex.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roco.dex.admin.entity.Admin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
```

```java
package com.roco.dex.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roco.dex.admin.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
```

```java
package com.roco.dex.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.roco.dex.admin.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {
}
```

- [ ] **Step 2: Commit**

```bash
git add roco-dex-admin-service/src/main/java/com/roco/dex/admin/mapper/
git commit -m "feat(admin): 创建 Mapper 接口"
```

---

### Task 6: 创建认证拦截器和 DTO

**Files:**
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/interceptor/AdminAuthInterceptor.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/dto/AdminLoginDTO.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/dto/AdminVO.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/common/AdminContext.java`

- [ ] **Step 1: 创建 AdminContext（线程级管理员信息存储）**

```java
package com.roco.dex.admin.common;

public class AdminContext {
    private static final ThreadLocal<AdminInfo> HOLDER = new ThreadLocal<>();

    public static void set(AdminInfo info) {
        HOLDER.set(info);
    }

    public static AdminInfo get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }

    public static class AdminInfo {
        private Long id;
        private String username;
        private String role;

        public AdminInfo(Long id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getRole() { return role; }
    }
}
```

- [ ] **Step 2: 创建 AdminAuthInterceptor**

```java
package com.roco.dex.admin.interceptor;

import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }

        try {
            Long adminId = jwtUtil.getAdminId(token);
            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);
            AdminContext.set(new AdminContext.AdminInfo(adminId, username, role));
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"登录已过期\"}");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AdminContext.clear();
    }
}
```

- [ ] **Step 3: 创建 AdminLoginDTO**

```java
package com.roco.dex.admin.dto;

import lombok.Data;

@Data
public class AdminLoginDTO {
    private String username;
    private String password;
}
```

- [ ] **Step 4: 创建 AdminVO**

```java
package com.roco.dex.admin.dto;

import lombok.Data;
import java.time.OffsetDateTime;

@Data
public class AdminVO {
    private Long id;
    private String username;
    private String nickname;
    private String role;
    private Integer status;
    private OffsetDateTime lastLoginTime;
    private String token;
}
```

- [ ] **Step 5: Commit**

```bash
git add roco-dex-admin-service/src/main/java/com/roco/dex/admin/interceptor/ \
       roco-dex-admin-service/src/main/java/com/roco/dex/admin/dto/ \
       roco-dex-admin-service/src/main/java/com/roco/dex/admin/common/AdminContext.java
git commit -m "feat(admin): 添加认证拦截器、DTO和上下文"
```

---

### Task 7: 创建认证 Controller 和 Service

**Files:**
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/service/AdminAuthService.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/service/impl/AdminAuthServiceImpl.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/controller/AdminAuthController.java`

- [ ] **Step 1: 创建 AdminAuthService 接口**

```java
package com.roco.dex.admin.service;

import com.roco.dex.admin.dto.AdminLoginDTO;
import com.roco.dex.admin.dto.AdminVO;
import jakarta.servlet.http.HttpServletRequest;

public interface AdminAuthService {
    AdminVO login(AdminLoginDTO dto, HttpServletRequest request);
    AdminVO getCurrentAdmin();
}
```

- [ ] **Step 2: 创建 AdminAuthServiceImpl**

```java
package com.roco.dex.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.JwtUtil;
import com.roco.dex.admin.dto.AdminLoginDTO;
import com.roco.dex.admin.dto.AdminVO;
import com.roco.dex.admin.entity.Admin;
import com.roco.dex.admin.entity.LoginLog;
import com.roco.dex.admin.mapper.AdminMapper;
import com.roco.dex.admin.mapper.LoginLogMapper;
import com.roco.dex.admin.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminMapper adminMapper;
    private final LoginLogMapper loginLogMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AdminVO login(AdminLoginDTO dto, HttpServletRequest request) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, dto.getUsername())
        );

        LoginLog log = new LoginLog();
        log.setUsername(dto.getUsername());
        log.setIp(request.getRemoteAddr());
        log.setUserAgent(request.getHeader("User-Agent"));

        if (admin == null || !passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            log.setStatus(0);
            log.setAdminId(admin != null ? admin.getId() : null);
            loginLogMapper.insert(log);
            throw new RuntimeException("用户名或密码错误");
        }

        if (admin.getStatus() == 0) {
            log.setStatus(0);
            log.setAdminId(admin.getId());
            loginLogMapper.insert(log);
            throw new RuntimeException("账号已被禁用");
        }

        // 更新最后登录时间
        admin.setLastLoginTime(OffsetDateTime.now());
        adminMapper.updateById(admin);

        // 记录登录成功日志
        log.setStatus(1);
        log.setAdminId(admin.getId());
        loginLogMapper.insert(log);

        // 生成 token
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), admin.getRole());

        AdminVO vo = new AdminVO();
        vo.setId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setNickname(admin.getNickname());
        vo.setRole(admin.getRole());
        vo.setStatus(admin.getStatus());
        vo.setLastLoginTime(admin.getLastLoginTime());
        vo.setToken(token);
        return vo;
    }

    @Override
    public AdminVO getCurrentAdmin() {
        AdminContext.AdminInfo info = AdminContext.get();
        Admin admin = adminMapper.selectById(info.getId());
        if (admin == null) throw new RuntimeException("管理员不存在");

        AdminVO vo = new AdminVO();
        vo.setId(admin.getId());
        vo.setUsername(admin.getUsername());
        vo.setNickname(admin.getNickname());
        vo.setRole(admin.getRole());
        vo.setStatus(admin.getStatus());
        vo.setLastLoginTime(admin.getLastLoginTime());
        return vo;
    }
}
```

- [ ] **Step 3: 创建 AdminAuthController**

```java
package com.roco.dex.admin.controller;

import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.dto.AdminLoginDTO;
import com.roco.dex.admin.dto.AdminVO;
import com.roco.dex.admin.service.AdminAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员认证")
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<AdminVO> login(@RequestBody AdminLoginDTO dto, HttpServletRequest request) {
        return Result.success(adminAuthService.login(dto, request));
    }

    @Operation(summary = "当前管理员信息")
    @GetMapping("/me")
    public Result<AdminVO> me() {
        return Result.success(adminAuthService.getCurrentAdmin());
    }
}
```

- [ ] **Step 4: 验证编译和登录接口**

Run: `cd roco-dex-admin-service && mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add roco-dex-admin-service/src/main/java/com/roco/dex/admin/service/ \
       roco-dex-admin-service/src/main/java/com/roco/dex/admin/controller/AdminAuthController.java
git commit -m "feat(admin): 添加管理员认证接口"
```

---

## Phase 2: 游戏数据 CRUD

### Task 8: 宠物管理 CRUD

**Files:**
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/dto/PetAdminDTO.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/service/AdminPetService.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/service/impl/AdminPetServiceImpl.java`
- Create: `roco-dex-admin-service/src/main/java/com/roco/dex/admin/controller/AdminPetController.java`

- [ ] **Step 1: 创建 PetAdminDTO**

```java
package com.roco.dex.admin.dto;

import lombok.Data;
import java.util.List;

@Data
public class PetAdminDTO {
    private String name;
    private String petNo;
    private String mainAttr;
    private String subAttr;
    private String imageUrl;
    private Integer hp;
    private Integer attack;
    private Integer defense;
    private Integer spAttack;
    private Integer spDefense;
    private Integer speed;
    private String description;
    private String obtainMethod;
    private List<Long> skillIds;
}
```

- [ ] **Step 2: 创建 AdminPetService 接口**

```java
package com.roco.dex.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.roco.dex.admin.dto.PetAdminDTO;
import com.roco.dex.entity.Pet;

public interface AdminPetService {
    IPage<Pet> list(String keyword, String attr, int page, int size);
    Pet getById(Long id);
    void create(PetAdminDTO dto);
    void update(Long id, PetAdminDTO dto);
    void delete(Long id);
}
```

- [ ] **Step 3: 创建 AdminPetServiceImpl**

```java
package com.roco.dex.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.dto.PetAdminDTO;
import com.roco.dex.admin.entity.OperationLog;
import com.roco.dex.admin.mapper.OperationLogMapper;
import com.roco.dex.admin.service.AdminPetService;
import com.roco.dex.entity.Pet;
import com.roco.dex.entity.PetSkill;
import com.roco.dex.mapper.PetMapper;
import com.roco.dex.mapper.PetSkillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.roco.dex.admin.common.AdminContext;

@Service
@RequiredArgsConstructor
public class AdminPetServiceImpl implements AdminPetService {

    private final PetMapper petMapper;
    private final PetSkillMapper petSkillMapper;
    private final OperationLogMapper operationLogMapper;

    @Override
    public IPage<Pet> list(String keyword, String attr, int page, int size) {
        LambdaQueryWrapper<Pet> qw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            qw.and(w -> w.like(Pet::getName, keyword).or().like(Pet::getPetNo, keyword));
        }
        if (StringUtils.hasText(attr)) {
            qw.eq(Pet::getMainAttr, attr);
        }
        qw.orderByDesc(Pet::getId);
        return petMapper.selectPage(new Page<>(page, size), qw);
    }

    @Override
    public Pet getById(Long id) {
        Pet pet = petMapper.selectById(id);
        if (pet == null) throw new RuntimeException("宠物不存在");
        return pet;
    }

    @Override
    @Transactional
    public void create(PetAdminDTO dto) {
        Pet pet = new Pet();
        copyDtoToEntity(dto, pet);
        petMapper.insert(pet);

        if (dto.getSkillIds() != null) {
            for (Long skillId : dto.getSkillIds()) {
                PetSkill ps = new PetSkill();
                ps.setPetId(pet.getId());
                ps.setSkillId(skillId);
                ps.setLearnLevel(1);
                petSkillMapper.insert(ps);
            }
        }

        logOperation("pet", "create", pet.getId(), "新增宠物: " + pet.getName());
    }

    @Override
    @Transactional
    public void update(Long id, PetAdminDTO dto) {
        Pet pet = getById(id);
        copyDtoToEntity(dto, pet);
        petMapper.updateById(pet);

        if (dto.getSkillIds() != null) {
            petSkillMapper.delete(new LambdaQueryWrapper<PetSkill>().eq(PetSkill::getPetId, id));
            for (Long skillId : dto.getSkillIds()) {
                PetSkill ps = new PetSkill();
                ps.setPetId(id);
                ps.setSkillId(skillId);
                ps.setLearnLevel(1);
                petSkillMapper.insert(ps);
            }
        }

        logOperation("pet", "update", id, "编辑宠物: " + pet.getName());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Pet pet = getById(id);
        petMapper.deleteById(id);
        petSkillMapper.delete(new LambdaQueryWrapper<PetSkill>().eq(PetSkill::getPetId, id));
        logOperation("pet", "delete", id, "删除宠物: " + pet.getName());
    }

    private void copyDtoToEntity(PetAdminDTO dto, Pet pet) {
        pet.setName(dto.getName());
        pet.setPetNo(dto.getPetNo());
        pet.setMainAttr(dto.getMainAttr());
        pet.setSubAttr(dto.getSubAttr());
        pet.setImageUrl(dto.getImageUrl());
        pet.setHp(dto.getHp());
        pet.setAttack(dto.getAttack());
        pet.setDefense(dto.getDefense());
        pet.setSpAttack(dto.getSpAttack());
        pet.setSpDefense(dto.getSpDefense());
        pet.setSpeed(dto.getSpeed());
        pet.setDescription(dto.getDescription());
        pet.setObtainMethod(dto.getObtainMethod());
    }

    private void logOperation(String module, String action, Long targetId, String detail) {
        OperationLog log = new OperationLog();
        AdminContext.AdminInfo info = AdminContext.get();
        if (info != null) {
            log.setAdminId(info.getId());
            log.setAdminUsername(info.getUsername());
        }
        log.setModule(module);
        log.setAction(action);
        log.setTargetId(targetId);
        log.setDetail(detail);
        operationLogMapper.insert(log);
    }
}
```

- [ ] **Step 4: 创建 AdminPetController**

```java
package com.roco.dex.admin.controller;

import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.dto.PetAdminDTO;
import com.roco.dex.admin.service.AdminPetService;
import com.roco.dex.entity.Pet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "宠物管理")
@RestController
@RequestMapping("/api/admin/pet")
@RequiredArgsConstructor
public class AdminPetController {

    private final AdminPetService adminPetService;

    @Operation(summary = "宠物列表")
    @GetMapping("/list")
    public Result<PageResult<Pet>> list(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "属性筛选") @RequestParam(required = false) String attr,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        return Result.success(PageResult.of(adminPetService.list(keyword, attr, page, size)));
    }

    @Operation(summary = "宠物详情")
    @GetMapping("/{id}")
    public Result<Pet> getById(@PathVariable Long id) {
        return Result.success(adminPetService.getById(id));
    }

    @Operation(summary = "新增宠物")
    @PostMapping
    public Result<Void> create(@RequestBody PetAdminDTO dto) {
        adminPetService.create(dto);
        return Result.success();
    }

    @Operation(summary = "编辑宠物")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody PetAdminDTO dto) {
        adminPetService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除宠物")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        adminPetService.delete(id);
        return Result.success();
    }
}
```

- [ ] **Step 5: Commit**

```bash
git add roco-dex-admin-service/src/main/java/com/roco/dex/admin/
git commit -m "feat(admin): 添加宠物管理 CRUD 接口"
```

---

### Task 9: 技能、道具、装备管理 CRUD

按照 Task 8 的模式，为 Skill、Item、Equipment 创建相同的管理接口。

- [ ] **Step 1: 创建 SkillAdminDTO / ItemAdminDTO / EquipmentAdminDTO**
- [ ] **Step 2: 创建 AdminSkillService + AdminSkillServiceImpl + AdminSkillController**
- [ ] **Step 3: 创建 AdminItemService + AdminItemServiceImpl + AdminItemController**
- [ ] **Step 4: 创建 AdminEquipmentService + AdminEquipmentServiceImpl + AdminEquipmentController**
- [ ] **Step 5: Commit**

```bash
git commit -m "feat(admin): 添加技能/道具/装备管理 CRUD 接口"
```

---

### Task 10: 属性克制管理

- [ ] **Step 1: 创建 AdminAttributeController**

```java
package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.OperationLog;
import com.roco.dex.admin.mapper.OperationLogMapper;
import com.roco.dex.entity.Attribute;
import com.roco.dex.mapper.AttributeMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.roco.dex.admin.common.AdminContext;
import java.math.BigDecimal;
import java.util.List;

@Tag(name = "属性克制管理")
@RestController
@RequestMapping("/api/admin/attribute")
@RequiredArgsConstructor
public class AdminAttributeController {

    private final AttributeMapper attributeMapper;
    private final OperationLogMapper operationLogMapper;

    @Operation(summary = "属性克制表")
    @GetMapping("/table")
    public Result<List<Attribute>> table() {
        return Result.success(attributeMapper.selectList(null));
    }

    @Operation(summary = "编辑克制倍率")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestParam BigDecimal multiplier) {
        Attribute attr = attributeMapper.selectById(id);
        if (attr == null) return Result.error(404, "记录不存在");
        attr.setMultiplier(multiplier);
        attributeMapper.updateById(attr);

        OperationLog log = new OperationLog();
        AdminContext.AdminInfo info = AdminContext.get();
        if (info != null) {
            log.setAdminId(info.getId());
            log.setAdminUsername(info.getUsername());
        }
        log.setModule("attribute");
        log.setAction("update");
        log.setTargetId(id);
        log.setDetail("编辑克制倍率: " + attr.getAttackAttr() + "→" + attr.getDefenseAttr() + " = " + multiplier);
        operationLogMapper.insert(log);

        return Result.success();
    }
}
```

- [ ] **Step 2: Commit**

```bash
git commit -m "feat(admin): 添加属性克制管理接口"
```

---

## Phase 3: 用户管理、仪表盘、导入导出

### Task 11: 用户管理

- [ ] **Step 1: 创建 AdminUserController**

```java
package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.OperationLog;
import com.roco.dex.admin.mapper.OperationLogMapper;
import com.roco.dex.entity.User;
import com.roco.dex.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private final OperationLogMapper operationLogMapper;

    @Operation(summary = "用户列表")
    @GetMapping("/list")
    public Result<PageResult<User>> list(
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            qw.like(User::getUsername, keyword).or().like(User::getNickname, keyword);
        }
        qw.orderByDesc(User::getId);
        IPage<User> result = userMapper.selectPage(new Page<>(page, size), qw);
        // 清除密码
        result.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "禁用/启用用户")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) return Result.error(404, "用户不存在");
        userMapper.updateById(user);

        OperationLog log = new OperationLog();
        AdminContext.AdminInfo info = AdminContext.get();
        if (info != null) {
            log.setAdminId(info.getId());
            log.setAdminUsername(info.getUsername());
        }
        log.setModule("user");
        log.setAction("update");
        log.setTargetId(id);
        log.setDetail("更新用户状态: " + user.getUsername());
        operationLogMapper.insert(log);

        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) return Result.error(404, "用户不存在");
        userMapper.deleteById(id);

        OperationLog log = new OperationLog();
        AdminContext.AdminInfo info = AdminContext.get();
        if (info != null) {
            log.setAdminId(info.getId());
            log.setAdminUsername(info.getUsername());
        }
        log.setModule("user");
        log.setAction("delete");
        log.setTargetId(id);
        log.setDetail("删除用户: " + user.getUsername());
        operationLogMapper.insert(log);

        return Result.success();
    }
}
```

- [ ] **Step 2: Commit**

```bash
git commit -m "feat(admin): 添加用户管理接口"
```

---

### Task 12: 仪表盘统计

- [ ] **Step 1: 创建 AdminDashboardController**

```java
package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.roco.dex.admin.common.Result;
import com.roco.dex.entity.Pet;
import com.roco.dex.entity.User;
import com.roco.dex.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Tag(name = "仪表盘")
@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final PetMapper petMapper;
    private final SkillMapper skillMapper;
    private final ItemMapper itemMapper;
    private final EquipmentMapper equipmentMapper;
    private final UserMapper userMapper;

    @Operation(summary = "统计数据")
    @GetMapping("/stats")
    public Result<Map<String, Long>> stats() {
        Map<String, Long> stats = new LinkedHashMap<>();
        stats.put("petCount", petMapper.selectCount(null));
        stats.put("skillCount", skillMapper.selectCount(null));
        stats.put("itemCount", itemMapper.selectCount(null));
        stats.put("equipmentCount", equipmentMapper.selectCount(null));
        stats.put("userCount", userMapper.selectCount(null));
        return Result.success(stats);
    }

    @Operation(summary = "近7天注册趋势")
    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            OffsetDateTime start = date.atStartOfDay().atOffset(ZoneOffset.ofHours(8));
            OffsetDateTime end = date.plusDays(1).atStartOfDay().atOffset(ZoneOffset.ofHours(8));
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .ge(User::getCreateTime, start)
                            .lt(User::getCreateTime, end)
            );
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date.toString());
            item.put("count", count);
            trend.add(item);
        }
        return Result.success(trend);
    }

    @Operation(summary = "属性分布")
    @GetMapping("/attr-dist")
    public Result<List<Map<String, Object>>> attrDist() {
        List<Pet> allPets = petMapper.selectList(null);
        Map<String, Long> dist = allPets.stream()
                .collect(Collectors.groupingBy(Pet::getMainAttr, Collectors.counting()));
        List<Map<String, Object>> result = new ArrayList<>();
        dist.forEach((attr, count) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("attr", attr);
            item.put("count", count);
            result.add(item);
        });
        return Result.success(result);
    }
}
```

- [ ] **Step 2: Commit**

```bash
git commit -m "feat(admin): 添加仪表盘统计接口"
```

---

### Task 13: 管理员管理（仅超级管理员）

- [ ] **Step 1: 创建 AdminAdminController**

```java
package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.Admin;
import com.roco.dex.admin.mapper.AdminMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理员管理")
@RestController
@RequestMapping("/api/admin/admin")
@RequiredArgsConstructor
public class AdminAdminController {

    private final AdminMapper adminMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private void checkSuper() {
        AdminContext.AdminInfo info = AdminContext.get();
        if (!"super".equals(info.getRole())) {
            throw new RuntimeException("无权限，仅超级管理员可操作");
        }
    }

    @Operation(summary = "管理员列表")
    @GetMapping("/list")
    public Result<PageResult<Admin>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        checkSuper();
        IPage<Admin> result = adminMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Admin>().orderByDesc(Admin::getId));
        result.getRecords().forEach(a -> a.setPassword(null));
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "新增管理员")
    @PostMapping
    public Result<Void> create(@RequestBody Map<String, String> body) {
        checkSuper();
        String username = body.get("username");
        String password = body.get("password");
        String nickname = body.get("nickname");
        String role = body.getOrDefault("role", "normal");

        Admin existing = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username));
        if (existing != null) return Result.error(400, "用户名已存在");

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setNickname(nickname);
        admin.setRole(role);
        admin.setStatus(1);
        adminMapper.insert(admin);
        return Result.success();
    }

    @Operation(summary = "编辑管理员")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Map<String, String> body) {
        checkSuper();
        Admin admin = adminMapper.selectById(id);
        if (admin == null) return Result.error(404, "管理员不存在");

        if (body.containsKey("nickname")) admin.setNickname(body.get("nickname"));
        if (body.containsKey("role")) admin.setRole(body.get("role"));
        if (body.containsKey("status")) admin.setStatus(Integer.parseInt(body.get("status")));
        if (body.containsKey("password") && !body.get("password").isEmpty()) {
            admin.setPassword(passwordEncoder.encode(body.get("password")));
        }
        adminMapper.updateById(admin);
        return Result.success();
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkSuper();
        AdminContext.AdminInfo info = AdminContext.get();
        if (info.getId().equals(id)) return Result.error(400, "不能删除自己");
        adminMapper.deleteById(id);
        return Result.success();
    }
}
```

- [ ] **Step 2: Commit**

```bash
git commit -m "feat(admin): 添加管理员管理接口"
```

---

### Task 14: 日志查询

- [ ] **Step 1: 创建 AdminLogController**

```java
package com.roco.dex.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.PageResult;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.LoginLog;
import com.roco.dex.admin.entity.OperationLog;
import com.roco.dex.admin.mapper.LoginLogMapper;
import com.roco.dex.admin.mapper.OperationLogMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "日志管理")
@RestController
@RequestMapping("/api/admin/log")
@RequiredArgsConstructor
public class AdminLogController {

    private final LoginLogMapper loginLogMapper;
    private final OperationLogMapper operationLogMapper;

    @Operation(summary = "登录日志")
    @GetMapping("/login")
    public Result<PageResult<LoginLog>> loginLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        AdminContext.AdminInfo info = AdminContext.get();
        LambdaQueryWrapper<LoginLog> qw = new LambdaQueryWrapper<>();
        if ("normal".equals(info.getRole())) {
            qw.eq(LoginLog::getAdminId, info.getId());
        }
        qw.orderByDesc(LoginLog::getId);
        IPage<LoginLog> result = loginLogMapper.selectPage(new Page<>(page, size), qw);
        return Result.success(PageResult.of(result));
    }

    @Operation(summary = "操作日志")
    @GetMapping("/operation")
    public Result<PageResult<OperationLog>> operationLogs(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int size) {
        AdminContext.AdminInfo info = AdminContext.get();
        LambdaQueryWrapper<OperationLog> qw = new LambdaQueryWrapper<>();
        if ("normal".equals(info.getRole())) {
            qw.eq(OperationLog::getAdminId, info.getId());
        }
        qw.orderByDesc(OperationLog::getId);
        IPage<OperationLog> result = operationLogMapper.selectPage(new Page<>(page, size), qw);
        return Result.success(PageResult.of(result));
    }
}
```

- [ ] **Step 2: Commit**

```bash
git commit -m "feat(admin): 添加日志查询接口"
```

---

### Task 15: 数据导入导出

- [ ] **Step 1: 创建 AdminImportExportController**

```java
package com.roco.dex.admin.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.roco.dex.admin.common.AdminContext;
import com.roco.dex.admin.common.Result;
import com.roco.dex.admin.entity.OperationLog;
import com.roco.dex.admin.mapper.OperationLogMapper;
import com.roco.dex.entity.*;
import com.roco.dex.mapper.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Tag(name = "数据导入导出")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminImportExportController {

    private final PetMapper petMapper;
    private final SkillMapper skillMapper;
    private final ItemMapper itemMapper;
    private final EquipmentMapper equipmentMapper;
    private final OperationLogMapper operationLogMapper;

    @Operation(summary = "导出数据")
    @GetMapping("/export/{module}")
    public void export(@PathVariable String module, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(module + ".xlsx", StandardCharsets.UTF_8));

        switch (module) {
            case "pet" -> EasyExcel.write(response.getOutputStream(), Pet.class).sheet("宠物").doWrite(petMapper.selectList(null));
            case "skill" -> EasyExcel.write(response.getOutputStream(), Skill.class).sheet("技能").doWrite(skillMapper.selectList(null));
            case "item" -> EasyExcel.write(response.getOutputStream(), Item.class).sheet("道具").doWrite(itemMapper.selectList(null));
            case "equipment" -> EasyExcel.write(response.getOutputStream(), Equipment.class).sheet("装备").doWrite(equipmentMapper.selectList(null));
            default -> throw new RuntimeException("不支持的模块: " + module);
        }

        logOperation(module, "export", null, "导出" + module + "数据");
    }

    @Operation(summary = "导入数据")
    @PostMapping("/import/{module}")
    public Result<String> importData(@PathVariable String module, @RequestParam("file") MultipartFile file) throws IOException {
        int count = 0;
        switch (module) {
            case "pet" -> {
                List<Pet> list = EasyExcel.read(file.getInputStream()).head(Pet.class).sheet().doReadSync();
                for (Pet p : list) { p.setId(null); petMapper.insert(p); }
                count = list.size();
            }
            case "skill" -> {
                List<Skill> list = EasyExcel.read(file.getInputStream()).head(Skill.class).sheet().doReadSync();
                for (Skill s : list) { s.setId(null); skillMapper.insert(s); }
                count = list.size();
            }
            case "item" -> {
                List<Item> list = EasyExcel.read(file.getInputStream()).head(Item.class).sheet().doReadSync();
                for (Item i : list) { i.setId(null); itemMapper.insert(i); }
                count = list.size();
            }
            case "equipment" -> {
                List<Equipment> list = EasyExcel.read(file.getInputStream()).head(Equipment.class).sheet().doReadSync();
                for (Equipment e : list) { e.setId(null); equipmentMapper.insert(e); }
                count = list.size();
            }
            default -> throw new RuntimeException("不支持的模块: " + module);
        }

        logOperation(module, "import", null, "导入" + module + "数据，共" + count + "条");
        return Result.success("导入成功，共" + count + "条");
    }

    private void logOperation(String module, String action, Long targetId, String detail) {
        OperationLog log = new OperationLog();
        AdminContext.AdminInfo info = AdminContext.get();
        if (info != null) {
            log.setAdminId(info.getId());
            log.setAdminUsername(info.getUsername());
        }
        log.setModule(module);
        log.setAction(action);
        log.setTargetId(targetId);
        log.setDetail(detail);
        operationLogMapper.insert(log);
    }
}
```

- [ ] **Step 2: Commit**

```bash
git commit -m "feat(admin): 添加数据导入导出接口"
```

---

## Phase 4: 前端搭建

### Task 16: 创建前端项目

- [ ] **Step 1: 使用 Vite 创建 Vue3 + TypeScript 项目**

```bash
cd /d/coding/roco
npm create vite@latest roco-dex-admin -- --template vue-ts
cd roco-dex-admin
npm install
```

- [ ] **Step 2: 安装依赖**

```bash
npm install element-plus @element-plus/icons-vue vue-router@4 pinia axios echarts xlsx
npm install -D @types/node unplugin-auto-import unplugin-vue-components sass
```

- [ ] **Step 3: Commit**

```bash
cd /d/coding/roco
git add roco-dex-admin/
git commit -m "feat(admin): 创建前端项目骨架"
```

---

### Task 17: 前端基础配置

- [ ] **Step 1: 配置 vite.config.ts、main.ts、router、store、axios**
- [ ] **Step 2: 创建 AdminLayout 布局组件（侧边栏 + 顶栏 + 内容区）**
- [ ] **Step 3: 创建登录页**
- [ ] **Step 4: Commit**

---

### Task 18: 前端核心页面

- [ ] **Step 1: 创建仪表盘页面（统计卡片 + ECharts 图表）**
- [ ] **Step 2: 创建宠物管理页面（表格 + 新增/编辑对话框）**
- [ ] **Step 3: 创建技能、道具、装备管理页面**
- [ ] **Step 4: 创建属性克制管理页面**
- [ ] **Step 5: 创建用户管理页面**
- [ ] **Step 6: 创建管理员管理页面（仅超级管理员可见）**
- [ ] **Step 7: 创建日志页面**
- [ ] **Step 8: 创建数据导入导出页面**
- [ ] **Step 9: Commit**

```bash
git commit -m "feat(admin): 完成所有前端页面"
```

---

## Phase 5: 联调测试

### Task 19: 端到端测试

- [ ] **Step 1: 启动后端，验证管理员登录**
- [ ] **Step 2: 验证所有 CRUD 接口**
- [ ] **Step 3: 启动前端，验证页面功能**
- [ ] **Step 4: 构建前端项目**
- [ ] **Step 5: Commit 并 Push**

```bash
cd /d/coding/roco/roco-dex-admin && npm run build
cd /d/coding/roco
git add -A
git commit -m "feat: 完成后台管理系统"
git push
```
