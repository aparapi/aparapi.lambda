setlocal 
set PATH=
set HSA_RUNTIME=1
set GPU_BLIT_ENGINE_TYPE=2
set ENABLE64=1
set APARAPI_HOME=C:\Users\user1\aparapi\branches\lambda
set OKRA_HOME=C:\Users\user1\okra
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0
set PATH=%OKRA_HOME%\hsa\bin\x86_64
"%JAVA_HOME%\bin\java" ^
 -agentpath:../../com.amd.aparapi.jni/dist/aparapi_x86_64.dll ^
 -Ddispatch=true ^
 -XX:-UseCompressedOops ^
 -Djava.library.path=%OKRA_HOME%\dist\bin;%OKRA_HOME%\hsa\bin\x86_64 ^
 -Dcom.amd.aparapi.useAgent=true ^
 -Dcom.amd.aparapi.executionMode=%1 ^
 -Dcom.amd.aparapi.logLevel=OFF^
 -Dcom.amd.aparapi.enableVerboseJNI=false ^
 -Dcom.amd.aparapi.enableProfiling=false ^
 -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true ^
 -Dcom.amd.aparapi.enableVerboseJNIOpenCLResourceTracking=false ^
 -Dcom.amd.aparapi.dumpFlags=true ^
 -Dcom.amd.aparapi.enableInstructionDecodeViewer=false ^
 -classpath ../../com.amd.aparapi/dist/aparapi.jar;mandel.jar;%OKRA_HOME%/dist/okra.jar ^
 com.amd.aparapi.sample.mandel.LambdaMain
endlocal 

