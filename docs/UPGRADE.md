J2Cache 版本更新流程

1. 完成代码开发和测试
2. 修改对应模块 `pom.xml` 中的版本定义与构件信息
3. 修改 `README.md` 中 Maven 依赖示例，确认 Spring Boot 4 Starter 坐标正确
4. 在 `CHANGES.md` 中记录本次升级内容
5. 发布到 Maven 中央库前，确认 `j2cache-spring-boot4-starter` 已进入主 Reactor 构建
6. 到 https://oss.sonatype.org/#stagingRepositories 发布新版
7. 推送代码到仓库
8. 打标签、创建 Release 发行版
9. 社区投递更新新闻
