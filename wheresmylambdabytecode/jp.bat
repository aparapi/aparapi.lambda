setlocal 
call ..\setvars.bat
%LAMBDA_JAVA_HOME%\bin\javap > jphelp

%LAMBDA_JAVA_HOME%\bin\javap ^
   -classpath classes ^
   -c -v -l -p -s ^
   Main > Main.bytecode

%LAMBDA_JAVA_HOME%\bin\javap ^
   -classpath classes ^
   -c  -v -l -p -s ^
   Aparapi > Aparapi.bytecode

%LAMBDA_JAVA_HOME%\bin\javap ^
   -classpath classes ^
   -c  -v -l -p -s ^
   Aparapi$SAM > Aparapi$SAM.bytecode
   
endlocal 

