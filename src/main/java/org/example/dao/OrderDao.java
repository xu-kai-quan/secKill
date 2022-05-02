package org.example.dao;

import org.apache.ibatis.annotations.*;
import org.example.entity.OrderInfo;
import org.example.entity.SecKillOrder;

@Mapper
public interface OrderDao {

    @Select("select * from SEC_KILL_ORDER where user_id=#{userId} and goods_id=#{goodsId}")
    public SecKillOrder getSecKillOrderByUserIdGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    @Insert("insert into ORDER_INFO(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    public long insertOrder(OrderInfo orderInfo);

    @Insert("insert into SEC_KILL_ORDER (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    public int insertSecKillOrder(SecKillOrder secKillOrder);

    @Select("select * from ORDER_INFO where id = #{orderId}")
    OrderInfo getOrderById(@Param("orderId") long orderId);

    @Update("update ORDER_INFO set status =#{status}")
    void paySuccess(OrderInfo order);
}
