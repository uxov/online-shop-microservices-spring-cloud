#!/bin/bash

set -e

project_name=spCommon

cd "$(dirname "$0")"

gradle clean build -p ../../$project_name/ &&
echo ">>>>>> build $project_name successful"
