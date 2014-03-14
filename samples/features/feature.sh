#!/bin/sh
. ../../env.sh
export JARS="${JARS}:features.jar"

java ${JVM_OPTS} -classpath ${JARS} com.amd.aparapi.sample.features.${1}
