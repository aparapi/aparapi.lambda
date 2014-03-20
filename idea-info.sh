. ./env.sh > /dev/null

clear
echo "ENV"
echo "  HSA_RUNTIME=${HSA_RUNTIME}"
echo "  PATH+=${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64"
echo "  LD_LIBRARY_PATH+=${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64:${OCL_LIB}"
echo 
echo "JVM opts"
echo " -Xmx2G"
echo " -XX:-UseCompressedOops"
echo "  -agentpath:${APARAPI_JNI_HOME}/${APARAPI_AGENT_NAME}"
echo "  -Djava.library.path=${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64"
echo 

