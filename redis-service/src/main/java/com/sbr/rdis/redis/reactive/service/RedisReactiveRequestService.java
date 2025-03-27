package com.sbr.rdis.redis.reactive.service;

import com.sbr.common.model.Request;
import com.sbr.rdis.redis.reactive.reposistory.RedisReactiveQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.listener.QueueRemoveListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class RedisReactiveRequestService implements RedisReactiveQueueService<Request> {
    private final RedisReactiveQueueRepository<Request> redisRequestRepository;
    
    @Override
    public Mono<Integer> lengthOfQueue() {
        return this.redisRequestRepository.length();
    }
    
    @Override
    public Mono<Request> peekFromQueue() {
        return this.redisRequestRepository.peek();
    }
    
    @Override
    public Mono<Request> pollFromQueue() {
        return this.redisRequestRepository.poll();
    }
    
    // Note: Use this method properly as it is a never ending stream
    @Override
    public Flux<Request> pollAllFromQueue(Flux<Boolean> statuses) {
        AtomicBoolean currentStatus = new AtomicBoolean(true);
        AtomicInteger count = new AtomicInteger(0);
        AtomicReference<Disposable> disposable = new AtomicReference<>();
        Sinks.Many<Request> requestsSink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
        statuses.onErrorResume(err -> Mono.empty())
                .subscribe(status -> {
                    if (Boolean.FALSE.equals(status)) {
                        currentStatus.set(false);
                        if (Optional.ofNullable(disposable.get()).isPresent()) {
                            disposable.get().dispose();
                        }
                        requestsSink.emitComplete((s, e) -> true);
                    }
                });
        disposable.set(this.redisRequestRepository.pollAll().takeUntil(s -> currentStatus.get() == false).doOnNext(request -> {
            count.incrementAndGet();
            requestsSink.emitNext(request, (s, e) -> true);
        }).doFinally(signal -> {
            log.info("Total Requests Count: {}", count.get());
        }).subscribe());
        
        return requestsSink.asFlux();
    }
    
    @Override
    public Mono<Request> pushIntoQueue(Request value) {
        return this.redisRequestRepository.push(value);
    }
    
    @Override
    public Flux<Request> pushAllIntoQueue(List<Request> values) {
        return this.redisRequestRepository.pushAll(values);
    }
    
    @Override
    public Flux<Request> pushAllIntoQueue(Flux<Request> values, Integer threshold) {
        return this.redisRequestRepository.pushAll(values, threshold);
    }
    
    @Override
    public Mono<Request> pollFromQueue(Consumer<? super Request> onNext, Consumer<? super Throwable> onError) {
        Mono<Request> request = this.pollFromQueue();
        Optional.ofNullable(onNext).ifPresent(request::doOnNext);
        Optional.ofNullable(onError).ifPresent(request::doOnError);
        return request;
    }
    
    // Note: Use this method properly as it is a never ending stream
    @Override
    public Flux<Request> pollAllFromQueue(Flux<Boolean> statuses, Consumer<? super Request> onNext, Consumer<? super Throwable> onError) {
        Flux<Request> requests = this.pollAllFromQueue(statuses);
        Optional.ofNullable(onNext).ifPresent(requests::doOnNext);
        Optional.ofNullable(onError).ifPresent(requests::doOnError);
        return requests;
    }
    
    @Override
    public Mono<Request> pushIntoQueue(Request value, Consumer<? super Request> onNext, Consumer<? super Throwable> onError) {
        Mono<Request> pushed = this.pushIntoQueue(value);
        Optional.ofNullable(onNext).ifPresent(pushed::doOnNext);
        Optional.ofNullable(onError).ifPresent(pushed::doOnError);
        return pushed;
    }
    
    @Override
    public Flux<Request> pushAllIntoQueue(List<Request> values, Consumer<? super Request> onNext, Consumer<? super Throwable> onError) {
        Flux<Request> pushed = this.pushAllIntoQueue(values);
        Optional.ofNullable(onNext).ifPresent(pushed::doOnNext);
        Optional.ofNullable(onError).ifPresent(pushed::doOnError);
        return pushed;
    }
    
    @Override
    public Flux<Request> pushAllIntoQueue(Flux<Request> values, Consumer<? super Request> onNext, Consumer<? super Throwable> onError, Integer threshold) {
        Flux<Request> pushed = this.pushAllIntoQueue(values, threshold);
        Optional.ofNullable(onNext).ifPresent(pushed::doOnNext);
        Optional.ofNullable(onError).ifPresent(pushed::doOnError);
        return pushed;
    }
    
    @Override
    public Mono<Integer> addRemoveListener(QueueRemoveListener removedListener) {
        return this.redisRequestRepository.addRemoveListener(removedListener);
    }
    
    @Override
    public Mono<Integer> addEmptyListener(DeletedObjectListener emptyListener) {
        return this.redisRequestRepository.addEmptyListener(emptyListener);
    }
    
}
