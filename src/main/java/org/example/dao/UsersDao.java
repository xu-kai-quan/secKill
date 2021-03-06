package org.example.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.entity.SecKillUser;

@Mapper
public interface UsersDao {
    @Select("select * from USERS where id = #{id}")
    public SecKillUser getById(long id);

    @Update("update USERS set password = #{password} where id = #{id}")
    public void updatePassword(SecKillUser toBeUpdate);

    @Update("insert into USERS (id,nickname,password,salt,register_date) values (#{id},#{nickname},#{password},#{salt},now())")
    public void insertNewUser(SecKillUser toBeSet);
}
