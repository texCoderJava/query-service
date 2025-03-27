package com.sbr.rdis.redis.reactive.service;

import com.sbr.common.model.Request;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.listener.QueueRemoveListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

public interface RedisReactiveQueueService<T> {
    
    Mono<Integer> lengthOfQueue();
    
    Mono<T> peekFromQueue();
    
    Mono<T> pollFromQueue();
    
    // Note: Use this method properly as it is a never ending stream
    Flux<T> pollAllFromQueue(Flux<Boolean> statuses);
    
    Mono<T> pushIntoQueue(T value);
    
    Flux<T> pushAllIntoQueue(List<T> values);
    
    Flux<T> pushAllIntoQueue(Flux<T> values, Integer threshold);
    
    Mono<T> pollFromQueue(Consumer<? super T> onNext, Consumer<? super Throwable> onError);
    
    // Note: Use this method properly as it is a never ending stream
    Flux<T> pollAllFromQueue(Flux<Boolean> statuses, Consumer<? super T> onNext, Consumer<? super Throwable> onError);
    
    Mono<T> pushIntoQueue(T value, Consumer<? super Request> onNext, Consumer<? super Throwable> onError);
    
    Flux<T> pushAllIntoQueue(List<T> values, Consumer<? super T> onNext, Consumer<? super Throwable> onError);
    
    Flux<T> pushAllIntoQueue(Flux<T> values, Consumer<? super T> onNext, Consumer<? super Throwable> onError, Integer threshold);
    
    Mono<Integer> addRemoveListener(QueueRemoveListener removedListener);
    
    Mono<Integer> addEmptyListener(DeletedObjectListener emptyListener);
    
}
