@echo off
setlocal 
call ../../env.bat

set JARS=%JARS%;javaonedemo.jar
set JARS=%JARS%;..\common\common.jar

java %JVM_OPTS% -classpath %JARS% com.amd.aparapi.examples.javaonedemo.Life
endlocal


