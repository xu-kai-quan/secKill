package org.example.service;

import org.example.dao.OrderDao;
import org.example.entity.OrderInfo;
import org.example.entity.SecKillOrder;
import org.example.entity.SecKillUser;
import org.example.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;

@Service
public class OrderService {
    OrderDao orderDao;

    @Inject
    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public SecKillOrder getSecKillOrderByUserIdGoodsId(long userId, long goodsId) {

        return orderDao.getSecKillOrderByUserIdGoodsId(userId, goodsId);
    }

    @Transactional
    public OrderInfo createOrder(SecKillUser user, GoodsVo goods) {
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
        long orderId = orderDao.insertOrder(orderInfo);
        System.out.println(orderId);
        SecKillOrder secKillOrder = new SecKillOrder();
        secKillOrder.setOrderId(orderId);
        secKillOrder.setGoodsId(goods.getId());
        secKillOrder.setUserId(user.getId());
        orderDao.insertSecKillOrder(secKillOrder);
        return orderInfo;
    }
}
