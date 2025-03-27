package com.sbr.ks.repository;


import com.sbr.common.model.Request;
import com.sbr.ks.repository.repositories.ConsumerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.concurrent.Queues;

@Slf4j
@Repository
public class ConsumerRepositoryImpl implements ConsumerRepository<Request> {

    Sinks.Many<Request> requestBuffer = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);
    @Autowired
    private KafkaReceiver<String, Request> receiver;

    @Override
    public Flux<Request> consumeMessage() {
        Flux<ReceiverRecord<String, Request>> receivedRequest = this.receiver.receive();
        receivedRequest.subscribe(record -> {
            this.requestBuffer.emitNext(record.value(), (s, e) -> true);
        });

        return this.requestBuffer.asFlux();
    }
}
