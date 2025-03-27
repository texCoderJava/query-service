package com.sbr.ks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class KafkaServiceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(KafkaServiceApplication.class, args);
    }

}
