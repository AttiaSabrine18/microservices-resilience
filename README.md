# Microservices résilients
Spring Boot · Nginx · Resilience4j

## Architecture
Nginx (port 80) — Reverse Proxy / Load Balancer (Round-Robin)
  ├── /api/users   → User 1 :8081 (Circuit Breaker) | User 2 :8083
  └── /api/products → Product 1 :8082 (Bulkhead)    | Product 2 :8084

## Patterns

### Circuit Breaker — User 1 (port 8081)
- sliding-window-size=5
- failure-rate-threshold=50%
- wait-duration-in-open-state=10s
- permitted-number-of-calls-in-half-open-state=2
- Fallback : "User 1 Service dégradé !"

### Bulkhead — Product 1 (port 8082)
- max-concurrent-calls=3
- max-wait-duration=100ms
- type=SEMAPHORE
- Latence simulée : Thread.sleep(50ms)
- Fallback : "Bulkhead plein !"

## Ports
| Service | Instance | Port | Pattern        |
|---------|----------|------|----------------|
| User    | 1        | 8081 | Circuit Breaker|
| User    | 2        | 8083 | —              |
| Product | 1        | 8082 | Bulkhead       |
| Product | 2        | 8084 | —              |
| Nginx   | Proxy    | 80   | Load Balancer  |

## Démarrage

### 1. Build
cd user-service-1    && mvn clean package -DskipTests
cd ../user-service-2    && mvn clean package -DskipTests
cd ../product-service-1 && mvn clean package -DskipTests
cd ../product-service-2 && mvn clean package -DskipTests

### 2. Lancer les instances
java -jar user-service-1/target/*.jar    --server.port=8081
java -jar user-service-2/target/*.jar    --server.port=8083
java -jar product-service-1/target/*.jar --server.port=8082
java -jar product-service-2/target/*.jar --server.port=8084

### 3. Nginx
nginx -c nginx/nginx.conf

## Tests

# Vérification
curl http://localhost/api/users/hello
curl http://localhost/api/products/hello

# Circuit Breaker 
for /L %i in (1,1,15) do curl -s http://localhost/api/users/hello & echo.

# Circuit Breaker 
for i in {1..15}; do curl -s http://localhost/api/users/hello; echo; done

# Bulkhead 
for /L %i in (1,1,10) do start /B curl -s http://localhost/api/products/hello

# Bulkhead 
for i in {1..10}; do curl -s http://localhost/api/products/hello &; done; wait

# Actuator
curl http://localhost:8081/actuator/circuitbreakers
curl http://localhost:8082/actuator/bulkheads

## Structure
├── user-service-1/     # Circuit Breaker
├── user-service-2/     # Instance standard
├── product-service-1/  # Bulkhead
├── product-service-2/  # Instance standard
├── nginx/              # nginx.conf
└── deploy/             # Scripts .bat et .sh

---
Auteur : Sabrine Attia & Raoua benhamed 
