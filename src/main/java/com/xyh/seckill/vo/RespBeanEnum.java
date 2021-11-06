package com.xyh.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author xyh
 * @date 2021/11/1 17:31
 * 公共返回对象枚举
 */

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),
    //登录模块5002
    LOGIN_ERROR(500210,"用户名或密码不正确"),
    MOBILE_ERROR(500211,"手机号码格式不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    /*MOBILE_NOT_EXIST(500213,"手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214,"密码更新失败"),*/

    //秒杀模块5005
    EMPTY_STOCK(500500,"库存不足"),
    REPEATE_EROR(500501,"该商品没人限购一件")
    ;
    private final Integer code;
    private final String message;

}
