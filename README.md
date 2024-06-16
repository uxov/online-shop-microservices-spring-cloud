# Online Shop - Microservices Example - Build Microservices Using Spring Boot And Spring Cloud

- Eureka
- Spring Cloud Gateway
- Spring Cloud OpenFeign
- Spring Cloud Circuit Breaker - Resilience4J
- ~~Spring Cloud Sleuth + Zipkin~~  
- Micrometer Tracing + Zipkin  
- Redis
- RedissonLock  
- RabbitMQ
- RabbitMQ RPC - Request / Reply Pattern
- JWT
- Spring Boot 3.3
- Spring Boot Admin

## Overview

![Overview](./images/sp-overview.png)  

### Order Process

![Order Process](./images/order-process.png)

### API Token Authentication

![API Token Authentication](./images/sp-token-authentication.png)

## Run And Test

### Requirements

- JDK 22
- Gradle 8.8
- Podman & `podman-compose` (or Docker & `docker cmopose`)

### Preparation

Create and run containers (PostgreSQL, RabbitMQ, Redis, Zipkin)

```shell
cd scripts
podman-compose up -d
```

Or use `docker compose`

```shell
cd scripts
sudo docker compose up -d
```

### Run
Build and run
- `eurekaServer`, `gateway`, `sbAdmin`
- `authServer, spProduct, spUser, spOrder, spPayment`
- `spWeb` (web App)

```shell
cd scripts
sh run-jars.sh
```

### Test

Add test data before run test

```shell
cd spTest
gradle clean test -i --tests AddTestData
```
*check codes  in `spTest/src/test/java/xyz/defe/sp/test/AddTestData.java`*  
  
Run test classes

```shell
cd spTest
gradle clean test --tests TestAllSuit
```
*see test classes in `spTest/src/test/java/xyz/defe/sp/test/*`*

### Quick run and test
```shell
cd scripts
sh run-and-test.sh
```

### Web UI

Eureka > http://localhost:8761  

Zipkin > http://localhost:9411  

RabbitMQ > http://localhost:15672  

Spring Boot Admin > http://localhost:11000/wallboard  

![Spring Boot Admin Overview](./images/sbAdmin-overview.png)


## Build docker image

Create Dockerfile
```shell
gradle dockerCreateDockerfile
```
*It will create a `Dockerfile` in `./build/docker/`*  
  
Build image
```shell
gradle dockerBuildImage
```

### Podman
Execute the command before build image :
```shell
systemctl --user restart podman
```

### Docker
Before build image, comment out this line in `build.gradle` file :
```groovy
url = 'unix:///run/user/1000/podman/podman.sock'
```
![](./images/build-image.png)

When you get the following error :
> Got permission denied while trying to connect to the Docker daemon socket

Add your user to `docker` group :
```shell
sudo groupadd docker
sudo gpasswd -a ${USER} docker
sudo systemctl restart docker
newgrp docker
```
Then you can running Docker without `sudo`, If that doesn't work, try logout or rebooting.
