# 提案：主线升级到 Spring Boot 4

## 意图

将 J2Cache 当前主线升级到 Spring Boot 4，并让仓库能够基于这一支持线完成构建、测试和构件发布。

## 范围

本次包含：

- 将当前 Boot Starter 支持线从 Spring Boot 3 升级到 Spring Boot 4
- 将仓库 Java 基线从 8 提升到 17
- 重命名 Boot Starter 模块及其发布构件，以准确反映 Spring Boot 4 支持
- 更新测试和文档，确保新的支持线可以稳定构建和发布

本次不包含：

- 保留对现有 Spring Boot 3 用户的兼容性
- 继续保留旧 Starter 坐标作为兼容别名或包装层
- 与本次升级无关的额外重构

## 方案

将当前 `spring-boot3-starter` 直接重命名为 `spring-boot4-starter`，同步调整 Reactor 模块声明和 Maven 坐标，将仓库构建基线提升到 Java 17，然后只做 Spring Boot 4 与 Spring Framework 7 所必需的最小代码与测试改造。
