package com.example.kstreams.streams;

import com.example.kstreams.config.TopicsProperties;
import com.example.kstreams.mappers.CompraMapper;
import com.example.kstreams.model.Compra;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class ComprasTopology {

    public static final String STORE_COUNT = "store-per-user-count";
    public static final String STORE_TOTAL = "store-per-user-total";

    private final CompraMapper compraMapper = new CompraMapper();

    @Bean
    public KStream<String, String> buildPipeline(StreamsBuilder builder, TopicsProperties props) {

        final Serde<String> stringSerde = Serdes.String();


        KStream<String, String> source = builder.stream(props.getTopics().getInput(), Consumed.with(stringSerde, stringSerde));

        KStream<String, Compra> compras = source
                .mapValues(compraMapper::toCompra)
                .filter((k, v) -> v != null);


        int threshold = props.getDemo().getHighValueThreshold();

        Map<String, KStream<String, Compra>> branches = compras
                .split(Named.as("compras-"))
                .branch(
                        (k, v) -> v.getValue() != null && v.getValue() >= threshold,
                        Branched.as("altas")
                )
                .branch(
                        (k, v) -> v.getValue() != null && v.getValue() < threshold,
                        Branched.as("bajas")
                )
                .noDefaultBranch();

        // Enviar valores altos a un tópico
        branches.get("compras-altas")
                .map((k, v) -> new KeyValue<>(String.valueOf(v.getUserId()), v.toString()))
                .to(props.getTopics().getHighValue(), Produced.with(stringSerde, stringSerde));

        // Enviar valores bajos a otro tópico
        branches.get("compras-bajas")
                .map((k, v) -> new KeyValue<>(String.valueOf(v.getUserId()), v.toString()))
                .to(props.getTopics().getLowValue(), Produced.with(stringSerde, stringSerde));


        return source;
    }


}
