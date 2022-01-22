package org.example.service;

import org.example.dao.GoodsDao;
import org.example.entity.Goods;
import org.example.entity.SecKillGoods;
import org.example.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class GoodsService {

    GoodsDao goodsDao;

    @Inject
    public GoodsService(GoodsDao goodsDao) {
        this.goodsDao = goodsDao;
    }

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        SecKillGoods sg = new SecKillGoods();
        sg.setGoodsId(goods.getId());
        int r = goodsDao.reduceStock(sg);
        return r > 0;
    }
}
