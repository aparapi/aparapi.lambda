. ./setvars.bash
${JAVA_HOME}/bin/java \
    -classpath aparapi.jar:samples.jar \
    com.amd.aparapi.samples.$1 $2 $3 $4 $6 $7 $8 $9 
