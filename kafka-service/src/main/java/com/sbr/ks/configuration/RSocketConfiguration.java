package com.sbr.ks.configuration;

import io.netty.buffer.Unpooled;
import io.rsocket.core.Resume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@Configuration
@Slf4j
public class RSocketConfiguration {

    private final Integer retryThreshold = 100;
    private final String applicationName = "kafka-service";

    @Bean("redisRSocketRequesterMono")
    public Mono<RSocketRequester> redisRSocketRequesterMono(RSocketRequester.Builder rSocketRequesterBuilder, RSocketStrategies rSocketStrategies) {
        RetryBackoffSpec retryBackoffSpec = Retry.backoff(this.retryThreshold, Duration.ofSeconds(2))
                .doBeforeRetry(rS -> log.warn("Reconnecting... {}", rS));

        UUID guid = UUID.randomUUID();
        String clientId = String.format("%s.%s", this.applicationName, guid);

        return Mono.just(rSocketRequesterBuilder
                .setupRoute("requests.registration")
                .setupData(clientId)
                .rsocketStrategies(rSocketStrategies)
                .rsocketConnector(connector -> connector.reconnect(retryBackoffSpec)
                        .resume(new Resume().token(
                                () -> Unpooled.copiedBuffer(guid.toString().getBytes(StandardCharsets.UTF_8))
                        ).retry(retryBackoffSpec)))
                .tcp("localhost", 8085));
    }
}
