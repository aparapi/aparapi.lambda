export HSA_RUNTIME=1
echo "HSA_RUNTIME:$HSA_RUNTIME"

export ANT_HOME=/usr/share/ant
echo "ANT_HOME:$ANT_HOME"

export APARAPI_HOME=/home/user1/aparapi-lambda
echo "APARAPI_HOME:$APARAPI_HOME"

export JAVA_HOME=/usr/lib/jvm/java-8-oracle
echo "JAVA_HOME:$JAVA_HOME"

export OKRA_HOME=/home/user1/SumatraDemos/okra
echo "OKRA_HOME:$OKRA_HOME"

export OKRA=/home/user1/SumatraDemos/okra
echo "OKRA:$OKRA"

export PATH=$JAVA_HOME/bin:$ANT_HOME/bin:$OKRA_HOME/dist/bin:$OKRA_HOME/hsa/bin/x86_64:$PATH
echo "PATH:$PATH"

export LD_LIBRARY_PATH=$OKRA_HOME/dist/bin:$OKRA_HOME/hsa/bin/x86_64:$LD_LIBRARY_PATH
echo "LD_LIBRARY_PATH:$LD_LIBRARY_PATH"

export JVM_OPTS="-Xmx2G -Ddispatch=true	-XX:-UseCompressedOops -agentpath:${APARAPI_HOME}/com.amd.aparapi.jni/dist/libaparapi_x86_64.so -Djava.library.path=${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64"
echo "JVM_OPTS:$JVM_OPTS"

export JARS="${APARAPI_HOME}/com.amd.aparapi/dist/aparapi.jar:${OKRA_HOME}/dist/okra.jar"
echo "JARS:$JARS"

export JAVA="${JAVA_HOME}/bin/java ${JVM_OPTS}"
echo "JAVA:$JAVA"
