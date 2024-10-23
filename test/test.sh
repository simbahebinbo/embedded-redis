#!/bin/bash

IMAGE_VERSION=3.9.9-amazoncorretto-21-al2023

git clone git@github.com:simbahebinbo/embedded-redis.git
cd embedded-redis

docker run -it --rm --name test-linux-amd64 -v "$(pwd)":/usr/src/mymaven -w /usr/src/mymaven maven:${IMAGE_VERSION} mvn clean package

cd ..
rm -rf embedded-redis
