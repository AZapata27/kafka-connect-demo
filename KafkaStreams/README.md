# Kafka Streams Demo (Spring Boot)

Este módulo `kafkaStreams/` contiene una aplicación Spring Boot con Kafka Streams pensada para una charla básica. Consume los eventos producidos por el conector JDBC Source configurado en `KafkaConnect/` (tópico `CDC_compras`) y realiza transformaciones simples para explicar conceptos clave.

## Qué hace la app

- Lee del tópico de entrada: `CDC_compras` (emitido por Kafka Connect desde la tabla `compras`).
- Parseo de JSON de Kafka Connect (con o sin schema/payload) al modelo `Compra { id, userId, value, orderId }`.
- Filtra "compras de alto valor" (>= umbral configurable) y las publica en `compras_altas`.
- Agrupa por `userId` y calcula en tiempo real:
  - Cantidad de compras por usuario → `compras_por_usuario_count`.
  - Total gastado por usuario → `compras_por_usuario_total`.
- Materializa los resultados en state stores y expone un API REST para consultas interactivas:
  - `GET /api/counts/{userId}` → cantidad de compras del usuario.
  - `GET /api/totals/{userId}` → total gastado por el usuario.
  - `GET /api/status` → estado del Kafka Streams runtime.

## Requisitos

- Tener corriendo el stack de `KafkaConnect/` (Zookeeper, Kafka, Connect, Postgres y Redpanda Console).
- Java 17.
- Maven (para compilar y ejecutar) o ejecutar desde IDE.

## Arranque del entorno Kafka Connect

Sigue las instrucciones en `KafkaConnect/README.md`:

1. Levantar el stack:
   ```bash
   cd KafkaConnect
   docker compose up -d
   ```
2. Inicializar conectores (cuando los servicios estén Up):
   ```bash
   ./init-connectors.bash
   ```
3. Verifica en Redpanda Console: http://localhost:8080 que exista el tópico `CDC_compras` y se estén publicando mensajes.

## Configuración de la app de Streams

Archivo: `kafkaStreams/src/main/resources/application.yml`

- Bootstrap servers: `localhost:9092`
- Application id: `kstreams-demo`
- Tópicos:
  - entrada: `CDC_compras`
  - salida (alto valor): `compras_altas`
  - salida (conteo por usuario): `compras_por_usuario_count`
  - salida (total por usuario): `compras_por_usuario_total`
- Umbral de alto valor configurable: `app.demo.highValueThreshold` (por defecto 100)
- Puerto HTTP: `9099`

Puedes ajustar estos valores según necesites para la demo.

## Compilar y ejecutar

Con Maven:
```bash
cd kafkaStreams
mvn spring-boot:run
```

Desde IDE (IntelliJ):
- Abrir el proyecto.
- Ejecutar la clase `KafkaStreamsDemoApplication`.

La app expondrá el API en `http://localhost:9099`.

## Probar la demo

1. Inserta datos en la tabla `compras` del Postgres de `KafkaConnect` para generar eventos vía Connect JDBC Source. Ejemplo (dentro del contenedor o conectándote a `localhost:5432` usuario `user` password `password` DB `postgres`):
   ```sql
   insert into compras(user_id, value, order_id) values (1, 50, 1001);
   insert into compras(user_id, value, order_id) values (1, 200, 1002);
   insert into compras(user_id, value, order_id) values (2, 300, 2001);
   ```

2. Observa los tópicos en Redpanda Console:
   - `CDC_compras` (entrada)
   - `compras_altas` (solo compras con `value >= 100` por defecto)
   - `compras_por_usuario_count`
   - `compras_por_usuario_total`

3. Consulta los state stores vía API REST:
   ```bash
   curl http://localhost:9099/api/status
   curl http://localhost:9099/api/counts/1
   curl http://localhost:9099/api/totals/1
   curl http://localhost:9099/api/counts/2
   curl http://localhost:9099/api/totals/2
   ```

Deberías ver incrementos en conteos y totales conforme insertas nuevas filas en `compras`.

## Puntos didácticos para la charla

- Diferencia entre Kafka Connect y Kafka Streams: ingestión vs procesamiento/transformación.
- Tópico de entrada desde Connect (`topic.prefix` + nombre de tabla).
- Operadores Streams:
  - `mapValues`, `filter`, `selectKey`, `groupByKey`, `count`, `aggregate`.
- Materialización de KTable en state stores y consultas interactivas (Interactive Queries) vía REST.
- Serdes por defecto y manejo de JSON de Connect (posibles estructuras: `payload`, `after`).
- Backpressure, commits, y desactivación de caché para demo (para ver resultados al instante).

## Notas

- Si ejecutas la app fuera de Docker, usa `localhost:9092` como bootstrap server (como está en `application.yml`). Si la metes en Docker en la misma red que Kafka, ajusta a `kafka:9092`.
- Los tópicos de salida se crean automáticamente si `auto.create.topics.enable=true` (por defecto en imágenes Confluent). Si no, créalos desde la consola.
- Para reiniciar state stores en la demo, cambia el `application-id` o borra el directorio de estado local de Kafka Streams.
