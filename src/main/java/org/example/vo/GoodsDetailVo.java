package org.example.vo;

import org.example.entity.SecKillUser;

public class GoodsDetailVo {
    private int secKillStatus;
    private int remainSeconds;
    private GoodsVo goods;
    private SecKillUser user;

    public SecKillUser getUser() {
        return user;
    }

    public void setUser(SecKillUser user) {
        this.user = user;
    }

    public int getSecKillStatus() {
        return secKillStatus;
    }

    public void setSecKillStatus(int secKillStatus) {
        this.secKillStatus = secKillStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }
}
