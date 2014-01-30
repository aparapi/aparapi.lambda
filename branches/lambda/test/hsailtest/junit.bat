setlocal 
set PATH=
set HSA_RUNTIME=1
set GPU_BLIT_ENGINE_TYPE=2
set ENABLE64=1
set APARAPI_HOME=C:\Users\user1\aparapi\branches\lambda
set OKRA_HOME=C:\Users\user1\okra
set PATH=%OKRA_HOME%\hsa\bin\x86_64;c:\Program Files\Java\jdk1.8.0\bin

java ^
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
 -Dcom.amd.aparapi.enableAlwaysCreateFakeLocalVariableTable=true ^
 -Dcom.amd.aparapi.enableVerboseJNIOpenCLResourceTracking=false ^
 -Dcom.amd.aparapi.dumpFlags=true ^
 -Dcom.amd.aparapi.enableInstructionDecodeViewer=false ^
 -classpath ../../samples/common/common.jar;../../com.amd.aparapi/dist/aparapi.jar;hsailtest.jar;%OKRA_HOME%\dist\okra.jar;.libs/junit-4.10.jar ^
 org.junit.runner.JUnitCore ^
    hsailtest.FloatSquaresFuncJUnit ^
    hsailtest.IntSquaresFuncJUnit ^
    hsailtest.IntMaxJUnit ^
    hsailtest.IntMinJUnit ^
    hsailtest.SqrtJUnit ^
    hsailtest.OopArray2DJUnit ^
    hsailtest.InitBodyJUnit ^
    hsailtest.FieldIntSquaresJUnit ^
    hsailtest.IntFieldAssignJUnit ^
    hsailtest.DoubleSquaresJUnit ^
    hsailtest.OddEvenJUnit ^
    hsailtest.TernaryOddEvenJUnit ^
    hsailtest.StringContainsJUnit ^
    hsailtest.StringCharAtJUnit ^
    hsailtest.HypotJUnit
endlocal

