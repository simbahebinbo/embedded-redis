#!/bin/bash

REDIS_VERSION=7.0.8

#下载镜像
docker build --build-arg REDIS_VERSION=${REDIS_VERSION} -t redis-binaries-builder-amd64 -f ./build-binaries-amd64.docker .

# 编译 linux amd64 版本
echo "编译 linux amd64 版本"
docker run -it --rm \
		-v "$(pwd)/":/redis-binaries \
		redis-binaries-builder-amd64 \
		sh -c "cd redis-${REDIS_VERSION}; make BUILD_TLS='yes' CC='gcc -static' LDFLAGS='-s' MALLOC='libc'; cp src/redis-server /redis-binaries/redis-server-${REDIS_VERSION}; cp src/redis-sentinel /redis-binaries/redis-sentinel-${REDIS_VERSION}; cp src/redis-cli /redis-binaries/redis-cli-${REDIS_VERSION}"

# 编译 macos 64 版本
#下载redis源码
echo "编译 macos 64 版本"
echo ${uname -m}
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

