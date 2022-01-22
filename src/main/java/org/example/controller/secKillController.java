package org.example.controller;

import org.example.entity.SecKillOrder;
import org.example.entity.SecKillUser;
import org.example.rabbitmq.MQSender;
import org.example.rabbitmq.SecKillMessage;
import org.example.redis.GoodsKey;
import org.example.redis.RedisService;
import org.example.result.CodeMsg;
import org.example.result.Result;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.service.SecKillService;
import org.example.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/secKill")
public class secKillController implements InitializingBean {


    GoodsService goodsService;
    OrderService orderService;
    SecKillService secKillService;
    RedisService redisService;
    MQSender mqSender;

    private Map<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * 系统初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null) {
            return;
        }
        for (GoodsVo goods : goodsList) {
            redisService.set(GoodsKey.getSecKillGoodsStock, "" + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }


    @Inject
    public secKillController(GoodsService goodsService, OrderService orderService,
                             SecKillService secKillService, RedisService redisService,
                             MQSender mqSender) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.secKillService = secKillService;
        this.redisService = redisService;
        this.mqSender = mqSender;
    }


    @RequestMapping(value = "/do_secKill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, SecKillUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //内存标记，减少redis访问
        Boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.SEC_KILL_OVER);
        }

        //预减库存
        long stock = redisService.decr(GoodsKey.getSecKillGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.SEC_KILL_OVER);
        }
        //判断是否已经秒杀到了
        SecKillOrder order = orderService.getSecKillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEAT_SEC_KILL);
        }

        //入队
        SecKillMessage message = new SecKillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        mqSender.sendSecKillMessage(message);
        return Result.success(0);//排队中
        /**
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
         **/
    }

    /**
     * orderId:成功
     * -1：库存不足，秒杀失败
     * 0：排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> secKillResult(Model model, SecKillUser user,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        System.out.println(1111);
        long result = secKillService.getSecKillResult(user.getId(), goodsId);
        return Result.success(result);
    }


}
