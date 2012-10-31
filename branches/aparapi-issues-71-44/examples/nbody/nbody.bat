@echo off

  REM -Djava.library.path=..\..\com.amd.aparapi.jni\dist;jogamp ^
java ^
  -Djava.library.path=jogamp ^
  -Dcom.amd.aparapi.executionMode=%1 ^
  -Dcom.amd.aparapi.enableProfiling=false ^
  -Dbodies=%2 ^
  -Dheight=600 ^
  -Dwidth=600 ^
  -classpath jogamp\gluegen-rt.jar;jogamp\jogl.all.jar;..\..\com.amd.aparapi\dist\aparapi.jar;nbody.jar ^
  com.amd.aparapi.examples.nbody.Main 


