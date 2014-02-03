set PATH=
set HSA_RUNTIME=1
set GPU_BLIT_ENGINE_TYPE=2
set ENABLE64=1
set ANT_HOME=c:\Users\user1\apache-ant-1.9.2
set APARAPI_HOME=C:\Users\user1\aparapi\branches\lambda
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0
set OKRA_HOME=C:\Users\user1\okra
set PATH=%ANT_HOME%\bin;c:\Users\user1\okra\hsa\bin\x86_64
set JVM_OPTS=-Ddispatch=true -XX:-UseCompressedOops -agentpath:%APARAPI_HOME%\com.amd.aparapi.jni\dist\aparapi_x86_64.dll -Djava.library.path=%OKRA_HOME%\dist\bin;%OKRA_HOME%\hsa\bin\x86_64 
set JARS=%APARAPI_HOME%\com.amd.aparapi\dist\aparapi.jar;%OKRA_HOME%\dist\okra.jar
set JAVA="%JAVA_HOME%/bin/java" %JVM_OPTS%

