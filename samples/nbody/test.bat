@echo off
setlocal 
call ../../env.bat

set JARS=%JARS%;nbody.jar
set JARS=%JARS%;..\common\common.jar
set JARS=%JARS%;..\third-party\jogamp\gluegen-rt.jar
set JARS=%JARS%;..\third-party\jogamp\jogl-all.jar
set JVM_OPTS=%JVM_OPTS% -Djava.library.path=..\third-party\jogamp\windows-%PROCESSOR_ARCHITECTURE%
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.executionMode=%1
set JVM_OPTS=%JVM_OPTS% -Dbodies=%2
set JVM_OPTS=%JVM_OPTS% -Dheight=600
set JVM_OPTS=%JVM_OPTS% -Dwidth=600

java %JVM_OPTS% -classpath %JARS% com.amd.aparapi.examples.nbody.Test
endlocal








