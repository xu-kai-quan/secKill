package org.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.entity.User;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM USERS WHERE id = #{id}")
    User findUserById(@Param("id") Integer id);

    @Insert("insert into USERS(id,name)values(#{id},#{name})")
    int insert(User user);

}
