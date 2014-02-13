setlocal 
call ..\..\env.bat
set JARS=%JARS%;../../samples/common/common.jar
set JARS=%JARS%;hsailtest.jar
set JARS=%JARS%;.libs/junit-4.10.jar
set JVM_OPTS=%JVM_OPTS% -DshowUI=false
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.enableShowGeneratedHSAIL=false

set CLASS=org.junit.runner.JUnitCore 

java %JVM_OPTS% -classpath %JARS% %CLASS% hsailtest.%1%JUnit
endlocal

