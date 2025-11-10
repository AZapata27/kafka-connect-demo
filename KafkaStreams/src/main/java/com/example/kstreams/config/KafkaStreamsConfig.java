package com.example.kstreams.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;

@Configuration
public class KafkaStreamsConfig {

    @Bean
    public KafkaStreamsInteractiveQueryService interactiveQueryService(StreamsBuilderFactoryBean factoryBean) {
        return new KafkaStreamsInteractiveQueryService(factoryBean);
    }
}