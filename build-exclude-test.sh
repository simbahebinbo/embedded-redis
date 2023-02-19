#!/usr/bin/env bash

CurrentDir=$(dirname $0)

find ${CurrentDir} -name "target" | xargs rm -rf
mvn clean package -DskipTests




