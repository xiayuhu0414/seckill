package com.xyh.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyh.seckill.pojo.Order;
import com.xyh.seckill.pojo.User;
import com.xyh.seckill.vo.GoodsVo;
import com.xyh.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-11-03
 */
public interface IOrderService extends IService<Order> {

    /**
    * @description: 秒杀 
    * @param: user
goods
    * @return: com.xyh.seckill.pojo.Order
    * @author xyh
    * @date: 2021/11/3 19:21
    */    
    Order secKill(User user, GoodsVo goods);

    /***
    * @description: 订单详情 
     * @param orderId:  
    * @return: com.xyh.seckill.vo.OrderDetaiVo
    * @author xyh
    * @date: 2021/11/6 15:00
    */ 
    OrderDetailVo detail(Long orderId);
}
