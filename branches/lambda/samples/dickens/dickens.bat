setlocal 
call ..\..\env.bat
%JAVA% ^
  -Dcom.amd.aparapi.enableShowGeneratedHSAIL=true ^
  -Dcom.amd.aparapi.enableShowGeneratedHSAILAndExit=false ^
  -classpath %JARS%;../common/common.jar;dickens.jar ^
  com.amd.aparapi.sample.dickens.Dickens
endlocal
