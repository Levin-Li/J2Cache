# J2Cache Spring Boot 4 升级设计

## 目标

将 J2Cache 当前主线升级为支持 Spring Boot 4，并让发布的 Maven 构件与该支持能力保持一致。现有 Spring Boot 3 用户的兼容性不在本次范围内。

## 范围

本次变更包含：

- 将当前 Spring Boot 3 Starter 支持线升级为 Spring Boot 4
- 将仓库构建基线切换到 Java 17
- 调整 Maven 模块命名与发布构件坐标，使其准确表达 Spring Boot 4 支持
- 更新 Starter 测试与相关文档，确保仓库能够稳定构建、测试和发布

本次变更不包含：

- 保留对旧 Spring Boot 3 用户的兼容能力
- 继续保留旧 Starter 坐标作为兼容别名或包装层
- 与本次升级无关的额外重构

## 当前状态

该仓库是一个 Maven 多模块项目，根构建当前仍使用 Java 8 编译配置。仓库中存在多条 Spring Boot Starter 支持线，其中包含 `modules/spring-boot3-starter` 模块。该模块已经使用 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 这种现代 Spring Boot 自动配置注册方式，但其 `pom.xml` 中仍保留了历史版本的硬编码 parent 和依赖配置。

此外，根聚合 `pom.xml` 当前并未将 `modules/spring-boot3-starter` 纳入 `<modules>` 列表，这意味着这条 Boot 3 支持线目前并没有真正进入主 Reactor 构建。

## 选定方案

采用直接升级主线的方式：

1. 将 `modules/spring-boot3-starter` 重命名为 `modules/spring-boot4-starter`
2. 将发布构件从 `j2cache-spring-boot3-starter` 重命名为 `j2cache-spring-boot4-starter`
3. 将仓库构建基线提升到 Java 17
4. 将 Starter 的依赖管理升级到 Spring Boot 4
5. 仅针对 Spring Framework 7 / Spring Boot 4 的兼容问题做最小代码调整
6. 同步更新测试和文档，使其与新的支持线一致

这个方案可以让仓库结构保持清晰，避免出现模块名称与实际支持能力不一致的问题。

## 备选方案

### 保留 `spring-boot3-starter` 模块名，只升级内部依赖版本

不采用。因为模块名与 artifactId 会立即失真，长期会误导使用者。

### 新增一个 `spring-boot4-starter`，同时继续保留 Spring Boot 3 线

不采用。因为本次已经明确不考虑旧用户兼容，并行维护只会扩大改动面和后续维护成本。

## 构建与构件调整

### 根构建

根 `pom.xml` 将做如下调整：

- 将编译基线设置为 Java 17
- 将 `maven-compiler-plugin` 的 `source` 和 `target` 更新为 17
- 增加统一 Maven 属性，避免重复硬编码 Java 版本
- 将 Spring Boot 4 Starter 模块纳入 Reactor 构建

这里的目标是让 Java 17 成为仓库主线统一基线，而不是只在某个 Boot Starter 子模块中单独提升。

### Starter 模块命名

当前 Boot 3 Starter 模块将按如下方式重命名：

- 目录：`modules/spring-boot3-starter` -> `modules/spring-boot4-starter`
- 构件：`j2cache-spring-boot3-starter` -> `j2cache-spring-boot4-starter`

模块的 parent 声明将与当前根项目版本对齐，不再保留 `2.8.x-release` 这类历史硬编码值。

### 依赖管理

Starter 模块将引入 Spring Boot 4 的 BOM，并尽量依赖该 BOM 管理 Spring 相关依赖版本。

具体清理目标：

- 移除 Starter 模块中遗留的旧版 J2Cache 硬编码版本
- 移除与 Boot 4 测试栈冲突的老旧测试依赖
- 保持 Starter 依赖面尽可能精简，让 Boot BOM 负责版本管理

## 代码兼容性调整

现有 Starter 实现将保留，并做最小必要适配。

### 自动配置

Starter 已使用 `AutoConfiguration.imports`，因此在自动配置注册机制上与 Boot 4 原则上兼容。实现阶段主要需要核查引用到的自动配置类与注解在 Boot 4 中是否发生包名变化、行为变化或弃用。

重点验证项：

- `@AutoConfigureAfter`
- `@AutoConfigureBefore`
- `@ConditionalOnClass`
- `@EnableConfigurationProperties`
- `RedisAutoConfiguration`

### Redis 集成

本次升级中最大的兼容风险集中在 Spring Data Redis 以及 Boot 4 管理下的 Jedis/Lettuce 版本演进。

实现时需要重点核查并按需调整：

- `JedisConnectionFactory` 的构造方式
- `LettuceConnectionFactory` 的构造方式
- `RedisSentinelConfiguration`、`RedisClusterConfiguration` 与 `RedisStandaloneConfiguration` 的使用方式
- `RedisTemplate` 的序列化与消息监听容器配置

首选结果是保留现有结构，只修复那些在 Boot 4 依赖栈下无法编译或行为明显不兼容的点。

### 测试

当前 Starter 测试仍使用 JUnit 4。升级到 Boot 4 后，测试应迁移到 JUnit Jupiter，并同步更新 Spring Boot 测试注解的写法。

迁移内容包括：

- 替换 JUnit 4 的导入与 Runner 用法
- 将断言迁移到 JUnit Jupiter 或 AssertJ
- 去除阻塞自动化执行的测试行为

现有 `ApplicationTests` 中存在 `System.in.read()`，必须删除或改写，否则会阻塞自动化测试执行。

## 文档调整

以下文档需要同步更新：

- `README.md` 中关于新的 Spring Boot 4 Starter Maven 依赖示例
- `CHANGES.md` 中关于 Boot 4 升级的发布说明
- `docs/UPGRADE.md` 中关于模块和构件重命名的发布流程说明

所有指向旧 Spring Boot 3 Starter 坐标的说明都需要更新或移除。

## 验证策略

只有在以下条件满足，或失败原因被明确识别并限定范围时，才可以认为实现完成：

1. Spring Boot 4 Starter 模块能在 Maven Reactor 中成功编译
2. Starter 模块在迁移到 Boot 4 测试栈后测试通过
3. 根 Reactor 在 Java 17 下能够成功完成 package/build
4. 生成和发布的构件坐标与 README 示例一致，均指向新的 Spring Boot 4 Starter

推荐的验证命令：

- `mvn test -pl modules/spring-boot4-starter -am`
- `mvn package -DskipTests`

如果 Java 17 仓库级升级引发其他历史模块失败，则逐个判断是否进行最小范围修复；默认策略是不做无关重构，只做维持主 Reactor 健康所必需的改动。

## 风险

### 仓库级 Java 17 升级风险

根项目当前仍以 Java 8 为目标版本。切换到 Java 17 后，可能会暴露出一些历史模块在现代工具链下未曾发现的编译告警或错误。

### Spring Data Redis API 演进风险

Starter 当前手工装配 Redis 连接工厂，这一部分最容易受到 Boot 4 依赖升级影响。

### 其他 Starter 或模块的构建漂移

仓库中部分 Starter 模块存在历史版本硬编码和 Reactor 集成不一致的问题。清理 Boot 4 主线时，可能会顺带暴露其他类似问题。

## 实施边界

实现过程中应优先选择最小且正确的改动：

- 不添加 Spring Boot 3 兼容垫片
- 不并行引入第二个 Boot 4 模块
- 不重构无关缓存逻辑
- 除非 Boot 4 兼容性确有要求，否则不调整对外配置语义

## 成功标准

满足以下条件即视为本次工作成功：

- 仓库主线统一切到 Java 17
- Boot Starter 支持线被清晰标识并以 Spring Boot 4 对外发布
- 更新后的 Starter 能在 Spring Boot 4 环境下通过编译和测试
- 项目文档与实际构件坐标保持一致
