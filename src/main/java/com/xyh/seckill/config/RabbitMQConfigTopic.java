package com.xyh.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类—Topic
 *
 * @author xyh
 * @date 2021/11/7 15:23
 */
@Configuration
public class RabbitMQConfigTopic {

    public static final String QUEUE= "seckillQueue";
    public static final String EXCHANGE = "seckillExchange";


    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(topicExchange()).with("seckill.#");
    }

}
