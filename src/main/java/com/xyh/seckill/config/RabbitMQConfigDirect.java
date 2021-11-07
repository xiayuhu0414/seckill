package com.xyh.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类-Direct模式
 *
 * @author xyh
 * @date 2021/11/7 14:24
 */
//@Configuration
public class RabbitMQConfigDirect {

    public static final String QUEUE01 = "queue_direct01";
    public static final String QUEUE02 = "queue_direct02";
    public static final String DIRECTEXCHANGE = "directExchange";
    public static final String ROUTINGKEY01 = "queue.red";
    public static final String ROUTINGKEY02 = "queue.green";


    @Bean
    public Queue queue01(){
        return new Queue(QUEUE01);
    }
    @Bean
    public Queue queue02(){
        return new Queue(QUEUE02);
    }
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(DIRECTEXCHANGE);
    }
    @Bean
    public Binding binding01(){
        return BindingBuilder.bind(queue01()).to(directExchange()).with(ROUTINGKEY01);
    }
    @Bean
    public Binding binding02(){
        return BindingBuilder.bind(queue02()).to(directExchange()).with(ROUTINGKEY02);
    }
}
