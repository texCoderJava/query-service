package com.sbr.ks.processor;

import com.sbr.common.model.Request;
import com.sbr.ks.service.services.ConsumerService;
import com.sbr.ks.service.services.DownstreamService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Slf4j
@Component
public class RedisProcessor {

    @Autowired
    @Qualifier("consumerServiceImpl")
    private ConsumerService<Request> consumerService;

    @Autowired
    @Qualifier("redisDownStreamService")
    private DownstreamService<Request> redisDownstreamService;
    private Sinks.Many<Request> redisBuffer = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    @PostConstruct
    public void run() {
        this.consumerService.consume().subscribe(request -> this.redisBuffer.emitNext(request, (s, e) -> true));
        this.redisDownstreamService.push(this.redisBuffer.asFlux()).subscribe();
    }
}
