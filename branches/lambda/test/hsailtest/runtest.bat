setlocal
set HSA_RUNTIME=1
set GPU_BLIT_ENGINE_TYPE=2
set ENABLE64=1
set ANT_HOME=C:\Users\user1\apache-ant-1.9.2
set APARAPI_HOME=C:\Users\user1\aparapi\branches\lambda
set OKRA_HOME=C:\Users\user1\okra
set PATH=%OKRA_HOME%\hsa\bin\x86_64;c:\Program Files\Java\jdk1.8.0\bin;%ANT_HOME%\bin
ant junit
endlocal
