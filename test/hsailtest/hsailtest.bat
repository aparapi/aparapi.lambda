setlocal 
call ..\..\env.bat

set JARS=%JARS%;../../samples/common/common.jar
set JARS=%JARS%;hsailtest.jar
set JARS=%JARS%;.libs/junit-4.10.jar

%JAVA% ^
 -Dcom.amd.aparapi.logLevel=OFF ^
 -Dcom.amd.aparapi.enableVerboseJNI=false ^
 -Dcom.amd.aparapi.dumpFlags=false ^
 -classpath %JARS% ^
 hsailtest.%1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal


