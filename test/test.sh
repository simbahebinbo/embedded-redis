#!/bin/bash

git clone git@github.com:simbahebinbo/embedded-redis.git
cd embedded-redis

docker run -it --rm --name test-linux-amd64 -v "$(pwd)":/usr/src/mymaven -w /usr/src/mymaven maven:3.9.9 mvn clean package

cd ..
rm -rf embedded-redis
