package com.example.seckill.controller;


import com.example.seckill.pojo.User;
import com.example.seckill.service.ISeckillOrderService;
import com.example.seckill.vo.OrderDeatilVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2022-08-09
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private ISeckillOrderService orderService;


//    @RequestMapping(value = "/detail", method = RequestMethod.GET)
//    @ResponseBody
//    public RespBean detail(User tUser, Long orderId) {
//        if (tUser == null) {
//            return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        }
//        OrderDeatilVo orderDeatilVo = orderService.detail(orderId);
//        return RespBean.success(orderDeatilVo);
//    }
}
