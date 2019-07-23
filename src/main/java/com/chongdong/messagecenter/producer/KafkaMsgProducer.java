package com.chongdong.messagecenter.producer;

import com.chongdong.messagecenter.utils.UidGenerator;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaMsgProducer {

    private String message;

    private String topic;

    private Properties properties;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMsgProducer.class);

    UidGenerator generator = UidGenerator.getInstance("kafka");

    public KafkaMsgProducer(String message, String topic, Properties properties) {
        this.message = message;
        this.topic = topic;
        this.properties = properties;
    }

    public KafkaMsgProducer getInstance(String message, String topic, Properties properties) {
        return new KafkaMsgProducer(message, topic, properties);
    }

    public void sendMessage() {
        KafkaProducer<String, String> producer = null;
        try {
            producer = new KafkaProducer(properties);
            String key = generator.nextUid();
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, message);
            producer.send(record);
            producer.close();
            LOGGER.info("kafka推送消息成功: topip is {}, key is {}, message is {} ", topic, key, message);
        } catch (Exception e) {
            LOGGER.error("kafka推送消息失败:topic is {},  message is {} ", topic, message, e);
        }
    }
}
