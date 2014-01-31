setlocal
call ../../env.bat
set ANT_HOME=C:\Users\user1\apache-ant-1.9.2
set PATH=%OKRA_HOME%\hsa\bin\x86_64;c:\Program Files\Java\jdk1.8.0\bin;%ANT_HOME%\bin
ant junit
endlocal
