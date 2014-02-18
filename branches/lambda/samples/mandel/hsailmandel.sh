#!/bin/sh
. ../../env.sh
export JARS="${JARS}:mandel.jar"
export JARS="${JARS}:../common/common.jar"

export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.useAgent=true"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.executionMode=${1}"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableVerboseJNI=false"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableShowGeneratedHSAIL=true"

java ${JVM_OPTS} -classpath ${JARS} com.amd.aparapi.sample.mandel.HSAILMandel
