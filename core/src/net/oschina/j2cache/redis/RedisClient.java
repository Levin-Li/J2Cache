/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.util.Pool;

import java.io.Closeable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class RedisClient implements Closeable, AutoCloseable {

    private final static Logger log = LoggerFactory.getLogger(RedisClient.class);

    private final static int CONNECT_TIMEOUT = 5000;
    private final static int SO_TIMEOUT = 5000;
    private final static int MAX_ATTEMPTS = 3;

    private ThreadLocal<Jedis> clients;

    private JedisCluster cluster;
    private JedisPool single;
    private JedisSentinelPool sentinel;

    public static class Builder {
        private String mode;
        private String hosts;
        private String password;
        private String cluster;
        private int database;
        private JedisPoolConfig poolConfig;
        private boolean ssl;

        public Builder(){}

        public Builder mode(String mode){
            if(mode == null || mode.trim().length() == 0)
                this.mode = "single";
            else
                this.mode = mode;
            return this;
        }
        public Builder hosts(String hosts){
            if(hosts == null || hosts.trim().length() == 0)
                this.hosts = "127.0.0.1:6379";
            else
                this.hosts = hosts;
            return this;
        }
        public Builder password(String password){
            if(password != null && password.trim().length() > 0)
                this.password = password;
            return this;
        }
        public Builder cluster(String cluster) {
            if(cluster == null || cluster.trim().length() == 0)
                this.cluster = "j2cache";
            else
                this.cluster = cluster;
            return this;
        }
        public Builder database(int database){
            this.database = database;
            return this;
        }
        public Builder poolConfig(JedisPoolConfig poolConfig){
            this.poolConfig = poolConfig;
            return this;
        }
        public Builder ssl(boolean ssl) {
            this.ssl = ssl;
            return this;
        }
        public RedisClient newClient() {
            return new RedisClient(mode, hosts, password, cluster, database, poolConfig, ssl);
        }
    }

    private RedisClient(String mode, String hosts, String password, String cluster_name, int database, JedisPoolConfig poolConfig, boolean ssl) {
        password = (password != null && password.trim().length() > 0)? password.trim(): null;
        this.clients = new ThreadLocal<>();
        switch(mode){
            case "sentinel":
                Set<String> nodes = new HashSet<>();
                for(String node : hosts.split(","))
                    nodes.add(node);
                this.sentinel = new JedisSentinelPool(cluster_name, nodes, poolConfig, CONNECT_TIMEOUT, password);
                break;
            case "cluster":
                Set<HostAndPort> hps = new HashSet<>();
                for(String node : hosts.split(",")){
                    net.oschina.j2cache.util.HostAndPort hostAndPort = net.oschina.j2cache.util.HostAndPort.fromString(node);
                    String host = hostAndPort.getHost();
                    int port = hostAndPort.getPort();
                    hps.add(new HostAndPort(host, port));
                }
                DefaultJedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                    .password(password)
                    .connectionTimeoutMillis(CONNECT_TIMEOUT)
                    .socketTimeoutMillis(SO_TIMEOUT)
                    .build();
                this.cluster = new JedisCluster(hps, clientConfig, MAX_ATTEMPTS,
                        (org.apache.commons.pool2.impl.GenericObjectPoolConfig) poolConfig);
                break;
            default:
                for(String node : hosts.split(",")) {
                    net.oschina.j2cache.util.HostAndPort hostAndPort = net.oschina.j2cache.util.HostAndPort.fromString(node);
                    String host = hostAndPort.getHost();
                    int port = hostAndPort.getPort();
                    this.single = new JedisPool(poolConfig, host, port, CONNECT_TIMEOUT, password, database);
                    break;
                }
                if(!"single".equalsIgnoreCase(mode))
                    log.warn("Redis mode [{}] not defined. Using 'single'.", mode);
                break;
        }
    }

    public Jedis get() {
        Jedis client = clients.get();
        if(client == null) {
            if (single != null)
                client = single.getResource();
            else if (sentinel != null)
                client = sentinel.getResource();
            else if (cluster != null)
                client = null;

            if (client != null)
                clients.set(client);
        }
        return client;
    }

    public void release() {
        Jedis client = clients.get();
        if(client != null) {
            try {
                client.close();
            } catch(Exception e) {
                log.error("Failed to release jedis connection.", e);
            }
            clients.remove();
        }
    }

    @Override
    public void close() throws IOException {
        if(single != null)
            single.close();
        if(sentinel != null)
            sentinel.close();
        if(cluster != null)
            cluster.close();
    }

}
