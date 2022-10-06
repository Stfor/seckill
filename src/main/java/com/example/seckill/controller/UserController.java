package com.example.seckill.controller;


import com.example.seckill.pojo.User;
import com.example.seckill.rabbitmq.MQSender;
import com.example.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2022-08-09
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    /**
     * 用户信息(测试)
     * @param user
     * @return
     */
    @RequestMapping("info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }


    /**
     * 测试发送mq消息
     */
    @RequestMapping("mq")
    @ResponseBody
    public void mq(){
        mqSender.send("Hello");
    }
}
