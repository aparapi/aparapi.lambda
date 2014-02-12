#!/bin/sh
. ../../env.sh
export JARS="${JARS}:javaonedemo.jar"
export JARS="${JARS}:../third-party/jogamp/gluegen-rt.jar"
export JARS="${JARS}:../third-party/jogamp/jogl-all.jar"

export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.useAgent=true"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.executionMode=${1}"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableVerboseJNI=false"
export JVM_OPTS="${JVM_OPTS} -Djava.library.path=../third-party/jogamp"
export JVM_OPTS="${JVM_OPTS} -Dwidth=512"
export JVM_OPTS="${JVM_OPTS} -Dheight=512"

java ${JVM_OPTS} -classpath ${JARS} com.amd.aparapi.examples.javaonedemo.NBody
