package org.hints.im.mq;

import org.hints.im.pojo.MsgBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/15 14:33
 */
@RestController
public class KafkaProducer {
    private final static String TOPIC_NAME = "test1";

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private MongoOperations mongoOperations;

    @RequestMapping("/send")
    public void send() {
        kafkaTemplate.send(TOPIC_NAME, "test message send~");


        MsgBody msgBody = new MsgBody();
        msgBody.setMsgId("123");
        msgBody.setFromUserId("123");
        msgBody.setMessage("123");

        mongoOperations.save(msgBody);

    }
}