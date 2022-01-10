package org.example.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.Users;

@Mapper
public interface UsersDao {
    @Select("select * from USERS where id = #{id}")
    public Users getById(long id);
}
