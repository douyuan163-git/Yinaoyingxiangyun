# 铁岭市医保影像云统一服务门户

**Tieling Healthcare Security Imaging Cloud Portal**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.3.12-brightgreen)](https://spring.io/projects/spring-boot)
[![Vue CLI](https://img.shields.io/badge/Vue-3.x-42b883)](https://vuejs.org/)
[![Java](https://img.shields.io/badge/Java-8%2B-orange)](https://www.oracle.com/java/)
[![SQLite](https://img.shields.io/badge/SQLite-3.x-blue)](https://www.sqlite.org/)

---

## 项目简介

铁岭市医保影像云统一服务门户，由铁岭市医疗保障局主办，面向**医保监管部门**、**医疗机构**、**参保人员**和**系统开发者**提供一站式服务入口。

### 核心功能

| 功能模块 | 说明 |
|---------|------|
| 统一服务门户 | 四大角色差异化入口，SSO 单点登录跳转子系统 |
| 政策法规 | 国家/省/市三级政策文件检索与阅读 |
| 新闻动态 | 平台公告、医保动态、行业资讯 |
| 检查互认 | 互认机构查询、个人影像记录查询 |
| 机构接入 | 接入机构展示、接入申请流程、技术文档 |
| 开发者中心 | API 接口文档、SDK 下载、安全规范 |
| SSO 认证 | JWT Token 统一认证，支持子系统授权跳转 |

---

## 技术架构

```
Yinaoyingxiangyun/
├── backend/          # Spring Boot 2.3.x 后端服务
│   ├── src/main/java/cn/tieling/yibao/
│   │   ├── controller/    # REST 控制器
│   │   ├── service/       # 业务逻辑层
│   │   ├── entity/        # JPA 实体
│   │   ├── repository/    # 数据访问层
│   │   ├── config/        # 配置类（CORS、Redis、Web）
│   │   ├── interceptor/   # JWT 拦截器
│   │   ├── util/          # JWT/Redis 工具类
│   │   └── dto/           # 请求/响应 DTO
│   └── src/main/resources/
│       └── application.yml
└── frontend/         # Vue CLI 3 前端项目
    ├── src/
    │   ├── views/     # 页面视图
    │   ├── components/ # 公共组件
    │   ├── api/       # API 接口封装
    │   ├── store/     # Vuex 状态管理
    │   ├── router/    # Vue Router 路由
    │   └── utils/     # Axios 请求封装
    └── vue.config.js  # 代理配置
```

### 后端技术栈

- **框架**：Spring Boot 2.3.12.RELEASE
- **语言**：Java 8+（兼容 Java 11/17）
- **数据库**：SQLite 3.x（通过 JPA/Hibernate 操作）
- **缓存**：Redis（Token 黑名单、会话管理）
- **认证**：JWT（jjwt 0.9.1）
- **构建**：Maven 3.6+

### 前端技术栈

- **框架**：Vue 3.x + Vue CLI 5
- **UI 组件**：Element Plus
- **状态管理**：Vuex 4
- **路由**：Vue Router 4
- **HTTP 客户端**：Axios（统一 `Content-Type: application/json; charset=utf-8`）
- **样式**：SCSS + 自定义设计系统

---

## 快速启动

### 环境要求

- JDK 8 或以上
- Maven 3.6+
- Node.js 16+
- Redis 6+（本地或远程）

### 1. 启动后端

```bash
cd backend

# 修改 Redis 配置（src/main/resources/application.yml）
# spring.redis.host: localhost
# spring.redis.port: 6379
# spring.redis.password: （无密码则留空）

# 编译并运行
mvn spring-boot:run

# 或打包后运行
mvn package -DskipTests
java -jar target/imaging-cloud-portal-1.0.0.jar
```

后端启动后访问：`http://localhost:8080/api`

> **注意**：首次启动会自动创建 SQLite 数据库（`~/imaging_cloud.db`）并初始化演示数据。

### 2. 启动前端

```bash
cd frontend

# 安装依赖
npm install  # 或 pnpm install

# 开发模式（自动代理到后端 8080）
npm run serve

# 生产构建
npm run build
```

前端开发服务器：`http://localhost:3001`

---

## 接口规范

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

### 认证接口（白名单，无需 Token）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/login` | 统一登录 |
| POST | `/api/auth/logout` | 退出登录 |
| POST | `/api/auth/refresh` | 刷新 Token |

### 业务接口（需要 Bearer Token）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/policy/list` | 政策法规列表 |
| GET | `/api/policy/{id}` | 政策详情 |
| GET | `/api/news/list` | 新闻动态列表 |
| GET | `/api/news/latest` | 最新5条新闻 |
| GET | `/api/institution/list` | 机构列表 |
| GET | `/api/institution/stats` | 机构统计 |
| GET | `/api/recognition/query` | 检查互认查询 |
| GET | `/api/recognition/stats` | 互认统计 |
| GET | `/api/auth/sso-token` | 获取SSO跳转Token |

### 请求头要求

```
Content-Type: application/json; charset=utf-8
Authorization: Bearer <access_token>
```

---

## SSO 单点登录流程

```
用户访问门户 → 统一登录页
    ↓
POST /api/auth/login
    ↓
返回 accessToken + refreshToken
    ↓
用户点击子系统入口
    ↓
GET /api/auth/sso-token?system=supervisor
    ↓
携带 ssoToken 跳转至子系统
    ↓
子系统验证 ssoToken 完成免密登录
```

### 用户角色

| 角色 | role 值 | 说明 |
|------|---------|------|
| 医保监管部门 | `supervisor` | 监管数据查看、统计分析 |
| 医疗机构 | `hospital` | 影像上传、互认查询 |
| 参保人员 | `patient` | 个人影像查询、云胶片 |
| 系统开发者 | `developer` | API 调试、接口文档 |

---

## 演示账号

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | admin123 | 医保监管部门 |
| doctor001 | doctor123 | 医疗机构 |
| 210224199001011234（身份证登录） | patient123 | 参保人员 |
| dev001 | dev123 | 系统开发者 |

---

## 配置说明

主要配置项（`backend/src/main/resources/application.yml`）：

```yaml
spring:
  datasource:
    url: jdbc:sqlite:${user.home}/imaging_cloud.db  # SQLite 数据库路径
  redis:
    host: localhost      # Redis 地址
    port: 6379
    password:           # Redis 密码（无则留空）

jwt:
  secret: TieLingYiBaoYingXiangYun2025  # JWT 密钥（生产环境请修改）
  expiration: 7200000   # Token 有效期（毫秒，默认2小时）
```

---

## 开发团队

- **主办单位**：铁岭市医疗保障局
- **技术支持**：融御科技（辽宁）有限公司
- **项目版本**：v1.0.0
- **更新日期**：2025年

---

## License

本项目为铁岭市医保影像云平台专属定制开发，版权归铁岭市医疗保障局所有。
