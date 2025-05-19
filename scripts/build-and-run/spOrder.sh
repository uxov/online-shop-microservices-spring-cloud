#!/bin/bash

set -e

port=9300 
project_name=spOrder

cd "$(dirname "$0")"

gradle clean build -p ../../$project_name/ &&
pgrep -f "$project_name.*jar" | xargs -r kill &&
#lsof -i:$port -t -sTCP:LISTEN | xargs -r kill &&
(nohup java -jar ../../$project_name/build/libs/$project_name-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &) &&
echo ">>>>>> run $project_name"
