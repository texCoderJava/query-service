package com.sbr.ks.repository.repositories;

import reactor.core.publisher.Flux;

public interface DownStreamRepository<T> {
    Flux<T> push(Flux<T> requests);
}
