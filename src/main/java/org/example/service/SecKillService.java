package org.example.service;

import org.example.entity.OrderInfo;
import org.example.entity.SecKillOrder;
import org.example.entity.SecKillUser;
import org.example.redis.RedisService;
import org.example.redis.SecKillKey;
import org.example.util.MD5Util;
import org.example.util.UUIDUtil;
import org.example.vo.GoodsVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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


    public boolean checkPath(SecKillUser user, long goodsId, String path) {
        if (user == null || path == null){
            return false;
        }
        String pathOld = redisService.get(SecKillKey.getSecKillPath, "" + user.getId() + "_" + goodsId, String.class);
        return path.equals(pathOld);
    }

    public boolean checkVerifyCode(SecKillUser user, long goodsId, int verifyCode) {
        if (user == null || goodsId <= 0){
            return false;
        }
        Integer codeOld = redisService.get(SecKillKey.getSecKillVerifyCode, user.getId() + "," + goodsId, Integer.class);
        if (codeOld == null || codeOld - verifyCode !=0){
            return false;
        }
        redisService.delete(SecKillKey.getSecKillVerifyCode, user.getId() + "," + goodsId);
        return true;
    }
    public String createSecKillPath(SecKillUser user, long goodsId) {
        if (user == null || goodsId <= 0){
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(SecKillKey.getSecKillPath, "" + user.getId() + "_" + goodsId, str);
        return str;
    }

    public BufferedImage createSecKillVerifyCode(SecKillUser user, long goodsId) {
        if (user == null || goodsId <= 0){
            return null;
        }
        int width = 80;
        int height = 32;
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0,0,width,height);
        g.setColor(Color.BLACK);
        g.drawRect(0,0,width-1,height-1);
        Random rdm = new Random();
        for (int i = 0; i < 50;i++){
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x,y,0,0);
        }
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0,100,0));
        g.setFont(new Font("Candara",Font.BOLD,24));
        g.drawString(verifyCode,8,24);
        g.dispose();
        int rnd = calc(verifyCode);
        redisService.set(SecKillKey.getSecKillVerifyCode,user.getId()+","+goodsId,rnd);
        return image;
    }

    private int calc(String exp) {
        try{
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

    }

    private static char[] ops = new char[]{'+','-','*'};

    /**
     *
     * + - *
     */
    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 +num2 + op2 + num3;
        return exp;

    }

}
