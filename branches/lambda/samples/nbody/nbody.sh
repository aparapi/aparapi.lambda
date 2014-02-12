#!/bin/sh
. ../../env.sh
export JARS="${JARS}:nbody.jar"
export JARS="${JARS}:../third-party/jogamp/jogl-all.jar"
export JARS="${JARS}:../third-party/jogamp/gluegen-rt.jar"

export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.useAgent=true"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.executionMode=${1}"
export JVM_OPTS="${JVM_OPTS} -Dbodies=${2}"
export JVM_OPTS="${JVM_OPTS} -Dwidth=600"
export JVM_OPTS="${JVM_OPTS} -Dheight=600"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableVerboseJNI=false"

java ${JVM_OPTS} -classpath ${JARS} com.amd.aparapi.examples.nbody.Main

