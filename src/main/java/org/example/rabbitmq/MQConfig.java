package org.example.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MQConfig {

    public static final String QUEUE = "secKill.queue";
    public static final String SEC_KILL_QUEUE = "queue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }
}
