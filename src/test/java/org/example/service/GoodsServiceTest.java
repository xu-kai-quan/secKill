package org.example.service;

import org.example.dao.GoodsDao;
import org.example.entity.SecKillGoods;
import org.example.vo.GoodsVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GoodsServiceTest {

    @Mock
    GoodsDao goodsDao;

    @InjectMocks
    GoodsService goodsService;
    static GoodsVo goodsVo1;
    static GoodsVo goodsVo2;
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static {
        try {
            goodsVo1 = new GoodsVo(0.1, 5 ,simpleDateFormat.parse("2022-05-01 10:30:00"), simpleDateFormat.parse("2022-12-03 10:30:00"));
            goodsVo2 = new GoodsVo(0.2, 10, simpleDateFormat.parse("2022-07-23 10:30:00"), simpleDateFormat.parse("2022-12-01 10:30:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void setUp() {
        //初始化mock对象
        MockitoAnnotations.initMocks(this);
        when(goodsDao.listGoodsVo()).thenReturn(Arrays.asList(goodsVo1, goodsVo2));
        when(goodsDao.getGoodsVoByGoodsId(2)).thenReturn(goodsVo2);
    }


    @Test
    public void testGetListGoodsVo() {
        List<GoodsVo> allGoods = goodsService.listGoodsVo();
        verify(goodsDao, times(1)).listGoodsVo();

        assertEquals(2, allGoods.size());

        GoodsVo goodsVo = allGoods.get(0);

        assertEquals(0.1, goodsVo.getSecKillPrice());
        assertEquals(5, goodsVo.getStockCount());
    }
    @Test
    public void testGetGoodsVoByGoodsId() {
        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(2);
        verify(goodsDao, times(1)).getGoodsVoByGoodsId(2);

        assertNotNull(goodsVo);

        assertEquals(0.2, goodsVo.getSecKillPrice());
        assertEquals(10, goodsVo.getStockCount());

    }

}
