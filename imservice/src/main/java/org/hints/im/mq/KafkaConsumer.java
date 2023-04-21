//package org.hints.im.mq;
//
//import com.alibaba.fastjson.JSONObject;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.hints.im.persist.DataBaseStore;
//import org.hints.im.pojo.GroupBody;
//import org.hints.im.pojo.MsgBody;
//import org.hints.im.pojo.entity.GroupHistoryDO;
//import org.hints.im.utils.AsyncFactory;
//import org.hints.im.utils.AsyncManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoOperations;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
///**
// * @Description TODO
// * @Author hints
// * @Date 2022/8/15 14:34
// */
//@Component
//@Slf4j
//public class KafkaConsumer {
//
//    @Autowired
//    private MongoOperations mongoOperations;
//
//
//
//    @KafkaListener(topics = "test")
//    public void listenChat(ConsumerRecord<String, String> record) {
//        String value = record.value();
//
//        MsgBody msgBody = JSONObject.parseObject(value, MsgBody.class);
//
//        log.info("async persistence ...");
//        AsyncManager.me().execute(AsyncFactory.asyncStoreChat(msgBody));
//
//        log.info("toMongodb ...");
//        mongoOperations.save(msgBody,"message");
//    }
//
//    @KafkaListener(topics = "group")
//    public void listenGroup(ConsumerRecord<String, String> record) {
//        String value = record.value();
//
//        GroupHistoryDO groupHistoryDO = JSONObject.parseObject(value, GroupHistoryDO.class);
//
//        log.info("async persistence ...");
//        AsyncManager.me().execute(AsyncFactory.asyncStoreGroup(groupHistoryDO));
//
//        log.info("toMongodb ...");
//        mongoOperations.save(groupHistoryDO,"group");
//
//    }
//
//
//}
