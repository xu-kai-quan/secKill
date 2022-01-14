package org.example.controller;

import org.example.entity.Users;
import org.example.redis.RedisService;
import org.example.service.GoodsService;
import org.example.service.UsersService;
import org.example.vo.GoodsVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    UsersService usersService;
    RedisService redisService;
    GoodsService goodsService;

    @Inject
    public GoodsController(UsersService usersService, RedisService redisService, GoodsService goodsService) {
        this.usersService = usersService;
        this.redisService = redisService;
        this.goodsService = goodsService;
    }


    @RequestMapping("/to_list")
    public String list(Model model, Users user) {
        model.addAttribute("user", user);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    @RequestMapping("/detail/{goodsId}")
    public String detail(Model model, Users user,
                         @PathVariable("goodsId") long goodsId) {
        model.addAttribute("user", user);
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

        return "goods_detail";
    }

}
