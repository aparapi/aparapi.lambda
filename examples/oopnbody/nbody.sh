#  -Dcom.amd.aparapi.executionMode=$1 \

java -ea \
  -Djava.library.path=../../com.amd.aparapi.jni/dist:jogamp \
  -Dcom.amd.aparapi.logLevel=INFO \
  -Dcom.amd.aparapi.enableShowGeneratedOpenCL=true \
  -Dbodies=$1 \
  -Dheight=800 \
  -Dwidth=1200 \
  -classpath jogamp/jogl-all.jar:jogamp/gluegen-rt.jar:../../com.amd.aparapi/dist/aparapi.jar:oopnbody.jar \
  com.amd.aparapi.examples.oopnbody.Main 

