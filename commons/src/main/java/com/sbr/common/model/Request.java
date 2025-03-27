package com.sbr.common.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor(staticName = "instance")
@NoArgsConstructor(staticName = "instance")
@Builder(builderMethodName = "request")
@ToString
@Document("requests")
public class Request implements MapModel {
    
    @NonNull
    private String requestId;
    
    @NonNull
    private String requestType;
    
    @NonNull
    private String customerId;
    
    @NonNull
    private LatLng source;
    
    @NonNull
    private LatLng destination;
    
    private Instant requestedDate;
    
    private Instant processingDate;
    
    private Instant completionDate;
    
    @Override
    public Request fromMap(@NonNull Object mapObj) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.convertValue(mapObj, new TypeReference<>() {
        });
        return mapper.convertValue(map, this.getClass());
    }
    
    @Override
    public Map<String, Object> toMap() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(this, new TypeReference<>() {
        });
    }
}
