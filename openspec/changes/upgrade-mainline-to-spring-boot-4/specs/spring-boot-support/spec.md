# Spring Boot 支持变更增量

## ADDED Requirements

### Requirement: Spring Boot 4 Starter 支持
仓库 SHALL 提供一个可用于 Spring Boot 应用集成 J2Cache 的 Spring Boot 4 Starter。

#### Scenario: 发布 Boot 4 Starter 构件
- GIVEN 项目基于主线完成构建
- WHEN Spring Boot Starter 构件被产出
- THEN 发布坐标 SHALL 为 `net.oschina.j2cache:j2cache-spring-boot4-starter`

#### Scenario: Starter 模块名称与支持线一致
- GIVEN 开发者查看仓库中的模块列表
- WHEN 定位 Spring Boot Starter 模块
- THEN 其模块名与目录名 SHALL 明确表示 Spring Boot 4 支持

### Requirement: Java 17 仓库基线
仓库 SHALL 以 Java 17 作为主线构建基线。

#### Scenario: 根 Maven 构建使用 Java 17
- GIVEN 开发者查看根 Maven 编译配置
- WHEN 检查仓库构建基线
- THEN 配置中的 `source` 与 `target` SHALL 为 Java 17

### Requirement: Boot 4 Reactor 集成
主 Reactor 构建 SHALL 包含 Spring Boot 4 Starter 模块。

#### Scenario: Reactor 包含 Boot 4 Starter
- GIVEN 开发者查看根模块列表
- WHEN 检查 Spring Boot 支持模块
- THEN Spring Boot 4 Starter 模块 SHALL 出现在 Reactor 构建中

### Requirement: Boot 4 Starter 可验证性
Spring Boot 4 Starter SHALL 能在 Boot 4 依赖栈下通过编译并通过自动化测试。

#### Scenario: Starter 验证通过
- GIVEN Spring Boot 4 Starter 模块及其依赖
- WHEN 执行模块的构建与测试命令
- THEN Starter SHALL 编译成功
- AND 其自动化测试 SHALL 在无人工交互的情况下通过

### Requirement: Boot 4 文档一致性
项目文档 SHALL 引用 Spring Boot 4 Starter 的构件坐标与支持说明。

#### Scenario: Maven 使用示例已更新
- GIVEN 开发者阅读项目使用文档
- WHEN 查找 Spring Boot Starter 的依赖示例
- THEN 文档中的依赖坐标 SHALL 引用 `j2cache-spring-boot4-starter`
