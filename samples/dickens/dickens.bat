java ^
 -agentpath:../../com.amd.aparapi.jni/dist/aparapi_x86_64.dll ^
 -Dcom.amd.aparapi.useAgent=true ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.logLevel=OFF^
 -Dcom.amd.aparapi.enableVerboseJNI=false ^
 -Dcom.amd.aparapi.enableProfiling=false ^
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
 -Dcom.amd.aparapi.enableAlwaysCreateFakeLocalVariableTable=true ^
 -Dcom.amd.aparapi.enableVerboseJNIOpenCLResourceTracking=false ^
 -Dcom.amd.aparapi.dumpFlags=true ^
 -Dcom.amd.aparapi.enableInstructionDecodeViewer=false ^
 -classpath ../common/common.jar;../../com.amd.aparapi/dist/aparapi.jar;dickens.jar ^
 com.amd.aparapi.sample.dickens.Dickens


