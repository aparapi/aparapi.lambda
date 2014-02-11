set HSA_RUNTIME=1
set GPU_BLIT_ENGINE_TYPE=2
set ENABLE64=1
set AMD_APP_HOME=C:\Program Files (x86)\AMD APP
set ENV_DIR=%~dp0
set APARAPI_HOME=%ENV_DIR%
set ANT_HOME=C:\Users\user1\apache-ant-1.9.2
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0
set OKRA_HOME=C:\Users\user1\okra
if exist %APARAPI_HOME%\aparapi.jar set APARAPI_JNI_HOME=%APARAPI_HOME%
if exist %APARAPI_HOME%\aparapi.jar set APARAPI_JAR_HOME=%APARAPI_HOME%
if not exist %APARAPI_HOME%\aparapi.jar set APARAPI_JNI_HOME=%APARAPI_HOME%\com.amd.aparapi.jni\dist
if not exist %APARAPI_HOME%\aparapi.jar set APARAPI_JAR_HOME=%APARAPI_HOME%\com.amd.aparapi\dist
echo APARAPI_JNI_HOME=%APARAPI_JNI_HOME%
echo APARAPI_JAR_HOME=%APARAPI_JAR_HOME%
set PATH=
set PATH=%PATH%;%JAVA_HOME%\bin
set PATH=%PATH%;%AMD_APP_HOME%\bin\x86_64
set PATH=%PATH%;%ANT_HOME%\bin
set PATH=%PATH%;c:\Users\user1\okra\hsa\bin\x86_64
echo PATH=%PATH%
set JVM_OPTS=
set JVM_OPTS=%JVM_OPTS% -Ddispatch=true
set JVM_OPTS=%JVM_OPTS% -XX:-UseCompressedOops
set JVM_OPTS=%JVM_OPTS% -agentpath:%APARAPI_JNI_HOME%\aparapi_x86_64.dll
set JVM_OPTS=%JVM_OPTS% -Djava.library.path=%OKRA_HOME%\dist\bin;%OKRA_HOME%\hsa\bin\x86_64 
set JARS=%APARAPI_JAR_HOME%\aparapi.jar;%OKRA_HOME%\dist\okra.jar

