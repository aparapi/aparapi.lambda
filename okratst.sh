export OKRA=/home/gfrost/okra/okra/dist
export PATH=${PATH}:${OKRA}/bin
export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${OKRA}/bin
java -agentpath:com.amd.aparapi.jni/dist/libaparapi_x86_64.so -cp com.amd.aparapi/dist/aparapi.jar:${OKRA}/okra.jar $1
