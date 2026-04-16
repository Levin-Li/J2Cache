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
