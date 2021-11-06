package com.xyh.seckill.vo;

import com.xyh.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 详情返回对象
 *
 * @author xyh
 * @date 2021/11/6 10:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {
    private User user;
    private GoodsVo goodsVo;
    private int secKillStats;
    private int remainSeconds;
}
