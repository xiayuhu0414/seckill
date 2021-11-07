package com.xyh.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息发送者
 *
 * @author xyh
 * @date 2021/11/7 13:25
 */
@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
    * @description: 发送秒杀信息 
     * @param message:  
    * @return: void
    * @author xyh
    * @date: 2021/11/7 17:02
    */ 
    public void sendSeckillMessage(String message){
        log.info("发送信息"+message);
        rabbitTemplate.convertAndSend("seckillExchange","seckill.message",message);
    }

 /*   public void send(Object msg) {
        log.info("发送消息" + msg);
        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
    }

    public void send01(Object msg){
        log.info("发送red消息"+msg);
        rabbitTemplate.convertAndSend("directExchange","queue.red",msg);
    }
    public void send02(Object msg){
        log.info("发送green消息"+msg);
        rabbitTemplate.convertAndSend("directExchange","queue.green",msg);
    }

    public void send03(Object msg){
        log.info("QUEUE01接收"+msg);
        rabbitTemplate.convertAndSend("TopicExchange","queue.red.message",msg);
    }

    public void send04(Object msg){
        log.info("两个QUEUE接收"+msg);
        rabbitTemplate.convertAndSend("TopicExchange","message.queue.message",msg);
    }

    public void send05(String msg){
        log.info("被两个QUEUE接收"+msg);
        MessageProperties properties=new MessageProperties();
        properties.setHeader("color","red");
        properties.setHeader("speed","fast");
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend("headerExchange","",message);
    }
    public void send06(String msg){
        log.info("被QUEUE01接收"+msg);
        MessageProperties properties=new MessageProperties();
        properties.setHeader("color","red");
        properties.setHeader("speed","normal");
        Message message = new Message(msg.getBytes(), properties);
        rabbitTemplate.convertAndSend("headerExchange","",message);
    }*/
}
