# 任务

## 1. Reactor 与模块重命名
- [ ] 1.1 将 `modules/spring-boot3-starter` 重命名为 `modules/spring-boot4-starter`
- [ ] 1.2 更新根 `pom.xml` 的模块列表，纳入重命名后的 Boot 4 Starter
- [ ] 1.3 将 Starter 发布构件重命名为 `j2cache-spring-boot4-starter`

## 2. Java 17 与依赖升级
- [ ] 2.1 在根 Maven 构建中将仓库 Java 基线提升到 17
- [ ] 2.2 清理 Boot Starter 模块中遗留的历史硬编码版本
- [ ] 2.3 将 Boot Starter 的 BOM 和相关依赖升级到 Spring Boot 4

## 3. Starter 兼容性修复
- [ ] 3.1 修复 Boot 4 下不兼容的自动配置导入或注解
- [ ] 3.2 如有需要，修复 Spring Data Redis API 变化带来的代码问题
- [ ] 3.3 所有兼容性修改保持在支持 Boot 4 所必需的最小范围内

## 4. 测试与验证
- [ ] 4.1 将 Starter 测试从 JUnit 4 迁移到 JUnit Jupiter
- [ ] 4.2 移除阻塞式测试行为，确保测试可自动执行
- [ ] 4.3 执行 Starter 的测试与构建验证，并修复出现的问题

## 5. 文档更新
- [ ] 5.1 在 `README.md` 中更新新的 Boot 4 Starter 坐标
- [ ] 5.2 在 `CHANGES.md` 中增加 Boot 4 升级说明
- [ ] 5.3 更新 `docs/UPGRADE.md`，同步新的模块名和构件名
