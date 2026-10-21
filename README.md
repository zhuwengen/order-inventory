# Order Inventory

## 1. 项目简介

Order Inventory 是一个基于 Spring Boot 的订单与库存服务项目。目前已完成基础工程配置，包括 Web MVC、MySQL 数据源、Flyway 数据库迁移和 Actuator 健康检查。

当前源码仅包含应用启动类和上下文加载测试；业务接口、订单及库存领域功能尚未实现。

## 2. 技术环境

- Java 17
- Spring Boot 4.1.1
- Maven Wrapper（推荐使用项目内的 `mvnw.cmd`，无需单独安装 Maven）
- Spring Web MVC
- Spring JDBC
- MySQL Connector/J
- Flyway 与 `flyway-mysql`
- Spring Boot Actuator
- MySQL 8.x（建议）

## 3. 本地环境准备

1. 安装并启动 MySQL，确保本地 `3306` 端口可访问。
2. 安装 JDK 17，并确认命令行可执行：

   ```powershell
   java -version
   ```

3. 创建数据库。迁移脚本只负责创建表，不会创建数据库本身：

   ```sql
   CREATE DATABASE order_inventory
     DEFAULT CHARACTER SET utf8mb4
     COLLATE utf8mb4_unicode_ci;
   ```

4. 检查 `src/main/resources/application.yml` 或 `src/main/resources/application-local.yml` 中的数据源配置。当前本地默认连接为：

   ```yaml
   spring:
     datasource:
       url: "jdbc:mysql://localhost:3306/order_inventory?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai"
       username: root
       password: root
   ```

   请按本机 MySQL 账号修改用户名和密码。请勿提交包含真实密码的配置文件。

## 4. 数据库初始化与迁移

数据库迁移脚本位于 `src/main/resources/db/migration/`，文件名遵循 Flyway 的版本化迁移格式：`V<版本号>__<描述>.sql`。

当前已有迁移：

- `V1__create_training_table.sql`：创建 `t_training_record` 表，包含自增主键、描述和创建时间字段。

本地配置文件 `application-local.yml` 已启用 Flyway，并指定迁移目录为 `classpath:db/migration`。以 `local` Profile 启动应用时，Flyway 会在连接到 `order_inventory` 后执行尚未应用的迁移。

新增表结构时，请创建新的迁移文件，例如 `V2__create_order_table.sql`；不要修改已经在其他环境执行过的版本化迁移脚本。

## 5. 应用启动步骤

在项目根目录执行以下命令。

1. 先下载依赖、编译并运行测试：

   ```powershell
   .\mvnw.cmd test
   ```

2. 使用本地 Profile 启动应用，以启用 Flyway 迁移：

   ```powershell
   .\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=local"
   ```

3. 或先打包再运行：

   ```powershell
   .\mvnw.cmd clean package
   java -jar .\target\order-inventory-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
   ```

首次构建会下载 Maven 和项目依赖，耗时通常比后续构建更长。

## 6. 启动成功的验证方法

应用默认使用 Spring Boot 的 `8080` 端口。启动成功后，在另一个 PowerShell 窗口执行：

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

预期响应中应包含：

```json
{
  "status": "UP"
}
```

项目只暴露了 `health` Actuator 端点，因此可通过浏览器或 HTTP 客户端访问 `http://localhost:8080/actuator/health`。健康检查详情被配置为显示；响应中可能包含数据库组件的状态。

## 7. 常见问题与排查

### 无法连接 MySQL

确认 MySQL 服务正在运行、端口为 `3306`，并核对数据库名、用户名和密码。可以使用 MySQL 客户端验证连接：

```powershell
mysql -h localhost -P 3306 -u root -p order_inventory
```

如果 MySQL 使用了不同的主机、端口或账号，请同步更新对应 Profile 的 `spring.datasource` 配置。

### 提示数据库 `order_inventory` 不存在

先执行“本地环境准备”中的 `CREATE DATABASE` 语句。Flyway 迁移不会替你创建数据库。

### Flyway 没有执行迁移

请确认以 `local` Profile 启动，且使用的是 `src/main/resources/application-local.yml` 中的 `spring.flyway.enabled: true` 配置。也请检查迁移文件位于 `src/main/resources/db/migration/`，并符合 Flyway 命名规则。

### 构建时下载依赖很慢或失败

首次执行 `mvnw.cmd` 会从 Maven Central 下载 Maven 与依赖。请检查网络连接、代理或镜像仓库设置，然后重试：

```powershell
.\mvnw.cmd -U test
```

### 健康检查返回非 `UP`

先查看应用启动日志。若数据库组件状态异常，优先检查 MySQL 是否启动、数据源连接信息是否正确，以及当前账号是否有访问 `order_inventory` 的权限。

### 端口 8080 已被占用

停止占用该端口的进程，或在配置中设置其他端口，例如：

```yaml
server:
  port: 8081
```
