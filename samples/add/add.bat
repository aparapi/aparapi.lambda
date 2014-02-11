setlocal 
call ../../env.bat

set JARS=%JARS%;add.jar 

java %JVM_OPTS% -classpath %JARS% com.amd.aparapi.sample.add.Main
endlocal 

