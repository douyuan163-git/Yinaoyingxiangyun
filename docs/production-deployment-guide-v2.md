# 铁岭市医保影像云统一服务门户
## 生产环境部署与运维手册 v2.0

**文档版本**：v2.0.0  
**适用项目**：铁岭市医保影像云统一服务门户（三端：后端 API + 前台展示站 + 后台管理系统）  
**技术栈**：Spring Boot 2.3.x / Vue CLI 3 / SQLite / Redis / Nginx  
**编制日期**：2026 年 4 月  
**密级**：内部使用

---

## 目录

1. [部署架构总览](#1-部署架构总览)
2. [服务器环境要求](#2-服务器环境要求)
3. [基础软件安装](#3-基础软件安装)
4. [后端服务部署](#4-后端服务部署)
5. [前台展示站部署](#5-前台展示站部署)
6. [后台管理系统部署](#6-后台管理系统部署)
7. [Nginx 反向代理配置](#7-nginx-反向代理配置)
8. [SSL/TLS 证书配置](#8-ssltls-证书配置)
9. [Redis 生产配置](#9-redis-生产配置)
10. [SQLite 数据库管理](#10-sqlite-数据库管理)
11. [安全加固（必读）](#11-安全加固必读)
12. [性能优化建议](#12-性能优化建议)
13. [日志管理](#13-日志管理)
14. [监控与告警](#14-监控与告警)
15. [数据备份与恢复](#15-数据备份与恢复)
16. [常见故障排查](#16-常见故障排查)
17. [版本升级流程](#17-版本升级流程)
18. [上线前检查清单](#18-上线前检查清单)

---

## 1. 部署架构总览

### 1.1 整体架构

本项目采用前后端分离的三端架构，通过 Nginx 统一入口分发流量：

```
互联网用户
    │
    ▼
┌─────────────────────────────────────────┐
│              Nginx (80/443)              │
│  ┌──────────┐ ┌──────────┐ ┌─────────┐ │
│  │ 前台展示  │ │后台管理  │ │ API代理 │ │
│  │  /       │ │ /admin   │ │  /api   │ │
│  └──────────┘ └──────────┘ └─────────┘ │
└─────────────────────────────────────────┘
         │                │
         │                ▼
         │    ┌──────────────────────┐
         │    │  Spring Boot (8080)  │
         │    │  ┌────────────────┐  │
         │    │  │  SQLite (.db)  │  │
         │    │  └────────────────┘  │
         │    │  ┌────────────────┐  │
         │    │  │  Redis (6379)  │  │
         │    │  └────────────────┘  │
         │    └──────────────────────┘
         │
         ▼
  静态文件服务（dist/）
```

### 1.2 端口规划

| 服务 | 端口 | 说明 |
|------|------|------|
| Nginx HTTP | 80 | 自动跳转 HTTPS |
| Nginx HTTPS | 443 | 对外统一入口 |
| Spring Boot | 8080 | 仅监听 127.0.0.1，不对外暴露 |
| Redis | 6379 | 仅监听 127.0.0.1，不对外暴露 |

### 1.3 URL 路由规则

| 访问路径 | 服务 | 说明 |
|---------|------|------|
| `https://域名/` | 前台展示站 dist | 参保人员、医疗机构等访问 |
| `https://域名/admin` | 后台管理系统 dist | 管理员访问 |
| `https://域名/api/*` | Spring Boot 8080 | 所有 API 请求 |

---

## 2. 服务器环境要求

### 2.1 最低配置（测试/小规模生产）

| 资源 | 最低要求 | 推荐配置 |
|------|---------|---------|
| CPU | 2 核 | 4 核 |
| 内存 | 4 GB | 8 GB |
| 磁盘 | 50 GB SSD | 200 GB SSD |
| 操作系统 | CentOS 7.9 / Ubuntu 20.04 LTS | Ubuntu 22.04 LTS |
| 带宽 | 5 Mbps | 20 Mbps |

### 2.2 推荐配置（正式生产）

鉴于医保影像云涉及大量 DICOM 影像数据传输，建议生产环境满足以下标准：

| 资源 | 推荐配置 | 说明 |
|------|---------|------|
| CPU | 8 核 | 支持并发影像处理 |
| 内存 | 16 GB | Redis 缓存 + JVM 堆 |
| 系统盘 | 100 GB SSD | 操作系统 + 应用程序 |
| 数据盘 | 1 TB+ SSD | SQLite 数据库 + 影像文件 |
| 带宽 | 100 Mbps | 影像上传/下载 |
| 操作系统 | Ubuntu 22.04 LTS | 长期支持版本 |

> **注意**：SQLite 适用于中小规模部署（日均影像上传量 < 5000 例）。若接入机构超过 50 家或日均影像量超过 1 万例，建议迁移至 MySQL 8.0 或 PostgreSQL 14+。

---

## 3. 基础软件安装

### 3.1 系统初始化

```bash
# 更新系统
sudo apt-get update && sudo apt-get upgrade -y

# 创建专用运行用户（禁止 root 运行应用）
sudo useradd -m -s /bin/bash yibao
sudo passwd yibao

# 创建应用目录
sudo mkdir -p /opt/yibao/{app,data,logs,backup}
sudo chown -R yibao:yibao /opt/yibao
```

### 3.2 安装 JDK 11

```bash
# Ubuntu 22.04
sudo apt-get install -y openjdk-11-jdk

# 验证安装
java -version
# 期望输出：openjdk version "11.x.x"

# 配置 JAVA_HOME
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> /home/yibao/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> /home/yibao/.bashrc
source /home/yibao/.bashrc
```

> **兼容性说明**：本项目后端代码以 Java 8 语法规范编写，在 JDK 11 环境下完全兼容运行，无需额外配置。

### 3.3 安装 Redis 7.x

```bash
# 安装 Redis
sudo apt-get install -y redis-server

# 验证安装
redis-server --version
# 期望输出：Redis server v=7.x.x

# 启用开机自启
sudo systemctl enable redis-server
```

### 3.4 安装 Nginx

```bash
sudo apt-get install -y nginx

# 验证安装
nginx -v
# 期望输出：nginx version: nginx/1.x.x

# 启用开机自启
sudo systemctl enable nginx
```

### 3.5 安装 Node.js（构建前端时使用）

```bash
# 使用 NodeSource 安装 Node.js 18 LTS
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt-get install -y nodejs

# 验证
node -v   # v18.x.x
npm -v    # 9.x.x

# 安装 pnpm
sudo npm install -g pnpm
```

---

## 4. 后端服务部署

### 4.1 获取代码并构建

```bash
# 切换到应用用户
su - yibao

# 克隆代码（或通过 SCP 上传 jar 包）
cd /opt/yibao/app
git clone https://github.com/douyuan163-git/Yinaoyingxiangyun.git
cd Yinaoyingxiangyun/backend

# 构建生产包（跳过测试）
mvn clean package -DskipTests -Pprod

# 确认 jar 包生成
ls -lh target/imaging-cloud-portal-*.jar
```

### 4.2 生产环境配置文件

在服务器上创建独立的生产配置文件，**不要将敏感信息提交到 Git**：

```bash
mkdir -p /opt/yibao/config
cat > /opt/yibao/config/application-prod.yml << 'EOF'
server:
  port: 8080
  address: 127.0.0.1          # 仅监听本地，由 Nginx 代理
  tomcat:
    max-threads: 200
    min-spare-threads: 20
    accept-count: 100
    connection-timeout: 20000

spring:
  datasource:
    url: jdbc:sqlite:/opt/yibao/data/imaging_cloud.db
    driver-class-name: org.sqlite.JDBC
  jpa:
    database-platform: org.hibernate.dialect.SQLiteDialect
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        jdbc:
          batch_size: 50
  redis:
    host: 127.0.0.1
    port: 6379
    password: "【替换为强密码，至少16位随机字符】"
    timeout: 5000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: 2000ms

jwt:
  secret: "【替换为32位以上随机字符串，例如：openssl rand -hex 32 生成】"
  expiration: 7200000         # 2小时，单位毫秒
  refresh-expiration: 604800000  # 7天

logging:
  level:
    cn.tieling.yibao: INFO
    org.springframework: WARN
  file:
    name: /opt/yibao/logs/app.log
    max-size: 100MB
    max-history: 30
EOF

# 设置配置文件权限（仅 yibao 用户可读）
chmod 600 /opt/yibao/config/application-prod.yml
```

### 4.3 部署 jar 包

```bash
# 复制 jar 包到部署目录
cp /opt/yibao/app/Yinaoyingxiangyun/backend/target/imaging-cloud-portal-*.jar \
   /opt/yibao/app/imaging-cloud-portal.jar
```

### 4.4 配置 Systemd 服务

```bash
sudo tee /etc/systemd/system/yibao-backend.service << 'EOF'
[Unit]
Description=铁岭市医保影像云后端服务
After=network.target redis-server.service
Requires=redis-server.service

[Service]
Type=simple
User=yibao
Group=yibao
WorkingDirectory=/opt/yibao/app

# JVM 参数（根据服务器内存调整）
Environment="JAVA_OPTS=-server \
  -Xms512m \
  -Xmx2048m \
  -XX:+UseG1GC \
  -XX:G1HeapRegionSize=16m \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/opt/yibao/logs/heap-dump.hprof \
  -Djava.security.egd=file:/dev/./urandom \
  -Dfile.encoding=UTF-8"

ExecStart=/usr/bin/java ${JAVA_OPTS} \
  -jar /opt/yibao/app/imaging-cloud-portal.jar \
  --spring.config.additional-location=/opt/yibao/config/application-prod.yml \
  --spring.profiles.active=prod

# 优雅停机（等待最多 60 秒处理完进行中的请求）
ExecStop=/bin/kill -SIGTERM $MAINPID
TimeoutStopSec=60

# 自动重启策略
Restart=on-failure
RestartSec=10
StartLimitInterval=60
StartLimitBurst=3

# 安全限制
NoNewPrivileges=true
PrivateTmp=true

StandardOutput=journal
StandardError=journal
SyslogIdentifier=yibao-backend

[Install]
WantedBy=multi-user.target
EOF

# 重载 systemd 配置
sudo systemctl daemon-reload

# 启动服务
sudo systemctl start yibao-backend
sudo systemctl enable yibao-backend

# 查看启动状态
sudo systemctl status yibao-backend
```

### 4.5 验证后端服务

```bash
# 等待服务启动（约 30 秒）
sleep 30

# 检查健康状态
curl -s http://127.0.0.1:8080/api/public/site-config/basic | python3 -m json.tool

# 检查进程
ps aux | grep imaging-cloud-portal

# 查看启动日志
sudo journalctl -u yibao-backend -n 50 --no-pager
```

---

## 5. 前台展示站部署

### 5.1 构建生产包

```bash
cd /opt/yibao/app/Yinaoyingxiangyun/frontend

# 安装依赖
pnpm install

# 设置生产环境 API 地址（修改 .env.production）
cat > .env.production << 'EOF'
VUE_APP_API_BASE_URL=/api
VUE_APP_TITLE=铁岭市医保影像云统一服务门户
EOF

# 构建生产包
pnpm build

# 确认构建产物
ls -lh dist/
```

### 5.2 部署静态文件

```bash
# 创建 Nginx 静态文件目录
sudo mkdir -p /var/www/yibao/frontend
sudo mkdir -p /var/www/yibao/admin

# 复制前台构建产物
sudo cp -r dist/* /var/www/yibao/frontend/

# 设置文件权限
sudo chown -R www-data:www-data /var/www/yibao/frontend
sudo chmod -R 755 /var/www/yibao/frontend

# 验证文件
ls -la /var/www/yibao/frontend/
```

---

## 6. 后台管理系统部署

### 6.1 构建生产包

```bash
cd /opt/yibao/app/Yinaoyingxiangyun/admin

# 安装依赖
pnpm install

# 设置生产环境配置
cat > .env.production << 'EOF'
VUE_APP_API_BASE_URL=/api
VUE_APP_TITLE=铁岭市医保影像云-管理后台
EOF

# 构建生产包
pnpm build

# 确认构建产物
ls -lh dist/
```

### 6.2 部署静态文件

```bash
# 复制后台管理构建产物
sudo cp -r dist/* /var/www/yibao/admin/

# 设置文件权限
sudo chown -R www-data:www-data /var/www/yibao/admin
sudo chmod -R 755 /var/www/yibao/admin
```

---

## 7. Nginx 反向代理配置

### 7.1 创建主配置文件

```bash
sudo tee /etc/nginx/sites-available/yibao.conf << 'NGINX_EOF'
# 限流区域定义（在 http 块中）
# 注意：以下 limit_req_zone 需放在 /etc/nginx/nginx.conf 的 http{} 块中
# limit_req_zone $binary_remote_addr zone=api_limit:10m rate=30r/m;
# limit_req_zone $binary_remote_addr zone=login_limit:10m rate=5r/m;

server {
    listen 80;
    server_name 你的域名或IP;
    
    # HTTP 强制跳转 HTTPS（配置 SSL 证书后启用）
    # return 301 https://$host$request_uri;
    
    # 未配置 SSL 时使用此配置
    include /etc/nginx/snippets/yibao-common.conf;
}

# HTTPS 配置（配置 SSL 证书后启用）
# server {
#     listen 443 ssl http2;
#     server_name 你的域名;
#     
#     ssl_certificate /etc/ssl/yibao/fullchain.pem;
#     ssl_certificate_key /etc/ssl/yibao/privkey.pem;
#     ssl_protocols TLSv1.2 TLSv1.3;
#     ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384;
#     ssl_prefer_server_ciphers off;
#     ssl_session_cache shared:SSL:10m;
#     ssl_session_timeout 1d;
#     ssl_stapling on;
#     ssl_stapling_verify on;
#     
#     include /etc/nginx/snippets/yibao-common.conf;
# }
NGINX_EOF
```

### 7.2 创建公共配置片段

```bash
sudo mkdir -p /etc/nginx/snippets

sudo tee /etc/nginx/snippets/yibao-common.conf << 'EOF'
# ========== 安全响应头 ==========
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "strict-origin-when-cross-origin" always;
add_header Content-Security-Policy "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src 'self' https://fonts.gstatic.com; img-src 'self' data: https:; connect-src 'self';" always;
add_header Permissions-Policy "camera=(), microphone=(), geolocation=()" always;

# 隐藏 Nginx 版本号
server_tokens off;

# ========== 客户端配置 ==========
client_max_body_size 50M;
client_body_timeout 60s;
client_header_timeout 30s;

# ========== Gzip 压缩 ==========
gzip on;
gzip_vary on;
gzip_min_length 1024;
gzip_comp_level 6;
gzip_types
    text/plain
    text/css
    text/javascript
    application/javascript
    application/json
    application/xml
    image/svg+xml
    font/woff2;

# ========== API 反向代理 ==========
location /api/ {
    # 限流（需在 nginx.conf http{} 块中定义 limit_req_zone）
    # limit_req zone=api_limit burst=20 nodelay;
    
    proxy_pass http://127.0.0.1:8080/api/;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header Connection "";
    
    proxy_connect_timeout 10s;
    proxy_send_timeout 60s;
    proxy_read_timeout 60s;
    
    proxy_buffering on;
    proxy_buffer_size 4k;
    proxy_buffers 8 4k;
}

# 登录接口单独限流（更严格）
location /api/auth/login {
    # limit_req zone=login_limit burst=3 nodelay;
    
    proxy_pass http://127.0.0.1:8080/api/auth/login;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}

# ========== 后台管理系统 ==========
location /admin {
    alias /var/www/yibao/admin;
    index index.html;
    try_files $uri $uri/ /admin/index.html;
    
    # 静态资源长期缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        alias /var/www/yibao/admin;
        expires 30d;
        add_header Cache-Control "public, immutable";
        access_log off;
    }
}

# ========== 前台展示站（根路径） ==========
location / {
    root /var/www/yibao/frontend;
    index index.html;
    try_files $uri $uri/ /index.html;
    
    # HTML 文件不缓存（确保更新即时生效）
    location ~* \.html$ {
        expires -1;
        add_header Cache-Control "no-cache, no-store, must-revalidate";
    }
    
    # 静态资源长期缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 30d;
        add_header Cache-Control "public, immutable";
        access_log off;
    }
}

# ========== 健康检查端点 ==========
location /health {
    access_log off;
    return 200 "OK\n";
    add_header Content-Type text/plain;
}
EOF
```

### 7.3 在 nginx.conf 中添加限流配置

```bash
# 编辑 /etc/nginx/nginx.conf，在 http{} 块中添加：
sudo sed -i '/http {/a\    limit_req_zone $binary_remote_addr zone=api_limit:10m rate=30r/m;\n    limit_req_zone $binary_remote_addr zone=login_limit:10m rate=5r/m;' /etc/nginx/nginx.conf
```

### 7.4 启用配置并测试

```bash
# 启用站点配置
sudo ln -s /etc/nginx/sites-available/yibao.conf /etc/nginx/sites-enabled/

# 删除默认站点（避免冲突）
sudo rm -f /etc/nginx/sites-enabled/default

# 测试配置语法
sudo nginx -t

# 重载 Nginx
sudo systemctl reload nginx

# 验证访问
curl -I http://localhost/
curl -I http://localhost/admin/
curl -s http://localhost/api/public/site-config/basic
```

---

## 8. SSL/TLS 证书配置

### 8.1 使用 Let's Encrypt 免费证书（推荐）

```bash
# 安装 Certbot
sudo apt-get install -y certbot python3-certbot-nginx

# 申请证书（替换为实际域名）
sudo certbot --nginx -d 你的域名 --non-interactive --agree-tos -m 管理员邮箱

# 验证证书自动续期
sudo certbot renew --dry-run
```

### 8.2 使用政务/商业 SSL 证书

若使用辽宁省政务云或商业 CA 颁发的证书：

```bash
# 创建证书目录
sudo mkdir -p /etc/ssl/yibao
sudo chmod 700 /etc/ssl/yibao

# 上传证书文件（通过 SCP）
# scp fullchain.pem yibao@服务器IP:/etc/ssl/yibao/
# scp privkey.pem yibao@服务器IP:/etc/ssl/yibao/

# 设置证书文件权限
sudo chmod 644 /etc/ssl/yibao/fullchain.pem
sudo chmod 600 /etc/ssl/yibao/privkey.pem
sudo chown root:root /etc/ssl/yibao/*
```

配置完成后，取消注释 Nginx 配置中的 HTTPS server 块，并启用 HTTP→HTTPS 重定向。

---

## 9. Redis 生产配置

### 9.1 修改 Redis 配置文件

```bash
sudo tee /etc/redis/redis.conf << 'EOF'
# ========== 网络配置 ==========
bind 127.0.0.1
port 6379
protected-mode yes

# ========== 认证 ==========
requirepass 【替换为强密码，与 application-prod.yml 保持一致】

# ========== 内存管理 ==========
maxmemory 1gb
maxmemory-policy allkeys-lru
maxmemory-samples 10

# ========== 持久化（AOF） ==========
appendonly yes
appendfilename "appendonly.aof"
appendfsync everysec
no-appendfsync-on-rewrite no
auto-aof-rewrite-percentage 100
auto-aof-rewrite-min-size 64mb

# ========== 持久化（RDB，作为备份） ==========
save 900 1
save 300 10
save 60 10000
dbfilename dump.rdb
dir /var/lib/redis

# ========== 安全 ==========
rename-command FLUSHALL ""
rename-command FLUSHDB ""
rename-command CONFIG "CONFIG_SECURE_CMD"
rename-command DEBUG ""
rename-command EVAL ""

# ========== 连接配置 ==========
maxclients 100
timeout 300
tcp-keepalive 60

# ========== 日志 ==========
loglevel notice
logfile /var/log/redis/redis-server.log
EOF

# 重启 Redis
sudo systemctl restart redis-server

# 验证连接（使用配置的密码）
redis-cli -a 【密码】 ping
# 期望输出：PONG
```

### 9.2 Redis 内存监控

```bash
# 查看内存使用情况
redis-cli -a 【密码】 info memory | grep -E "used_memory_human|maxmemory_human|mem_fragmentation_ratio"

# 查看 Key 数量和过期情况
redis-cli -a 【密码】 info keyspace
```

---

## 10. SQLite 数据库管理

### 10.1 数据库性能优化

SQLite 默认配置在高并发场景下性能有限。通过 Spring Boot 启动时执行 PRAGMA 命令进行优化。在 `DataInitializer.java` 中添加以下初始化逻辑（已在代码中实现）：

```sql
-- WAL 模式（提升并发读写性能）
PRAGMA journal_mode = WAL;

-- 同步模式（NORMAL 在 WAL 模式下安全且性能更好）
PRAGMA synchronous = NORMAL;

-- 缓存大小（单位：页，每页 4KB，此处约 64MB）
PRAGMA cache_size = -16384;

-- 启用外键约束
PRAGMA foreign_keys = ON;

-- 内存映射 I/O（256MB）
PRAGMA mmap_size = 268435456;
```

### 10.2 数据库文件管理

```bash
# 数据库文件位置
ls -lh /opt/yibao/data/imaging_cloud.db*
# imaging_cloud.db       - 主数据库文件
# imaging_cloud.db-shm   - WAL 共享内存文件
# imaging_cloud.db-wal   - WAL 日志文件

# 设置正确的文件权限
sudo chown yibao:yibao /opt/yibao/data/imaging_cloud.db*
sudo chmod 640 /opt/yibao/data/imaging_cloud.db*

# 定期执行 VACUUM 整理数据库碎片（建议每周一次，在业务低峰期执行）
sqlite3 /opt/yibao/data/imaging_cloud.db "VACUUM;"

# 检查数据库完整性
sqlite3 /opt/yibao/data/imaging_cloud.db "PRAGMA integrity_check;"
# 期望输出：ok
```

### 10.3 数据库迁移建议

当满足以下任一条件时，建议迁移至 MySQL 8.0：

- 接入医疗机构超过 50 家
- 日均影像上传量超过 10,000 例
- 并发用户数超过 200
- 数据库文件大小超过 10 GB

---

## 11. 安全加固（必读）

> **重要**：以下安全项目必须在正式上线前全部完成，否则存在数据泄露和系统入侵风险。

### 11.1 【必须执行】密码 BCrypt 加密升级

当前代码中演示数据使用明文密码存储，**生产环境严禁使用明文密码**。

**第一步**：在 `pom.xml` 中确认已添加 `spring-security-crypto` 依赖：

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
    <version>5.3.13.RELEASE</version>
</dependency>
```

**第二步**：在 `AuthServiceImpl.java` 的登录验证逻辑中，将明文比较替换为 BCrypt 验证：

```java
// 修改前（明文比较，禁止用于生产）
if (!user.getPassword().equals(loginRequest.getPassword())) {
    throw new RuntimeException("密码错误");
}

// 修改后（BCrypt 验证）
private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
    throw new RuntimeException("密码错误");
}
```

**第三步**：在 `DataInitializer.java` 中，将初始化用户的密码替换为 BCrypt 哈希值：

```java
// 生成 BCrypt 哈希（在本地执行一次）
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
System.out.println(encoder.encode("admin123")); // 复制输出值

// 在 DataInitializer 中使用哈希值
user.setPassword("$2a$10$...【BCrypt哈希值】...");
```

### 11.2 JWT 密钥强化

```bash
# 生成 32 字节随机密钥
openssl rand -hex 32
# 示例输出：a3f8b2c1d4e5f6a7b8c9d0e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5b6c7d8e9f0a1

# 将生成的密钥填入 application-prod.yml 的 jwt.secret 字段
```

### 11.3 防火墙配置

```bash
# 使用 UFW 配置防火墙
sudo ufw default deny incoming
sudo ufw default allow outgoing

# 允许 SSH（修改为实际 SSH 端口）
sudo ufw allow 22/tcp

# 允许 HTTP 和 HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# 启用防火墙
sudo ufw enable

# 验证规则
sudo ufw status verbose

# 确认 8080 和 6379 端口不对外暴露
sudo ss -tlnp | grep -E "8080|6379"
# 期望：两个端口均只绑定 127.0.0.1
```

### 11.4 敏感数据脱敏

后端 API 返回用户信息时，必须对以下字段进行脱敏处理：

| 字段 | 脱敏规则 | 示例 |
|------|---------|------|
| 身份证号 | 保留前 6 位和后 4 位 | 210224******1234 |
| 手机号 | 保留前 3 位和后 4 位 | 138****5678 |
| 密码 | 永远不返回 | — |
| JWT 密钥 | 永远不返回 | — |

### 11.5 SQL 注入防护

本项目使用 Spring Data JPA，参数化查询已内置防注入。但需注意以下几点：

- 禁止在 Repository 中使用 `@Query` 拼接字符串
- 所有搜索关键词必须通过 JPA 的 `Specification` 或命名查询传递
- 定期检查日志中是否存在异常 SQL 执行记录

### 11.6 CORS 配置收紧

生产环境中，`WebMvcConfig.java` 的 CORS 配置需要将 `allowedOrigins("*")` 替换为实际域名：

```java
// 修改前（允许所有来源，仅用于开发）
config.addAllowedOrigin("*");

// 修改后（仅允许指定域名）
config.addAllowedOrigin("https://你的域名");
config.addAllowedOrigin("https://www.你的域名");
```

---

## 12. 性能优化建议

### 12.1 JVM 参数调优

根据服务器内存大小调整以下参数（以 8GB 内存服务器为例）：

```bash
# 在 yibao-backend.service 的 JAVA_OPTS 中调整
-Xms1g           # 初始堆内存（建议为最大堆的 50%）
-Xmx4g           # 最大堆内存（建议为物理内存的 50%）
-XX:+UseG1GC     # 使用 G1 垃圾收集器
-XX:G1HeapRegionSize=16m
-XX:MaxGCPauseMillis=200
-XX:InitiatingHeapOccupancyPercent=45
-XX:+ParallelRefProcEnabled
```

### 12.2 数据库连接池优化

在 `application-prod.yml` 中添加 HikariCP 配置：

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20        # 最大连接数
      minimum-idle: 5              # 最小空闲连接
      idle-timeout: 600000         # 空闲连接超时（10分钟）
      connection-timeout: 30000    # 获取连接超时（30秒）
      max-lifetime: 1800000        # 连接最大生命周期（30分钟）
      leak-detection-threshold: 60000  # 连接泄漏检测（60秒）
```

### 12.3 Redis 缓存策略

以下数据适合使用 Redis 缓存，可在 Service 层添加 `@Cacheable` 注解：

| 数据类型 | 缓存 Key 模式 | 建议 TTL | 失效策略 |
|---------|-------------|---------|---------|
| 网站基本配置 | `site:config:{group}` | 1 小时 | 后台修改后主动清除 |
| 首页统计数字 | `home:stats` | 30 分钟 | 定时刷新 |
| 政策法规列表 | `policy:list:{page}` | 10 分钟 | 新增/修改后清除 |
| 新闻动态列表 | `news:list:{page}` | 5 分钟 | 新增/修改后清除 |
| 机构列表 | `institution:list` | 1 小时 | 机构变更后清除 |
| JWT Token | `token:{userId}` | 与 JWT 过期时间一致 | 退出登录时清除 |

### 12.4 Nginx 性能调优

在 `/etc/nginx/nginx.conf` 中调整以下参数：

```nginx
worker_processes auto;           # 工作进程数等于 CPU 核数
worker_rlimit_nofile 65535;      # 每个 worker 最大文件描述符

events {
    worker_connections 4096;     # 每个 worker 最大并发连接数
    use epoll;                   # Linux 使用 epoll 事件模型
    multi_accept on;             # 一次接受多个连接
}

http {
    keepalive_timeout 65;        # Keep-Alive 超时
    keepalive_requests 1000;     # 单个连接最大请求数
    
    # 开启 sendfile 和 TCP 优化
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    
    # 连接超时
    send_timeout 30s;
    
    # 开启文件缓存
    open_file_cache max=10000 inactive=30s;
    open_file_cache_valid 60s;
    open_file_cache_min_uses 2;
    open_file_cache_errors on;
}
```

---

## 13. 日志管理

### 13.1 日志文件位置

| 日志类型 | 文件路径 | 说明 |
|---------|---------|------|
| 后端应用日志 | `/opt/yibao/logs/app.log` | Spring Boot 应用日志 |
| 后端错误日志 | `/opt/yibao/logs/app-error.log` | ERROR 级别日志 |
| Nginx 访问日志 | `/var/log/nginx/access.log` | HTTP 访问记录 |
| Nginx 错误日志 | `/var/log/nginx/error.log` | Nginx 错误记录 |
| Redis 日志 | `/var/log/redis/redis-server.log` | Redis 运行日志 |
| Systemd 日志 | `journalctl -u yibao-backend` | 服务启停日志 |

### 13.2 配置 logrotate 日志轮转

```bash
sudo tee /etc/logrotate.d/yibao << 'EOF'
/opt/yibao/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    create 640 yibao yibao
    sharedscripts
    postrotate
        # 通知 Spring Boot 重新打开日志文件
        /bin/kill -HUP $(cat /var/run/yibao-backend.pid 2>/dev/null) 2>/dev/null || true
    endscript
}
EOF

# 测试 logrotate 配置
sudo logrotate --debug /etc/logrotate.d/yibao
```

### 13.3 关键日志监控命令

```bash
# 实时查看后端错误日志
tail -f /opt/yibao/logs/app.log | grep -E "ERROR|WARN"

# 查看最近 100 条 API 请求
tail -n 100 /var/log/nginx/access.log | awk '{print $7, $9}' | sort | uniq -c | sort -rn

# 统计登录失败次数（安全监控）
grep "密码错误\|登录失败\|账号不存在" /opt/yibao/logs/app.log | wc -l

# 查看 Redis 慢查询
redis-cli -a 【密码】 slowlog get 10
```

---

## 14. 监控与告警

### 14.1 健康检查脚本

```bash
cat > /opt/yibao/scripts/health-check.sh << 'SCRIPT'
#!/bin/bash

LOG="/opt/yibao/logs/health-check.log"
ALERT_EMAIL="管理员邮箱@example.com"
DATE=$(date '+%Y-%m-%d %H:%M:%S')

check_service() {
    local name=$1
    local cmd=$2
    if ! eval "$cmd" > /dev/null 2>&1; then
        echo "[$DATE] ALERT: $name 服务异常" >> $LOG
        # 发送告警（需配置 mailutils）
        # echo "$name 服务异常，请立即检查！" | mail -s "[告警] 铁岭医保影像云 $name 异常" $ALERT_EMAIL
        return 1
    fi
    return 0
}

# 检查后端 API
check_service "后端API" "curl -sf http://127.0.0.1:8080/api/public/site-config/basic"

# 检查 Nginx
check_service "Nginx" "curl -sf http://127.0.0.1/health"

# 检查 Redis
check_service "Redis" "redis-cli -a 【密码】 ping | grep -q PONG"

# 检查磁盘空间（超过 85% 告警）
DISK_USAGE=$(df /opt/yibao/data | awk 'NR==2{print $5}' | tr -d '%')
if [ "$DISK_USAGE" -gt 85 ]; then
    echo "[$DATE] ALERT: 数据盘使用率 ${DISK_USAGE}%，请及时清理" >> $LOG
fi

# 检查内存使用（超过 90% 告警）
MEM_USAGE=$(free | awk 'NR==2{printf "%.0f", $3/$2*100}')
if [ "$MEM_USAGE" -gt 90 ]; then
    echo "[$DATE] ALERT: 内存使用率 ${MEM_USAGE}%，请检查" >> $LOG
fi

echo "[$DATE] 健康检查完成" >> $LOG
SCRIPT

chmod +x /opt/yibao/scripts/health-check.sh

# 配置每 5 分钟执行一次健康检查
(crontab -l 2>/dev/null; echo "*/5 * * * * /opt/yibao/scripts/health-check.sh") | crontab -
```

### 14.2 关键监控指标

| 指标 | 正常范围 | 告警阈值 | 检查命令 |
|------|---------|---------|---------|
| 后端 API 响应时间 | < 500ms | > 2000ms | `curl -w "%{time_total}" http://127.0.0.1:8080/api/public/site-config/basic` |
| JVM 堆内存使用率 | < 70% | > 85% | `jstat -gcutil $(pgrep -f imaging-cloud) 1000 5` |
| Redis 内存使用率 | < 70% | > 85% | `redis-cli -a 【密码】 info memory` |
| 磁盘使用率 | < 70% | > 85% | `df -h /opt/yibao/data` |
| 系统负载 | < CPU核数 | > CPU核数×2 | `uptime` |
| Nginx 5xx 错误率 | < 0.1% | > 1% | `grep " 5[0-9][0-9] " /var/log/nginx/access.log \| wc -l` |
| 数据库文件大小 | < 5GB | > 8GB | `du -sh /opt/yibao/data/imaging_cloud.db` |

---

## 15. 数据备份与恢复

### 15.1 自动备份脚本

```bash
cat > /opt/yibao/scripts/backup.sh << 'SCRIPT'
#!/bin/bash

BACKUP_DIR="/opt/yibao/backup"
DATA_DIR="/opt/yibao/data"
DATE=$(date '+%Y%m%d_%H%M%S')
RETENTION_DAYS=30

# 创建备份目录
mkdir -p "$BACKUP_DIR/$DATE"

# 备份 SQLite 数据库（使用 SQLite 在线备份 API，确保数据一致性）
sqlite3 "$DATA_DIR/imaging_cloud.db" ".backup '$BACKUP_DIR/$DATE/imaging_cloud_$DATE.db'"

# 备份应用配置
cp /opt/yibao/config/application-prod.yml "$BACKUP_DIR/$DATE/"

# 压缩备份
tar -czf "$BACKUP_DIR/yibao_backup_$DATE.tar.gz" -C "$BACKUP_DIR" "$DATE"
rm -rf "$BACKUP_DIR/$DATE"

# 删除超过保留期的备份
find "$BACKUP_DIR" -name "yibao_backup_*.tar.gz" -mtime +$RETENTION_DAYS -delete

# 记录备份日志
echo "[$(date '+%Y-%m-%d %H:%M:%S')] 备份完成: yibao_backup_$DATE.tar.gz" >> /opt/yibao/logs/backup.log

# 可选：上传到远程存储（需配置 rclone 或 ossutil）
# rclone copy "$BACKUP_DIR/yibao_backup_$DATE.tar.gz" oss:backup-bucket/yibao/
SCRIPT

chmod +x /opt/yibao/scripts/backup.sh

# 配置每日凌晨 2:00 自动备份
(crontab -l 2>/dev/null; echo "0 2 * * * /opt/yibao/scripts/backup.sh") | crontab -
```

### 15.2 数据恢复流程

```bash
# 1. 停止后端服务
sudo systemctl stop yibao-backend

# 2. 解压备份文件
cd /opt/yibao/backup
tar -xzf yibao_backup_20260415_020000.tar.gz

# 3. 恢复数据库
cp 20260415_020000/imaging_cloud_20260415_020000.db /opt/yibao/data/imaging_cloud.db
chown yibao:yibao /opt/yibao/data/imaging_cloud.db
chmod 640 /opt/yibao/data/imaging_cloud.db

# 4. 验证数据库完整性
sqlite3 /opt/yibao/data/imaging_cloud.db "PRAGMA integrity_check;"

# 5. 重启后端服务
sudo systemctl start yibao-backend

# 6. 验证服务恢复正常
sleep 30
curl -s http://127.0.0.1:8080/api/public/site-config/basic
```

---

## 16. 常见故障排查

### 16.1 后端服务无法启动

**现象**：`systemctl status yibao-backend` 显示 `failed`

**排查步骤**：

```bash
# 查看详细错误日志
sudo journalctl -u yibao-backend -n 100 --no-pager

# 常见原因及解决方案：

# 原因1：端口 8080 被占用
sudo ss -tlnp | grep 8080
# 解决：kill 占用进程或修改 application-prod.yml 中的端口

# 原因2：Redis 未启动
sudo systemctl status redis-server
sudo systemctl start redis-server

# 原因3：数据库文件权限错误
ls -la /opt/yibao/data/
sudo chown yibao:yibao /opt/yibao/data/imaging_cloud.db

# 原因4：配置文件语法错误
java -jar /opt/yibao/app/imaging-cloud-portal.jar \
  --spring.config.additional-location=/opt/yibao/config/application-prod.yml \
  --spring.profiles.active=prod 2>&1 | head -50
```

### 16.2 前台页面返回 404

**现象**：刷新页面后出现 Nginx 404 错误

**原因**：Vue Router 使用 HTML5 History 模式，Nginx 需要配置 `try_files`

**解决**：确认 Nginx 配置中包含 `try_files $uri $uri/ /index.html;`（已在上述配置中包含）

### 16.3 API 请求返回 401

**现象**：登录后访问 API 返回 `{"code":401,"msg":"Token已过期或无效"}`

**排查步骤**：

```bash
# 检查 Redis 中 Token 是否存在
redis-cli -a 【密码】 keys "token:*"

# 检查 JWT 密钥是否与生成 Token 时一致
grep "jwt.secret" /opt/yibao/config/application-prod.yml

# 检查服务器时间是否正确（JWT 依赖时间戳）
date
timedatectl status
```

### 16.4 数据库锁定错误

**现象**：日志中出现 `SQLITE_BUSY: database is locked`

**原因**：SQLite 在高并发写入时可能出现锁竞争

**解决方案**：

```bash
# 方案1：增加 SQLite 等待超时时间（在 application-prod.yml 中添加）
spring:
  datasource:
    url: jdbc:sqlite:/opt/yibao/data/imaging_cloud.db?busy_timeout=30000

# 方案2：检查是否有未正常关闭的数据库连接
lsof /opt/yibao/data/imaging_cloud.db

# 方案3：如果频繁出现，考虑迁移到 MySQL
```

### 16.5 内存溢出（OOM）

**现象**：服务自动重启，`/opt/yibao/logs/heap-dump.hprof` 文件生成

**排查步骤**：

```bash
# 分析堆转储文件（需要 Eclipse Memory Analyzer）
# 或使用命令行工具查看大对象
jmap -histo $(pgrep -f imaging-cloud) | head -20

# 临时解决：增加 JVM 堆内存
# 修改 yibao-backend.service 中的 -Xmx 参数

# 根本解决：分析内存泄漏原因，检查是否有大量对象未被 GC 回收
```

---

## 17. 版本升级流程

### 17.1 标准升级步骤

```bash
# 1. 备份当前数据
/opt/yibao/scripts/backup.sh

# 2. 拉取最新代码
cd /opt/yibao/app/Yinaoyingxiangyun
git pull origin main

# 3. 构建新版本后端
cd backend
mvn clean package -DskipTests
cp target/imaging-cloud-portal-*.jar /opt/yibao/app/imaging-cloud-portal-new.jar

# 4. 构建新版本前台
cd ../frontend
pnpm install && pnpm build
sudo cp -r dist/* /var/www/yibao/frontend-new/

# 5. 构建新版本后台管理
cd ../admin
pnpm install && pnpm build
sudo cp -r dist/* /var/www/yibao/admin-new/

# 6. 停止服务（业务低峰期执行）
sudo systemctl stop yibao-backend

# 7. 替换文件
mv /opt/yibao/app/imaging-cloud-portal.jar /opt/yibao/app/imaging-cloud-portal-backup.jar
mv /opt/yibao/app/imaging-cloud-portal-new.jar /opt/yibao/app/imaging-cloud-portal.jar
sudo rsync -a --delete /var/www/yibao/frontend-new/ /var/www/yibao/frontend/
sudo rsync -a --delete /var/www/yibao/admin-new/ /var/www/yibao/admin/

# 8. 启动新版本
sudo systemctl start yibao-backend

# 9. 验证升级成功
sleep 30
curl -s http://127.0.0.1:8080/api/public/site-config/basic | python3 -m json.tool
```

### 17.2 回滚流程

```bash
# 停止服务
sudo systemctl stop yibao-backend

# 恢复后端 jar 包
mv /opt/yibao/app/imaging-cloud-portal.jar /opt/yibao/app/imaging-cloud-portal-failed.jar
mv /opt/yibao/app/imaging-cloud-portal-backup.jar /opt/yibao/app/imaging-cloud-portal.jar

# 恢复数据库（如果数据库结构有变更）
# /opt/yibao/scripts/restore.sh 【备份文件名】

# 重启服务
sudo systemctl start yibao-backend
```

---

## 18. 上线前检查清单

在正式对外开放前，请逐项确认以下内容：

### 18.1 安全检查

- [ ] **密码 BCrypt 加密**：所有用户密码已使用 BCrypt 哈希存储，无明文密码
- [ ] **JWT 密钥替换**：`jwt.secret` 已替换为 32 位以上随机字符串
- [ ] **Redis 密码设置**：Redis 已配置强密码认证
- [ ] **端口防护**：8080 和 6379 端口仅监听 127.0.0.1，不对外暴露
- [ ] **防火墙启用**：UFW 已启用，仅开放 22/80/443 端口
- [ ] **CORS 配置**：已将 `allowedOrigins("*")` 替换为实际域名
- [ ] **HTTPS 启用**：SSL 证书已配置，HTTP 已强制跳转 HTTPS
- [ ] **安全响应头**：Nginx 已配置 X-Frame-Options、CSP 等安全头
- [ ] **危险 Redis 命令禁用**：FLUSHALL、FLUSHDB、DEBUG 等命令已禁用

### 18.2 功能验证

- [ ] **前台首页正常访问**：`https://域名/` 可正常打开
- [ ] **后台管理登录**：`https://域名/admin` 可用 admin 账号登录
- [ ] **API 接口正常**：`https://域名/api/public/site-config/basic` 返回正确数据
- [ ] **SSO 登录流程**：四种角色（监管/医疗机构/参保人员/开发者）均可正常登录
- [ ] **内容管理**：后台新增政策法规后，前台可正常显示
- [ ] **移动端适配**：在手机浏览器中访问前台，布局正常

### 18.3 性能验证

- [ ] **首页加载时间**：在 4G 网络下首页加载时间 < 3 秒
- [ ] **API 响应时间**：常用接口响应时间 < 500ms
- [ ] **并发测试**：使用 `ab` 或 `wrk` 进行基础并发测试，50 并发下无 5xx 错误

### 18.4 运维准备

- [ ] **自动备份配置**：每日凌晨 2:00 自动备份任务已配置
- [ ] **健康检查配置**：每 5 分钟健康检查任务已配置
- [ ] **日志轮转配置**：logrotate 已配置，防止日志文件过大
- [ ] **监控告警配置**：关键指标告警已配置
- [ ] **备份恢复演练**：已完成至少一次完整的备份恢复演练
- [ ] **运维文档归档**：本文档已打印并存档，相关人员已阅读

### 18.5 业务准备

- [ ] **初始管理员密码已修改**：默认 admin/admin123 已修改为强密码
- [ ] **网站基本信息已配置**：主办单位、联系电话、ICP 备案号已在后台填写
- [ ] **首页内容已配置**：Banner、统计数字、新闻动态已在后台录入
- [ ] **接入机构信息已录入**：铁岭市各医疗机构信息已在后台维护
- [ ] **政策法规文件已上传**：相关政策文件已在后台发布

---

## 附录 A：常用运维命令速查

```bash
# ===== 服务管理 =====
sudo systemctl start|stop|restart|status yibao-backend
sudo systemctl reload nginx
sudo systemctl restart redis-server

# ===== 日志查看 =====
sudo journalctl -u yibao-backend -f          # 实时查看后端日志
tail -f /opt/yibao/logs/app.log              # 应用日志
tail -f /var/log/nginx/access.log            # Nginx 访问日志
tail -f /var/log/nginx/error.log             # Nginx 错误日志

# ===== 进程检查 =====
ps aux | grep imaging-cloud                  # 查看后端进程
sudo ss -tlnp | grep -E "80|443|8080|6379"  # 查看端口监听

# ===== 资源监控 =====
free -h                                      # 内存使用
df -h /opt/yibao/data                        # 磁盘使用
top -u yibao                                 # CPU 使用

# ===== 数据库操作 =====
sqlite3 /opt/yibao/data/imaging_cloud.db ".tables"    # 查看所有表
sqlite3 /opt/yibao/data/imaging_cloud.db "SELECT count(*) FROM sys_user;"
sqlite3 /opt/yibao/data/imaging_cloud.db "PRAGMA integrity_check;"

# ===== Redis 操作 =====
redis-cli -a 【密码】 info server            # 服务器信息
redis-cli -a 【密码】 info memory            # 内存信息
redis-cli -a 【密码】 dbsize                 # Key 数量
redis-cli -a 【密码】 keys "token:*"         # 查看所有 Token
```

---

## 附录 B：演示账号（上线前必须修改）

| 账号 | 初始密码 | 角色 | 说明 |
|------|---------|------|------|
| admin | admin123 | 系统管理员 | 后台管理系统登录 |
| doctor001 | doctor123 | 医疗机构 | 前台展示站登录 |
| dev001 | dev123 | 系统开发者 | 前台展示站登录 |
| 210224199001011234 | patient123 | 参保人员 | 以身份证号登录 |

> **警告**：以上账号密码为演示数据，正式上线前必须全部修改，并按照第 11.1 节完成 BCrypt 加密升级。

---

*本文档由融御科技技术团队编制，适用于铁岭市医保影像云统一服务门户项目生产环境部署。如有疑问，请联系技术支持。*
