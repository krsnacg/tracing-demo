# Distributed Tracing Demo — Spring Boot 4

Demo de propagación de `traceId` a través de 3 microservicios Spring MVC
usando `spring-boot-starter-opentelemetry` (Spring Boot 4).

## Arquitectura

```
                      traceparent: 00-abc123-span001-01
[Cliente HTTP] ──────────────────────────────────────────► [BFF :8080]
                                                                │
                          ┌─────────────────────────────────────┤
                          │                                     │
                          │  traceparent: 00-abc123-span002-01  │  traceparent: 00-abc123-span003-01
                          ▼                                     ▼
                  [customer-api :8081]               [product-api :8082]
                          │                                     │
                          └──────────────┬──────────────────────┘
                                         │
                                         ▼
                               [OTel Collector :4317]
                                         │
                                         ▼
                                [Jaeger UI :16686]

Todos los spans comparten el mismo traceId: abc123...
```

## Qué demuestra este proyecto

| Concepto | Implementación |
|---|---|
| Propagación automática del `traceId` | `spring-boot-starter-opentelemetry` + `RestClient.Builder` inyectado |
| Formato de propagación | W3C Trace Context (`traceparent` header) |
| Correlación de logs | `%X{traceId}` en Logback via MDC (sin config manual) |
| Visualización de traces | Jaeger all-in-one |
| Clientes HTTP declarativos | `@HttpExchange` sobre `RestClient` instrumentado |

## Ejecución

### 1. Levantar el stack de observabilidad

```bash
cd docker/
docker compose up -d
```

Verifica que Jaeger esté listo: http://localhost:16686

### 2. Iniciar los microservicios (en terminales separadas)

```bash
# Terminal 1 — customer-api
cd customer-api
mvn spring-boot:run

# Terminal 2 — product-api
cd product-api
mvn spring-boot:run

# Terminal 3 — BFF
cd bff
mvn spring-boot:run
```

### 3. Hacer una request

```bash
curl http://localhost:8080/dashboard/1 | jq
```

Respuesta esperada:
```json
{
  "requestTraceId": "4bf92f3577b34da6a3ce929d0e0e4736",
  "customer": {
    "id": 1,
    "name": "Gabriel Torres",
    "email": "gabriel@demo.com",
    "tier": "PREMIUM"
  },
  "recommendedProducts": [...]
}
```

### 4. Verificar la propagación en los logs

Los 3 servicios deben mostrar el **mismo traceId**:

```
# BFF (puerto 8080)
2025-06-20 10:23:01 INFO  [bff,4bf92f3577b34da6a3ce929d0e0e4736,a3ce929d0e0e4736] ...DashboardController - [BFF] Iniciando agregación...
2025-06-20 10:23:01 INFO  [bff,4bf92f3577b34da6a3ce929d0e0e4736,a3ce929d0e0e4736] ...DashboardController - [BFF] Customer recibido: name=Gabriel Torres

# customer-api (puerto 8081)
2025-06-20 10:23:01 INFO  [customer-api,4bf92f3577b34da6a3ce929d0e0e4736,00f067aa0ba902b7] ...CustomerController - [CUSTOMER-API] Request recibido...
                                         ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                                         MISMO traceId que el BFF ✓

# product-api (puerto 8082)
2025-06-20 10:23:01 INFO  [product-api,4bf92f3577b34da6a3ce929d0e0e4736,b34da6a3ce929d0e] ...ProductController - [PRODUCT-API] Listando productos...
                                        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
                                        MISMO traceId que el BFF ✓
```

### 5. Ver el trace en Jaeger

1. Abrir http://localhost:16686
2. Service → **bff**
3. Click "Find Traces"
4. Expandir el trace — verás los 3 spans anidados:

```
bff: GET /dashboard/1                              [200ms]
  └── customer-api: GET /customers/1              [ 30ms]
  └── product-api:  GET /products                 [ 45ms]
```

## Cómo funciona la propagación (sin código manual)

```
1. Request llega al BFF sin header traceparent
   → OTel crea span raíz con traceId=abc123, spanId=span001

2. BFF llama a customer-api vía RestClient.Builder instrumentado
   → El interceptor de Micrometer inyecta:
      traceparent: 00-abc123-span001-01
   → customer-api extrae abc123, crea span hijo span002

3. BFF llama a product-api
   → Mismo mecanismo, product-api crea span hijo span003

4. Los 3 spans se exportan al OTel Collector → Jaeger
   → Jaeger los une por traceId=abc123 → trace completo
```

## Regla crítica — usar siempre el Builder inyectado

```java
// ✅ CORRECTO — Builder auto-configurado con interceptor de tracing
@Bean
public RestClient customerClient(RestClient.Builder builder) {
    return builder.baseUrl("http://customer-api").build();
}

// ❌ INCORRECTO — el traceId NO se propagará
RestClient client = RestClient.create();
```

## Estructura del proyecto

```
tracing-demo/
├── pom.xml                    ← Multi-module root, dependencias comunes
├── bff/                       ← Puerto 8080
│   └── src/main/java/com/demo/bff/
│       ├── config/
│       │   ├── RestClientConfig.java    ← Clientes con Builder instrumentado
│       │   └── HttpExchangeConfig.java  ← Proxies @HttpExchange
│       ├── client/
│       │   ├── CustomerClient.java      ← @HttpExchange interface
│       │   └── ProductClient.java
│       ├── controller/
│       │   └── DashboardController.java ← Agrega datos de ambos servicios
│       └── model/
│           └── DashboardResponse.java
├── customer-api/              ← Puerto 8081
│   └── src/main/java/com/demo/customer/
│       ├── controller/CustomerController.java
│       └── model/Customer.java
├── product-api/               ← Puerto 8082
│   └── src/main/java/com/demo/product/
│       ├── controller/ProductController.java
│       └── model/Product.java
└── docker/
    ├── docker-compose.yml          ← Jaeger + OTel Collector
    └── otel-collector-config.yml
```
