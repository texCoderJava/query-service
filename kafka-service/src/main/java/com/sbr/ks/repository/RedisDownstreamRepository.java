package com.sbr.ks.repository;

import com.sbr.common.model.Request;
import com.sbr.ks.repository.repositories.DownStreamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Repository("redisDownstreamRepository")
public class RedisDownstreamRepository implements DownStreamRepository<Request> {

    @Qualifier("redisRSocketRequesterMono")
    private final Mono<RSocketRequester> redisRSocketRequesterMono;

    private final Integer retryThreshold = 100;

    @Override
    public Flux<Request> push(Flux<Request> requests) {
        return this.redisRSocketRequesterMono.flatMapMany(rSocketRequester -> rSocketRequester
                .route("create.flux.{threshold}", 1000)
                .data(requests)
                .retrieveFlux(Request.class)
                .retryWhen(Retry.backoff(this.retryThreshold, Duration.ofSeconds(2)))
        );
    }
}
