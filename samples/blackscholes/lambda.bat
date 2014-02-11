setlocal 
call ../../env.bat

set JARS=%JARS%;blackscholes.jar
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.executionMode=%1
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true

java %JVM_OPTS% -classpath %JARS% com.amd.aparapi.samples.blackscholes.LambdaMain
endlocal

