package com.sbr.rdis.redis.reactive.config;

import com.sbr.common.model.Request;
import com.sbr.rdis.redis.reactive.reposistory.RedisReactiveQueueRepository;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisReactiveConfig {
    
    @Value("${redis.request.queue.name}")
    private String requestQueueName;
    
    @Bean
    public RedisReactiveQueueRepository<Request> redisRequestRepository(RedissonReactiveClient client) {
        RedisReactiveQueueRepository<Request> requestRepository = RedisReactiveQueueRepository.create(client, this.requestQueueName, Request.class);
        requestRepository.init();
        return requestRepository;
    }
    
    
}
