package org.example.service;

import com.sun.org.apache.regexp.internal.RE;
import org.example.dao.OrderDao;
import org.example.entity.OrderInfo;
import org.example.entity.SecKillOrder;
import org.example.entity.SecKillUser;
import org.example.redis.OrderKey;
import org.example.redis.RedisService;
import org.example.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;

@Service
public class OrderService {
    OrderDao orderDao;
    RedisService redisService;

    @Inject
    public OrderService(OrderDao orderDao, RedisService redisService) {
        this.orderDao = orderDao;
        this.redisService = redisService;
    }

    public SecKillOrder getSecKillOrderByUserIdGoodsId(long userId, long goodsId) {

//        return orderDao.getSecKillOrderByUserIdGoodsId(userId, goodsId);
        return redisService.get(OrderKey.getSecKillOrderByUserIdGoodsId, "" + userId + "_" + goodsId, SecKillOrder.class);
    }

    @Transactional
    public OrderInfo createOrder(SecKillUser user, GoodsVo goods) {
        //订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getSecKillPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        orderDao.insertOrder(orderInfo);
        //秒杀订单
        SecKillOrder secKillOrder = new SecKillOrder();
        secKillOrder.setOrderId(orderInfo.getId());
        secKillOrder.setGoodsId(goods.getId());
        secKillOrder.setUserId(user.getId());
        orderDao.insertSecKillOrder(secKillOrder);

        redisService.set(OrderKey.getSecKillOrderByUserIdGoodsId, "" + user.getId() + "_" + goods.getId(), secKillOrder);

        return orderInfo;
    }

    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }
}
