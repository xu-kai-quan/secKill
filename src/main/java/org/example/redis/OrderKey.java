package org.example.redis;

public class OrderKey extends BasePrefix{
    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getSecKillOrderByUserIdGoodsId = new OrderKey("order");
}
