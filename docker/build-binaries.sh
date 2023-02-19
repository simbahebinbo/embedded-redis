#!/bin/bash

REDIS_VERSION=7.0.8

#下载镜像
docker build --build-arg REDIS_VERSION=${REDIS_VERSION} -t redis-binaries-builder-amd64 -f ./build-binaries-amd64.docker .
docker build --build-arg REDIS_VERSION=${REDIS_VERSION} -t redis-binaries-builder-x86   -f ./build-binaries-x86.docker .


# 编译linux x86版本
docker run -it --rm \
		-v "$(pwd)/":/redis-binaries \
		redis-binaries-builder-x86 \
		sh -c "cd redis-${REDIS_VERSION}; make CC='gcc -static' CFLAGS='-m32' LDFLAGS='-m32 -s' MALLOC='libc'; cp src/redis-server /redis-binaries/redis-server-${REDIS_VERSION}-32; cp src/redis-sentinel /redis-binaries/redis-sentinel-${REDIS_VERSION}-32; cp src/redis-cli /redis-binaries/redis-cli-${REDIS_VERSION}-32"

# 编译linux amd64版本
docker run -it --rm \
		-v "$(pwd)/":/redis-binaries \
		redis-binaries-builder-amd64 \
		sh -c "cd redis-${REDIS_VERSION}; make CC='gcc -static' LDFLAGS='-s' MALLOC='libc'; cp src/redis-server /redis-binaries/redis-server-${REDIS_VERSION}; cp src/redis-sentinel /redis-binaries/redis-sentinel-${REDIS_VERSION}; cp src/redis-cli /redis-binaries/redis-cli-${REDIS_VERSION}"

# 编译 macos 64 版本
#下载redis源码
wget https://download.redis.io/releases/redis-${REDIS_VERSION}.tar.gz
tar zxf redis-${REDIS_VERSION}.tar.gz
cd redis-${REDIS_VERSION}
make
cp src/redis-server ../redis-server-${REDIS_VERSION}.app
cp src/redis-sentinel ../redis-sentinel-${REDIS_VERSION}.app
cp src/redis-cli ../redis-cli-${REDIS_VERSION}.app
cd ..
rm -rf redis-${REDIS_VERSION}
rm -rf redis-${REDIS_VERSION}.tar.gz

