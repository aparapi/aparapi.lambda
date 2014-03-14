setlocal 
call ../../env.bat
set JARS=%JARS%;features.jar
java %JVM_OPTS% -classpath %JARS% com.amd.aparapi.sample.features.%1
endlocal

