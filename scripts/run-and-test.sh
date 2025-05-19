#!/bin/bash

set -e

cd "$(dirname "$0")"

sh kill-java.sh &&
podman-compose down && podman-compose up -d &&
sh run-jars.sh &&
sh check_eureka_services.sh &&
echo '>>>>>> start testing' &&
gradle clean test -p ../spTest --tests TestAllSuit
