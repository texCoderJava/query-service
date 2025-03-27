package com.sbr.ms.mongo.reactive.controller;

import com.sbr.common.model.Request;
import com.sbr.ms.mongo.reactive.service.MongoRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/mongo/request")
@CrossOrigin(origins = "*")
public class MongoReactiveRequestController {
    
    @Autowired
    MongoRequestsService mongoRequestsService;
    
    @PostMapping
    public Mono<Request> create(@RequestBody Request request) {
        return this.mongoRequestsService.save(request);
    }
    
    @PostMapping("list")
    public Flux<Request> create(@RequestBody List<Request> requests) {
        return this.mongoRequestsService.saveAll(requests);
    }
    
    // Note: Use this api properly as it is a never ending stream
    @GetMapping("listen/change")
    public Flux<Request> listenRemove() {
        return this.mongoRequestsService.listenRequestsChange();
    }
    
    
}

