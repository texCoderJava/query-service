package com.sbr.rdis.redis.reactive.service;

import com.sbr.common.model.Request;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Service
public class RedisReactiveRequestQueueListenService {
    @Autowired
    private RedisReactiveQueueService<Request> redisRequestQueueService;
    
    private Sinks.Many<String> removeSink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    private Sinks.Many<String> emptySink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    
    @PostConstruct
    private void subscribe() {
        removeListener();
        emptyListener();
    }
    
    private void removeListener() {
        this.redisRequestQueueService.addRemoveListener((message -> {
            if (StringUtils.isNotBlank(message)) {
                removeSink.emitNext(String.format("Removed from %s.", message), (s, e) -> true);
            }
        })).subscribe();
    }
    
    private void emptyListener() {
        this.redisRequestQueueService.addEmptyListener((message -> {
            if (StringUtils.isNotBlank(message)) {
                emptySink.emitNext(String.format("Empty %s queue.", message), (s, e) -> true);
            }
        })).subscribe();
    }
    
    public Flux<String> listenRemove() {
        return removeSink.asFlux();
    }
    
    public Flux<String> listenEmpty() {
        return emptySink.asFlux();
    }
}


