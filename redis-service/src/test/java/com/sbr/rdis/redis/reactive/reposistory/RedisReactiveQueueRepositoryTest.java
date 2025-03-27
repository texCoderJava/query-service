package com.sbr.rdis.redis.reactive.reposistory;

import com.sbr.common.model.LatLng;
import com.sbr.common.model.Request;
import com.sbr.rdis.BaseSpringBootRedisTest;
import com.sbr.rdis.redis.normal.reposistory.RedisQueueRepository;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

public class RedisReactiveQueueRepositoryTest extends BaseSpringBootRedisTest {
    
    @Autowired
    protected RedisReactiveQueueRepository<Request> redisReactiveQueueRepository;
    
    @Autowired
    protected RedisQueueRepository<Request> redisQueueRepository;
    
    @Test
    public void testRequestCreationInRedis() {
        
        Mono<Request> pushed = this.redisReactiveQueueRepository.push(Request.request()
                                                                              .requestId("testId")
                                                                              .requestType("testType")
                                                                              .customerId("testUser")
                                                                              .source(LatLng.location()
                                                                                              .latitude("28.6988812")
                                                                                              .longitude("77.1153696")
                                                                                              .build())
                                                                              .destination(LatLng.location()
                                                                                                   .latitude("29.6988812")
                                                                                                   .longitude("78.1153696")
                                                                                                   .build())
                                                                              .build());
        
        StepVerifier.create(pushed.then())
                .verifyComplete();
        cleanup();
        
    }
    
    @RepeatedTest(1)
    public void testManyRequestCreationInRedis() {
        boolean isCreationInRedisEnabled = Optional.ofNullable(System.getProperty("redis.creation.enabled")).isPresent();
        if (!isCreationInRedisEnabled) {
            return;
        }
        long before = System.currentTimeMillis();
        Mono<Void> mono = Flux.range(1, 1000_000)
                                  .flatMap(i -> this.redisReactiveQueueRepository.push(Request.request()
                                                                                               .requestId("testId" + i)
                                                                                               .requestType("testType" + i)
                                                                                               .customerId("testUser" + i)
                                                                                               .source(LatLng.location()
                                                                                                               .latitude("28.6988812")
                                                                                                               .longitude("77.1153696")
                                                                                                               .build())
                                                                                               .destination(LatLng.location()
                                                                                                                    .latitude("29.6988812")
                                                                                                                    .longitude("78.1153696")
                                                                                                                    .build())
                                                                                               .build()))
                                  .then();
        StepVerifier.create(mono)
                .verifyComplete();
        long after = System.currentTimeMillis();
        System.out.println((after - before) + " ms");
        cleanup();
    }
    
    
    @RepeatedTest(1)
    public void testManyRequestReadInRedis() {
        boolean isReadInRedisEnabled = Optional.ofNullable(System.getProperty("redis.read.enabled")).isPresent();
        if (!isReadInRedisEnabled) {
            return;
        }
        long before = System.currentTimeMillis();
        Mono<Void> mono = Flux.range(1, 1000_000)
                                  .flatMap(i -> this.redisReactiveQueueRepository.poll())
                                  .then();
        StepVerifier.create(mono)
                .verifyComplete();
        long after = System.currentTimeMillis();
        System.out.println((after - before) + " ms");
    }
    
    private void cleanup() {
        this.redisQueueRepository.pollAll();
        StepVerifier.create(this.redisReactiveQueueRepository.length()).expectNext(0)
                .verifyComplete();
    }
}
