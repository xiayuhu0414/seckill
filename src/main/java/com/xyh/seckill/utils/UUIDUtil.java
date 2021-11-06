package com.xyh.seckill.utils;

import java.util.UUID;

/**
 * @author xyh
 * @date 2021/11/2 16:43
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}