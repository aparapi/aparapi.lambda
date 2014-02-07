java \
 -agentpath:../../com.amd.aparapi.jni/dist/libaparapi_x86_64.so  \
 -Ddispatch=true \
 -XX:-UseCompressedOops \
 -Djava.library.path=${OKRA_HOME}/dist/bin:${OKRA_HOME}/hsa/bin/x86_64 \
 -Dcom.amd.aparapi.logLevel=OFF\
 -Dcom.amd.aparapi.dumpFlags=false \
 -classpath ../../samples/common/common.jar:../../com.amd.aparapi/dist/aparapi.jar:hsailtest.jar:${OKRA_HOME}/dist/okra.jar:.libs/junit-4.10.jar \
 org.junit.runner.JUnitCore \
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
    hsailtest.IntVectorMultiplyAddJUnit \
    hsailtest.StaticFieldAccess \
    hsailtest.OopPointsJUnit \
    hsailtest.HypotJUnit
