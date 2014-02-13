setlocal 
call ..\..\env.bat

set JARS=%JARS%;../../samples/common/common.jar
set JARS=%JARS%;hsailtest.jar
set JARS=%JARS%;.libs/junit-4.10.jar

set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.logLevel=OFF
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.enableVerboseJNI=false 
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.dumpFlags=true 
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.enableShowGeneratedHSAIL=false

java -classpath %JARS%  hsailtest.%1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal


