import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * @author: Lenovo
 * @date:Create：in 2020/10/15 12:04
 */
public class Tset {


    private static KafkaConsumer<String, String> consumer = null;

    static {
        Properties props = new Properties();
        // 必须设置的属性
        props.put("bootstrap.servers", "esnode1:19092");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("group.id", "group1");

        // 可选设置属性
        props.put("enable.auto.commit", "true");
        // 自动提交offset,每1s提交一次
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "latest ");
        props.put("client.id", "zy_client_id");
        consumer = new KafkaConsumer<>(props);
        // 订阅test1 topic
        consumer.subscribe(Collections.singletonList("agent_heartbeat_result"));
    }

    public static void main(String[] args) {

        while (true) {
            //  从服务器开始拉取数据
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            records.forEach(record -> {
                String heartBeatResult = record.value();
                if(heartBeatResult!=null){
                    System.out.println(heartBeatResult);
                    Map<String, Object> data = JSON.parseObject(heartBeatResult, Map.class);


                    System.out.println(data);
                }

            });
        }


    }
}
