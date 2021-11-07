package com.xyh.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyh.seckill.pojo.SeckillOrder;
import com.xyh.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-11-03
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
    * @description: 获取秒杀结果 
     * @param user: 
* @param goodsId:
    * @return: orderId:成功，-1： 秒杀失败，0 ：排队中
    * @author xyh
    * @date: 2021/11/7 17:32
    */ 
    Long getResult(User user, Long goodsId);
}
