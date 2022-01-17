package org.example.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.SecKillUser;

@Mapper
public interface UsersDao {
    @Select("select * from USERS where id = #{id}")
    public SecKillUser getById(long id);
}
