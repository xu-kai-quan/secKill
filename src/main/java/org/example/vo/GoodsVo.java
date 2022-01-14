package org.example.vo;

import org.example.entity.Goods;

import java.util.Date;

public class GoodsVo extends Goods {
    private Double secKillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Double getSecKillPrice() {
        return secKillPrice;
    }

    public void setSecKillPrice(Double secKillPrice) {
        this.secKillPrice = secKillPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
