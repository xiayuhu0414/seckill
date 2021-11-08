package com.xyh.seckill;

import com.xyh.seckill.mapper.OrderMapper;
import com.xyh.seckill.mapper.SeckillOrderMapper;
import com.xyh.seckill.pojo.Order;
import com.xyh.seckill.pojo.SeckillOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SeckillApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisScript script;

    @Test
    void contextLoads() {


        Order order = orderMapper.selectById(1);
        System.out.println(order);
        SeckillOrder order2 = seckillOrderMapper.selectById(1);
        System.out.println(order2);

        //  redisTemplate.opsForValue().set("111", "2st");
        // String seckillOrder = (String)redisTemplate.opsForValue().get("order:13000000001:1");
        // System.out.println(seckillOrder);

    }

    @Test
    public void testLock01() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //占位，如果key不存在才可以设置成功
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1");
        //如果占位成功，进行正常操作
        if (isLock) {
            valueOperations.set("name", "22222");
            String name = (String) valueOperations.get("name");
            System.out.println("name" + name);
            Integer.parseInt("xxxxx");
            //操作结束，删除锁
            //redisTemplate.delete("k1");
        } else {
            System.out.println("有线程使用中，请稍后再试！");
        }

    }

    @Test
    public void testLock02() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //给锁添加一个过期时间，防止应用在运行过程中抛出异常导致锁无法正常释放
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        //如果占位成功，进行正常操作
        if (isLock) {
            valueOperations.set("name", "22222");
            String name = (String) valueOperations.get("name");
            System.out.println("name" + name);
            //产生异常
            Integer.parseInt("xxxxx");
            //操作结束，删除锁
            //redisTemplate.delete("k1");
        } else {
            System.out.println("有线程使用中，请稍后再试！");
        }
    }

    @Test
    public void testLock03() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        Boolean isLock = valueOperations.setIfAbsent("k1", value, 125, TimeUnit.SECONDS);
        //如果占位成功，进行正常操作
        if (isLock) {
            valueOperations.set("name", "22222");
            String name = (String) valueOperations.get("name");
            System.out.println("name" + name);
            System.out.println(valueOperations.get("k1"));
            Boolean result = (Boolean) redisTemplate.execute(script, Collections.singletonList("k1"), value);
            System.out.println(result);
        } else {
            System.out.println("有线程使用中，请稍后再试！");
        }
    }


}
