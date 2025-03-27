package com.sbr.ms.mongo.reactive.config;

import com.sbr.common.model.Request;
import com.sbr.ms.mongo.reactive.reposistory.MongoReactiveRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories
public class MongoReactiveConfig {
    
    @Value("${mongo.request.collection.name}")
    private String requestCollectionName;
    
    @Bean
    public MongoReactiveRepository<Request> mongoReactiveRepository(ReactiveMongoTemplate template) {
        return MongoReactiveRepository.create(template, Request.class, this.requestCollectionName);
    }
}
