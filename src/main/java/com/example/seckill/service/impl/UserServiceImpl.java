package com.example.seckill.service.impl;

import com.example.seckill.exception.GlobalException;
import com.example.seckill.pojo.User;
import com.example.seckill.mapper.UserMapper;
import com.example.seckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.seckill.utils.CookieUtil;
import com.example.seckill.utils.MD5Util;
import com.example.seckill.utils.UUIDUtil;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;
import com.example.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zhoubin
 * @since 2022-08-09
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper mapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request) {
        String phone = loginVo.getMobile();
        String password = loginVo.getPassword();
        //判断是否为空
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(password)){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        User user = mapper.selectById(phone);
        if (user == null){
            throw new GlobalException(RespBeanEnum.ACCESS_LIMIT_REACHED);
        }

        //判断密码是否正确
        if (!user.getPassword().equals(MD5Util.formPassToDBPass(password,user.getSalt()))){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //登入成功获取cookie id
        String cookie = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("user:"+cookie,user);
        CookieUtil.setCookie(request,response,"UserTicket",cookie);
        return RespBean.success(cookie);
    }


//    public RespBean doLogin(LoginVo loginVo, HttpServletResponse response, HttpServletRequest request) {
//        String phone = loginVo.getMobile();
//        String password = loginVo.getPassword();
//        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(password)){
////            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
//        }
////        if (!ValidatorUtil.isMobile(phone)){
////            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
////        }
//        //获取用户，根据手机号即id
//        User user = mapper.selectById(phone);
//        if (user == null){
////            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//            throw new GlobalException(RespBeanEnum.ACCESS_LIMIT_REACHED);
//        }
//        //判断用户名密码是否正确，这里要注意得是数据库里得是前端传来得再次MD5加密得结果
//        if (!MD5Util.formPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
//            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
//        }
//        //用户可以登入之后就需要有一个登入状态的判断这里使用到的是cookie
//        String ticket = UUIDUtil.uuid();
////        request.getSession().setAttribute(ticket,user); 由于这里使用redis进行user对象的存储
//        redisTemplate.opsForValue().set("user:"+ticket,user);
//        CookieUtil.setCookie(request,response,"userTicket",ticket);
//        log.info(ticket);
//        return RespBean.success(ticket);
//    }

    public User getUserByCookie(String userTicket,HttpServletResponse response,HttpServletRequest request){
        if (StringUtils.isEmpty(userTicket)){
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:"+userTicket);
        if (user != null){
            CookieUtil.setCookie(request,response,"UserTicket",userTicket);
        }
        return user;
    }
}
