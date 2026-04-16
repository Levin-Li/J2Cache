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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.Level2Cache;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

/**
 * Redis 缓存操作封装，基于 region+_key 实现多个 Region 的缓存（
 * @author Winter Lau(javayou@gmail.com)
 */
public class RedisGenericCache implements Level2Cache {

    private final static Logger log = LoggerFactory.getLogger(RedisGenericCache.class);

    private String namespace;
    private String region;
    private RedisClient client;
    private int scanCount;

    /**
     * 缓存构造
     * @param namespace 命名空间，用于在多个实例中避免 _key 的重叠
     * @param region 缓存区域的名称
     * @param client 缓存客户端接口
     */
    public RedisGenericCache(String namespace, String region, RedisClient client, int scanCount) {
        if (region == null || region.isEmpty())
            region = "_"; // 缺省region

        this.client = client;
        this.namespace = namespace;
        this.region = _regionName(region);
        this.scanCount = scanCount;
    }

    @Override
    public boolean supportTTL() {
        return true;
    }

    /**
     * 在region里增加一个可选的层级,作为命名空间,使结构更加清晰
     * 同时满足小型应用,多个J2Cache共享一个redis database的场景
     *
     * @param region
     * @return
     */
    private String _regionName(String region) {
        if (namespace != null && !namespace.trim().isEmpty())
            region = namespace + ":" + region;
        return region;
    }

    private byte[] _key(String key) {
        try {
            return (this.region + ":" + key).getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            return (this.region + ":" + key).getBytes();
        }
    }

    @Override
    public byte[] getBytes(String key) {
        try {
            return client.get().get(_key(key));
        } finally {
            client.release();
        }
    }

    @Override
    public List<byte[]> getBytes(Collection<String> keys) {
        try {
            Jedis cmd = client.get();
            return keys.stream().map(k -> getBytes(k)).collect(Collectors.toList());
        } finally {
            client.release();
        }
    }

    @Override
    public void setBytes(String key, byte[] bytes) {
        try {
            client.get().set(_key(key), bytes);
        } finally {
            client.release();
        }
    }

    @Override
    public void setBytes(Map<String,byte[]> bytes) {
        try {
            Jedis cmd = client.get();
            bytes.forEach((k,v) -> cmd.set(_key(k), v));
        } finally {
            client.release();
        }
    }

    @Override
    public void setBytes(String key, byte[] bytes, long timeToLiveInSeconds) {
        if (timeToLiveInSeconds <= 0) {
            log.debug(String.format("Invalid timeToLiveInSeconds value : %d , skipped it.", timeToLiveInSeconds));
            setBytes(key, bytes);
        }
        else {
            try {
                client.get().setex(_key(key), (int) timeToLiveInSeconds, bytes);
            } finally {
                client.release();
            }
        }
    }

    @Override
    public void setBytes(Map<String,byte[]> bytes, long timeToLiveInSeconds) {
        try {
            /* 为了支持 TTL ，没法使用批量写入方法 */
            /*
            BinaryJedisCommands cmd = client.get();
            if(cmd instanceof MultiKeyBinaryCommands) {
                byte[][] data = new byte[bytes.size() * 2][];
                int idx = 0;
                for(String key : bytes.keySet()){
                    data[idx++] = _key(key);
                    data[idx++] = bytes.get(key);
                }
                ((MultiKeyBinaryCommands)cmd).mset(data);
            }
            else
            */

            if (timeToLiveInSeconds <= 0) {
                log.debug(String.format("Invalid timeToLiveInSeconds value : %d , skipped it.", timeToLiveInSeconds));
                setBytes(bytes);
            }
            else
                bytes.forEach((k,v) -> setBytes(k, v, timeToLiveInSeconds));
        } finally {
            client.release();
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            return client.get().exists(_key(key));
        } finally {
            client.release();
        }
    }

    /**
     * 1、线上redis服务大概率会禁用或重命名keys命令；
     * 2、keys命令效率太低容易致使redis宕机；
     * 所以使用scan命令替换keys命令操作，增加可用性及提升执行性能
     */
    @Override
    public Collection<String> keys() {
        try {
            Jedis cmd = client.get();
            Collection<String> keys = keys(cmd);
            return keys.stream().map(k -> k.substring(this.region.length()+1)).collect(Collectors.toList());
        } finally {
            client.release();
        }
    }

    private Collection<String> keys(Jedis cmd) {
        Collection<String> keys = new ArrayList<>();
        String cursor = "0";
        ScanParams scanParams = new ScanParams();
        scanParams.match(this.region + ":*");
        scanParams.count(scanCount);
        ScanResult<String> scan = cmd.scan(cursor, scanParams);
        while (!scan.isCompleteIteration()) {
            keys.addAll(scan.getResult());
            scan = cmd.scan(scan.getCursor(), scanParams);
        }
        return keys;
    }

    @Override
    public void evict(String...keys) {
        try {
            Jedis cmd = client.get();
            for (String key : keys)
                cmd.del(_key(key));
        } finally {
            client.release();
        }
    }

    /**
     * 已使用scan命令替换keys命令操作
     */
    @Override
    public void clear() {
        try {
            Jedis cmd = client.get();
            Collection<String> keysCollection = keys(cmd);
            String[] keys = keysCollection.stream().toArray(String[]::new);
            if (keys != null && keys.length > 0)
                cmd.del(keys);
        } finally {
            client.release();
        }
    }
}