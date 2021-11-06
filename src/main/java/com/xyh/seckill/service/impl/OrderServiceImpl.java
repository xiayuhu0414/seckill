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
import com.xyh.seckill.vo.GoodsVo;
import com.xyh.seckill.vo.OrderDetailVo;
import com.xyh.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2021-11-03
 */
@Service
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
        //秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.update(new
                UpdateWrapper<SeckillGoods>().setSql("stock_count="+"stock_count-1")
                .eq("goods_id", goods.getId()).gt("stock_count",0));
         // seckillGoodsService.updateById(seckillGoods);
        if (!result) {
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
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);
        return detail;
    }
}
