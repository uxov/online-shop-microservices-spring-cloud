#!/bin/bash

set -e

cd "$(dirname "$0")"

mkdir -p ../logs &&
gradle clean build -p ../spCommon/ &&

cd ./build-and-run/

sh eureka.sh;
sh gateway.sh;

sh spUser.sh;
sh authServer.sh;
sh spProduct.sh;
sh spOrder.sh;
sh spPayment.sh;

sh spWeb.sh;
sh sbAdmin.sh;

