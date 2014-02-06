setlocal 
call ..\..\env.bat
set JARS=%JARS%;../../samples/common/common.jar
set JARS=%JARS%;hsailtest.jar
set JARS=%JARS%;.libs/junit-4.10.jar

set CLASS=org.junit.runner.JUnitCore 

%JAVA% -DshowUI=false -Dcom.amd.aparapi.enableShowGeneratedHSAIL=false -classpath %JARS% %CLASS% hsailtest.%1%JUnit
endlocal

