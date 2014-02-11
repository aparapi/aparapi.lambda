setlocal 
call ../../env.bat

set JARS=%JARS%;info.jar

java %JVM_OPTS% -classpath %JARS% com.amd.aparapi.sample.info.Main
endlocal

