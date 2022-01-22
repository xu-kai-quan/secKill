package org.example.redis;

public class SecKillKey extends BasePrefix {

    public SecKillKey(String prefix) {
        super(prefix);
    }

    public static SecKillKey isGoodsOver = new SecKillKey("g0");

}
