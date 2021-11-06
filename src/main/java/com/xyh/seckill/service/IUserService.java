package com.xyh.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xyh.seckill.pojo.User;
import com.xyh.seckill.vo.LoginVo;
import com.xyh.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2021-11-02
 */
public interface IUserService extends IService<User> {


    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);
    
    /**
    * @description: 根据cookie获取用户
    * @param: userTicket 
    * @return: com.xyh.seckill.pojo.User
    * @author xyh
    * @date: 2021/11/3 13:36
    */ 
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
}
