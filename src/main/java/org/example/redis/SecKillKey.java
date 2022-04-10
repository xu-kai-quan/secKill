package org.example.redis;

public class SecKillKey extends BasePrefix {

    public SecKillKey(int expireSeconds, String prefix) {
        super(expireSeconds,prefix);
    }

    public static SecKillKey isGoodsOver = new SecKillKey(0,"g0");
    public static SecKillKey getSecKillPath = new SecKillKey(60,"sp");
    public static SecKillKey getSecKillVerifyCode = new SecKillKey(300,"vc");

}
