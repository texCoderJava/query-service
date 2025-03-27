package com.sbr.ms.mongo.reactive.reposistory;


import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor(onConstructor = @__(@Autowired), staticName = "create")
public class MongoReactiveRepository<T> {
    private final ReactiveMongoTemplate template;
    private final Class<T> type;
    private final String collectionName;
    
    public Mono<T> fetch(Query query) {
        return this.template.findOne(query, this.type, this.collectionName);
    }
    
    public Flux<T> fetchAll(Query query) {
        return this.template.find(query, this.type, this.collectionName);
    }
    
    public Flux<T> fetchAll() {
        return this.template.findAll(this.type, this.collectionName);
    }
    
    public Mono<T> save(T req) {
        return this.template.insert(req, this.collectionName);
    }
    
    public Flux<T> saveAll(List<T> reqs) {
        return this.template.insertAll(Mono.just(reqs), this.collectionName);
    }
    
    public Mono<UpdateResult> update(Query query, UpdateDefinition update) {
        return this.template.updateFirst(query, update, this.type, this.collectionName);
    }
    
    public Mono<UpdateResult> updateAll(Query query, UpdateDefinition update) {
        return this.template.upsert(query, update, this.type, this.collectionName);
    }
    
    public Flux<T> listenRequestsChange() {
        return this.template.changeStream(this.type).listen().map(listenReq -> listenReq.getBody());
    }
}

