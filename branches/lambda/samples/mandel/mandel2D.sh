#-agentpath:../../com.amd.aparapi.jni/dist/libaparapi_x86_64.dyLib
java\
 -agentpath:../../com.amd.aparapi.jni/dist/libaparapi_x86_64.so\
 -Dcom.amd.aparapi.executionMode=$1\
 -classpath ../../com.amd.aparapi/dist/aparapi.jar:mandel.jar\
 com.amd.aparapi.sample.mandel.Main2D
