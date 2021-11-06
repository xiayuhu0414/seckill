package com.xyh.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xyh.seckill.pojo.Order;
import com.xyh.seckill.pojo.SeckillOrder;
import com.xyh.seckill.pojo.User;
import com.xyh.seckill.service.IGoodsService;
import com.xyh.seckill.service.IOrderService;
import com.xyh.seckill.service.ISeckillOrderService;
import com.xyh.seckill.vo.GoodsVo;
import com.xyh.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 秒杀
 * windows 优化前QPS：39.5
 * Linux优化前QPS: 21.2
 * @author xyh
 * @date 2021/11/3 18:50
 */
@Component
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;

    @RequestMapping("/doSeckill")
    public String doSecKill(Model model, User user,Long goodsId){
        if(user==null){
            return "login";
        }
        model.addAttribute("user",user);
        GoodsVo goods = goodsService.findGoodVoByGoodsId(goodsId);
        //判断库存
        if (goods.getStockCount()<1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId())
                .eq("goods_id", goodsId));
            if(seckillOrder!=null){
                model.addAttribute("errmsg", RespBeanEnum.REPEATE_EROR.getMessage());
                return "secKillFail";
            }
           Order order= orderService.secKill(user,goods);
            model.addAttribute("order",order);
            model.addAttribute("goods",goods);
            return "orderDetail";


    }
}
