setlocal 
call ../../env.bat

set JARS=%JARS%;convolution.jar
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.dumpFlags=true 
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.executionMode=%1
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.enableShowGeneratedHSAILAndExit=false

java %JVM_OPTS% -classpath %JARS% com.amd.aparapi.sample.convolution.ConvolutionOpenCL %2
endlocal

