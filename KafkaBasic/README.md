# KafkaBasic

Aplicación Spring Boot (Spring for Apache Kafka) simple para acompañar una charla básica sobre Kafka.

Objetivo:
- Consumir los tópicos generados por el módulo de Kafka Streams (`KafkaStreams`).
- Demostrar listeners y grupos de consumidores en Spring Kafka.
- Añadir una funcionalidad de negocio didáctica: calcular puntos redimibles y decidir obsequios a partir de compras de ALTO valor.
- Incluir un listener de `CDC_compras` que delega una notificación en un puerto/iface sin implementación (para explicar puertos/adaptadores).

## Tópicos usados
- `app.topics.input` → `CDC_compras` (salida del Source Connector de Kafka Connect)
- `app.topics.highValue` → `compras_altas` (salida de la topología de Kafka Streams)
- `app.topics.lowValue` → `compras_bajas` (salida de la topología de Kafka Streams)
- `app.topics.points` → `compras_puntos` (nuevo: eventos con puntos y obsequios)

Configurable vía `application.yml`.

## Estructura clave
- `com.example.kbasic.listeners.ComprasListeners`
  - `onCompraAlta(...)`: consume `compras_altas`, calcula puntos y publica a `compras_puntos` en formato JSON.
  - `onCompraBaja(...)`: consume `compras_bajas`, hace logging (útil para métricas/explicación de grupos).
  - `onCdcCompra(...)`: consume `CDC_compras`, intenta notificar mediante `NotificationPort` si existe un bean, si no, solo loguea.
- `com.example.kbasic.service.PointsService`: calcula puntos y decide obsequios.
- `com.example.kbasic.service.dto.PointsEvent`: payload JSON enviado a `compras_puntos`.
- `com.example.kbasic.ports.NotificationPort`: interfaz de notificación sin implementación.
- `com.example.kbasic.util.CompraParser`: parser sencillo del `toString()` de `Compra` emitido por Kafka Streams.

Nota: `KafkaStreams` actualmente publica el valor como `String` con el `toString()` de `Compra`. Por eso el parser simple.

## Cómo ejecutar el demo completo
1) Iniciar infraestructura y conectores (ver carpeta `KafkaConnect/`):
   - `docker-compose up -d` (Zookeeper, Kafka, Connect, etc.)
   - `./init-connectors.bash` (crea Source y Sink connectors; el Source emite a `CDC_compras`)

2) Ejecutar la app de Kafka Streams (`KafkaStreams/`):
   - `mvn spring-boot:run` (o desde IDE) → separa `CDC_compras` en `compras_altas` y `compras_bajas` según `highValueThreshold`.

3) Ejecutar esta app `KafkaBasic/`:
   - `mvn spring-boot:run` (o desde IDE)
   - Ver logs: cuando lleguen mensajes a `compras_altas`, se publicará un evento de puntos en `compras_puntos`.

4) Observar mensajes:
   - Usar `kafka-console-consumer` o su herramienta favorita para leer `compras_puntos` y `compras_bajas`.

## Parámetros de la demo
En `KafkaBasic/src/main/resources/application.yml`:
- `app.demo.pointsFactor`: puntos = `value / pointsFactor`.
- `app.demo.bigGiftThreshold` y `app.demo.smallGiftThreshold`: determinan `GRAN_REGALO`, `REGALO_PEQUENO` o `SIN_REGALO`.

## Extensiones sugeridas para la charla
- Añadir implementación de `NotificationPort` (por ejemplo, enviar email o Slack) para mostrar inyección de dependencias.
- Cambiar `group-id` de listeners para explicar paralelismo.
- Mostrar reintentos y DLT con `SeekToCurrentErrorHandler`.
- Reemplazar el parser por JSON usando un Serializer/Deserializer dedicado.
