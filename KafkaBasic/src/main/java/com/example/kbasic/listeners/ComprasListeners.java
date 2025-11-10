package com.example.kbasic.listeners;

import com.example.kbasic.config.TopicsProperties;
import com.example.kbasic.model.Compra;
import com.example.kbasic.ports.NotificationPort;
import com.example.kbasic.service.PointsService;
import com.example.kbasic.service.dto.PointsEvent;
import com.example.kbasic.util.CompraParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

@Component
public class ComprasListeners {

    private static final Logger log = LoggerFactory.getLogger(ComprasListeners.class);

    private final TopicsProperties props;
    private final PointsService pointsService;
    private final ObjectProvider<NotificationPort> notificationPortProvider;

    public ComprasListeners(TopicsProperties props,
                            PointsService pointsService,
                            ObjectProvider<NotificationPort> notificationPortProvider) {
        this.props = props;
        this.pointsService = pointsService;
        this.notificationPortProvider = notificationPortProvider;
    }

    // Listener para compras altas
    @KafkaListener(topics = "#{@topicsProperties.topics.highValue}", groupId = "kafka-basic-altas")
    public void onCompraAlta(@Header(KafkaHeaders.RECEIVED_KEY) String key,
                             @Payload String value) {
        log.info("[ALTAS] key={}, value={}", key, value);
        Compra compra = CompraParser.parseFromToString(value);
        if (compra == null) {
            log.warn("[ALTAS] No se pudo parsear Compra del mensaje: {}", value);
            return;
        }
        int points = pointsService.computePoints(compra.getValue());
        String gift = pointsService.decideGift(points);
        PointsEvent event = new PointsEvent(compra.getUserId(), compra.getOrderId(), compra.getValue(), points, gift);
        pointsService.publishPointsEvent(event);
    }

    // Listener para compras bajas
    @KafkaListener(topics = "#{@topicsProperties.topics.lowValue}", groupId = "kafka-basic-bajas")
    public void onCompraBaja(@Header(KafkaHeaders.RECEIVED_KEY) String key,
                             @Payload String value) {
        log.info("[BAJAS] key={}, value={}", key, value);
    }

    // Listener para CDC_Compras:
    @KafkaListener(topics = "#{@topicsProperties.topics.input}", groupId = "kafka-basic-cdc")
    public void onCdcCompra(@Header(name = KafkaHeaders.RECEIVED_KEY, required = false) String key,
                            @Payload String value) {
        log.info("[CDC] key={}, raw={} ... intentando enviar notificacion si existe un NotificationPort", key, summarize(value));
        NotificationPort port = notificationPortProvider.getIfAvailable();
        if (port == null) {
            log.info("[CDC] No hay NotificationPort registrado. Solo logging.");
            return;
        }
        Compra compra = CompraParser.parseFromToString(value);
        if (compra == null) {
            log.warn("[CDC] No se pudo parsear Compra del CDC payload. Se omite notificacion.");
            return;
        }
        try {
            port.notifyCompraCreated(compra);
            log.info("[CDC] Notificacion enviada para compra id={} userId={}", compra.getId(), compra.getUserId());
        } catch (Exception e) {
            log.error("[CDC] Error llamando a NotificationPort: {}", e.getMessage(), e);
        }
    }

    private String summarize(String v) {
        if (v == null) return null;
        String s = v.replaceAll("\n", " ");
        return s.length() > 200 ? s.substring(0, 200) + "..." : s;
        }
}
