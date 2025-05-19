#!/bin/bash

set -e

port=9000
project_name=gateway

cd "$(dirname "$0")"

gradle clean build -p ../../$project_name/ &&
pgrep -f "$project_name.*jar" | xargs -r kill &&
(nohup java -jar ../../$project_name/build/libs/$project_name-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &) &&
echo ">>>>>> run $project_name"
