setlocal 
call ..\..\env.bat
%JAVA% -classpath %JARS%;../common/common.jar;dickens.jar com.amd.aparapi.sample.dickens.Dickens
endlocal
