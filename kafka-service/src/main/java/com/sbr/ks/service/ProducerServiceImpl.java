package com.sbr.ks.service;

import com.sbr.common.model.Request;
import com.sbr.ks.repository.repositories.ProducerRepository;
import com.sbr.ks.service.services.ProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProducerServiceImpl implements ProducerService<Request> {

    @Autowired
    private ProducerRepository<Request> producerRepository;


    @Override
    public void sendMessage(Flux<Request> request) {
        this.producerRepository.sendRequest(request);
    }
}
