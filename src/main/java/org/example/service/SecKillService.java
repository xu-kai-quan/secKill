package org.example.service;

import org.example.entity.OrderInfo;
import org.example.entity.SecKillUser;
import org.example.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
public class SecKillService {

    GoodsService goodsService;
    OrderService orderService;

    @Inject
    public SecKillService(GoodsService goodsService, OrderService orderService) {
        this.goodsService = goodsService;
        this.orderService = orderService;

    }

    @Transactional //事务原子操作
    public OrderInfo secKill(SecKillUser user, GoodsVo goods) {
        //减库存
        goodsService.reduceStock(goods);
        //下订单 写入秒杀订单 ORDER_INFO SEC_KILL_ORDER
        return orderService.createOrder(user, goods);
    }
}
