#!/usr/bin/env bash

CurrentDir=$(dirname $0)

find ${CurrentDir} -name "target" | xargs rm -rf
mvn spotless:apply
mvn clean package -DskipTests




