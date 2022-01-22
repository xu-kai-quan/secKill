package org.example.rabbitmq;

import org.example.entity.OrderInfo;
import org.example.entity.SecKillOrder;
import org.example.entity.SecKillUser;
import org.example.redis.RedisService;
import org.example.result.CodeMsg;
import org.example.result.Result;
import org.example.service.GoodsService;
import org.example.service.OrderService;
import org.example.service.SecKillService;
import org.example.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class MQReceiver {

    private GoodsService goodsService;
    private OrderService orderService;
    private SecKillService secKillService;
    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Inject
    public MQReceiver(GoodsService goodsService, OrderService orderService, SecKillService secKillService) {
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.secKillService = secKillService;
    }


    @RabbitListener(queues = MQConfig.SEC_KILL_QUEUE)
    public void receive(String message) {
        log.info("receive message:" + message);
        SecKillMessage secKillMessage = RedisService.stringToBean(message, SecKillMessage.class);
        SecKillUser user = secKillMessage.getUser();
        long goodsId = secKillMessage.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stockCount = goods.getStockCount();
        if (stockCount <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        SecKillOrder order = orderService.getSecKillOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return;
        }
        //减库存  下订单 写入秒杀订单
        OrderInfo orderInfo = secKillService.secKill(user, goods);
    }
}