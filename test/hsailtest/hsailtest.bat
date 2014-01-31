setlocal 
call ..\..\env.bat
%JAVA% ^
 -Dcom.amd.aparapi.logLevel=OFF^
 -Dcom.amd.aparapi.enableVerboseJNI=false ^
 -Dcom.amd.aparapi.dumpFlags=false ^
 -classpath %JARS%;../../samples/common/common.jar;hsailtest.jar;.libs/junit-4.10.jar ^
 hsailtest.%1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal


