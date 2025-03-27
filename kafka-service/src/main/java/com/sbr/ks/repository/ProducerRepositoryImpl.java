package com.sbr.ks.repository;

import com.sbr.common.model.Request;
import com.sbr.ks.repository.repositories.ProducerRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Slf4j
@Repository
public class ProducerRepositoryImpl implements ProducerRepository<Request> {

    private static final String TOPIC = "producer-request";
    @Autowired
    private KafkaSender<String, Request> sender;

    @Override
    public void sendRequest(Flux<Request> requests) {
        this.sender.send(requests.map(request ->
                        SenderRecord.create(new ProducerRecord<>(TOPIC, request), request)))
                .doOnError(error -> log.error("unable to send due to", error))
                .subscribe(r -> {
                    RecordMetadata metadata = r.recordMetadata();
                    System.out.printf("Message %s sent successfully, topic-partition=%s-%d offset=%d\n",
                            r.correlationMetadata().getRequestId(),
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset());
                });
    }

    public void close() {
        this.sender.close();
    }
}
