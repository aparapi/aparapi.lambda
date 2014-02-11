setlocal 
call ../../env.bat

set JARS=%JARS%;squares.jar
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.executionMode=%1
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true

java %JVM_OPTS% -classpath %JARS% com.amd.aparapi.sample.squares.Main
endlocal

