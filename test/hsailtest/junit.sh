. ../../env.sh
#export OKRA_HSA_NON_COHERENT=1
export JARS="${JARS}:../../samples/common/common.jar"
export JARS="${JARS}:hsailtest.jar"
export JARS="${JARS}:.libs/junit-4.10.jar"

export JVM_OPTS="${JVM_OPTS} -DshowUI=false"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableShowGeneratedHSAIL=false"
export JVM_OPTS="${JVM_OPTS} -Dcom.amd.aparapi.enableSetOKRACoherence=true"

export CLASS=hsailtest.JUnitTester
 
java ${JVM_OPTS} -classpath ${JARS} ${CLASS} ${1}
