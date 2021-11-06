package com.xyh.seckill;

import com.xyh.seckill.pojo.SeckillOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class SeckillApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {

      //  redisTemplate.opsForValue().set("111", "2st");
        String seckillOrder = (String)redisTemplate.opsForValue().get("order:13000000001:1");
        System.out.println(seckillOrder);

    }

}
