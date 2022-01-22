package org.example.rabbitmq;

import org.example.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class MQSender {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    AmqpTemplate amqpTemplate;

    @Inject
    public MQSender(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

//    public void send(Object message) {
//        String msg = RedisService.beanToString(message);
//        log.info("send message:" + msg);
//        amqpTemplate.convertAndSend(MQConfig.QUEUE, msg);
//    }

    public void sendSecKillMessage(SecKillMessage message) {
        String msg = RedisService.beanToString(message);
        log.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.SEC_KILL_QUEUE, msg);

    }
}
