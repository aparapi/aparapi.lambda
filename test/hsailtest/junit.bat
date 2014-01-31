setlocal 
call ..\..\env.bat
%JAVA% ^
 -Dcom.amd.aparapi.logLevel=OFF^
 -Dcom.amd.aparapi.dumpFlags=false ^
 -classpath %JARS%;../../samples/common/common.jar;hsailtest.jar;.libs/junit-4.10.jar ^
 org.junit.runner.JUnitCore ^
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
    hsailtest.HypotJUnit
endlocal

