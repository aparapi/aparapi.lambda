SETLOCAL 
set PATH=%PATH%;ffmpeg\ffmpeg-git-9c2651a-win64-shared\bin
set PATH=%PATH%;jjmpeg\jjmpeg-0.0\native\mswin-amd64
set PATH=%PATH%;..\..\..\..\trunk\com.amd.aparapi.jni\dist
java -Dcom.amd.aparapi.enableProfiling=false  -classpath jjmpeg\jjmpeg-0.0\dist\jjmpeg.jar;..\..\..\..\trunk\com.amd.aparapi\dist\aparapi.jar;movie.jar;..\jviolajones\jvj.jar;..\jviolajones\jdom.jar com.amd.aparapi.sample.jjmpeg.PureJava

