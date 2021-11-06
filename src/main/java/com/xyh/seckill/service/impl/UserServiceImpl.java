package com.xyh.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xyh.seckill.exception.GlobalException;
import com.xyh.seckill.mapper.UserMapper;
import com.xyh.seckill.pojo.User;
import com.xyh.seckill.service.IUserService;
import com.xyh.seckill.utils.CookieUtils;
import com.xyh.seckill.utils.MD5Util;
import com.xyh.seckill.utils.UUIDUtil;
import com.xyh.seckill.vo.LoginVo;
import com.xyh.seckill.vo.RespBean;
import com.xyh.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2021-11-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {


    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        //参数校验
//        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)) {
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//
//        if (!ValidatorUtil.isMobile(mobile)) {
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        User user = userMapper.selectById(mobile);
        //更具手机号获取用户
        if (null==user){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        if (!MD5Util.fromPassToDBPass(password,user.getSlat()).equals(user.getPassword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成cookie
       String ticket = UUIDUtil.uuid();
       // String ticket = "1w2e3t4h5j6jbrehh";
        //将用户信息存入redis中
        redisTemplate.opsForValue().set("user:" + ticket, user);
        //request.getSession().setAttribute(ticket,user);
        CookieUtils.setCookie(request,response,"userTicket",ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket,HttpServletRequest request,HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)){
            return null;
        }
        User user =(User) redisTemplate.opsForValue().get("user:" + userTicket);
        if (user!=null){
            CookieUtils.setCookie(request,response,"userTicket",userTicket);
        }
        return user;
    }
}
