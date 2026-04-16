# J2Cache 独立集成测试模块设计

## 目标

新增一个独立测试模块，统一验证 J2Cache 在当前主线下对 Hibernate 7 与 Spring Cache 的支持能力。

## 范围

本次设计包含：

- 新增 `modules/integration-tests`
- 在同一个模块中放置 Hibernate 7 集成测试与 Spring Cache 集成测试
- Hibernate 7 测试使用 H2 内存数据库
- Spring Cache 测试验证 `@Cacheable` 与 `@CacheEvict` 的基本行为

本次设计不包含：

- 引入 MySQL、Redis 等外部依赖作为该模块的必需运行条件
- 覆盖并发、多节点、性能压测等扩展测试场景
- 为测试模块引入对外发布用途

## 选定方案

采用一个独立的 Maven 测试模块 `modules/integration-tests`，统一承载两类主线验证：

1. Hibernate 7 + H2 二级缓存集成测试
2. Spring Cache + J2Cache 适配层集成测试

这个方案能把主线能力验证集中到一个入口中，避免把测试逻辑分散到 `hibernate7`、`springcache`、`spring-boot4-starter` 等多个生产模块里。

## 模块结构

建议结构如下：

- `modules/integration-tests/pom.xml`
  定义测试模块依赖与测试插件
- `modules/integration-tests/src/test/java/.../hibernate7/`
  Hibernate 7 测试代码、实体、配置类
- `modules/integration-tests/src/test/java/.../springcache/`
  Spring Cache 测试代码、配置类、测试服务
- `modules/integration-tests/src/test/resources/hibernate7/`
  Hibernate 7 测试资源，例如 H2 配置
- `modules/integration-tests/src/test/resources/springcache/`
  Spring Cache 测试资源，例如 J2Cache 配置

## 依赖设计

该模块应依赖：

- `j2cache-core`
- `j2cache-hibernate7`
- `j2cache-springcache`
- `hibernate-core`
- `spring-context-support`
- `spring-test`
- `h2`
- `junit-jupiter`

如果实际验证需要复用 Boot 4 Starter 的部分能力，可以按需增加 `j2cache-spring-boot4-starter` 测试依赖，但默认不把整个模块设计成 Spring Boot 应用测试。

## 测试范围

### Hibernate 7

应至少覆盖：

- `J2CacheRegionFactory` 可被 Hibernate 7 加载
- H2 下实体二级缓存生效
- 相同实体的二次查询能复用缓存结果

### Spring Cache

应至少覆盖：

- `@Cacheable` 触发缓存命中
- `@CacheEvict` 触发缓存失效
- J2Cache 适配层可以被 Spring `CacheManager` 正常调用

## 执行方式

模块应支持独立执行：

- `mvn -pl modules/integration-tests test`

并且应可被纳入主 Reactor 验证链路中。

## 风险

### Hibernate 7 集成最小可测面

当前 `hibernate7` 模块是一个薄适配层，因此测试应先聚焦“能否工作”，而不是一开始就覆盖所有缓存策略细节。

### Spring Cache 历史依赖版本偏旧

当前 `springcache` 模块自身依赖还比较老，实现时可能需要先补齐对当前主线测试场景所需的最低兼容调整。

## 成功标准

满足以下条件即视为本次测试模块设计成功：

- 仓库新增 `modules/integration-tests`
- Hibernate 7 与 Spring Cache 测试都位于该模块中
- Hibernate 7 测试使用 H2 内存数据库
- 测试模块可通过单独命令执行
