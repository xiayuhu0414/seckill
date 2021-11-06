package com.xyh.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyh.seckill.pojo.Goods;
import com.xyh.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-11-03
 */
public interface IGoodsService extends IService<Goods> {

    /**
    * @description: 获取商品列表
    * @param:  
    * @return: java.util.List<com.xyh.seckill.vo.GoodsVo>
    * @author xyh
    * @date: 2021/11/3 16:04
    */ 
    List<GoodsVo> findGoodVo();

    /**
    * @description: 获取商品详情 
    * @param: goodsId 
    * @return: java.lang.String
    * @author xyh
    * @date: 2021/11/3 17:12
    */ 
    GoodsVo findGoodVoByGoodsId(Long goodsId);
}
