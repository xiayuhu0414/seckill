package com.xyh.seckill.controller;


import com.xyh.seckill.pojo.User;
import com.xyh.seckill.rabbitmq.MQReceiver;
import com.xyh.seckill.rabbitmq.MQSender;
import com.xyh.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MQSender mqSender;

    /**
     * @description: 用户信息（测试）
     * @param: user
     * @return: com.xyh.seckill.vo.RespBean
     * @author xyh
     * @date: 2021/11/4 13:43
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }

/*    *//**
     * @description: 测试发送RabbitMQ消息
     * @return: void
     * @author xyh
     * @date: 2021/11/7 13:31
     *//*
    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        mqSender.send("Hello");
    }

    *//**
     * @description: fanout模式
     * @return: void
     * @author xyh
     * @date: 2021/11/7 14:15
     *//*
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq01() {
        mqSender.send("Hello");
    }

    *//**
     * @description: direct模式
     * @return: void
     * @author xyh
     * @date: 2021/11/7 14:38
     *//*
    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mq02() {
        mqSender.send01("Hello,red");
    }

    *//**
     * @description: direct 模式
     * @return: void
     * @author xyh
     * @date: 2021/11/7 14:39
     *//*
    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mq03() {
        mqSender.send02("Hello,green");
    }

    *//**
    * @description: topic 模式
    * @return: void
    * @author xyh
    * @date: 2021/11/7 15:34
    *//*
    @RequestMapping("/mq/direct03")
    @ResponseBody
    public void mq04() {
        mqSender.send03("Hello,topic，匹配1个");
    }

    *//**
    * @description: topic 模式
    * @return: void
    * @author xyh
    * @date: 2021/11/7 15:35
    *//*
    @RequestMapping("/mq/direct04")
    @ResponseBody
    public void mq05() {
        mqSender.send04("Hello,topic，匹配两个");
    }

    *//**
    * @description: header模式
    * @return: void
    * @author xyh
    * @date: 2021/11/7 16:22
    *//*
    @RequestMapping("/mq/header01")
    @ResponseBody
    public void mq06() {
        mqSender.send05("Hello,Header01");
    }
    *//**
    * @description: header模式
    * @return: void
    * @author xyh
    * @date: 2021/11/7 16:22
    *//*
    @RequestMapping("/mq/header02")
    @ResponseBody
    public void mq07() {
        mqSender.send06("Hello,Header02");
    }*/
}
