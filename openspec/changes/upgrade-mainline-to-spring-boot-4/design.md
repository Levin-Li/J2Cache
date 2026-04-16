# 设计：主线升级到 Spring Boot 4

## 技术方案

本次升级将继续以现有 Spring Boot Starter 实现作为基础，不做功能性重写，重点完成依赖、模块命名、构件坐标和兼容性提升。

## 架构决策

### 决策：直接替换 Boot 3 支持线，而不是并行维护 Boot 3 与 Boot 4

仓库当前已经按 Boot 代际拆分 Starter。由于本次明确不保留旧用户兼容，因此最清晰的方案就是直接把当前 Boot 3 支持线升级并重命名为 Boot 4，使模块名、构件名和实际支持能力保持一致。

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

### Starter 模块

当前 Boot 3 Starter 支持线将调整为：

- 目录：`modules/spring-boot3-starter` -> `modules/spring-boot4-starter`
- 构件：`j2cache-spring-boot3-starter` -> `j2cache-spring-boot4-starter`
- parent 和内部 J2Cache 依赖版本与当前根项目版本保持一致
- Spring Boot BOM 升级到 4.x 版本线

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

## 文档调整

需要同步更新以下文档中的对外使用说明：

- `README.md`
- `CHANGES.md`
- `docs/UPGRADE.md`

## 验证标准

以下条件全部满足时，视为本次变更完成：

1. Boot 4 Starter 能在 Reactor 中成功编译
2. Starter 测试能在 Boot 4 依赖栈下通过
3. 仓库能够在 Java 17 环境下成功打包
4. 文档中的构件坐标与实际实现保持一致
