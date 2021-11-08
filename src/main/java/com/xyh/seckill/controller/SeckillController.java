package com.xyh.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wf.captcha.ArithmeticCaptcha;
import com.xyh.seckill.config.AccessLimit;
import com.xyh.seckill.exception.GlobalException;
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
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedisScript<Long> script;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();


    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId) {
        log.info("进入秒杀" + user.getId() + "==========================================");
        if (user == null) {
            log.info("用户不存在");
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //判断重复抢购
        String seckillOrder = (String) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        // log.info("redis查询用户订单信息"+seckillOrder.toString());
        if (!StringUtils.isEmpty(seckillOrder)) {
            log.info("订单已存在" + seckillOrder);
            return RespBean.error(RespBeanEnum.REPEATE_EROR);
        }
        //内存标记，减少Redis的访问
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存操作
        //Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        log.info("LUA脚本开始执行！");
        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        log.info("LUA脚本执行完毕！");
        if (stock < 0) {
            EmptyStockMap.put(goodsId, true);
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


    @AccessLimit(second=5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //限制访问次数，5秒内访问5次
        /*String uri = request.getRequestURI();
        captcha="0";
        Integer count = (Integer) valueOperations.get(uri+":"+user.getId());
        if (count==null){
            valueOperations.set(uri+":"+user.getId(),1,5,TimeUnit.SECONDS);
        }else if (count<5){
            valueOperations.increment(uri+":"+user.getId());
        }else {
            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REAHCED);
        }*/
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }
        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
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
     * @param user:
     * @param goodsId:
     * @description: 获取秒杀结果
     * @return: orderId:成功，-1： 秒杀失败，0 ：排队中
     * @author xyh
     * @date: 2021/11/7 17:27
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (user == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入Redis
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, arithmeticCaptcha.text(), 300, TimeUnit.SECONDS);
        try {
            arithmeticCaptcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }
}
