package com.example.kstreams.api;

import com.example.kstreams.streams.ComprasTopology;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.streams.KafkaStreamsInteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/api")
public class QueryController {

    private final KafkaStreamsInteractiveQueryService interactiveQueryService;
    private final StreamsBuilderFactoryBean factoryBean;

    public QueryController(KafkaStreamsInteractiveQueryService interactiveQueryService,
                           StreamsBuilderFactoryBean factoryBean) {
        this.interactiveQueryService = interactiveQueryService;
        this.factoryBean = factoryBean;
    }

    @GetMapping("/counts/{userId}")
    public ResponseEntity<?> getCount(@PathVariable String userId) {
        ReadOnlyKeyValueStore<String, Long> store = interactiveQueryService
                .retrieveQueryableStore(ComprasTopology.STORE_COUNT, QueryableStoreTypes.keyValueStore());
        Long value = store.get(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "count", value == null ? 0 : value
        ));
    }

    @GetMapping("/totals/{userId}")
    public ResponseEntity<?> getTotal(@PathVariable String userId) {
        ReadOnlyKeyValueStore<String, Long> store = interactiveQueryService
                .retrieveQueryableStore(ComprasTopology.STORE_TOTAL, QueryableStoreTypes.keyValueStore());
        Long value = store.get(userId);
        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "total", value == null ? 0 : value
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        var streams = factoryBean.getKafkaStreams();
        String state = streams == null ? "NOT_STARTED" : streams.state().toString();
        return ResponseEntity.ok(Map.of("state", state));
    }
}
