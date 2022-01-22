package org.example.service;

import org.example.entity.OrderInfo;
import org.example.entity.SecKillOrder;
import org.example.entity.SecKillUser;
import org.example.redis.RedisService;
import org.example.redis.SecKillKey;
import org.example.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
public class SecKillService {

    GoodsService goodsService;
    OrderService orderService;
    RedisService redisService;

    @Inject
    public SecKillService(GoodsService goodsService, OrderService orderService, RedisService redisService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.redisService = redisService;

    }

    @Transactional //事务原子操作
    public OrderInfo secKill(SecKillUser user, GoodsVo goods) {
        //减库存
        boolean success = goodsService.reduceStock(goods);
        if (success) {
            //下订单 写入秒杀订单 ORDER_INFO SEC_KILL_ORDER
            return orderService.createOrder(user, goods);
        } else {
            setGoodsOver(goods.getId());
            return null;
        }

    }

    public long getSecKillResult(Long userId, long goodsId) {
        SecKillOrder secKillOrder = orderService.getSecKillOrderByUserIdGoodsId(userId, goodsId);
        if (secKillOrder != null) {//秒杀成功
            System.out.println(secKillOrder.getOrderId());
            return secKillOrder.getOrderId();
        } else {
            boolean isOver = getGoodsOver(goodsId);
            if (isOver) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SecKillKey.isGoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SecKillKey.isGoodsOver, "" + goodsId);
    }


}
