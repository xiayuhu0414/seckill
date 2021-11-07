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
    /*public static final String QUEUE01 = "queue_topic01";
    public static final String QUEUE02 = "queue_topic02";
    public static final String EXCHANGE = "TopicExchange";
    public static final String ROUNTINGKEY01 = "#.queue.#";
    public static final String ROUNTINGKEY02 = "*.queue.*";


    @Bean
    public Queue queue01(){
        return new Queue(QUEUE01);
    }
    @Bean
    public Queue queue02(){
        return new Queue(QUEUE02);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding01(){
        return BindingBuilder.bind(queue01()).to(topicExchange()).with(ROUNTINGKEY01);
    }
    @Bean
    public Binding binding02(){
        return BindingBuilder.bind(queue02()).to(topicExchange()).with(ROUNTINGKEY02);
    }
*/
}
