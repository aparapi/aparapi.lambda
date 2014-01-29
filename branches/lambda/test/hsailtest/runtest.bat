setlocal
set HSA_RUNTIME=1
set GPU_BLIT_ENGINE_TYPE=2
set ENABLE64=1
set APARAPI_HOME=C:\Users\user1\aparapi\branches\lambda
set OKRA_HOME=C:\Users\user1\okra
set PATH=%OKRA_HOME%\hsa\bin\x86_64;c:\Program Files\Java\jdk1.8.0\bin;%PATH%
set ANT_OPTS=-agentpath:../../com.amd.aparapi.jni/dist/aparapi_x86_64.dll
ant junit
endlocal
