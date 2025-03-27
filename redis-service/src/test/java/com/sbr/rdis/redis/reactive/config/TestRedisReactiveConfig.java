package com.sbr.rdis.redis.reactive.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestRedisReactiveConfig {
    
    @Value("${spring.redis.host}")
    private String redisHost;
    
    @Value("${spring.redis.port}")
    private String redisPort;
    
    @Bean
    public RedissonClient getReactiveClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(String.format("redis://%s:%s", this.redisHost, this.redisPort));
        RedissonClient client = Redisson.create(config);
        return client;
    }
}
