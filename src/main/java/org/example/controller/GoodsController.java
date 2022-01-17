package org.example.controller;

import org.example.entity.SecKillUser;
import org.example.redis.GoodsKey;
import org.example.redis.RedisService;
import org.example.service.GoodsService;
import org.example.service.UsersService;
import org.example.vo.GoodsVo;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    UsersService usersService;
    RedisService redisService;
    GoodsService goodsService;
    ApplicationContext applicationContext;
    ThymeleafViewResolver thymeleafViewResolver;

    @Inject
    public GoodsController(UsersService usersService, RedisService redisService,
                           GoodsService goodsService, ApplicationContext applicationContext,
                           ThymeleafViewResolver thymeleafViewResolver) {
        this.usersService = usersService;
        this.redisService = redisService;
        this.goodsService = goodsService;
        this.applicationContext = applicationContext;
        this.thymeleafViewResolver = thymeleafViewResolver;
    }


    @RequestMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, SecKillUser user) {
        model.addAttribute("user", user);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);

        //取缓存
        String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        SpringWebContext context = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);

        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsList, "", html);
        }
        return html;
    }

    @RequestMapping(value = "/detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String detail(HttpServletRequest request, HttpServletResponse response, Model model, SecKillUser user,
                         @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user", user);

        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, "" + goodsId, String.class);//url缓存和页面缓存区别就是加上了这个goodsId
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        //查询商品列表
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int secKillStatus = 0;
        int remainSeconds = 0;

        if (now < startAt) {//秒杀还没开始，倒计时
            secKillStatus = 0;
            remainSeconds = (int) (startAt - now) / 1000;
        } else if (now > endAt) {//秒杀已经结束了
            secKillStatus = 2;
            remainSeconds = -1;
        } else {//秒杀进行中
            secKillStatus = 1;
            remainSeconds = 0;
        }

        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(remainSeconds * 1000);//转换为毫秒
        Date remainDate = calendar.getTime();
        model.addAttribute("remainDate", remainDate);

//        return "goods_detail";

        SpringWebContext context = new SpringWebContext(request, response,
                request.getServletContext(), request.getLocale(), model.asMap(), applicationContext);

        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsKey.getGoodsDetail, "" + goodsId, html);
        }

        return html;

    }

}
