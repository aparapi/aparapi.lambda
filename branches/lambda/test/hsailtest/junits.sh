. ../../env.sh

export TESTS=" \
    FloatSquaresFunc\
    IntSquaresFunc\
    IntMax\
    IntMin\
    Sqrt\
    OopArray2D\
    InitBody\
    FieldIntSquares\
    IntFieldAssign\
    DoubleSquares\
    OddEven\
    OddEvenFunc\
    TernaryOddEven\
    StringContains\
    StringCharAt\
    IntArray2D\
    StringHashCode\
    StringLen\
    StringIndexOf\
    Hypot\
    Mandel\
    Dickens\
    MatchedString\
    CharArrayStateMachine\
    CharAdd\
    SinCos\
    IntVectorMultiplyAdd\
    OopPoints\
    OopPointsDefaultFieldsNoAccessors\
    StaticFieldAccess"

export JARS="${JARS}:../../samples/common/common.jar"
export JARS="${JARS}:hsailtest.jar"
export JARS="${JARS}:.libs/junit-4.10.jar"

export JVM_OPTS="${JVM_OPTS} -DshowUI=false"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableShowGeneratedHSAIL=false"

export CLASS=hsailtest.JUnitTester
 
java ${JVM_OPTS} -classpath ${JARS} ${CLASS} ${TESTS}
