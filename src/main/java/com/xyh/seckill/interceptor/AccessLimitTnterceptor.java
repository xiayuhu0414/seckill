package com.xyh.seckill.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyh.seckill.config.AccessLimit;
import com.xyh.seckill.config.UserContext;
import com.xyh.seckill.pojo.User;
import com.xyh.seckill.service.IUserService;
import com.xyh.seckill.utils.CookieUtils;
import com.xyh.seckill.vo.RespBean;
import com.xyh.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @author xyh
 * @date 2021/11/8 16:23
 */
@Component
public class AccessLimitTnterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            User user =getUser(request,response);
            UserContext.setUser(user);
            HandlerMethod hm= (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit==null){
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin){
                if (user==null){
                    render(response,RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                key +=":"+user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count=(Integer) valueOperations.get(key);
            if (count==null){
                valueOperations.set(key,1,second, TimeUnit.SECONDS);
            }else if (count<maxCount){
                valueOperations.increment(key);
            }else {
                render(response,RespBeanEnum.ACCESS_LIMIT_REAHCED);
                return false;
            }
        }
        return true;
    }

    /**
    * @description: 构建返回对象
     * @param response
     * @param respBeanEnum
    * @return: void
    * @author xyh
    * @date: 2021/11/8 16:45
    */
    private void render( HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }

    /**
    * @description: 获取当前登录对象
     * @param request:
* @param response:
    * @return: com.xyh.seckill.pojo.User
    * @author xyh
    * @date: 2021/11/8 16:44
    */
    private User getUser(HttpServletRequest request, HttpServletResponse response){
        String ticket = CookieUtils.getCookieValue(request, "userTicket");
        if (StringUtils.isEmpty(ticket)) {
            return null;
        }
        return userService.getUserByCookie(ticket, request, response);
    }
}
