package com.sbr.ks.configuration;

import com.sbr.common.model.Request;
import com.sbr.ks.utils.MessageSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProducerConfiguration {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public Map<String, Object> getConfigurationProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageSerializer.class);

        return props;
    }

    @Bean
    public KafkaSender<String, Request> sender() {
        SenderOptions<String, Request> senderOptions = SenderOptions.create(getConfigurationProps());
        return KafkaSender.create(senderOptions);
    }
}
