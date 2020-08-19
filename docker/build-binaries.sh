#!/bin/bash

REDIS_VERSION=6.0.6

docker build --build-arg REDIS_VERSION=${REDIS_VERSION} -t redis-binaries-builder-amd64 -f ./build-binaries-amd64.docker .
docker build --build-arg REDIS_VERSION=${REDIS_VERSION} -t redis-binaries-builder-x86   -f ./build-binaries-x86.docker .

docker run -it --rm \
		-v "$(pwd)/":/redis-binaries \
		redis-binaries-builder-x86 \
		sh -c "cd redis-${REDIS_VERSION}; make CC='gcc -static' CFLAGS='-m32' LDFLAGS='-m32 -s' MALLOC='libc'; cp src/redis-server /redis-binaries/redis-server-${REDIS_VERSION}-32; cp src/redis-sentinel /redis-binaries/redis-sentinel-${REDIS_VERSION}-32; cp src/redis-cli /redis-binaries/redis-cli-${REDIS_VERSION}-32"

docker run -it --rm \
		-v "$(pwd)/":/redis-binaries \
		redis-binaries-builder-amd64 \
		sh -c "cd redis-${REDIS_VERSION}; make CC='gcc -static' LDFLAGS='-s' MALLOC='libc'; cp src/redis-server /redis-binaries/redis-server-${REDIS_VERSION}; cp src/redis-sentinel /redis-binaries/redis-sentinel-${REDIS_VERSION}; cp src/redis-cli /redis-binaries/redis-cli-${REDIS_VERSION}"

