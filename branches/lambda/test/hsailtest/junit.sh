. ../../env.sh

export JARS="$JARS:../../samples/common/common.jar"
export JARS="$JARS:hsailtest.jar"
export JARS="$JARS:.libs/junit-4.10.jar"

export CLASS=org.junit.runner.JUnitCore
 
$JAVA -DshowUI=false -Dcom.amd.aparapi.enableShowGeneratedHSAIL=false -classpath $JARS $CLASS hsailtest.${1}JUnit
