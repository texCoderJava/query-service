package com.sbr.ks.configuration;

import com.sbr.common.model.Request;
import com.sbr.ks.utils.MessageDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class ConsumerConfiguration {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String TOPIC = "producer-request";

    public Map<String, Object> getConfigurationProps() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "sample-consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return props;
    }

    protected ReceiverOptions<String, Request> kafkaReceiverOptions() {
        ReceiverOptions<String, Request> options = ReceiverOptions.create(getConfigurationProps());

        return options.subscription(Collections.singleton(TOPIC))
                .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));
    }

    @Bean
    public KafkaReceiver<String, Request> receiver() {
        return KafkaReceiver.create(kafkaReceiverOptions());
    }
}
