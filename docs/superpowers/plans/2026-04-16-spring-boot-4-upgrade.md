# Spring Boot 4 主线升级 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 J2Cache 主线切换到 Java 17 与 Spring Boot 4，并发布 `j2cache-spring-boot4-starter` 构件。

**Architecture:** 复用现有 `modules/spring-boot3-starter` 的实现，直接升级为 Boot 4 支持线，不保留 Boot 3 兼容层。改动分成四块：根构建与模块重命名、Starter 依赖升级、Starter 代码与测试兼容修复、文档与验证收尾。

**Tech Stack:** Maven、多模块 Java 项目、Spring Boot 4.0.5、Spring Framework 7、Spring Data Redis、JUnit Jupiter。

---

## 文件结构与职责

- `pom.xml`
  仓库根构建，负责 Java 版本基线、模块聚合与全局依赖管理。
- `modules/spring-boot4-starter/pom.xml`
  Boot 4 Starter 的 Maven 定义，负责 artifactId、parent、Boot BOM 和测试依赖。
- `modules/spring-boot4-starter/src/net/oschina/j2cache/autoconfigure/J2CacheAutoConfiguration.java`
  J2Cache 主自动配置入口。
- `modules/spring-boot4-starter/src/net/oschina/j2cache/autoconfigure/J2CacheSpringCacheAutoConfiguration.java`
  Spring Cache 集成自动配置。
- `modules/spring-boot4-starter/src/net/oschina/j2cache/autoconfigure/J2CacheSpringRedisAutoConfiguration.java`
  Redis 连接工厂、模板和监听容器自动配置，属于本次兼容性风险最高的文件。
- `modules/spring-boot4-starter/src/net/oschina/j2cache/autoconfigure/J2CacheConfig.java`
  Starter 配置属性绑定。
- `modules/spring-boot4-starter/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
  自动配置注册文件。
- `modules/spring-boot4-starter/test/src/com/test/ApplicationTests.java`
  Starter 集成测试，需要迁移到 JUnit Jupiter 并移除阻塞行为。
- `modules/spring-boot4-starter/test/src/com/test/service/TestService.java`
  Starter 测试辅助服务，验证 Spring Cache 行为。
- `modules/spring-boot4-starter/test/src/com/test/bean/TestBean.java`
  测试实体对象。
- `modules/spring-boot4-starter/test/src/com/test/j2cache-test.properties`
  Starter 测试使用的配置。
- `modules/spring-boot4-starter/readme.md`
  Starter 模块局部说明，需要更新为 Boot 4 表述。
- `README.md`
  仓库总说明，需要更新新的 Boot 4 构件引用。
- `CHANGES.md`
  发布变更说明。
- `docs/UPGRADE.md`
  发版流程补充，需要反映模块/构件重命名。

### Task 1: 调整 Reactor 与模块命名

**Files:**
- Modify: `pom.xml`
- Move: `modules/spring-boot3-starter` -> `modules/spring-boot4-starter`
- Modify: `modules/spring-boot4-starter/pom.xml`
- Modify: `modules/spring-boot4-starter/.project`
- Modify: `modules/spring-boot4-starter/readme.md`

- [ ] **Step 1: 先确认重命名后会受影响的路径**

Run: `grep -R "spring-boot3-starter\|j2cache-spring-boot3-starter" -n .`
Expected: 输出至少包含根 `pom.xml`、Starter `pom.xml`、模块 `.project`、OpenSpec 文档与实现计划文档。

- [ ] **Step 2: 重命名模块目录**

Run: `mv modules/spring-boot3-starter modules/spring-boot4-starter`
Expected: `modules/spring-boot4-starter/pom.xml` 存在，旧目录不存在。

- [ ] **Step 3: 更新根 `pom.xml` 的 Java 基线与模块列表**

将根 `pom.xml` 中的属性、编译插件和模块列表改成下面的内容：

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.release>17</maven.compiler.release>
</properties>
```

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.8.1</version>
    <configuration>
        <release>${maven.compiler.release}</release>
        <encoding>${project.build.sourceEncoding}</encoding>
    </configuration>
</plugin>
```

```xml
<modules>
    <module>core</module>
    <module>modules/hibernate3</module>
    <module>modules/hibernate4</module>
    <module>modules/hibernate5</module>
    <module>modules/spring-boot-starter</module>
    <module>modules/spring-boot2-starter</module>
    <module>modules/spring-boot4-starter</module>
    <module>modules/mybatis</module>
    <module>modules/springcache</module>
    <module>modules/session-manager</module>
</modules>
```

- [ ] **Step 4: 更新 Boot 4 Starter 的 `pom.xml` 基本坐标**

将 `modules/spring-boot4-starter/pom.xml` 中 parent、artifactId、内部依赖版本和 Boot 版本调整为下面这组内容：

```xml
<parent>
    <artifactId>j2cache</artifactId>
    <groupId>net.oschina.j2cache</groupId>
    <version>4.0.0-SNAPSHOT</version>
    <relativePath>../../pom.xml</relativePath>
</parent>
```

```xml
<artifactId>j2cache-spring-boot4-starter</artifactId>
```

```xml
<dependency>
    <groupId>net.oschina.j2cache</groupId>
    <artifactId>j2cache-core</artifactId>
    <version>${project.version}</version>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
        </exclusion>
    </exclusions>
    <scope>provided</scope>
</dependency>
```

```xml
<properties>
    <spring-boot-dependencies.version>4.0.5</spring-boot-dependencies.version>
</properties>
```

- [ ] **Step 5: 清理 Starter `pom.xml` 中的 JUnit 4 依赖**

删除下面这段测试依赖，避免与 Boot 4 默认测试栈重复：

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <scope>test</scope>
</dependency>
```

保留：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

- [ ] **Step 6: 更新模块局部说明和 Eclipse 项目名**

将 `modules/spring-boot4-starter/readme.md` 开头改成：

```md
此为 Spring Boot 4 版本。

如下即可使用 J2Cache 缓存：

```java
@Autowired
private CacheChannel cacheChannel;
```
```

将 `modules/spring-boot4-starter/.project` 中的项目名改成：

```xml
<name>j2cache-spring-boot4-starter</name>
```

- [ ] **Step 7: 运行一次最小 Maven 校验，确认重命名和 POM 没有破坏解析**

Run: `mvn -pl modules/spring-boot4-starter -am -DskipTests help:effective-pom`
Expected: BUILD SUCCESS，且输出中能看到 `j2cache-spring-boot4-starter` 和 Java 17 编译配置。

### Task 2: 修复 Starter 依赖和自动配置兼容性

**Files:**
- Modify: `modules/spring-boot4-starter/pom.xml`
- Modify: `modules/spring-boot4-starter/src/net/oschina/j2cache/autoconfigure/J2CacheAutoConfiguration.java`
- Modify: `modules/spring-boot4-starter/src/net/oschina/j2cache/autoconfigure/J2CacheSpringCacheAutoConfiguration.java`
- Modify: `modules/spring-boot4-starter/src/net/oschina/j2cache/autoconfigure/J2CacheSpringRedisAutoConfiguration.java`
- Modify: `modules/spring-boot4-starter/src/net/oschina/j2cache/autoconfigure/J2CacheConfig.java`
- Modify: `modules/spring-boot4-starter/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

- [ ] **Step 1: 先编译 Starter，拿到真实 Boot 4 兼容错误列表**

Run: `mvn -pl modules/spring-boot4-starter -am -DskipTests compile`
Expected: 先失败。错误大概率落在 Spring Data Redis API、测试依赖或 Boot 4 包路径变化上。

- [ ] **Step 2: 如果 `J2CacheSpringRedisAutoConfiguration` 的 Redis API 报错，按下面的检查顺序修正**

先检查这些导入在 Boot 4 管理版本下是否仍然存在且可编译：

```java
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
```

如果编译错误落在构造器或 builder API 上，修复时遵循下面两条约束：

```text
1. 保持 bean 名称 `j2CahceRedisConnectionFactory`、`j2CacheRedisTemplate`、`j2CacheValueSerializer`、`j2CacheRedisMessageListenerContainer` 不变
2. 保持 single / sentinel / cluster 三种分支结构不变，只替换报错的构造器或配置对象调用
```

修复后的类至少要保留下面这组方法签名：

```java
@Configuration
@AutoConfigureAfter({ RedisAutoConfiguration.class })
@AutoConfigureBefore({ J2CacheAutoConfiguration.class })
@ConditionalOnProperty(value = "j2cache.l2-cache-open", havingValue = "true", matchIfMissing = true)
public class J2CacheSpringRedisAutoConfiguration {

    @Bean("j2CahceRedisConnectionFactory")
    @ConditionalOnMissingBean(name = "j2CahceRedisConnectionFactory")
    @ConditionalOnProperty(name = "j2cache.redis-client", havingValue = "jedis", matchIfMissing = true)
    public JedisConnectionFactory jedisConnectionFactory(net.oschina.j2cache.J2CacheConfig j2CacheConfig) {
        throw new UnsupportedOperationException("replace with Boot 4 compatible implementation");
    }

    @Primary
    @Bean("j2CahceRedisConnectionFactory")
    @ConditionalOnMissingBean(name = "j2CahceRedisConnectionFactory")
    @ConditionalOnProperty(name = "j2cache.redis-client", havingValue = "lettuce")
    public LettuceConnectionFactory lettuceConnectionFactory(net.oschina.j2cache.J2CacheConfig j2CacheConfig) {
        throw new UnsupportedOperationException("replace with Boot 4 compatible implementation");
    }
}
```

修完后立刻执行：`mvn -pl modules/spring-boot4-starter -am -DskipTests compile`。只有当前一个 Redis 编译错误消失后，才继续处理下一个错误。

- [ ] **Step 3: 如果自动配置类导入或注解失效，按下面的目标状态修正**

`J2CacheAutoConfiguration.java` 目标结构：

```java
@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({ J2CacheConfig.class })
@Configuration
@PropertySource(value = "${j2cache.config-location}", encoding = "UTF-8", ignoreResourceNotFound = true)
public class J2CacheAutoConfiguration {

    @Autowired
    private StandardEnvironment standardEnvironment;

    @Bean
    public net.oschina.j2cache.J2CacheConfig j2CacheConfig() throws IOException {
        return SpringJ2CacheConfigUtil.initFromConfig(standardEnvironment);
    }

    @Bean
    @DependsOn({ "springUtil", "j2CacheConfig" })
    public CacheChannel cacheChannel(net.oschina.j2cache.J2CacheConfig j2CacheConfig) throws IOException {
        return J2CacheBuilder.init(j2CacheConfig).getChannel();
    }

    @Bean
    public SpringUtil springUtil() {
        return new SpringUtil();
    }
}
```

`J2CacheSpringCacheAutoConfiguration.java` 目标结构：

```java
@Configuration
@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({ J2CacheConfig.class, CacheProperties.class })
@ConditionalOnProperty(name = "j2cache.open-spring-cache", havingValue = "true")
@EnableCaching
public class J2CacheSpringCacheAutoConfiguration {

    private final CacheProperties cacheProperties;
    private final J2CacheConfig j2CacheConfig;

    J2CacheSpringCacheAutoConfiguration(CacheProperties cacheProperties, J2CacheConfig j2CacheConfig) {
        this.cacheProperties = cacheProperties;
        this.j2CacheConfig = j2CacheConfig;
    }

    @Bean
    @ConditionalOnBean(CacheChannel.class)
    public J2CacheCacheManger cacheManager(CacheChannel cacheChannel) {
        List<String> cacheNames = cacheProperties.getCacheNames();
        J2CacheCacheManger cacheManager = new J2CacheCacheManger(cacheChannel);
        cacheManager.setAllowNullValues(j2CacheConfig.isAllowNullValues());
        cacheManager.setCacheNames(cacheNames);
        return cacheManager;
    }
}
```

- [ ] **Step 4: 验证自动配置注册文件仍然与类名一致**

`modules/spring-boot4-starter/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 应保留以下内容：

```text
net.oschina.j2cache.autoconfigure.J2CacheAutoConfiguration
net.oschina.j2cache.autoconfigure.J2CacheSpringCacheAutoConfiguration
net.oschina.j2cache.autoconfigure.J2CacheSpringRedisAutoConfiguration
```

- [ ] **Step 5: 重新编译 Starter，确保主代码已经过关**

Run: `mvn -pl modules/spring-boot4-starter -am -DskipTests compile`
Expected: BUILD SUCCESS。若仍失败，错误应只剩测试代码或仓库中其他被 Java 17 连带打爆的模块。

### Task 3: 迁移 Starter 测试到 JUnit Jupiter

**Files:**
- Modify: `modules/spring-boot4-starter/test/src/com/test/ApplicationTests.java`
- Modify: `modules/spring-boot4-starter/test/src/com/test/service/TestService.java`
- Test: `modules/spring-boot4-starter/test/src/com/test/j2cache-test.properties`

- [ ] **Step 1: 将 `ApplicationTests.java` 改成 JUnit Jupiter 结构**

把测试类改成下面这个骨架，关键点是去掉 `@RunWith(SpringRunner.class)`、改用 `org.junit.jupiter.api.Test`、改用 Jupiter 断言：

```java
package com.test;

import com.test.bean.TestBean;
import com.test.service.TestService;
import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.autoconfigure.J2CacheAutoConfiguration;
import net.oschina.j2cache.autoconfigure.J2CacheSpringCacheAutoConfiguration;
import net.oschina.j2cache.autoconfigure.J2CacheSpringRedisAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        classes = {
                TestService.class,
                J2CacheAutoConfiguration.class,
                J2CacheSpringCacheAutoConfiguration.class,
                J2CacheSpringRedisAutoConfiguration.class
        },
        properties = {
                "j2cache.config-location=classpath:/com/test/j2cache-test.properties",
                "spring.cache.type=GENERIC",
                "j2cache.open-spring-cache=true",
                "j2cache.j2CacheConfig.serialization=json",
                "j2cache.redis-client=jedis",
                "j2cache.cache-clean-mode=active",
                "j2cache.allow-null-values=true",
                "j2cache.l2-cache-open=true"
        }
)
class ApplicationTests {

    @Autowired
    private TestService testService;

    @Autowired
    private CacheChannel cacheChannel;

    @Test
    void testCache() {
        testService.reset();
        testService.evict();
        testService.getNum();
        testService.getNum();
        testService.getNum();
        testService.getNum();
        Integer n = testService.getNum();
        assertEquals(1, n);
    }

    @Test
    void clearCache() {
        testService.reset();
        testService.getNum();
        testService.reset();
        testService.evict();
        Integer value = testService.getNum();
        assertEquals(1, value);
    }

    @Test
    void beanCache() {
        testService.reset();
        testService.evict();
        testService.testBean();
        TestBean bean = testService.testBean();
        assertEquals(1, bean.getNum());
    }

    @Test
    void cacheChannelRoundTrip() {
        cacheChannel.set("test", "123", "321");
        CacheObject cacheObject = cacheChannel.get("test", "123");
        assertEquals("321", cacheObject.getValue());
    }
}
```

- [ ] **Step 2: 删除错误且不稳定的 `test1()` 用例，不要迁移它**

不要保留这段逻辑：

```java
@Test
public void test1() {
    CacheObject a = cacheChannel.get("test", "1233");
    Assert.isTrue(a.getValue().equals("321"), "失败！");
}
```

原因：它依赖前置测试顺序和共享状态，不符合自动化测试要求。

- [ ] **Step 3: 去掉所有阻塞式测试行为**

明确删除这行代码，不要替换成其他交互式等待：

```java
System.in.read();
```

- [ ] **Step 4: 先只运行 Starter 测试，确认 Jupiter 迁移后的真实失败点**

Run: `mvn -pl modules/spring-boot4-starter -am test`
Expected: 如果失败，错误应集中在 Redis 连接、Spring Boot 4 配置或测试断言，而不是 JUnit 4/5 混用。

- [ ] **Step 5: 若测试因环境中无 Redis 而失败，则先记录失败原因并执行无测试打包验证**

Run: `mvn -pl modules/spring-boot4-starter -am -DskipTests package`
Expected: BUILD SUCCESS。若测试失败原因是外部 Redis 环境缺失，则在最终说明中明确记录“代码已编译通过，集成测试依赖本地 Redis”。

### Task 4: 更新发布文档与仓库说明

**Files:**
- Modify: `README.md`
- Modify: `CHANGES.md`
- Modify: `docs/UPGRADE.md`
- Modify: `modules/spring-boot4-starter/readme.md`

- [ ] **Step 1: 在 `README.md` 中加入 Boot 4 Starter 依赖示例**

在 Spring Boot 相关说明位置加入下面的依赖片段：

```xml
<dependency>
    <groupId>net.oschina.j2cache</groupId>
    <artifactId>j2cache-spring-boot4-starter</artifactId>
    <version>4.0.0-SNAPSHOT</version>
</dependency>
```

并配套说明这是当前主线支持的 Spring Boot 4 Starter。

- [ ] **Step 2: 在 `CHANGES.md` 顶部新增 Boot 4 升级记录**

插入如下版本记录：

```md
**j2cache 4.0.0-SNAPSHOT (2026-04-16)**

1. 主线升级到 Spring Boot 4
2. 仓库构建基线提升到 Java 17
3. 新的 Starter 构件更名为 `j2cache-spring-boot4-starter`
```

- [ ] **Step 3: 更新 `docs/UPGRADE.md` 的发版说明**

将第 2-5 步补充为下面这版，使其反映模块与构件重命名后的发版关注点：

```md
2. 修改对应模块 `pom.xml` 中的版本定义与构件信息
3. 修改 `README.md` 中 Maven 依赖示例，确认 Spring Boot 4 Starter 坐标正确
4. 在 `CHANGES.md` 中记录本次升级内容
5. 发布到 Maven 中央库前，确认 `j2cache-spring-boot4-starter` 已进入主 Reactor 构建
```

- [ ] **Step 4: 统一 Starter 模块局部文档的 Boot 4 表述**

确保 `modules/spring-boot4-starter/readme.md` 至少包含下面这些文字替换：

```md
此为 Spring Boot 4 版本。
```

```md
在 application.properties 中支持 redis 客户端：
- jedis
- lettuce
```

不要保留“此为 spring boot3 版本”或“jedis 不支持”这类与当前实现/目标不一致的描述。

- [ ] **Step 5: 搜索旧坐标和旧模块名，确保对外文档已清干净**

Run: `grep -R "spring-boot3-starter\|j2cache-spring-boot3-starter" -n README.md CHANGES.md docs modules/spring-boot4-starter`
Expected: 仅允许 OpenSpec 变更文档、历史草稿或明确的历史说明中出现；对外使用文档不应再引用旧坐标。

### Task 5: 最终验证与收尾

**Files:**
- Modify: `openspec/changes/upgrade-mainline-to-spring-boot-4/tasks.md`
- Verify: `pom.xml`
- Verify: `modules/spring-boot4-starter/**`
- Verify: `README.md`
- Verify: `CHANGES.md`
- Verify: `docs/UPGRADE.md`

- [ ] **Step 1: 运行主验证命令**

Run: `mvn -pl modules/spring-boot4-starter -am -DskipTests compile`
Expected: BUILD SUCCESS

Run: `mvn -pl modules/spring-boot4-starter -am test`
Expected: 若本地有 Redis，BUILD SUCCESS；若无 Redis，记录为环境依赖失败。

Run: `mvn package -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 2: 将 OpenSpec 任务勾选到完成状态**

把 `openspec/changes/upgrade-mainline-to-spring-boot-4/tasks.md` 更新为如下状态：

```md
## 1. Reactor 与模块重命名
- [x] 1.1 将 `modules/spring-boot3-starter` 重命名为 `modules/spring-boot4-starter`
- [x] 1.2 更新根 `pom.xml` 的模块列表，纳入重命名后的 Boot 4 Starter
- [x] 1.3 将 Starter 发布构件重命名为 `j2cache-spring-boot4-starter`
```

```md
## 2. Java 17 与依赖升级
- [x] 2.1 在根 Maven 构建中将仓库 Java 基线提升到 17
- [x] 2.2 清理 Boot Starter 模块中遗留的历史硬编码版本
- [x] 2.3 将 Boot Starter 的 BOM 和相关依赖升级到 Spring Boot 4
```

```md
## 3. Starter 兼容性修复
- [x] 3.1 修复 Boot 4 下不兼容的自动配置导入或注解
- [x] 3.2 如有需要，修复 Spring Data Redis API 变化带来的代码问题
- [x] 3.3 所有兼容性修改保持在支持 Boot 4 所必需的最小范围内
```

```md
## 4. 测试与验证
- [x] 4.1 将 Starter 测试从 JUnit 4 迁移到 JUnit Jupiter
- [x] 4.2 移除阻塞式测试行为，确保测试可自动执行
- [x] 4.3 执行 Starter 的测试与构建验证，并修复出现的问题
```

```md
## 5. 文档更新
- [x] 5.1 在 `README.md` 中更新新的 Boot 4 Starter 坐标
- [x] 5.2 在 `CHANGES.md` 中增加 Boot 4 升级说明
- [x] 5.3 更新 `docs/UPGRADE.md`，同步新的模块名和构件名
```

- [ ] **Step 3: 做一次 diff 自查，确认只改了计划内内容**

Run: `git diff -- pom.xml modules/spring-boot4-starter README.md CHANGES.md docs/UPGRADE.md openspec/changes/upgrade-mainline-to-spring-boot-4/tasks.md`
Expected: diff 只包含 Java 17、Boot 4 Starter、测试迁移、文档更新和 OpenSpec 任务勾选。

- [ ] **Step 4: 准备交付说明**

最终说明必须覆盖这四项：

```text
1. 实际改了哪些模块和关键文件
2. 最终发布坐标是否已变为 j2cache-spring-boot4-starter
3. 哪些验证命令通过，哪些受外部 Redis 环境影响
4. OpenSpec 任务是否已全部完成
```
