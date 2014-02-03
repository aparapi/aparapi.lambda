setlocal 
call ..\..\env.bat
%JAVA% -classpath %JARS%;mandel.jar com.amd.aparapi.sample.mandel.LambdaMain
endlocal 

