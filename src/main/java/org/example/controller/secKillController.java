package org.example.controller;

import org.example.access.AccessLimit;
import org.example.entity.SecKillOrder;
import org.example.entity.SecKillUser;
import org.example.rabbitmq.MQSender;
import org.example.rabbitmq.SecKillMessage;
import org.example.redis.AccessKey;
import org.example.redis.GoodsKey;
import org.example.redis.RedisService;
import org.example.redis.SecKillKey;
import org.example.result.CodeMsg;
import org.example.result.Result;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.service.SecKillService;
import org.example.util.MD5Util;
import org.example.util.UUIDUtil;
import org.example.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/secKill")
public class secKillController implements InitializingBean {


    GoodsService goodsService;
    OrderService orderService;
    SecKillService secKillService;
    RedisService redisService;
    MQSender mqSender;
    private StringRedisTemplate stringRedisTemplate;

    private Map<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();

    /**
     * 系统初始化
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        Set<String> keys = stringRedisTemplate.keys("*");//清空redis数据库中所有的键值对
        stringRedisTemplate.delete(keys);
        System.out.println("redisKey全部删除了");


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
                             MQSender mqSender,
                             StringRedisTemplate stringRedisTemplate) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.secKillService = secKillService;
        this.redisService = redisService;
        this.mqSender = mqSender;
        this.stringRedisTemplate = stringRedisTemplate;
    }


    @RequestMapping(value = "/{path}/do_secKill", method = RequestMethod.POST)
    @ResponseBody
    public Result<Long> secKill(Model model, SecKillUser user,
                                @RequestParam("goodsId") long goodsId,
                                @PathVariable("path") String path) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        //验证path
        boolean check = secKillService.checkPath(user, goodsId, path);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
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
        return Result.success(0L);//排队中
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
     *
     * -1：库存不足，秒杀失败
     * 0：排队中
     */
    @AccessLimit(seconds = 5, maxCount = 10, needLogin = true)
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> secKillResult(Model model, SecKillUser user,
                                      @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        long result = secKillService.getSecKillResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds = 2, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSecKillPath(HttpServletRequest request, SecKillUser user,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam("verifyCode") int verifyCode) {

        if (user == null) {
            return Result.error(CodeMsg.SEC_KILL_FAIL);
        }
        boolean check = secKillService.checkVerifyCode(user, goodsId, verifyCode);
        if (!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = secKillService.createSecKillPath(user, goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getSecKillVerifyCode(HttpServletResponse response, SecKillUser user, @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        BufferedImage image = secKillService.createSecKillVerifyCode(user, goodsId);
        try {
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SERVER_ERROR);
        }

    }

}
