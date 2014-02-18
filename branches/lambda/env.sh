###################################
# If you create myEnv.sh in this dir it can overide any of these. 

# set APARAPI_HOME to the current directory
export APARAPI_HOME=/home/${LOGNAME}/aparapi-lambda

# set ANT_HOME to the  directory where ant is installed (/bin is beneath here)
export ANT_HOME=/usr/share/ant

# set JAVA_HOME to a java 8 sdk install (/bin and /jre are beneath here)
export JAVA_HOME=/usr/lib/jvm/java-8-oracle

# set OCL_LIB and OCL_INCLUDE to point to your OpenCL SDK installation lib and include dirs (so Aparapi com.amd.aparapi.jni can build)
export OCL_HOME=${APARAPI_HOME}/../ocl
export OCL_LIB=${OCL_HOME}/lib/x86_64
export OCL_INCLUDE=${OCL_HOME}/include

# set OKRA_HOME to your OKRA directory (/dist is underneath here)
export OKRA_HOME=${APARAPI_HOME}/../okra

if test -f ${APARAPI_HOME}/myEnv.sh ; then 
   echo "${APARAPI_HOME}/myEnv.sh content start #########{"
   . ${APARAPI_HOME}/myEnv.sh 
   echo "} ############ ${APARAPI_HOME}/myEnv.sh content end"
fi

###################################
echo "APARAPI_HOME:${APARAPI_HOME}"
echo "ANT_HOME:${ANT_HOME}"
echo "APARAPI_JNI_HOME:${APARAPI_JNI_HOME}"
echo "APARAPI_JAR_HOME:${APARAPI_JAR_HOME}"
echo "JAVA_HOME:${JAVA_HOME}"
echo "OCL_LIB:${OCL_LIB}"
echo "OCL_INCLUDE:${OCL_INCLUDE}"
echo "OKRA_HOME:${OKRA_HOME}"

# This looks odd, but when we create a dist zip the dist has aparapi.jar and {lib}aparapi_x86_64.{dll|dylib|so} at the root
# The following allows this env script to work for either configuration.
test -d ${APARAPI_HOME}/com.amd.aparapi.jni/dist && export APARAPI_JNI_HOME=${APARAPI_HOME}/com.amd.aparapi.jni/dist  || export APARAPI_JNI_HOME=${APARAPI_HOME}
test -d ${APARAPI_HOME}/com.amd.aparapi/dist && export APARAPI_JAR_HOME=${APARAPI_HOME}/com.amd.aparapi/dist  || export APARAPI_JAR_HOME=${APARAPI_HOME}


export PATH=${JAVA_HOME}/bin:${ANT_HOME}/bin:${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64:${PATH}
echo "PATH:$PATH"

export LD_LIBRARY_PATH=${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64:${OCL_LIB}:${LD_LIBRARY_PATH}
echo "LD_LIBRARY_PATH:${LD_LIBRARY_PATH}"

export LIB_ARCH=$(uname -m)
echo "LIB_ARCH:${LIB_ARCH}"
case $(uname -s) in 
  Darwin) LIB_PREFIX=lib;;
  Linux)  LIB_PREFIX=lib;;
esac
case $(uname -s) in 
  Darwin) LIB_SUFFIX=dyLib;;
  Linux)  LIB_SUFFIX=so;;
esac
echo "LIB_SUFFIX:${LIB_SUFFIX}"
echo "LIB_PREFIX:${LIB_PREFIX}"

export HSA_RUNTIME=1
echo "HSA_RUNTIME:${HSA_RUNTIME}"

export JVM_OPTS=
export JVM_OPTS="${JVM_OPTS} -Xmx2G"
export JVM_OPTS="${JVM_OPTS} -Ddispatch=true"
export JVM_OPTS="${JVM_OPTS} -XX:-UseCompressedOops"
export JVM_OPTS="${JVM_OPTS} -agentpath:${APARAPI_JNI_HOME}/${LIB_PREFIX}aparapi_${LIB_ARCH}.${LIB_SUFFIX}"
export JVM_OPTS="${JVM_OPTS} -Djava.library.path=${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64"
echo "JVM_OPTS:$JVM_OPTS"

export JARS=
export JARS="${JARS}:${APARAPI_JAR_HOME}/aparapi.jar"
export JARS="${JARS}:${OKRA_HOME}/dist/okra.jar"
echo "JARS:${JARS}"
