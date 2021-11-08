package com.xyh.seckill.config;

import com.xyh.seckill.pojo.User;

/**
 * @author xyh
 * @date 2021/11/8 16:33
 */
public class UserContext {
    private static ThreadLocal<User> userHolder=new ThreadLocal<>();

    public static void setUser(User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }

}
