package net.oschina.j2cache.cluster;

import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.CacheProviderHolder;
import net.oschina.j2cache.Command;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

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
    private volatile Consumer kafkaConsumer;

    private Thread listenerThread;
    private static boolean kafkaConsumerRunFlag = true;


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
            HashMap<String, Object> propsMap = new HashMap<>();
            propsMap.put("bootstrap.servers", props.getProperty("kafka.bootstrap.servers", "127.0.0.1:9092"));
            //消息确认机制
            propsMap.put("acks", props.getProperty("kafka.acks", "all"));
            //重试次数
            propsMap.put("retries", Integer.valueOf(props.getProperty("kafka.retries", "0")));
            propsMap.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            propsMap.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            kafkaProducer = new KafkaProducer<>(propsMap);
            //发送消息，加入集群
            publish(Command.join());

            //以LOCAL_COMMAND_ID作为group.id,确保所有kafkaConsumer都属于不同组。达到kafka消息广播的效果
            propsMap.put("group.id", LOCAL_COMMAND_ID + "");
            //使用自动确认
            propsMap.put("enable.auto.commit", "true");
            propsMap.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            propsMap.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            kafkaConsumer = new KafkaConsumer<>(propsMap);
            //链接完毕后，拉起监听线程
            startListener();
            log.info("Connected to Kafka:{}, time {}ms", kafkaConsumer, System.currentTimeMillis() - ct);
        } catch (NumberFormatException e) {
            throw new CacheException(String.format("Failed to connect to Kafka (%s)", props.getProperty("bootstrap.servers", "127.0.0.1:9092"), e));
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
            publish(Command.quit());
            kafkaConsumerRunFlag = false;
            listenerThread.interrupt();
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

    public void startListener() {
        listenerThread = new Thread(() -> listener());
        listenerThread.setName("listenerThread");
        listenerThread.start();
    }

    private void listener() {
        kafkaConsumer.subscribe(Arrays.asList(topic));
        while (true) {
            log.info("kafkaConsumer  listener is running ");
            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                log.info("kafkaConsumer  listener  Command {} ", Command.parse(record.value()));
                handleCommand(Command.parse(record.value()));
            }
        }
    }
}
