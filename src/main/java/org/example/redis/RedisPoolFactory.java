package org.example.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.inject.Inject;

@Service
public class RedisPoolFactory {
    RedisConfig redisConfig;

    @Inject
    public RedisPoolFactory(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    @Bean
    public JedisPool jedisPoolFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait() * 1000L);
        return new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(),
                redisConfig.getTimeout() * 1000);

    }

}
