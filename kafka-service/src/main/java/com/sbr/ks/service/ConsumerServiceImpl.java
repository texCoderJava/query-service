package com.sbr.ks.service;

import com.sbr.common.model.Request;
import com.sbr.ks.repository.repositories.ConsumerRepository;
import com.sbr.ks.service.services.ConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service("consumerServiceImpl")
public class ConsumerServiceImpl implements ConsumerService<Request> {

    @Autowired
    private ConsumerRepository<Request> consumerRepository;

    @Override
    public Flux<Request> consume() {
        return this.consumerRepository.consumeMessage();
    }
}
