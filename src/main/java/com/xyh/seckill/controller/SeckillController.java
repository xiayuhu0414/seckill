package com.xyh.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xyh.seckill.pojo.Order;
import com.xyh.seckill.pojo.SeckillMessage;
import com.xyh.seckill.pojo.SeckillOrder;
import com.xyh.seckill.pojo.User;
import com.xyh.seckill.rabbitmq.MQSender;
import com.xyh.seckill.service.IGoodsService;
import com.xyh.seckill.service.IOrderService;
import com.xyh.seckill.service.ISeckillOrderService;
import com.xyh.seckill.utils.JsonUtil;
import com.xyh.seckill.vo.GoodsVo;
import com.xyh.seckill.vo.RespBean;
import com.xyh.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.event.ListDataEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 秒杀
 * windows 优化前QPS：39.5
 * Linux优化前QPS: 21.2
 *
 * @author xyh
 * @date 2021/11/3 18:50
 */
@Component
@RequestMapping("/seckill")
@Slf4j
public class SeckillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();


    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(User user, Long goodsId) {
        log.info("进入秒杀" + user.getId() + "==========================================");
        if (user == null) {
            log.info("用户不存在");
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断重复抢购
        String seckillOrder = (String) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        // log.info("redis查询用户订单信息"+seckillOrder.toString());
        if (!StringUtils.isEmpty(seckillOrder)) {
            log.info("订单已存在" + seckillOrder);
            return RespBean.error(RespBeanEnum.REPEATE_EROR);
        }
        //内存标记，减少Redis的访问
        if (EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存操作
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
       /* GoodsVo goods = goodsService.findGoodVoByGoodsId(goodsId);
        log.info("商品信息"+goods.toString());
        //判断库存
        if (goods.getStockCount() < 1) {
            log.info("库存小于1");
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        log.info("完成库存判断");
        //判断是否重复抢购
       *//* SeckillOrder seckillOrder = seckillOrderService.getOne(new
                QueryWrapper<SeckillOrder>().eq("user_id",
                user.getId()).eq(
                "goods_id",
                goodsId));*//*

        String seckillOrder = (String) redisTemplate.opsForValue().get("order:"+user.getId()+":"+goods.getId());
       // log.info("redis查询用户订单信息"+seckillOrder.toString());
        if (!StringUtils.isEmpty(seckillOrder)) {
            log.info("订单已存在"+seckillOrder);
            return RespBean.error(RespBeanEnum.REPEATE_EROR);
        }
        Order order = orderService.secKill(user, goods);
        if (null!=order){
            return RespBean.success(order);
        }
        return RespBean.success(order);*/
    }


    @RequestMapping("/doSeckill2")
    public String doSecKill2(Model model, User user, Long goodsId) {
        if (user == null) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId())
                .eq("goods_id", goodsId));
        if (seckillOrder != null) {
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_EROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.secKill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";


    }

    /**
     * @description: 初始化时执行的方法
     * @return: void
     * @author xyh
     * @date: 2021/11/7 16:43
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }

    /**
    * @description:  获取秒杀结果
     * @param user:
    * @param goodsId:
    * @return: orderId:成功，-1： 秒杀失败，0 ：排队中
    * @author xyh
    * @date: 2021/11/7 17:27
    */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user,Long goodsId){
        if (user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long  orderId =  seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }
}
