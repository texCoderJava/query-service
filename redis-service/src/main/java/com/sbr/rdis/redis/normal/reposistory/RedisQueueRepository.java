package com.sbr.rdis.redis.normal.reposistory;

import lombok.RequiredArgsConstructor;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.QueueRemoveListener;
import org.redisson.client.codec.ByteArrayCodec;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor(staticName = "create")
public class RedisQueueRepository<T> {
    final private RedissonClient redissonClient;
    final private String queueName;
    final private Class<T> valueClass;
    private RBlockingQueue<T> messagingQueue;
    
    public final void init() {
        Codec codec;
        codec = new TypedJsonJacksonCodec(valueClass);
        if (valueClass == Byte[].class) {
            codec = new ByteArrayCodec();
        } else if (valueClass == String.class) {
            codec = new StringCodec();
        }
        this.messagingQueue = this.redissonClient.getBlockingQueue(queueName, codec);
    }
    
    public Integer length() {
        return this.messagingQueue.size();
    }
    
    public T peek() {
        return this.messagingQueue.peek();
    }
    
    public T poll() {
        return this.messagingQueue.poll();
    }
    
    public List<T> pollAll() {
        return IntStream.range(0, this.length()).mapToObj(i -> this.messagingQueue.poll()).toList();
    }
    
    public Boolean push(T v) {
        return this.messagingQueue.add(v);
    }
    
    public Boolean pushAll(List<T> values) {
        return this.messagingQueue.addAll(values);
    }
    
    public Integer addRemoveListener(QueueRemoveListener removedListener) {
        return this.messagingQueue.addListener(removedListener);
    }
    
    public Integer addEmptyListener(DeletedObjectListener emptyListener) {
        return this.messagingQueue.addListener(emptyListener);
    }
}

