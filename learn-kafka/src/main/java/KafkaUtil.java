import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * kafka工具类
 *
 * @author: liyuzhi
 * @date: 2020/10/15 11:07
 * @version: 1
 */
public class KafkaUtil {

    private static Producer<String, String> producer = null;
    private Properties props = new Properties();
    private List<ProducerRecord<String, String>> kafkaData = null;
    private long localCacheSize = 100L;

    static {
        KafkaUtil kafkaUtil = KafkaUtil.SingletonInner.singleton;
    }

    /**
     * 静态内部类，一种构建单利的方法
     */
    private static class SingletonInner {
        private static KafkaUtil singleton = new KafkaUtil();
    }

    /**
     * Construction Method.
     */
    private KafkaUtil() {
        props.put("bootstrap.servers", "192.168.251.227:19092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 33554432);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 335544320);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(props);
        kafkaData = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * set Local Cache Size.
     *
     * @param size Local Cache Size
     * @return this
     */
    public KafkaUtil setLocalCacheSize(long size) {
        this.localCacheSize = size;
        return this;
    }

    /**
     * Send One Message to Local Cache.
     *
     * @param topic topic name
     * @param msg   message contents
     */
    public void sendOneMessageUseLocalCache(String topic, String msg) {
        synchronized (kafkaData) {
            kafkaData.add(new ProducerRecord<>(topic, msg));
            if (localCacheSize < kafkaData.size()) {
                sendLocalCache();
            }
        }
    }

    /**
     * Send a set number of Messages to Local Cache.
     *
     * @param topic topic name
     * @param msgs  message contents
     */
    public void sendListMessageUseLocalCache(String topic, List<String> msgs) {
        msgs.forEach(msg -> sendOneMessageUseLocalCache(topic, msg));
    }

    /**
     * Send a set number of Local Cache to Kafka Broker.
     */
    public void sendLocalCache() {
        synchronized (kafkaData) {
            kafkaData.forEach(msg -> producer.send(msg));
            flushKafkaProducer();
            kafkaData.clear();
        }
    }

    /**
     * Send One Message to Kafka Broker.
     *
     * @param topic topic name
     * @param msg   message contents
     */
    public static void sendOneMessage(String topic, String msg) {
        producer.send(new ProducerRecord<>(topic, msg));
        flushKafkaProducer();
    }

    /**
     * Send a set number of Messages to Kafka Broker.
     *
     * @param topic topic name
     * @param msgs  message contents
     */
    public void sendListMessage(String topic, List<String> msgs) {
        msgs.forEach(msg -> producer.send(new ProducerRecord<>(topic, msg)));
        flushKafkaProducer();
    }

    /**
     * Flush Kafka Producer.
     */
    public static void flushKafkaProducer() {
        producer.flush();
    }

    /**
     * Close Kafka Producer.
     */
    public static void closeKafkaProducer() {
        producer.flush();
        producer.close();
    }
}
