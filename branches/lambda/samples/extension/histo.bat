setlocal 
call ../../env.bat

set JARS=%JARS%;extension.jar
set JVM_OPTS=%JVM_OPTS% -Xmx1024M
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.dumpFlags=true 
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.executionMode=%1
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.enableShowGeneratedHSAILAndExit=false

java %JVM_OPTS% -classpath %JARS% com.amd.aparapi.sample.extension.Histogram
endlocal

