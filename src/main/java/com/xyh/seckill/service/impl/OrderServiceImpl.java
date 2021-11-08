package com.xyh.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyh.seckill.exception.GlobalException;
import com.xyh.seckill.mapper.OrderMapper;
import com.xyh.seckill.pojo.Order;
import com.xyh.seckill.pojo.SeckillGoods;
import com.xyh.seckill.pojo.SeckillOrder;
import com.xyh.seckill.pojo.User;
import com.xyh.seckill.service.IGoodsService;
import com.xyh.seckill.service.IOrderService;
import com.xyh.seckill.service.ISeckillGoodsService;
import com.xyh.seckill.service.ISeckillOrderService;
import com.xyh.seckill.utils.JsonUtil;
import com.xyh.seckill.utils.MD5Util;
import com.xyh.seckill.utils.UUIDUtil;
import com.xyh.seckill.vo.GoodsVo;
import com.xyh.seckill.vo.OrderDetailVo;
import com.xyh.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2021-11-03
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @description: 秒杀
     * @param: user
     * goods
     * @return: com.xyh.seckill.pojo.Order
     * @author xyh
     * @date: 2021/11/3 19:21
     */
    @Transactional
    @Override
    public Order secKill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.update(new
                UpdateWrapper<SeckillGoods>().setSql("stock_count=" + "stock_count-1")
                .eq("goods_id", goods.getId()).gt("stock_count", 0));
        // seckillGoodsService.updateById(seckillGoods);
        if (seckillGoods.getStockCount() < 1) {
            //判断是否还有库存
            valueOperations.set("isStockEmpty:" + goods.getId(), "0");
            return null;
        }
        seckillGoodsService.updateById(seckillGoods);
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAdderId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreatData(new Date());
        orderMapper.insert(order);
        System.out.println("开始生成秒杀订单" + order.toString());
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getId(), JsonUtil.object2JsonStr(seckillOrder));
        return order;
    }

    /**
     * @param orderId:
     * @description: 订单详情
     * @return: com.xyh.seckill.vo.OrderDetaiVo
     * @author xyh
     * @date: 2021/11/6 15:00
     */
    @Override
    public OrderDetailVo detail(Long orderId) {
        log.info("订单服务层");
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        log.info("订单id" + orderId);
        Order order = orderMapper.selectById(orderId);
        log.info("订单查询" + order);
        GoodsVo goodsVo = goodsService.findGoodVoByGoodsId(order.getGoodsId());
        log.info("vo" + "==========================" + goodsVo);
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        log.info("订单详情" + detail);
        return detail;
    }

    /**
     * @param user:
     * @param goodsId:
     * @description: 获取秒杀地址
     * @return: java.lang.String
     * @author xyh
     * @date: 2021/11/8 13:46
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "12345");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);

        return str;
    }

    /**
     * @param user:
     * @param goodsId:
     * @description: 校验秒杀地址
     * @return: boolean
     * @author xyh
     * @date: 2021/11/8 13:55
     */
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    /**
     * @param user:
     * @param goodsId:
     * @param captcha:
     * @description: 校验验证码
     * @return: boolean
     * @author xyh
     * @date: 2021/11/8 15:10
     */
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (StringUtils.isEmpty(captcha) || user == null || goodsId < 0) {
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
