package com.example.seckill.controller;

import com.example.seckill.service.impl.UserServiceImpl;
import com.example.seckill.vo.LoginVo;
import com.example.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("login")
@Slf4j
public class LoginController {
    @Autowired
    private UserServiceImpl userService;
    /**
     *
     * @return
     */
    @RequestMapping("tologin")
    public String toLogin(){
        return "login";
    }

    /**
     * 登录功能
     * @param loginVo
     * @return
     */
    @RequestMapping("/doLogin")
    @ResponseBody
    public RespBean dologin(@Valid LoginVo loginVo, HttpServletResponse response, HttpServletRequest request){
        return  userService.doLogin(loginVo,response,request);
    }

}
