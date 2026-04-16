# 提案：重建 Spring Boot 4 主线并支持 Hibernate 7

## 意图

将 J2Cache 当前主线升级到 Spring Boot 4 / Hibernate 7 组合，并让仓库围绕这一支持线完成构建、测试和构件发布。

## 范围

本次包含：

- 将当前 Boot Starter 支持线从 Spring Boot 3 升级到 Spring Boot 4
- 删除旧的 Spring Boot 1/2 支持线
- 删除低于 Hibernate 7 的 Hibernate 集成模块
- 新增或迁移到 Hibernate 7 支持模块
- 将仓库 Java 基线从 8 提升到 17
- 重命名 Boot Starter 模块及其发布构件，以准确反映 Spring Boot 4 支持
- 更新测试和文档，确保新的支持线可以稳定构建和发布

本次不包含：

- 保留对现有 Spring Boot 1/2/3 用户的兼容性
- 继续保留旧 Starter 坐标作为兼容别名或包装层
- 继续保留 Hibernate 3/4/5 集成模块
- 与本次升级无关的额外重构

## 方案

将当前 `spring-boot3-starter` 直接重命名为 `spring-boot4-starter`，移除 `spring-boot-starter`、`spring-boot2-starter`、`hibernate3`、`hibernate4`、`hibernate5` 这些旧支持线，并补入 `hibernate7` 模块。仓库构建基线统一提升到 Java 17，之后只做 Spring Boot 4、Spring Framework 7、Hibernate 7 所必需的最小代码与测试改造。
