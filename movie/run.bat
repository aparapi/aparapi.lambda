SETLOCAL 
set PATH=%PATH%;c:\users\gfrost\ffmpeg-git-9c2651a-win64-shared\bin
set PATH=%PATH%;c:\users\gfrost\jjmpeg-0.0\native\mswin-amd64
set PATH=%PATH%;c:\Users\gfrost\aparapi\branches\AddExtensionMechanism\com.amd.aparapi.jni\dist
javac  -sourcepath src -d classes -classpath ..\dist\jjmpeg.jar;C:\Users\gfrost\aparapi\branches\AddExtensionMechanism\com.amd.aparapi\dist\aparapi.jar src\com\amd\aparapi\sample\jjmpeg\Aparapi.java
java  -classpath ..\dist\jjmpeg.jar;C:\Users\gfrost\aparapi\branches\AddExtensionMechanism\com.amd.aparapi\dist\aparapi.jar;classes com.amd.aparapi.sample.jjmpeg.Aparapi

