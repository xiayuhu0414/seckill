package com.xyh.seckill.controller;

import com.xyh.seckill.service.IUserService;
import com.xyh.seckill.vo.LoginVo;
import com.xyh.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author xyh
 * @date 2021/11/1 17:03
 */

@RequestMapping("/login")
@Controller
@Slf4j
public class LoginController {

    @Autowired
    private IUserService userService;


    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }
    
    /**
    * @description: 登录功能
    * @param: loginVo
    * @return: com.xyh.seckill.vo.RespBean
    * @author xyh
    * @date: 2021/11/2 10:09
    */
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){

        log.info("{}",loginVo);
        return userService.doLogin(loginVo,request,response);
    }
}
