package com.example.seckill.controller;


import com.example.seckill.exception.GlobalException;
import com.example.seckill.pojo.Goods;
import com.example.seckill.pojo.User;
import com.example.seckill.service.IGoodsService;
import com.example.seckill.service.impl.UserServiceImpl;
import com.example.seckill.vo.DetailVo;
import com.example.seckill.vo.GoodsVo;
import com.example.seckill.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2022-08-09
 */
@Controller
@Slf4j
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;


    //vesion-0.1 QPS 817
//    @RequestMapping("toList")
//    public String toList(@CookieValue("UserTicket") String cookie,Model model,
//                         HttpServletResponse response,HttpServletRequest request){
//        if (StringUtils.isEmpty(cookie)){
//            return "login";
//        }
//        User user = userService.getUserByCookie(cookie,response,request);
//        if (user == null){
//            return "login";
//        }
//        List<GoodsVo> goodsVo = goodsService.findGoodVo();
//        model.addAttribute("user",user);
//        model.addAttribute("goodsList",goodsVo);
//        return "goodsList";
//    }

    //version-0.2 使用对象缓存 QPS 1000
//    @RequestMapping(value = "toList",produces = "text/html;charset=utf-8")
//    public String toList(@CookieValue("UserTicket") String cookie,Model model,
//                         HttpServletResponse response,HttpServletRequest request){
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        if (StringUtils.isEmpty(cookie)){
//            return "login";
//        }
//        User user = (User) valueOperations.get("user:"+cookie);
//        if (user == null){
//            return "login";
//        }
//        List<GoodsVo> goodsVo = goodsService.findGoodVo();
//        model.addAttribute("user",user);
//        model.addAttribute("goodsList",goodsVo);
//        return "goodsList";
//    }

    //version0.3 使用对象缓存 QPS2206
    @RequestMapping(value = "toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(@CookieValue("UserTicket") String cookie,Model model,
                         HttpServletResponse response,HttpServletRequest request){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)){
            return html;
        }
        if (StringUtils.isEmpty(cookie)){
            return "login";
        }
        User user = (User) valueOperations.get("user:"+cookie);
        if (user == null){
            return "login";
        }
        List<GoodsVo> goodsVo = goodsService.findGoodVo();

        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsVo);
        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if (!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html,60,TimeUnit.SECONDS);
        }
        return html;
    }


//*****************************************************************************************************************
//    /**
//     * 跳转到商品列表页面
//     * windows优化前： 814QPS   优化后：1550QPS
//     * linux优化前： 166.9QPS
//     * @param
//     * @param model
//     * @param
//     * @return
//     */
//    @RequestMapping(value = "toList",produces = "text/html;charset=utf-8")
//    @ResponseBody
//    public String toList(Model model,User user,HttpServletResponse response,HttpServletRequest request){
//        //使用redis做页面缓存,先从redis中获取页面如果不为空就直接返回页面,如果为空手动渲染，存入redis并返回
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        String html = (String) valueOperations.get("goodsList");
//        if (!StringUtils.isEmpty(html)){
//            return html;
//        }
////        if (StringUtils.isEmpty(ticket)){
////            return "login";
////        }
//////        User user = (User) session.getAttribute(ticket);
////        User user = userService.getUserByCookie(ticket,response,request);
////        if (user == null){
////            return "login";
////        }
//        model.addAttribute("goodsList",goodsService.findGoodVo());
//        model.addAttribute("user",user);
//        WebContext webContext = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
//        if (!StringUtils.isEmpty(html)){
//            valueOperations.set("goodsList",html,60, TimeUnit.SECONDS);
//        }
//        return html;
//    }

//    @RequestMapping(value = "/toDetail/{goodsId}",produces = "text/html;charset=utf-8")
//    @ResponseBody
//    public String toDetail(Model model, User user, @PathVariable Long goodsId,
//                           HttpServletRequest request,HttpServletResponse response){
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //redis中获取页面，如果不为空，直接返回页面 ---页面缓存
//        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
//        if (!StringUtils.isEmpty(html)){
//            return html;
//        }
//
//        model.addAttribute("user",user);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodId(goodsId);
//        Date startDate = goodsVo.getStartDate();
//        Date endDate = goodsVo.getEndDate();
//        Date nowDate = new Date();
//        //秒杀状态
//        int secKillStatus = 0;
//        //秒杀倒计时
//        int remainSencond = 0;
//        if (nowDate.before(startDate)){
//            remainSencond = (int) (startDate.getTime() - nowDate.getTime() / 1000);
//        }else if (nowDate.after(endDate)){
//            secKillStatus = 2;
//            remainSencond = -1;
//        }else {
//            secKillStatus = 1;
//            remainSencond = 0;
//        }
//        model.addAttribute("remainSeconds",remainSencond);
//        model.addAttribute("secKillStatus",secKillStatus);
//        model.addAttribute("goods",goodsVo);
//        WebContext webContext = new WebContext(request,response,request.getServletContext()
//                ,request.getLocale(),model.asMap());
//        thymeleafViewResolver.getTemplateEngine().process("goodsDetail",webContext);
//        if (!StringUtils.isEmpty(html)){
//            valueOperations.set("goodsDetail:"+goodsId,html,60,TimeUnit.SECONDS);
//        }
//        return html;
//    }

    @RequestMapping(value = "/toDetail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable Long goodsId){
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSencond = 0;
        if (nowDate.before(startDate)){
            remainSencond = (int) (startDate.getTime() - nowDate.getTime() / 1000);
        }else if (nowDate.after(endDate)){
            secKillStatus = 2;
            remainSencond = -1;
        }else {
            secKillStatus = 1;
            remainSencond = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSencond);
        return RespBean.success(detailVo);
    }


}
