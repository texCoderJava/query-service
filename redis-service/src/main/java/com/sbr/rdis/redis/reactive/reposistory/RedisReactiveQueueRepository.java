package com.sbr.rdis.redis.reactive.reposistory;


import lombok.RequiredArgsConstructor;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.RBlockingQueueReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.listener.QueueRemoveListener;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor(staticName = "create")
public class RedisReactiveQueueRepository<T> {
    final private RedissonReactiveClient redissonReactiveClient;
    final private String queueName;
    final private Class<T> valueClass;
    private RBlockingQueueReactive<T> messagingQueue;
    
    public final void init() {
        Codec codec;
        codec = new TypedJsonJacksonCodec(valueClass);
        if (valueClass == Byte[].class) {
            codec = new ByteArrayCodec();
        } else if (valueClass == String.class) {
            codec = new StringCodec();
        }
        this.messagingQueue = this.redissonReactiveClient.getBlockingQueue(queueName, codec);
    }
    
    public Mono<Integer> length() {
        return this.messagingQueue.size();
    }
    
    public Mono<T> peek() {
        return this.messagingQueue.peek();
    }
    
    public Mono<T> poll() {
        return this.messagingQueue.poll();
    }
    
    // Note: Use this method properly as it is a never ending stream
    public Flux<T> pollAll() {
        return this.messagingQueue.takeElements();
    }
    
    public Mono<T> push(T v) {
        return this.messagingQueue.add(v).map(x -> v);
    }
    
    public Flux<T> pushAll(List<T> values) {
        return this.messagingQueue.addAll(values).flatMapIterable(status -> values);
    }
    
    public Flux<T> pushAll(Flux<T> values, Integer threshold) {
        Integer thresholdOrOne = Optional.ofNullable(threshold).orElse(1);
        AtomicInteger count = new AtomicInteger();
        Sinks.Many<T> sink = Sinks.many().multicast().onBackpressureBuffer();
        values.doOnNext(value -> this.push(value).doOnNext(val -> {
            sink.emitNext(value, (s, e) -> true);
            count.set(count.get() + 1);
            if (count.get() == thresholdOrOne) {
                sink.emitComplete((s, e) -> true);
            }
        }).subscribe()).subscribe();
        return sink.asFlux();
    }
    
    public Mono<Integer> addRemoveListener(QueueRemoveListener removedListener) {
        return this.messagingQueue.addListener(removedListener);
    }
    
    public Mono<Integer> addEmptyListener(DeletedObjectListener emptyListener) {
        return this.messagingQueue.addListener(emptyListener);
    }
}

