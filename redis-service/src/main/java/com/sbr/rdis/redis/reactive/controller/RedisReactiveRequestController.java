package com.sbr.rdis.redis.reactive.controller;

import com.sbr.common.model.Request;
import com.sbr.rdis.redis.reactive.service.RedisReactiveQueueService;
import com.sbr.rdis.redis.reactive.service.RedisReactiveRequestQueueListenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/redis/request")
@CrossOrigin(origins = "*")
public class RedisReactiveRequestController {
    
    @Autowired
    RedisReactiveQueueService<Request> redisRequestQueueService;
    
    @Autowired
    RedisReactiveRequestQueueListenService redisRequestQueueListenService;
    
    // Note: Use this api properly as it is a never ending stream
    @GetMapping("fetch")
    public Flux<Request> requests(Flux<Boolean> statuses) {
        return redisRequestQueueService.pollAllFromQueue(statuses);
    }
    
    @PostMapping
    public Mono<Request> create(@RequestBody Request request) {
        return this.redisRequestQueueService.pushIntoQueue(request);
    }
    
    @PostMapping("list")
    public Flux<Request> create(@RequestBody List<Request> requests) {
        return this.redisRequestQueueService.pushAllIntoQueue(requests);
    }
    
    @PostMapping("flux/{threshold}")
    public Flux<Request> create(@RequestBody Flux<Request> requests, @PathVariable Integer threshold) {
        return this.redisRequestQueueService.pushAllIntoQueue(requests, threshold);
    }
    
    // Note: Use this api properly as it is a never ending stream
    @GetMapping("listen/remove")
    public Flux<String> listenRemove() {
        return this.redisRequestQueueListenService.listenRemove();
    }
    
    // Note: Use this api properly as it is a never ending stream
    @GetMapping("listen/empty")
    public Flux<String> listenEmpty() {
        return this.redisRequestQueueListenService.listenEmpty();
    }
    
    @DeleteMapping("delete")
    public Mono<Request> delete() {
        return this.redisRequestQueueService.pollFromQueue();
    }
}

