package com.sbr.rdis.redis.reactive.controller;

import com.sbr.common.model.LatLng;
import com.sbr.common.model.Request;
import com.sbr.rdis.BaseSpringBootRedisTest;
import com.sbr.rdis.redis.reactive.reposistory.RedisReactiveQueueRepository;
import io.rsocket.transport.netty.client.TcpClientTransport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.stream.IntStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RedisReactiveRequestRSControllerTest extends BaseSpringBootRedisTest {
    @Autowired
    protected RedisReactiveQueueRepository<Request> redisReactiveQueueRepository;
    
    @Autowired
    private RSocketRequester.Builder rSocketRequesterBuilder;
    
    private RSocketRequester rSocketRequester;
    
    @BeforeAll
    public void setup() {
        this.rSocketRequester = this.rSocketRequesterBuilder.transport(TcpClientTransport.create("localhost", 8085));
    }
    
    @Test
    public void testRequestsCreateAndFetch() {
        Flux<Request> requestsToBeCreated = Flux.just(IntStream.range(1, 6)
                                                              .mapToObj(i -> Request.request()
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
                                                                                     .build()).toArray(Request[]::new)).delayElements(Duration.ofMillis(1000));
        
        
        Flux<Request> createdReqs = this.rSocketRequester.route("requests.create.flux.5")
                                            .data(requestsToBeCreated)
                                            .retrieveFlux(Request.class).doOnNext(System.out::println);
        StepVerifier.create(createdReqs).expectNextCount(5).verifyComplete();
        
        Flux<Request> requests = this.rSocketRequester.route("requests.fetch")
                                         .retrieveFlux(Request.class).take(5);
        StepVerifier.create(requests).expectNextCount(5).verifyComplete();
        
        StepVerifier.create(this.redisReactiveQueueRepository.length()).expectNext(0)
                .verifyComplete();
    }
    
}
