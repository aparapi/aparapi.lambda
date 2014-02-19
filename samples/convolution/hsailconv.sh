#!/bin/sh
. ../../env.sh
export JARS="${JARS}:convolution.jar"

export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.useAgent=true"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.executionMode=${1}"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableVerboseJNI=false"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableSetOKRACoherence=true"

java ${JVM_OPTS} -classpath ${JARS} com.amd.aparapi.sample.convolution.HSAILConvolution

