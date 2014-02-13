setlocal 
call ..\..\env.bat
set TESTS= ^
    hsailtest.FloatSquaresFuncJUnit ^
    hsailtest.IntSquaresFuncJUnit ^
    hsailtest.IntMaxJUnit ^
    hsailtest.IntMinJUnit ^
    hsailtest.SqrtJUnit ^
    hsailtest.OopArray2DJUnit ^
    hsailtest.InitBodyJUnit ^
    hsailtest.FieldIntSquaresJUnit ^
    hsailtest.IntFieldAssignJUnit ^
    hsailtest.DoubleSquaresJUnit ^
    hsailtest.OddEvenJUnit ^
    hsailtest.OddEvenFuncJUnit ^
    hsailtest.TernaryOddEvenJUnit ^
    hsailtest.StringContainsJUnit ^
    hsailtest.StringCharAtJUnit ^
    hsailtest.IntArray2DJUnit ^
    hsailtest.StringHashCodeJUnit ^
    hsailtest.StringLenJUnit ^
    hsailtest.StringIndexOfJUnit ^
    hsailtest.HypotJUnit ^
    hsailtest.MandelJUnit ^
    hsailtest.DickensJUnit ^
    hsailtest.MatchedStringJUnit ^
    hsailtest.CharArrayStateMachineJUnit ^
    hsailtest.CharAddJUnit ^
    hsailtest.SinCosJUnit ^
    hsailtest.IntVectorMultiplyAddJUnit ^
    hsailtest.StaticFieldAccessJUnit ^
    hsailtest.OopPointsDefaultFieldsNoAccessorsJUnit ^
    hsailtest.OopPointsJUnit 

set JARS=%JARS%;../../samples/common/common.jar
set JARS=%JARS%;hsailtest.jar
set JARS=%JARS%;.libs/junit-4.10.jar

set JVM_OPTS=%JVM_OPTS% -DshowUI=false
set JVM_OPTS=%JVM_OPTS% -Dcom.amd.aparapi.enableShowGeneratedHSAIL=false

set CLASS=org.junit.runner.JUnitCore 

java %JVM_OPTS% -classpath %JARS% %CLASS% %TESTS%
endlocal

