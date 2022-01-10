package org.example.service;

import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
public class UserService {
    private UserMapper userMapper;

    @Inject
    public UserService(UserMapper userMapper){
        this.userMapper = userMapper;
    }
    public User getUserById(Integer id){
        return userMapper.findUserById(id);
    }
    public int insertUser(User user){
        return userMapper.insert(user);
    }

    @Transactional
    public boolean insertName(){
        User user1 = new User();
        user1.setId(2);
        user1.setName("2222");
        userMapper.insert(user1);
//
//        User user2 = new User();
//        user1.setId(1);
//        user1.setName("1111");
//        userMapper.insert(user1);
        return true;
    }

}
