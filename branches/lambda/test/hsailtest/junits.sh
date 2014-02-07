. ../../env.sh

export TESTS=" \
    hsailtest.FloatSquaresFuncJUnit \
    hsailtest.IntSquaresFuncJUnit \
    hsailtest.IntMaxJUnit \
    hsailtest.IntMinJUnit \
    hsailtest.SqrtJUnit \
    hsailtest.OopArray2DJUnit \
    hsailtest.InitBodyJUnit \
    hsailtest.FieldIntSquaresJUnit \
    hsailtest.IntFieldAssignJUnit \
    hsailtest.DoubleSquaresJUnit \
    hsailtest.OddEvenJUnit \
    hsailtest.OddEvenFuncJUnit \
    hsailtest.TernaryOddEvenJUnit \
    hsailtest.StringContainsJUnit \
    hsailtest.StringCharAtJUnit \
    hsailtest.IntArray2DJUnit \
    hsailtest.StringHashCodeJUnit \
    hsailtest.StringLenJUnit \
    hsailtest.StringIndexOfJUnit \
    hsailtest.HypotJUnit \
    hsailtest.MandelJUnit \
    hsailtest.DickensJUnit \
    hsailtest.MatchedStringJUnit \
    hsailtest.CharArrayStateMachineJUnit \
    hsailtest.CharAddJUnit \
    hsailtest.SinCosJUnit \
    hsailtest.IntVectorMultiplyAddJUnit \
    hsailtest.OopPointsJUnit \
    hsailtest.OopPointsDefaultFieldsNoAccessorsJUnit \
    hsailtest.StaticFieldAccess"

export JARS="$JARS:../../samples/common/common.jar"
export JARS="$JARS:hsailtest.jar"
export JARS="$JARS:.libs/junit-4.10.jar"

export CLASS=org.junit.runner.JUnitCore

$JAVA -DshowUI=false -Dcom.amd.aparapi.enableShowGeneratedHSAIL=false -classpath $JARS $CLASS $TESTS

