package org.example.controller;

import org.example.entity.OrderInfo;
import org.example.entity.SecKillOrder;
import org.example.entity.SecKillUser;
import org.example.result.CodeMsg;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.service.SecKillService;
import org.example.vo.GoodsVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;

@Controller
@RequestMapping("/secKill")
public class secKillController {
    GoodsService goodsService;
    OrderService orderService;
    SecKillService secKillService;

    @Inject
    public secKillController(GoodsService goodsService, OrderService orderService, SecKillService secKillService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.secKillService = secKillService;
    }


    @RequestMapping("/do_secKill")
    public String list(Model model, SecKillUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return "login";
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stockCount = goods.getStockCount();
        if (stockCount <= 0) {
            model.addAttribute("errMsg", CodeMsg.SEC_KILL_OVER.getMsg());
            return "secKill_fail";
        }
        //判断是否已经秒杀到了
        SecKillOrder order = orderService.getSecKillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errMsg", CodeMsg.REPEAT_SEC_KILL.getMsg());
            return "secKill_fail";
        }
        // 减库存  下订单 写入秒杀订单(原子操作)

        OrderInfo orderInfo = secKillService.secKill(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }


}
