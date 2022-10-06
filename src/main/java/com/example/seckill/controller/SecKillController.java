package com.example.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.seckill.mapper.SeckillOrderMapper;
import com.example.seckill.pojo.Order;
import com.example.seckill.pojo.SeckillGoods;
import com.example.seckill.pojo.SeckillOrder;
import com.example.seckill.pojo.User;
import com.example.seckill.service.IGoodsService;
import com.example.seckill.service.ISeckillGoodsService;
import com.example.seckill.service.ISeckillOrderService;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 秒杀
 */
@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean{
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private ISeckillOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;

//    /**
//     * window优化前： 1292QPS
//     * linux优化前：77.2QPS
//     * @param model
//     * @param user
//     * @param goodsId
//     * @return
//     */
//    @RequestMapping(value = "/doSeckill")
//    public String doSecKill(Model model, User user,Long goodsId){
//        if (user == null){
//            return "login";
//        }
//        model.addAttribute("user",user);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodId(goodsId);
//        //判断库存
//        if (goodsVo.getStockCount() < 1){
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK);
//            return "secKillFail";
//        }
//        //判断是否重复抢购
//        SeckillOrder secKillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
//        if (secKillOrder != null){
//            model.addAttribute("essmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
//            return "secKillFail";
//        }
//        Order order = orderService.seckill(user,goodsVo);
//        model.addAttribute("order",order);
//        model.addAttribute("goods",goodsVo);
//        return "orderDetail";
//    }

    @RequestMapping(value = "/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(Model model, User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodId(goodsId);
        //判断库存
        if (goodsVo.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢购
        SeckillOrder secKillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (secKillOrder != null){
            model.addAttribute("essmsg",RespBeanEnum.REPEATE_ERROR.getMessage());
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);

        Order order = orderService.seckill(user,goodsVo);
        return RespBean.success(order);
    }


//    /**
//     * 系统初始化，把商品的库存存入redis当中 ---Bean的生命周期
//     * @throws Exception
//     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodVo();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo ->
                redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(),goodsVo.getStockCount()));
    }
}
