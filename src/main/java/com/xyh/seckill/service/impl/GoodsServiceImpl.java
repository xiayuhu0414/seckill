package com.xyh.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyh.seckill.mapper.GoodsMapper;
import com.xyh.seckill.pojo.Goods;
import com.xyh.seckill.service.IGoodsService;
import com.xyh.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2021-11-03
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    /**
    * @description: 获取商品列表
    * @param:  
    * @return: java.util.List<com.xyh.seckill.vo.GoodsVo>
    * @author xyh
    * @date: 2021/11/3 16:05
    */ 
    @Override
    public List<GoodsVo> findGoodVo() {
        return goodsMapper.findGoodVo();
    }

    @Override
    public GoodsVo findGoodVoByGoodsId(Long goodsId) {
        return goodsMapper.findGoodVoByGoodsId(goodsId);
    }
}
