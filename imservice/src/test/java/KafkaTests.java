import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hints.im.ImServiceApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/8/15 15:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ImServiceApplication.class)
public class KafkaTests {
    @Autowired
    private KafkaProducer kafkaProducer;

    /**
     * 测试生产者生产者消息消费者是否能够消费掉
     */
    @Test
    public void testKafka() {
        kafkaProducer.sendMessage("test", "你好");
        kafkaProducer.sendMessage("test", "在吗");
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 生产者发消息是主动去发送的
 */
@Component
class KafkaProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 发送消息
     *
     * @param topic 消息主题(分区)
     * @param content 消息的内容
     */
    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }
}

/**
 * 消费者消费消息是被动去消费的
 */
@Component
class KafkaConsumer {
    //绑定需要监听的主题,只要有消息就调用handleMessage方法进行消费，没有消息就阻塞住
    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }
}
