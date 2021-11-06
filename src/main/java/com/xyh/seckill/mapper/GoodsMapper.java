package com.xyh.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xyh.seckill.pojo.Goods;
import com.xyh.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhoubin
 * @since 2021-11-03
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
    * @description: 获取商品列表 
    * @param:  
    * @return: java.util.List<com.xyh.seckill.vo.GoodsVo>
    * @author xyh
    * @date: 2021/11/3 16:13
    */ 
    List<GoodsVo> findGoodVo();
  
    /**
    * @description: 获取商品详情 
    * @param:  
    * @return: java.lang.String
    * @author xyh
    * @date: 2021/11/3 17:11
     * @param goodsId
    */ 
    GoodsVo findGoodVoByGoodsId(Long goodsId);
}
