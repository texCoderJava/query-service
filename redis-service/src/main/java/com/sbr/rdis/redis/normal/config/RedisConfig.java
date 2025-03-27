package com.sbr.rdis.redis.normal.config;

import com.sbr.common.model.Request;
import com.sbr.rdis.redis.normal.reposistory.RedisQueueRepository;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    
    @Value("${redis.request.queue.name}")
    private String requestQueueName;
    
    @Bean("redisRequestQueueRepository")
    public RedisQueueRepository<Request> redisRequestRepository(RedissonClient client) {
        RedisQueueRepository<Request> requestRepository = RedisQueueRepository.create(client, this.requestQueueName, Request.class);
        requestRepository.init();
        return requestRepository;
    }
}
