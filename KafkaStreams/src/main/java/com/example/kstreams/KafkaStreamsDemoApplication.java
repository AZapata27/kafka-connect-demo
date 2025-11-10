package com.example.kstreams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@SpringBootApplication
@EnableKafkaStreams
public class KafkaStreamsDemoApplication {
    static void main(String[] args) {
        SpringApplication.run(KafkaStreamsDemoApplication.class, args);
    }
}
