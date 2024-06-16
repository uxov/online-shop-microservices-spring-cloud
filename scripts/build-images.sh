#!/bin/bash

gradle clean dockerBuildImage -p ../eurekaServer/ &&
gradle clean dockerBuildImage -p ../gateway/ &&
gradle clean dockerBuildImage -p ../authServer/ &&
gradle clean dockerBuildImage -p ../spUser/ &&
gradle clean dockerBuildImage -p ../spProduct/ &&
gradle clean dockerBuildImage -p ../spOrder/ &&
gradle clean dockerBuildImage -p ../spPayment/ &&
gradle clean dockerBuildImage -p ../spWeb/ &&
gradle clean dockerBuildImage -p ../sbAdmin/