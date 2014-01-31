setlocal 
call env.bat
%JAVA% -Dbodies=8192 -classpath %JARS%;samples\common\common.jar;test\hsailtest\hsailtest.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal

