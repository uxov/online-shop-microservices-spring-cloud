#!/bin/bash

gradle clean build -p ../spCommon/ &&
gradle clean build -p ../eurekaServer/ &&
gradle clean build -p ../gateway/ &&
gradle clean build -p ../authServer/ &&
gradle clean build -p ../spUser/ &&
gradle clean build -p ../spProduct/ &&
gradle clean build -p ../spOrder/ &&
gradle clean build -p ../spPayment/ &&
gradle clean build -p ../spWeb/ &&
gradle clean build -p ../sbAdmin/