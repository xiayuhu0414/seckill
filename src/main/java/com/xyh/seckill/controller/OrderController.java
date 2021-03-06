package com.xyh.seckill.controller;


import com.xyh.seckill.pojo.User;
import com.xyh.seckill.service.IOrderService;
import com.xyh.seckill.vo.OrderDetailVo;
import com.xyh.seckill.vo.RespBean;
import com.xyh.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.rmi.runtime.Log;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2021-11-03
 */
@Controller
@RequestMapping("/order")
@Slf4j
public class OrderController {
    @Autowired
    private IOrderService orderService;

    /**
    * @description: 订单详情 
     * @param user: 
* @param orderId:
    * @return: com.xyh.seckill.vo.RespBean
    * @author xyh
    * @date: 2021/11/6 14:51
    */ 
    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user,Long orderId){
        if (user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        log.info("我传入的是"+orderId+"------------------------------------------------------");
        OrderDetailVo detail=orderService.detail(orderId);

        log.info("返回订单信息"+detail);
        return RespBean.success(detail);

    }

}
