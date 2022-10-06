package com.example.seckill.service;

import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.pojo.User;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.OrderDeatilVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2022-08-09
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Order seckill(User user, GoodsVo goodsVo);

    /**
     * 订单详情方法
     *
     * @param orderId
     * @return com.example.seckilldemo.vo.OrderDeatilVo
     * @author LC
     * @operation add
     * @date 10:21 下午 2022/3/6
     **/
    OrderDeatilVo detail(Long orderId);
}
