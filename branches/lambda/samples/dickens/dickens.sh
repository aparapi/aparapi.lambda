#!/bin/sh
. ../../env.sh
export JARS="${JARS}:dickens.jar"
export JARS="${JARS}:../common/common.jar"

export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableSetOKRACoherence=false"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.useAgent=true"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.executionMode=${1}"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableVerboseJNI=false"

java ${JVM_OPTS} -classpath ${JARS} com.amd.aparapi.sample.dickens.Dickens
