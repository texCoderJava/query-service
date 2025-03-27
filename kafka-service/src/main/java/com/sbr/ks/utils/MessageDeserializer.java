package com.sbr.ks.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbr.common.model.Request;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MessageDeserializer implements Deserializer<Request> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public Request deserialize(String s, byte[] request) {
        try {
            if (request == null) {
                System.out.println("Null received at deserializing");
                return null;
            }
            System.out.println("Deserializing...");
            return objectMapper.readValue(new String(request, StandardCharsets.UTF_8), Request.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to MessageDto");
        }
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
