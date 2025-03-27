package com.sbr.ms.mongo.reactive.controller;

import com.sbr.common.model.Request;
import com.sbr.ms.mongo.reactive.service.MongoRequestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.messaging.rsocket.annotation.support.RSocketFrameTypeMessageCondition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@MessageMapping("requests")
public class MongoReactiveRequestRSController {
    
    private static final String RSOCKET_FRAME_TYPE = RSocketFrameTypeMessageCondition.FRAME_TYPE_HEADER;
    private static final String CONTENT_TYPE = "contentType";
    
    @Autowired
    MongoRequestsService mongoRequestsService;
    
    @MessageMapping("create")
    public Mono<Request> create(@RequestBody Request request) {
        return this.mongoRequestsService.save(request);
    }
    
    @MessageMapping("create.list")
    public Flux<Request> create(@RequestBody List<Request> requests) {
        return this.mongoRequestsService.saveAll(requests);
    }
    
    // Note: Use this api properly as it is a never ending stream
    @MessageMapping("listen.change")
    public Flux<Request> listenRemove() {
        return this.mongoRequestsService.listenRequestsChange();
    }
    
    @ConnectMapping("registration")
    public Mono<Void> requestsRegistration(RSocketRequester rSocketRequester,
                                           @Payload String clientId,
                                           @Header(RSOCKET_FRAME_TYPE) String rsocketFrameType,
                                           @Header(CONTENT_TYPE) String contentType) {
        return Mono.empty();
    }
}

