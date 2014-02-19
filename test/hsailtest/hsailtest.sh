. ../../env.sh 
export OKRA_HSA_NON_COHERENCE=1
export JARS="${JARS} ../../samples/common/common.jar"
export JARS="${JARS} hsailtest.jar"

java ${JVM_OPTS} -classpath ${JARS} hsailtest.${1}
