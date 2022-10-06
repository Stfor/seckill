package com.example.seckill.service;

import com.example.seckill.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zhoubin
 * @since 2022-08-09
 */
public interface IUserService extends IService<User> {
    public RespBean doLogin(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request);

    public User getUserByCookie(String userTicket,HttpServletResponse response,HttpServletRequest request);
}
