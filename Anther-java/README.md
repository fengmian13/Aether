# Anther Java

Anther Java 是一个基于 Spring Boot 的即时通讯后端项目，提供账号、联系人、群组、会话、消息发送、文件上传下载，以及音视频通话相关能力。项目同时集成了 Netty WebSocket，用于处理实时消息推送与在线连接管理。

## 项目介绍

### 核心能力

- 账号注册、登录、验证码、资料维护、密码修改
- 联系人搜索、申请、审批、黑名单管理
- 群组创建、查询、解散、退群
- 聊天会话管理与消息发送
- 文件上传与下载
- 基于 Netty 的 WebSocket 实时通信
- 音频/视频通话流程管理
- Redis / RabbitMQ 消息分发支持

### 技术栈

- Java 17
- Spring Boot 2.7.18
- Maven
- MyBatis
- MySQL 8
- Redis
- Netty
- RabbitMQ
- Lombok

### 默认服务配置

项目默认配置位于 `src/main/resources/application.properties`：

- HTTP 服务端口：`6060`
- WebSocket 端口：`6061`
- API 上下文路径：`/api`
- 数据库：MySQL `anther`
- Redis：`127.0.0.1:6379`
- 消息处理通道：`redis`

因此默认接口访问前缀为：

```text
http://localhost:6060/api
```

WebSocket 默认接入地址为：

```text
ws://localhost:6061/ws
```

## 项目结构

```text
src/main/java/com/anther
├─ Controller        控制器层
├─ service           业务接口
├─ service/impl      业务实现
├─ mappers           MyBatis Mapper 接口
├─ entity            实体、DTO、VO、枚举、配置
├─ websocket         WebSocket 与消息处理
├─ redis             Redis 相关配置与组件
├─ task              定时任务
└─ utils             工具类

src/main/resources
├─ application.properties   应用配置
├─ logback-spring.xml       日志配置
├─ sql/call.sql             通话相关表结构
└─ com/anther/mappers       MyBatis XML 映射
```

## 项目构建

### 1. 环境要求

- JDK 17
- Maven 3.6+
- MySQL 8.x
- Redis 6.x+
- 可选：RabbitMQ（当 `messaging.handle.channel=rabbitmq` 时需要）

### 2. 配置依赖服务

启动项目之前，请先准备并按需修改 `src/main/resources/application.properties` 中的配置：

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.redis.host`
- `spring.redis.port`
- `spring.redis.password`
- `messaging.handle.channel`
- `project.folder`

当前仓库中的配置包含本地开发默认值，实际部署前建议按环境重新配置。

### 3. 初始化数据库

项目使用 MySQL，除业务主表外，仓库中已提供通话模块建表脚本：

```text
src/main/resources/sql/call.sql
```

请先创建数据库：

```sql
CREATE DATABASE anther DEFAULT CHARACTER SET utf8mb4;
```

然后导入项目所需表结构。`call.sql` 可直接执行，其余业务表请结合现有 Mapper 与实体表结构补齐。

### 4. 编译打包

在项目根目录执行：

```bash
mvn clean package
```

打包完成后，产物默认位于：

```text
target/anther-1.0.jar
```

### 5. 本地启动

方式一：使用 Maven 启动

```bash
mvn spring-boot:run
```

方式二：直接运行 Jar

```bash
java -jar target/anther-1.0.jar
```

启动后：

- HTTP 接口服务运行在 `6060`
- Netty WebSocket 服务运行在 `6061`

## 主要接口模块

- `/account`：注册、登录、验证码、资料与密码维护
- `/userContact`：联系人搜索、申请、审批、联系人列表
- `/group`：群组管理
- `/chat`：聊天消息、文件上传下载
- `/call`：音视频通话创建、接听、拒绝、取消、挂断、RTC 配置查询
- `/userInfo`：用户信息管理

完整接口可在 `src/main/java/com/anther/Controller` 下查看。

## 说明

- 项目主启动类：`com.anther.AntherApplication`
- 当前工程为 `jar` 打包方式
- 项目已启用异步任务、事务管理和定时任务
- WebSocket 基于 Netty 独立端口启动，不走 Spring MVC 端口复用

## 开发建议

- 不建议将数据库和 Redis 密码直接保存在正式环境配置文件中
- 建议按环境拆分配置，例如 `application-dev.properties`、`application-prod.properties`
- 如果需要切换消息分发实现，可调整 `messaging.handle.channel` 为 `redis` 或 `rabbitmq`

