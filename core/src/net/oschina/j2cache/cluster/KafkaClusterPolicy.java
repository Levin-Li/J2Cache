package net.oschina.j2cache.cluster;

import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.CacheProviderHolder;
import net.oschina.j2cache.Command;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author liulekang
 * @date 2020/8/17
 */
public class KafkaClusterPolicy implements ClusterPolicy {

    private static final Logger log = LoggerFactory.getLogger(KafkaClusterPolicy.class);

    private int LOCAL_COMMAND_ID = Command.genRandomSrc(); //命令源标识，随机生成，每个节点都有唯一标识

    private String topic;

    private CacheProviderHolder holder;

    private Producer kafkaProducer;

    private Consumer kafkaConsumer;

    private volatile boolean kafkaConsumerRunFlag = false;

    private static Future<Boolean> kafkaConsumerFuture = null;

    private static ThreadPoolExecutor kafkaConsumerExecutor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue(1), new BasicThreadFactory.Builder().namingPattern("kafka-consumer-thread-%d").build());

    private static final String jaasTemplate = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";
    /**
     * @param props RabbitMQ 配置信息
     */
    public KafkaClusterPolicy(Properties props) {
        this.topic = props.getProperty("topic", "j2cache");
    }

    /**
     * 连接到集群
     *
     * @param props  j2cache 配置信息
     * @param holder Cache Provider Instance
     */
    @Override
    public void connect(Properties props, CacheProviderHolder holder) {
        this.holder = holder;
        try {
            long ct = System.currentTimeMillis();
            Map<String, Object> propsMap = buildConnectProps(props);
            kafkaProducer = new KafkaProducer<>(propsMap);
            //发送消息，加入集群
            publish(Command.join());

            buildListenerProps(propsMap);
            kafkaConsumer = new KafkaConsumer<>(propsMap);
            //创建监听
            startListener();
            log.info("Connected to Kafka:{}, time {}ms", kafkaConsumer, System.currentTimeMillis() - ct);
        } catch (NumberFormatException e) {
            throw new CacheException(String.format("Failed to connect to Kafka (%s)", props.getProperty("bootstrap.servers", "127.0.0.1:9092")), e);
        }
    }

    /**
     * 构建创建监听的参数信息
     * @param propsMap
     */
    private void buildListenerProps(Map<String, Object> propsMap) {
        //以LOCAL_COMMAND_ID作为group.id,确保所有kafkaConsumer都属于不同组。达到kafka消息广播的效果
        propsMap.put("group.id", LOCAL_COMMAND_ID + "");
        //使用自动确认
        propsMap.put("enable.auto.commit", "true");
        propsMap.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        propsMap.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    }

    /**
     * 构建用于连接的参数信息
     * @param props
     * @return
     */
    private Map<String, Object> buildConnectProps(Properties props) {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, props.getProperty("bootstrap.servers", "127.0.0.1:9092"));
        //消息确认机制
        propsMap.put(ProducerConfig.ACKS_CONFIG, props.getProperty("acks", "all"));
        //重试次数
        propsMap.put(ProducerConfig.RETRIES_CONFIG, Integer.valueOf(props.getProperty("retries", "0")));
        //序列化方式
        propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //添加使用用户名密码时的处理
        useAuth(props, propsMap);
        return propsMap;
    }

    /**
     * 用户认证的处理
     * @param props
     * @param propsMap
     */
    private void useAuth(Properties props, Map<String, Object> propsMap) {
        if(Boolean.parseBoolean(props.getProperty("useAuth", "false"))){
            String jaasConfig = String.format(jaasTemplate, props.getProperty("username", "")
                    , props.getProperty("password", ""));
            propsMap.put("security.protocol", "SASL_PLAINTEXT");
            propsMap.put("sasl.mechanism", "SCRAM-SHA-256");
            propsMap.put("sasl.jaas.config", jaasConfig);
        }
    }

    /**
     * 发送消息
     *
     * @param cmd command to send
     */
    @Override
    public void publish(Command cmd) {
        try {
            cmd.setSrc(LOCAL_COMMAND_ID);
            kafkaProducer.send(new ProducerRecord(topic, cmd.toString()));
        } catch (Exception e) {
            throw new CacheException("Failed to publish cmd to Kafka!", e);
        }

    }

    /**
     * 断开集群连接
     */
    @Override
    public void disconnect() {
        try {
            //发送退出集群的指令
            publish(Command.quit());
            //停用消息监听
            kafkaConsumerRunFlag = false;
            if (kafkaConsumerFuture != null) {
                kafkaConsumerFuture.cancel(true);
            }
        } finally {
            try {
                kafkaProducer.close();
            } catch (Exception e) {
            }
            try {
                kafkaConsumer.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 删除本地某个缓存条目
     *
     * @param region 区域名称
     * @param keys   缓存键值
     */
    @Override
    public void evict(String region, String... keys) {
        holder.getLevel1Cache(region).evict(keys);
    }

    /**
     * 清除本地整个缓存区域
     *
     * @param region 区域名称
     */
    @Override
    public void clear(String region) {
        holder.getLevel1Cache(region).clear();
    }

    /**
     * 判断是否本地实例的命令
     *
     * @param cmd 命令信息
     * @return true if the cmd sent by self
     */
    @Override
    public boolean isLocalCommand(Command cmd) {
        return cmd.getSrc() == LOCAL_COMMAND_ID;
    }

    /**
     * 创建监听
     */
    private void startListener() {
        kafkaConsumerRunFlag = true;
        kafkaConsumerFuture = kafkaConsumerExecutor.submit(() -> {
            kafkaConsumer.subscribe(Arrays.asList(topic));
            while (kafkaConsumerRunFlag) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    log.info("kafkaConsumer  listener  Command {} ", Command.parse(record.value()));
                    handleCommand(Command.parse(record.value()));
                }
            }
            return true;
        });
        log.info("kafkaConsumer listener is running ");
    }
}
