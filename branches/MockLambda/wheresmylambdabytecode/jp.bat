setlocal 
call ..\setvars.bat
%LAMBDA_JAVA_HOME%\bin\javap ^
   -classpath classes ^
   -c -v -s -l ^
   Main > Main.bytecode
%LAMBDA_JAVA_HOME%\bin\javap ^
   -classpath classes ^
   -c -v -s -l ^
   Aparapi > Aparapi.bytecode
%LAMBDA_JAVA_HOME%\bin\javap ^
   -classpath classes ^
   -c -v -s -l ^
   Aparapi$SAM > Aparapi$SAM.bytecode
   
endlocal 

