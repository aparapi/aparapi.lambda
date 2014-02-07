setlocal 
call ../../env.bat
%JAVA% -classpath %JARS%;../common/common.jar;mandel.jar com.amd.aparapi.sample.mandel.HSAILMandel
endlocal


