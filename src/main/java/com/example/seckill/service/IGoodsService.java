package com.example.seckill.service;

import com.example.seckill.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhoubin
 * @since 2022-08-09
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodVo();

    GoodsVo findGoodsVoByGoodId(Long goodsId);
}
