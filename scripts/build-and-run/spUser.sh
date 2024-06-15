#!/bin/bash

set -e

port=9200
project_name=spUser

cd "$(dirname "$0")"

gradle clean build -p ../../$project_name/ &&
lsof -i:$port -t -sTCP:LISTEN | xargs -r kill &&
(nohup java -jar ../../$project_name/build/libs/$project_name-0.0.1-SNAPSHOT.jar >../../logs/$project_name.log 2>&1 &) &&
echo ">>>>>> run $project_name"
