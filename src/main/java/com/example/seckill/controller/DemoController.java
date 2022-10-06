package com.example.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.seckill.pojo.User;
import com.example.seckill.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo")
@Api(tags = "asdasdasd")
public class DemoController {
    @Autowired
    private IUserService userService;
    /**
     * 功能描述：测试页面跳转
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "hello",method = RequestMethod.POST)
    public String hello(Model model){
        model.addAttribute("name","xxxx");
        return "hello";
    }

    @ApiOperation(value = "test测试",notes = "我也不知道是什么")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "AA",value = "不知道" ,paramType = "header",dataType = "string",required = true)
    })
    @RequestMapping(value = "test",method = RequestMethod.POST)
    public User test(){
        User user = userService.getOne(new QueryWrapper<User>().eq("nickname","admin"));
        return user;
    }

    @ApiOperation(value = "test测试",notes = "我也不知道是什么")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "aa",value = "不知daaosad" ,paramType = "header",dataType = "string",required = true)
    })
    @PostMapping(value = "testa")
    public String testa(String aa){
        return aa;
    }

}
