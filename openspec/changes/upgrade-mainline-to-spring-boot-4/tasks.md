# 任务

## 1. 主线裁剪与模块重命名
- [x] 1.1 将 `modules/spring-boot3-starter` 重命名为 `modules/spring-boot4-starter`
- [x] 1.2 从主 Reactor 中移除 `spring-boot-starter` 与 `spring-boot2-starter`
- [x] 1.3 从主 Reactor 中移除 `hibernate3`、`hibernate4`、`hibernate5`
- [x] 1.4 新增或迁移出 `hibernate7` 模块，并纳入主 Reactor
- [x] 1.5 将 Starter 发布构件重命名为 `j2cache-spring-boot4-starter`

## 2. Java 17 与依赖升级
- [x] 2.1 在根 Maven 构建中将仓库 Java 基线提升到 17
- [x] 2.2 清理 Boot Starter 模块中遗留的历史硬编码版本
- [x] 2.3 将 Boot Starter 的 BOM 和相关依赖升级到 Spring Boot 4

## 3. Starter 与 Hibernate 兼容性修复
- [x] 3.1 修复 Boot 4 下不兼容的自动配置导入或注解
- [x] 3.2 如有需要，修复 Spring Data Redis API 变化带来的代码问题
- [x] 3.3 将 Hibernate 集成迁移到 Hibernate 7 兼容 API
- [x] 3.4 所有兼容性修改保持在支持 Boot 4 / Hibernate 7 所必需的最小范围内

## 4. 测试与验证
- [x] 4.1 将 Starter 测试从 JUnit 4 迁移到 JUnit Jupiter
- [x] 4.2 移除阻塞式测试行为，确保测试可自动执行
- [x] 4.3 验证 Hibernate 7 模块能在主线中编译通过
- [x] 4.4 执行主线构建验证，并修复出现的问题

## 5. 文档更新
- [x] 5.1 在 `README.md` 中更新新的 Boot 4 Starter 坐标
- [x] 5.2 在 `CHANGES.md` 中增加 Boot 4 升级说明
- [x] 5.3 更新 `docs/UPGRADE.md`，同步新的模块名和构件名
- [x] 5.4 移除对旧 Boot / 旧 Hibernate 支持线的对外说明
