# 设计：重建 Spring Boot 4 / Hibernate 7 主线

## 技术方案

本次升级将继续以现有 Spring Boot Starter 实现作为基础，不做功能性重写，重点完成主线裁剪、模块命名、构件坐标和兼容性提升。Hibernate 集成不再延续历史分线，主线只保留 Hibernate 7 支持。

## 架构决策

### 决策：主线只保留 Spring Boot 4，不并行维护旧 Boot 支持线

由于本次明确不保留旧用户兼容，因此 `spring-boot-starter`、`spring-boot2-starter` 都应从主线移除，仅保留 `spring-boot4-starter`。这样 Reactor、文档和发布物只表达当前真实支持能力。

### 决策：Hibernate 相关支持只保留 Hibernate 7

Spring Boot 4 的主线依赖生态应与 Hibernate 7 对齐，因此 `hibernate3`、`hibernate4`、`hibernate5` 模块都应从主线移除。仓库应改为提供 `hibernate7` 模块，避免继续维护与主线不一致的历史集成。

### 决策：仓库主线统一提升到 Java 17

Spring Boot 4 要求 Java 17 及以上版本。相比只在单个子模块上单独提升 Java 版本，直接将仓库主线统一切到 Java 17，更利于 Reactor 构建、一致发布和后续维护。

### 决策：代码改动保持最小化

当前 Starter 已经使用 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册自动配置，整体方向与现代 Spring Boot 机制一致。因此实现时只修改那些在 Spring Boot 4 管理依赖下实际不兼容的 API 或测试代码。

## 模块与构件调整

### 根构建

根 `pom.xml` 将调整为：

- 将 Java 构建基线提升到 17
- 将 `maven-compiler-plugin` 的 `source` 和 `target` 更新为 17
- 将 Boot 4 Starter 模块加入 Reactor 构建
- 从 Reactor 中移除旧 Boot 与旧 Hibernate 模块
- 将 Hibernate 7 模块加入 Reactor 构建

### Starter 模块

当前 Boot 3 Starter 支持线将调整为：

- 目录：`modules/spring-boot3-starter` -> `modules/spring-boot4-starter`
- 构件：`j2cache-spring-boot3-starter` -> `j2cache-spring-boot4-starter`
- parent 和内部 J2Cache 依赖版本与当前根项目版本保持一致
- Spring Boot BOM 升级到 4.x 版本线

### Hibernate 模块

仓库将删除：

- `modules/hibernate3`
- `modules/hibernate4`
- `modules/hibernate5`

仓库将新增或迁移得到：

- `modules/hibernate7`

`hibernate7` 模块应使用 Jakarta 命名空间和 Hibernate 7 兼容 API，不保留旧 `javax.*` 时代的适配代码。

### 集成测试模块

仓库将新增：

- `modules/integration-tests`

该模块只承担主线验证职责，不作为对外发布给业务方使用的能力模块。它将统一覆盖两组测试：

- Hibernate 7 + H2 内存数据库的二级缓存集成测试
- Spring Cache + J2Cache 适配层的缓存行为测试

测试模块应独立依赖：

- `j2cache-core`
- `j2cache-hibernate7`
- `j2cache-springcache`
- `j2cache-spring-boot4-starter`（如用于复用现有自动配置或行为验证）
- `hibernate-core`
- `spring-context-support`
- `h2`
- `junit-jupiter`

## 需要重点验证的兼容区域

### 自动配置装配

需要重点检查以下导入和注解在 Boot 4 下是否仍然可用：

- `@AutoConfigureAfter`
- `@AutoConfigureBefore`
- `@ConditionalOnClass`
- `@EnableConfigurationProperties`
- `RedisAutoConfiguration`

### Redis 集成

Spring Data Redis API 演进是本次升级最容易出问题的区域，需要重点验证并按需调整：

- `JedisConnectionFactory`
- `LettuceConnectionFactory`
- `RedisStandaloneConfiguration`
- `RedisSentinelConfiguration`
- `RedisClusterConfiguration`
- `RedisTemplate` 与消息监听容器相关装配

### 测试

当前 Starter 测试仍然依赖 JUnit 4，并且存在阻塞式测试行为。升级过程中需要：

- 迁移到 JUnit Jupiter
- 更新断言和测试注解
- 去掉 `System.in.read()` 这类阻塞自动化执行的代码

新增集成测试模块需要满足：

- Hibernate 7 测试使用 H2，不依赖外部 MySQL 或 Redis 环境
- Spring Cache 测试验证 `@Cacheable` 与 `@CacheEvict` 的基本行为
- 测试模块可以通过单独命令执行，例如 `mvn -pl modules/integration-tests test`

## 文档调整

需要同步更新以下文档中的对外使用说明：

- `README.md`
- `CHANGES.md`
- `docs/UPGRADE.md`

同时需要移除所有对旧 Boot / 旧 Hibernate 支持线的对外说明。

## 验证标准

以下条件全部满足时，视为本次变更完成：

1. Boot 4 Starter 能在 Reactor 中成功编译并通过测试
2. Hibernate 7 模块已纳入 Reactor 并能成功编译
3. 旧 Boot / 旧 Hibernate 模块已从主线 Reactor 中移除
4. 独立集成测试模块已纳入 Reactor，并能验证 Hibernate 7 与 Spring Cache 主线能力
5. 仓库能够在 Java 17 环境下完成主线构建
6. 文档中的模块与构件坐标与实际实现保持一致
