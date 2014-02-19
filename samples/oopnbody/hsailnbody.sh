#!/bin/sh
. ../../env.sh
export JARS="${JARS}:oopnbody.jar"
export JARS="${JARS}:../common/common.jar"

export JVM_OPTS="${JVM_OPTS} -Dmode=${1}"
export JVM_OPTS="${JVM_OPTS} -Dbodies=${2}"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableSetOKRACoherence=${3}"

java ${JVM_OPTS} -classpath ${JARS} com.amd.aparapi.examples.oopnbody.HSAILNBody
