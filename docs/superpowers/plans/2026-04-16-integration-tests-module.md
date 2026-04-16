# 独立集成测试模块 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 新增 `modules/integration-tests`，统一验证 J2Cache 对 Hibernate 7 与 Spring Cache 的主线支持。

**Architecture:** 新建一个只用于测试的 Maven 模块，内部拆成 `hibernate7` 与 `springcache` 两组测试。Hibernate 7 测试使用 H2 和最小实体模型验证二级缓存能被 RegionFactory 驱动，Spring Cache 测试通过 Spring 上下文验证 `@Cacheable` 与 `@CacheEvict` 的实际行为。

**Tech Stack:** Maven、多模块 Java 项目、Java 17、Hibernate ORM 7、H2、Spring Cache、Spring Test、JUnit Jupiter。

---

## 文件结构与职责

- `pom.xml`
  根 Reactor，需要纳入新测试模块。
- `modules/integration-tests/pom.xml`
  独立集成测试模块的依赖、测试资源、Surefire 配置。
- `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/hibernate7/Hibernate7CacheEntity.java`
  Hibernate 7 测试实体。
- `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/hibernate7/Hibernate7CacheEntityTest.java`
  Hibernate 7 二级缓存测试。
- `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/hibernate7/Hibernate7SessionFactoryHelper.java`
  测试用 SessionFactory 构建辅助类，隔离 Hibernate 启动细节。
- `modules/integration-tests/src/test/resources/hibernate7/j2cache.properties`
  Hibernate 7 测试使用的 J2Cache 配置。
- `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/springcache/SpringCacheTestConfig.java`
  Spring Cache 测试上下文配置。
- `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/springcache/SpringCacheTestService.java`
  带 `@Cacheable` / `@CacheEvict` 的测试服务。
- `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/springcache/SpringCacheIntegrationTest.java`
  Spring Cache 行为测试。
- `modules/integration-tests/src/test/resources/springcache/j2cache.properties`
  Spring Cache 测试使用的 J2Cache 配置。
- `openspec/changes/upgrade-mainline-to-spring-boot-4/tasks.md`
  需要勾选 4.5、4.6、4.7。

### Task 1: 建立集成测试模块骨架

**Files:**
- Modify: `pom.xml`
- Create: `modules/integration-tests/pom.xml`
- Create: `modules/integration-tests/src/test/resources/hibernate7/j2cache.properties`
- Create: `modules/integration-tests/src/test/resources/springcache/j2cache.properties`

- [ ] **Step 1: 先在根 Reactor 中加入新模块，验证模块路径被识别**

将根 `pom.xml` 的 `<modules>` 段加入：

```xml
<module>modules/integration-tests</module>
```

位置放在 `modules/hibernate7` 与 `modules/spring-boot4-starter` 之后都可以，但应保持测试模块作为主线模块的一部分进入 Reactor。

- [ ] **Step 2: 新建 `modules/integration-tests/pom.xml`**

使用下面这份最小 POM 作为起点：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>j2cache</artifactId>
        <groupId>net.oschina.j2cache</groupId>
        <version>4.0.0-SNAPSHOT</version>
        <relativePath>../../pom.xml</relativePath>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>j2cache-integration-tests</artifactId>
    <packaging>jar</packaging>

    <properties>
        <hibernate.version>7.0.6.Final</hibernate.version>
        <junit.jupiter.version>5.10.2</junit.jupiter.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>net.oschina.j2cache</groupId>
            <artifactId>j2cache-core</artifactId>
            <version>${project.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>net.oschina.j2cache</groupId>
            <artifactId>j2cache-hibernate7</artifactId>
            <version>${project.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>net.oschina.j2cache</groupId>
            <artifactId>j2cache-springcache</artifactId>
            <version>${project.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>${hibernate.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>7.0.6</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>7.0.6</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.jupiter.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <testResources>
            <testResource>
                <directory>src/test/resources</directory>
            </testResource>
        </testResources>
    </build>
</project>
```

- [ ] **Step 3: 为两组测试分别写最小 J2Cache 配置文件**

`modules/integration-tests/src/test/resources/hibernate7/j2cache.properties`：

```properties
j2cache.serialization=java
j2cache.L1.provider_class=caffeine
j2cache.L2.provider_class=[disabled]
j2cache.broadcast=none
caffeine.region.default=1000, 30m
```

`modules/integration-tests/src/test/resources/springcache/j2cache.properties`：

```properties
j2cache.serialization=java
j2cache.L1.provider_class=caffeine
j2cache.L2.provider_class=[disabled]
j2cache.broadcast=none
caffeine.region.default=1000, 30m
```

- [ ] **Step 4: 先跑一个空模块测试命令，确认 Maven 结构正确**

Run: `mvn -pl modules/integration-tests test`
Expected: 先成功或提示没有测试；不应出现模块找不到、父 POM 找不到或依赖解析错误。

### Task 2: 先用 Hibernate 7 测试驱动最小集成链路

**Files:**
- Create: `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/hibernate7/Hibernate7CacheEntity.java`
- Create: `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/hibernate7/Hibernate7SessionFactoryHelper.java`
- Create: `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/hibernate7/Hibernate7CacheEntityTest.java`
- Modify: `modules/integration-tests/pom.xml` if missing Jakarta Persistence test dependencies

- [ ] **Step 1: 先写会失败的 Hibernate 7 测试**

创建 `Hibernate7CacheEntityTest.java`：

```java
package net.oschina.j2cache.integration.hibernate7;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class Hibernate7CacheEntityTest {

    @Test
    void buildsSessionFactoryWithJ2CacheRegionFactory() {
        assertDoesNotThrow(Hibernate7SessionFactoryHelper::buildSessionFactory);
    }
}
```

- [ ] **Step 2: 运行测试，确认它因缺少辅助类而失败**

Run: `mvn -pl modules/integration-tests -Dtest=Hibernate7CacheEntityTest test`
Expected: FAIL，报 `Hibernate7SessionFactoryHelper` 不存在或相关编译错误。

- [ ] **Step 3: 写最小实体与 SessionFactory 辅助类**

`Hibernate7CacheEntity.java`：

```java
package net.oschina.j2cache.integration.hibernate7;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "cache_entity")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Hibernate7CacheEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    public Hibernate7CacheEntity() {
    }

    public Hibernate7CacheEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
```

`Hibernate7SessionFactoryHelper.java`：

```java
package net.oschina.j2cache.integration.hibernate7;

import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import net.oschina.j2cache.hibernate7.J2CacheRegionFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public final class Hibernate7SessionFactoryHelper {

    private Hibernate7SessionFactoryHelper() {
    }

    static SessionFactory buildSessionFactory() throws Exception {
        J2CacheConfig config = J2CacheConfig.initFromConfig("/hibernate7/j2cache.properties");
        J2CacheBuilder.init(config).getChannel();

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                .applySetting("hibernate.connection.url", "jdbc:h2:mem:j2cache-hibernate7;DB_CLOSE_DELAY=-1")
                .applySetting("hibernate.connection.username", "sa")
                .applySetting("hibernate.connection.password", "")
                .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                .applySetting("hibernate.cache.use_second_level_cache", "true")
                .applySetting("hibernate.cache.region.factory_class", J2CacheRegionFactory.class.getName())
                .applySetting("hibernate.generate_statistics", "true")
                .build();

        return new MetadataSources(registry)
                .addAnnotatedClass(Hibernate7CacheEntity.class)
                .buildMetadata()
                .buildSessionFactory();
    }
}
```

- [ ] **Step 4: 再跑测试，确认 SessionFactory 已能启动**

Run: `mvn -pl modules/integration-tests -Dtest=Hibernate7CacheEntityTest test`
Expected: PASS。

- [ ] **Step 5: 扩展为真正的二级缓存行为测试**

将 `Hibernate7CacheEntityTest.java` 改成：

```java
package net.oschina.j2cache.integration.hibernate7;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Hibernate7CacheEntityTest {

    @Test
    void secondLevelCacheHitsOnSecondLoad() throws Exception {
        try (SessionFactory sessionFactory = Hibernate7SessionFactoryHelper.buildSessionFactory()) {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                session.persist(new Hibernate7CacheEntity(1L, "demo"));
                session.getTransaction().commit();
            }

            Statistics statistics = sessionFactory.getStatistics();
            statistics.clear();

            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                Hibernate7CacheEntity entity = session.find(Hibernate7CacheEntity.class, 1L);
                assertEquals("demo", entity.getName());
                session.getTransaction().commit();
            }

            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();
                Hibernate7CacheEntity entity = session.find(Hibernate7CacheEntity.class, 1L);
                assertEquals("demo", entity.getName());
                session.getTransaction().commit();
            }

            assertEquals(1, statistics.getSecondLevelCacheHitCount());
        }
    }
}
```

- [ ] **Step 6: 运行测试，确认缓存行为通过**

Run: `mvn -pl modules/integration-tests -Dtest=Hibernate7CacheEntityTest test`
Expected: PASS，且 `getSecondLevelCacheHitCount()` 等于 `1`。

### Task 3: 用 Spring Cache 测试驱动适配层行为

**Files:**
- Create: `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/springcache/SpringCacheTestConfig.java`
- Create: `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/springcache/SpringCacheTestService.java`
- Create: `modules/integration-tests/src/test/java/net/oschina/j2cache/integration/springcache/SpringCacheIntegrationTest.java`

- [ ] **Step 1: 先写会失败的 Spring Cache 测试**

创建 `SpringCacheIntegrationTest.java`：

```java
package net.oschina.j2cache.integration.springcache;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringJUnitConfig(SpringCacheTestConfig.class)
class SpringCacheIntegrationTest {

    @Test
    void contextLoads(SpringCacheTestService service) {
        assertNotNull(service);
    }
}
```

- [ ] **Step 2: 运行测试，确认它因缺少配置类而失败**

Run: `mvn -pl modules/integration-tests -Dtest=SpringCacheIntegrationTest test`
Expected: FAIL，报 `SpringCacheTestConfig` 或 `SpringCacheTestService` 缺失。

- [ ] **Step 3: 写最小 Spring Cache 测试配置与服务**

`SpringCacheTestConfig.java`：

```java
package net.oschina.j2cache.integration.springcache;

import net.oschina.j2cache.J2CacheBuilder;
import net.oschina.j2cache.J2CacheConfig;
import net.oschina.j2cache.springcache.J2CacheSpringCacheManageAdapter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
class SpringCacheTestConfig {

    @Bean
    J2CacheBuilder j2CacheBuilder() throws Exception {
        J2CacheConfig config = J2CacheConfig.initFromConfig("/springcache/j2cache.properties");
        return J2CacheBuilder.init(config);
    }

    @Bean
    CacheManager cacheManager(J2CacheBuilder builder) {
        return new J2CacheSpringCacheManageAdapter(builder, true);
    }

    @Bean
    SpringCacheTestService springCacheTestService() {
        return new SpringCacheTestService();
    }
}
```

`SpringCacheTestService.java`：

```java
package net.oschina.j2cache.integration.springcache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.concurrent.atomic.AtomicInteger;

class SpringCacheTestService {

    private final AtomicInteger counter = new AtomicInteger();

    @Cacheable(cacheNames = "numbers", key = "#key")
    int load(String key) {
        return counter.incrementAndGet();
    }

    @CacheEvict(cacheNames = "numbers", key = "#key")
    void evict(String key) {
    }
}
```

- [ ] **Step 4: 把测试扩展为 `@Cacheable` / `@CacheEvict` 行为验证**

将 `SpringCacheIntegrationTest.java` 改成：

```java
package net.oschina.j2cache.integration.springcache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(SpringCacheTestConfig.class)
class SpringCacheIntegrationTest {

    @Autowired
    private SpringCacheTestService service;

    @Test
    void cacheableCachesSecondInvocation() {
        int first = service.load("demo");
        int second = service.load("demo");
        assertEquals(first, second);
        assertEquals(1, first);
    }

    @Test
    void cacheEvictForcesReload() {
        int first = service.load("demo-evict");
        service.evict("demo-evict");
        int second = service.load("demo-evict");
        assertEquals(1, first);
        assertEquals(2, second);
    }
}
```

- [ ] **Step 5: 运行 Spring Cache 测试并确认通过**

Run: `mvn -pl modules/integration-tests -Dtest=SpringCacheIntegrationTest test`
Expected: PASS。

### Task 4: 整体验证与 OpenSpec 收尾

**Files:**
- Modify: `openspec/changes/upgrade-mainline-to-spring-boot-4/tasks.md`
- Verify: `modules/integration-tests/**`
- Verify: `pom.xml`

- [ ] **Step 1: 运行整个集成测试模块**

Run: `mvn -pl modules/integration-tests test`
Expected: PASS，Hibernate 7 与 Spring Cache 两组测试都通过。

- [ ] **Step 2: 运行主线 Reactor 验证，确认新模块已接入**

Run: `mvn package -DskipTests`
Expected: BUILD SUCCESS，且 Reactor Summary 中包含 `j2cache-integration-tests`。

- [ ] **Step 3: 将 OpenSpec 任务勾选完成**

把 `openspec/changes/upgrade-mainline-to-spring-boot-4/tasks.md` 中这三项改为完成：

```md
- [x] 4.5 新增独立集成测试模块，统一验证 Hibernate 7 与 Spring Cache
- [x] 4.6 使用 H2 为 Hibernate 7 集成测试提供内存数据库
- [x] 4.7 验证集成测试模块可单独执行并通过
```

- [ ] **Step 4: 做一次 diff 自查**

Run: `git diff -- pom.xml modules/integration-tests openspec/changes/upgrade-mainline-to-spring-boot-4/tasks.md`
Expected: diff 只包含新测试模块、Reactor 接入和 OpenSpec 任务更新。
