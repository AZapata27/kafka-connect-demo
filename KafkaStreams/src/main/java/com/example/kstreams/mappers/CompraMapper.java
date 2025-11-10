package com.example.kstreams.mappers;

import com.example.kstreams.model.Compra;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir mensajes JSON de Kafka Connect a objetos Compra.
 * Maneja diferentes formatos de payloads (con schema, CDC, etc.)
 */
public class CompraMapper {

    private final ObjectMapper objectMapper;

    public CompraMapper() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Convierte un JSON string a objeto Compra.
     * Maneja payloads de Kafka Connect con o sin schema y CDC formats.
     *
     * @param json String JSON del mensaje de Kafka
     * @return Objeto Compra o null si hay error en el parseo
     */
    public Compra toCompra(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            
            // Si Kafka Connect tiene schemas.enable=true, los datos están bajo "payload"
            if (root.has("payload")) {
                root = root.get("payload");
            }
            
            // Algunos conectores CDC agregan "after" para cambios
            if (root.has("after")) {
                root = root.get("after");
            }
            
            // Extraer campos con nombres alternativos
            Integer id = getInt(root, "id");
            Integer userId = getInt(root, "user_id", "userId");
            Integer value = getInt(root, "value", "amount");
            Integer orderId = getInt(root, "order_id", "orderId");
            
            return new Compra(id, userId, value, orderId);
        } catch (Exception e) {
            // Log opcional: logger.warn("Error parsing Compra from JSON: {}", json, e);
            return null;
        }
    }

    /**
     * Extrae un Integer de un JsonNode buscando múltiples nombres de campo.
     * Útil para manejar diferentes convenciones de nombres (snake_case, camelCase).
     *
     * @param node JsonNode del que extraer el valor
     * @param names Nombres alternativos del campo a buscar
     * @return Valor Integer o null si no se encuentra o es nulo
     */
    private Integer getInt(JsonNode node, String... names) {
        for (String name : names) {
            if (node != null && node.has(name) && !node.get(name).isNull()) {
                try {
                    return node.get(name).asInt();
                } catch (Exception ignored) {
                    // Si falla la conversión, intentar con el siguiente nombre
                }
            }
        }
        return null;
    }
}