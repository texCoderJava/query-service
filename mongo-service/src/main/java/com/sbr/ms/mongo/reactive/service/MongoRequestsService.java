package com.sbr.ms.mongo.reactive.service;

import com.sbr.common.model.Request;
import com.sbr.ms.mongo.reactive.reposistory.MongoReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Slf4j
public class MongoRequestsService {
    private final MongoReactiveRepository<Request> reposistory;
    
    public Mono<Request> save(Request req) {
        return this.reposistory.save(req);
    }
    
    public Flux<Request> saveAll(List<Request> reqs) {
        return this.reposistory.saveAll(reqs);
    }
    
    public Flux<Request> listenRequestsChange() {
        return this.reposistory.listenRequestsChange();
    }
}
