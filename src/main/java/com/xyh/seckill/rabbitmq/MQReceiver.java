package com.xyh.seckill.rabbitmq;

import com.xyh.seckill.pojo.SeckillMessage;
import com.xyh.seckill.pojo.User;
import com.xyh.seckill.service.IGoodsService;
import com.xyh.seckill.service.IOrderService;
import com.xyh.seckill.utils.JsonUtil;
import com.xyh.seckill.vo.GoodsVo;
import com.xyh.seckill.vo.RespBean;
import com.xyh.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 消息的消费者
 *
 * @author xyh
 * @date 2021/11/7 13:27
 */
@Slf4j
@Service
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
        log.info("接收的消息" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodId = seckillMessage.getGoodId();
        User user = seckillMessage.getUser();
        //判断库存
        GoodsVo goodsVo = goodsService.findGoodVoByGoodsId(goodId);
        if (goodsVo.getStockCount() < 1) {
            return;
        }
        //判断重复抢购
        String seckillOrder = (String) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodId);
        // log.info("redis查询用户订单信息"+seckillOrder.toString());
        if (!StringUtils.isEmpty(seckillOrder)) {
            log.info("订单已存在" + seckillOrder);
            return;
        }
        orderService.secKill(user, goodsVo);
    }

    /*@RabbitListener(queues = "queue")
    public void receive(Object msg) {
        log.info("接收消息" + msg);
    }

    @RabbitListener(queues = "queue_fanout01")
    public void receive01(Object msg){
        log.info("Fanout模式 QUEUE01接收消息"+msg);
    }
    @RabbitListener(queues = "queue_fanout02")
    public void receive02(Object msg){
        log.info("Fanout模式 QUEUE02接收消息"+msg);
    }

    @RabbitListener(queues = "queue_direct01")
    public void receive03(Object msg){
        log.info("Direct模式 QUEUE01接收消息"+msg);
    }
    @RabbitListener(queues = "queue_direct02")
    public void receive04(Object msg){
        log.info("Direct模式 QUEUE02接收消息"+msg);
    }
    @RabbitListener(queues = "queue_topic01")
    public void receive05(Object msg){
        log.info("Topic模式 QUEUE01接收消息"+msg);
    }

    @RabbitListener(queues = "queue_topic02")
    public void receive06(Object msg){
        log.info("Topic模式 QUEUE02接收消息"+msg);
    }


    @RabbitListener(queues = "queue_header01")
    public void receive07(Message msg){
        log.info("Header模式 QUEUE01接收Message对象"+msg);
        log.info("QUEUE01接收消息："+new String(msg.getBody()));
    }

    @RabbitListener(queues = "queue_header02")
    public void receive08(Message msg){
        log.info("Header模式 QUEUE02接收Message对象"+msg);
        log.info("QUEUE02接收消息："+new String(msg.getBody()));
    }*/
}
