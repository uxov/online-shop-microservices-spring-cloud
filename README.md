# Microservices Example - Online Shop - Base On Spring Cloud


- Eureka
- Spring Cloud Gateway
- Spring Cloud Circuit Breaker - Resilience4J
- Spring Cloud Sleuth + Zipkin  
- Redis
- RedissionLock  
- RabbitMQ
- RabbitMQ RPC - Request / Reply Pattern

## Overview
![Overview](./images/sp-overview.png)  

### Order Process
![Order Process](./images/order-process.png)

## Run And Test  
### Requirements
- Java version >= 8
- Gradle
- Database
- RabbitMQ
- Redis
- Zipkin

### Preparation
1. Start servers: database, RabbitMQ, Redis, Zipkin

2. Create databases, names: sp-product, sp-user, sp-order, sp-payment

3. Configure connection settings for projects,
set databases / RabbitMQ / Redis connection info

4. Build `spCommon`, it will generate `spCommon-0.0.1-SNAPSHOT.jar` in `spCommon/build/libs` directory and as a dependency for projects
```shell
cd spCommon
gradle build
```

### Run
1. Start Eureka server: `eurekaServer`

2. Start services: `spProduct, spUser, spOrder, spPayment`  

3. Start gateway: `gateway`

4. Start web App: `spWeb`

5. Add test data by execute
`addData()` in `spTest/src/test/java/xyz/defe/sp/test/services/AddTestData.java`

### Test
See test codes in `spTest/src/test/java/xyz/defe/sp/test/*`
