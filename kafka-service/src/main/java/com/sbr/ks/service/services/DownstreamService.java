package com.sbr.ks.service.services;

import reactor.core.publisher.Flux;

public interface DownstreamService<T> {

    Flux<T> push(Flux<T> requests);
}
