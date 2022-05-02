package org.example;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Set;

@Component
public class MyApplicationRunner implements ApplicationRunner {

    private StringRedisTemplate stringRedisTemplate;

    @Inject
    public MyApplicationRunner(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        Set<String> keys = stringRedisTemplate.keys("*");//清空redis数据库中所有的键值对
//        stringRedisTemplate.delete(keys);
//        System.out.println("redisKey全部删除了");
    }
}