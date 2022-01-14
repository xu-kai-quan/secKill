package org.example.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.Goods;
import org.example.entity.SecKillGoods;
import org.example.vo.GoodsVo;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select g.*,sg.stock_count,sg.start_date,sg.end_date ,sg.secKill_price from SEC_KILL_GOODS sg left join GOODS g on sg.goods_id=g.id")
    List<GoodsVo> listGoodsVo();

    @Select("select g.*,sg.stock_count,sg.start_date,sg.end_date ,sg.secKill_price from SEC_KILL_GOODS sg left join GOODS g on sg.goods_id=g.id where g.id=#{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Update("update SEC_KILL_GOODS set stock_count = stock_count -1 where goods_id = #{goodsId}")
    int reduceStock(SecKillGoods sg);
}
