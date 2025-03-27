package com.sbr.rdis.redis.reactive.controller;

import com.sbr.common.model.Request;
import com.sbr.rdis.redis.reactive.service.RedisReactiveQueueService;
import com.sbr.rdis.redis.reactive.service.RedisReactiveRequestQueueListenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.rsocket.annotation.support.RSocketFrameTypeMessageCondition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@MessageMapping("requests")
@Slf4j
public class RedisReactiveRequestRSController {
    
    private static final String RSOCKET_FRAME_TYPE = RSocketFrameTypeMessageCondition.FRAME_TYPE_HEADER;
    private static final String CONTENT_TYPE = "contentType";
    
    private static final Map<String, RSocketRequester> clientVsRequester = new ConcurrentHashMap<>();
    
    @Autowired
    RedisReactiveQueueService<Request> redisRequestQueueService;
    @Autowired
    RedisReactiveRequestQueueListenService redisRequestQueueListenService;
    
    // Note: Use this api properly as it is a never ending stream
    @MessageMapping("fetch")
    public Flux<Request> requests(Flux<Boolean> statuses) {
        return redisRequestQueueService.pollAllFromQueue(statuses);
    }
    
    @MessageMapping("create")
    public Mono<Request> create(@RequestBody Request request) {
        return this.redisRequestQueueService.pushIntoQueue(request);
    }
    
    @MessageMapping("create.list")
    public Flux<Request> create(@RequestBody List<Request> requests) {
        return redisRequestQueueService.pushAllIntoQueue(requests);
    }
    
    @MessageMapping("create.flux.{threshold}")
    public Flux<Request> create(@RequestBody Flux<Request> requests, @DestinationVariable Integer threshold) {
        return this.redisRequestQueueService.pushAllIntoQueue(requests, threshold);
    }
    
    // Note: Use this api properly as it is a never ending stream
    @MessageMapping("listen.remove")
    public Flux<String> listenRemove() {
        return this.redisRequestQueueListenService.listenRemove();
    }
    
    // Note: Use this api properly as it is a never ending stream
    @MessageMapping("listen.empty")
    public Flux<String> listenEmpty() {
        return this.redisRequestQueueListenService.listenEmpty();
    }
    
    @MessageMapping("delete")
    public Mono<Request> delete() {
        return this.redisRequestQueueService.pollFromQueue();
    }
    
    @ConnectMapping("registration")
    public Mono<Void> requestsRegistration(RSocketRequester rSocketRequester,
                                           @Payload String clientId,
                                           @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                                           @Header(CONTENT_TYPE) String contentType) {
        clientVsRequester.put(clientId, rSocketRequester);
        return Mono.empty();
    }
    
    @MessageMapping("cancel")
    public Mono<Void> cancelSubscription(Mono<String> clientIdMono) {
        clientIdMono.subscribe(clientId -> {
            log.warn("Cancelling: {}", clientId);
            if (clientVsRequester.containsKey(clientId)) {
                clientVsRequester.get(clientId).rsocket().dispose();
            }
        });
        return Mono.empty();
    }
}
