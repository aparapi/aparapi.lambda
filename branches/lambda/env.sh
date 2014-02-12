export HSA_RUNTIME=1
echo "HSA_RUNTIME:$HSA_RUNTIME"

export ANT_HOME=/Users/garyfrost/apache-ant-1.9.2
export ANT_HOME=/usr/share/ant
echo "ANT_HOME:${ANT_HOME}"

export APARAPI_HOME=/home/user1/aparapi-lambda
export APARAPI_HOME=/Users/garyfrost/aparapi/aparapi/branches/lambda
echo "APARAPI_HOME:${APARAPI_HOME}"

test -d ${APARAPI_HOME}/com.amd.aparapi.jni/dist && export APARAPI_JNI_HOME=${APARAPI_HOME}/com.amd.aparapi.jni/dist  || export APARAPI_JNI_HOME=${APARAPI_HOME}
test -d ${APARAPI_HOME}/com.amd.aparapi/dist && export APARAPI_JAR_HOME=${APARAPI_HOME}/com.amd.aparapi/dist  || export APARAPI_JAR_HOME=${APARAPI_HOME}

echo "APARAPI_JNI_HOME:${APARAPI_JNI_HOME}"
echo "APARAPI_JAR_HOME:${APARAPI_JAR_HOME}"

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home/
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
echo "JAVA_HOME:${JAVA_HOME}"

export OKRA_HOME=/home/user1/SumatraDemos/okra
echo "OKRA_HOME:${OKRA_HOME}"

export PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64:${PATH}
echo "PATH:$PATH"

export LD_LIBRARY_PATH=${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64:${LD_LIBRARY_PATH}
echo "LD_LIBRARY_PATH:${LD_LIBRARY_PATH}"

export LIBOS=libaparapi_$(uname -m)
case $(uname -s) in 
  Darwin) LIBNAME=${LIBOS}.dyLib;;
  Linux)  LIBNAME=${LIBOS}.so;;
esac
echo libname = ${LIBNAME}


export JVM_OPTS=
export JVM_OPTS="${JVM_OPTS} -Xmx2G"
export JVM_OPTS="${JVM_OPTS} -Ddispatch=true"
export JVM_OPTS="${JVM_OPTS} -XX:-UseCompressedOops"
export JVM_OPTS="${JVM_OPTS} -agentpath:${APARAPI_JNI_HOME}/${LIBNAME}"
export JVM_OPTS="${JVM_OPTS} -Djava.library.path=${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64"
echo "JVM_OPTS:$JVM_OPTS"

export JARS=
export JARS="${JARS}:${APARAPI_JAR_HOME}/aparapi.jar"
export JARS="${JARS}:${OKRA_HOME}/dist/okra.jar"
echo "JARS:${JARS}"
