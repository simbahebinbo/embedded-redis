#!/bin/bash

# 根据 redis 源码，编译出可执行文件

set -e

REDIS_VERSION=7.4.1
REDIS_TARBALL="redis-${REDIS_VERSION}.tar.gz"
REDIS_URL="https://download.redis.io/releases/${REDIS_TARBALL}"

echo ${ARCH}

function copy_openssl_and_remove_dylibs() {
  # To make macOS builds more portable, we want to statically link OpenSSL,
  # which is not straightforward. To force static compilation, we copy
  # the openssl libraries and remove dylibs, forcing static linking
  OPENSSL_HOME="${1}"
  ARCH=${2}
  OPENSSL_HOME_COPY="${3}/${ARCH}"

  echo "*** Copying openssl libraries for static linking"
  cp -RL "${OPENSSL_HOME}" "${OPENSSL_HOME_COPY}"
  rm -f "${OPENSSL_HOME_COPY}"/lib/*.dylib
}

if [ "$(dirname ${0})" != "." ]; then
  echo "This script must be run from $(dirname ${0}). \`cd\` there and run again"
  exit 1
fi

if ! [ -f "${REDIS_TARBALL}" ]; then
  curl -o "${REDIS_TARBALL}" "${REDIS_URL}"
fi

all_linux=0
if command -pv docker buildx 2>/dev/null; then
  for arch in amd64 arm64; do

    builder_name="embedded-redis-builder-$RANDOM"

    docker buildx create \
      --name "$builder_name" \
      --platform linux/${arch}

    docker buildx use "$builder_name"

    echo "*** Building redis version ${REDIS_VERSION} for linux-${arch}"

    set +e

    docker buildx build \
      "--platform=linux/${arch}" \
      --build-arg "REDIS_VERSION=${REDIS_VERSION}" \
      --build-arg "ARCH=${arch}" \
      -t "redis-server-builder-${arch}" \
      --load \
      .

    if [[ $? -ne 0 ]]; then
      echo "*** ERROR: could not build for linux-${arch}"
      continue
    fi

    set -e

    docker buildx rm "$builder_name"

    docker run -it --rm \
      "--platform=linux/${arch}" \
      -v "$(pwd)/":/mnt \
      --user "$(id -u):$(id -g)" \
      "redis-server-builder-${arch}" \
      sh -c "cp /build/redis-server-${REDIS_VERSION}-linux-${arch} /mnt && cp /build/redis-sentinel-${REDIS_VERSION}-linux-${arch} /mnt && cp /build/redis-cli-${REDIS_VERSION}-linux-${arch} /mnt"

    ((all_linux+=1))

  done
else
  echo "*** WARNING: No docker command found or docker does not support buildx. Cannot build for linux."
fi

if [[ "${all_linux}" -lt 2 ]]; then
  echo "*** WARNING: was not able to build for all linux arches; see above for errors"
fi

# To build for macOS, you must be running this script from a Mac. The script requires that openssl
# be installed via Homebrew.
#
# To build Redis binaries for both arm64 and x86_64, you'll need to run this script from an arm64
# Mac with _two_ parallel installations of Homebrew (see
# https://stackoverflow.com/questions/64951024/how-can-i-run-two-isolated-installations-of-homebrew),
# and install openssl with each.
# arm64 /opt/homebrew/bin/brew
# x86_64 /usr/local/homebrew/bin/brew
if [[ "$(uname -s)" == "Darwin" ]]; then

  tar zxf "${REDIS_TARBALL}"
  cd "redis-${REDIS_VERSION}"

  # temporary directory for openssl libraries for static linking.
  # assumes standard Homebrew openssl install:
  #   - arm64 at /opt/homebrew/opt/openssl
  #   - x86_64 at /usr/local/homebrew/opt/openssl
  OPENSSL_TEMP=$(mktemp -d /tmp/embedded-redis-darwin-openssl.XXXXX)

  # build for arm64 on apple silicon
  if arch -arm64 true 2>/dev/null; then
    if [ -d /opt/homebrew/opt/openssl ]; then
      copy_openssl_and_remove_dylibs /opt/homebrew/opt/openssl arm64 "${OPENSSL_TEMP}"
      echo "*** Building redis version ${REDIS_VERSION} for darwin-arm64 (apple silicon)"
      make distclean
      arch -arm64 make -j3 BUILD_TLS=yes OPENSSL_PREFIX="$OPENSSL_TEMP/arm64"
      mv src/redis-server "../redis-server-${REDIS_VERSION}-darwin-arm64"
      mv src/redis-sentinel "../redis-sentinel-${REDIS_VERSION}-darwin-arm64"
      mv src/redis-cli "../redis-cli-${REDIS_VERSION}-darwin-arm64"
    else
      echo "*** WARNING: openssl not found for darwin-arm64; skipping build"
    fi
  else
    echo "*** WARNING: could not build for darwin-arm64; you probably want to do this on an apple silicon device"
  fi

  # build for x86_64 if we're on apple silicon or a recent macos on x86_64
  if arch -x86_64 true 2>/dev/null; then
    if [ -d /usr/local/homebrew/opt/openssl ]; then
      copy_openssl_and_remove_dylibs /usr/local/homebrew/opt/openssl x86_64 "${OPENSSL_TEMP}"
      echo "*** Building redis version ${REDIS_VERSION} for darwin-x86_64"
      make distclean
      arch -x86_64 make -j3 BUILD_TLS=yes OPENSSL_PREFIX="$OPENSSL_TEMP/x86_64"
      # x86_64 and amd64 are effectively synonymous; we use amd64 here to match the naming scheme used by Docker builds
      mv src/redis-server "../redis-server-${REDIS_VERSION}-darwin-amd64"
      mv src/redis-sentinel "../redis-sentinel-${REDIS_VERSION}-darwin-amd64"
      mv src/redis-cli "../redis-cli-${REDIS_VERSION}-darwin-amd64"
    else
        echo "*** WARNING: openssl not found for darwin-x86_64; skipping build"
    fi
  else
    echo "*** WARNING: you are on a version of macos that lacks /usr/bin/arch, you probably do not want this"
    exit 1
  fi
  cd ..
  rm -rf redis-${REDIS_VERSION}
  echo "build for macos/darwin done"
else
  echo "*** WARNING: Cannot build for macos/darwin on a $(uname -s) host"
fi

ls -l redis-server-*
ls -l redis-sentinel-*
ls -l redis-cli-*
