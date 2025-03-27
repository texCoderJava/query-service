package com.sbr.ks.service.services;


import reactor.core.publisher.Flux;

public interface ConsumerService<T> {
    Flux<T> consume();
}
