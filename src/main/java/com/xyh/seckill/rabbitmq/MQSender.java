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


}
