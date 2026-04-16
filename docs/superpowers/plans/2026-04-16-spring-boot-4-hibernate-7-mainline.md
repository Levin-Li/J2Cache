# Spring Boot 4 / Hibernate 7 主线收敛 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 J2Cache 主线收敛到 Java 17、Spring Boot 4 和 Hibernate 7，删除所有旧 Boot 与旧 Hibernate 支持线。

**Architecture:** 保留并完成 `spring-boot4-starter`，从根 Reactor 中移除 `spring-boot-starter`、`spring-boot2-starter`、`hibernate3`、`hibernate4`、`hibernate5`，新增或迁移出 `hibernate7` 模块，并同步清理文档与发布坐标。

**Tech Stack:** Maven、多模块 Java 项目、Java 17、Spring Boot 4.0.5、Spring Framework 7、Hibernate ORM 7、Jakarta API、JUnit Jupiter。

---

## 文件结构与职责

- `pom.xml`
  根 Reactor 与全局构建入口，需要反映新的主线模块集合。
- `modules/spring-boot4-starter/**`
  Boot 4 Starter 主实现与测试。
- `modules/hibernate7/pom.xml`
  Hibernate 7 模块定义。
- `modules/hibernate7/src/**`
  Hibernate 7 集成实现。
- `README.md`
  对外使用说明，必须只表达当前主线支持范围。
- `CHANGES.md`
  版本升级说明。
- `docs/UPGRADE.md`
  发布流程说明。
- `openspec/changes/upgrade-mainline-to-spring-boot-4/**`
  本次需求与任务状态。

### Task 1: 裁剪主 Reactor

**Files:**
- Modify: `pom.xml`
- Delete: `modules/spring-boot-starter/**`
- Delete: `modules/spring-boot2-starter/**`
- Delete: `modules/hibernate3/**`
- Delete: `modules/hibernate4/**`
- Delete: `modules/hibernate5/**`

- [ ] 从根 `pom.xml` 的 `<modules>` 中删除旧 Boot 与旧 Hibernate 模块，只保留 `core`、`modules/spring-boot4-starter`、`modules/hibernate7`、`modules/mybatis`、`modules/springcache`、`modules/session-manager`
- [ ] 删除 `modules/spring-boot-starter`
- [ ] 删除 `modules/spring-boot2-starter`
- [ ] 删除 `modules/hibernate3`
- [ ] 删除 `modules/hibernate4`
- [ ] 删除 `modules/hibernate5`
- [ ] 运行 `git status --short`，确认删除集合与计划一致

### Task 2: 建立 Hibernate 7 模块

**Files:**
- Create: `modules/hibernate7/pom.xml`
- Create or Modify: `modules/hibernate7/src/**`
- Create or Modify: `modules/hibernate7/test/src/**`

- [ ] 先检查仓库是否已有可迁移的 Hibernate 模块骨架
- [ ] 选取最接近的历史 Hibernate 模块作为迁移基础，但改为 `hibernate7` 模块名
- [ ] 将依赖升级到 Hibernate 7 / Jakarta 命名空间
- [ ] 修复因 `javax.*` -> `jakarta.*` 迁移导致的 API 或注解问题
- [ ] 执行 `mvn -pl modules/hibernate7 -am -DskipTests compile`

### Task 3: 收尾 Boot 4 Starter 主线

**Files:**
- Modify: `modules/spring-boot4-starter/**`

- [ ] 保留当前已经通过编译与测试的 Boot 4 Starter 改动
- [ ] 确认 Starter 不再引用旧 Boot 线模块
- [ ] 执行 `mvn -pl modules/spring-boot4-starter -am test`

### Task 4: 清理文档与 OpenSpec 任务

**Files:**
- Modify: `README.md`
- Modify: `CHANGES.md`
- Modify: `docs/UPGRADE.md`
- Modify: `openspec/changes/upgrade-mainline-to-spring-boot-4/tasks.md`

- [ ] 删除 README 中任何对旧 Boot / 旧 Hibernate 模块的对外说明
- [ ] 在 CHANGES 中明确主线只保留 Boot 4 / Hibernate 7
- [ ] 更新 UPGRADE 流程，强调旧模块已从主线移除
- [ ] 勾选 OpenSpec 中已完成的任务

### Task 5: 验证与提交

**Files:**
- Verify: `pom.xml`
- Verify: `modules/spring-boot4-starter/**`
- Verify: `modules/hibernate7/**`

- [ ] 运行 `mvn -pl modules/spring-boot4-starter -am test`
- [ ] 运行 `mvn -pl modules/hibernate7 -am -DskipTests compile`
- [ ] 运行主线构建命令，至少保证新的主线模块集合可构建
- [ ] 更新 OpenSpec 任务状态
- [ ] 提交代码
