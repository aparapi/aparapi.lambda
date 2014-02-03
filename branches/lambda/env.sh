export HSA_RUNTIME=1
export GPU_BLIT_ENGINE_TYPE=2
export ENABLE64=1
export ANT_HOME=<your path here>
export APARAPI_HOME=<your path here>
export JAVA_HOME=<your path here>
export OKRA_HOME=<yor path here>
export PATH=${PATH}:${ANT_HOME}/bin:${OKRA_HOME}/bin/x86_64
export JVM_OPTS=-Ddispatch=true -XX:-UseCompressedOops -agentpath:${APARAPI_HOME}/com.amd.aparapi.jni/dist/libaparapi_x86_64.so -Djava.library.path=${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64 
export JARS=${APARAPI_HOME}/com.amd.aparapi/dist/aparapi.jar:${OKRA_HOME}/dist/okra.jar
export JAVA="${JAVA_HOME}/bin/java" ${JVM_OPTS}
