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


}
