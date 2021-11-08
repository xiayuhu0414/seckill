package com.xyh.seckill.controller;


import com.xyh.seckill.pojo.User;
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

}
