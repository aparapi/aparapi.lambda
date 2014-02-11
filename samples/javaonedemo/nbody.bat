@echo off
setlocal 
call ../../env.bat

set JARS=%JARS%;javaonedemo.jar
set JARS=%JARS%;..\common\common.jar
set JARS=%JARS%;..\third-party\jogamp\gluegen-rt.jar
set JARS=%JARS%;..\third-party\jogamp\jogl-all.jar
set JVM_OPTS=%JVM_OPTS% -Djava.library.path=..\third-party\jogamp\windows-%PROCESSOR_ARCHITECTURE%

java %JVM_OPTS% -classpath %JARS% com.amd.aparapi.examples.javaonedemo.NBody
endlocal


