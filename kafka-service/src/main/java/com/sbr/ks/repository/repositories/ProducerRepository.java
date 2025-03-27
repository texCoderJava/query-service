package com.sbr.ks.repository.repositories;

import reactor.core.publisher.Flux;

public interface ProducerRepository<T> {
    void sendRequest(Flux<T> request);
}
