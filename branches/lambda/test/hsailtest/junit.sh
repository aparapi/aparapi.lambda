. ../../env.sh
export JARS="${JARS}:../../samples/common/common.jar"
export JARS="${JARS}:hsailtest.jar"
export JARS="${JARS}:.libs/junit-4.10.jar"

export JVM_OPTS="${JVM_OPTS} -DshowUI=false"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableShowGeneratedHSAIL=false"

export CLASS=org.junit.runner.JUnitCore
 
java ${JVM_OPTS} -classpath ${JARS} ${CLASS} hsailtest.${1}JUnit
