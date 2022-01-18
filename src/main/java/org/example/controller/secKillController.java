package org.example.controller;

import com.sun.org.apache.bcel.internal.classfile.Code;
import org.example.entity.OrderInfo;
import org.example.entity.SecKillOrder;
import org.example.entity.SecKillUser;
import org.example.result.CodeMsg;
import org.example.result.Result;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.service.SecKillService;
import org.example.vo.GoodsVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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


    @RequestMapping(value = "/do_secKill", method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> list(Model model, SecKillUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stockCount = goods.getStockCount();
        if (stockCount <= 0) {
            return Result.error(CodeMsg.SEC_KILL_OVER);
        }
        //判断是否已经秒杀到了
        SecKillOrder order = orderService.getSecKillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEAT_SEC_KILL);
        }
        // 减库存  下订单 写入秒杀订单(原子操作)

        OrderInfo orderInfo = secKillService.secKill(user, goods);
        return Result.success(orderInfo);
    }


}
