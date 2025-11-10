package com.example.kbasic.service;

import com.example.kbasic.config.TopicsProperties;
import com.example.kbasic.service.dto.PointsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PointsService {

    private static final Logger log = LoggerFactory.getLogger(PointsService.class);

    private final TopicsProperties props;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PointsService(TopicsProperties props, KafkaTemplate<String, String> kafkaTemplate) {
        this.props = props;
        this.kafkaTemplate = kafkaTemplate;
    }

    public int computePoints(int value) {
        int factor = props.getDemo().getPointsFactor();
        return Math.max(0, value / factor);
    }

    public String decideGift(int points) {
        if (points >= props.getDemo().getBigGiftThreshold()) return "GRAN_REGALO";
        if (points >= props.getDemo().getSmallGiftThreshold()) return "REGALO_PEQUENO";
        return "SIN_REGALO";
    }

    public void publishPointsEvent(PointsEvent event) {
        String topic = props.getTopics().getPoints();
        String key = String.valueOf(event.getUserId());
        String payload = event.toJson();
        log.info("[PUNTOS] publicando a topic={} key={}, payload={}", topic, key, payload);
        kafkaTemplate.send(topic, key, payload);
    }
}
