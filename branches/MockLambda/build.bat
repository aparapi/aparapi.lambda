setlocal 
REM Hack the following lines to setup for your environment
set LAMBDA_JAVA_HOME=C:\Users\gfrost\lambda-8-b45\jdk1.8.0
set AMD_APP_SDK_DIR=C:\Program Files (x86)\AMD APP
set MSVC_DIR=C:\Program Files (x86)\Microsoft Visual Studio 10.0
set MSVC_SDK_DIR=C:\Program Files\Microsoft SDKs\Windows\v7.1
set PATH=%PATH%;%MSVC_DIR%\Common7\IDE
REM no need to edit below here

rm -r -f ^
  aparapi.obj ^
  aparapi.exp ^
  aparapi.jar ^
  aparapi_x86_64.dll ^
  arrayBuffer.obj ^
  clHelper.obj ^
  config.obj ^
  jniHelper.obj ^
  opencljni.obj ^
  profileInfo.obj ^
  classes ^
  include 

mkdir ^
  classes^
  include

%LAMBDA_JAVA_HOME%\bin\javac ^
 -XDlambdaToMethod ^
 -g ^
 -d classes ^
 -sourcepath src\java^
 src\java\com\amd\aparapi\Annotations.java ^
 src\java\com\amd\aparapi\Aparapi.java ^
 src\java\com\amd\aparapi\AparapiException.java ^
 src\java\com\amd\aparapi\BlockWriter.java ^
 src\java\com\amd\aparapi\BranchSet.java ^
 src\java\com\amd\aparapi\ByteBuffer.java ^
 src\java\com\amd\aparapi\ByteReader.java ^
 src\java\com\amd\aparapi\ClassModel.java ^
 src\java\com\amd\aparapi\ClassParseException.java ^
 src\java\com\amd\aparapi\CodeGenException.java ^
 src\java\com\amd\aparapi\Config.java ^
 src\java\com\amd\aparapi\DeprecatedException.java ^
 src\java\com\amd\aparapi\Device.java ^
 src\java\com\amd\aparapi\Entrypoint.java ^
 src\java\com\amd\aparapi\ExpressionList.java ^
 src\java\com\amd\aparapi\Instruction.java ^
 src\java\com\amd\aparapi\InstructionHelper.java ^
 src\java\com\amd\aparapi\InstructionPattern.java ^
 src\java\com\amd\aparapi\InstructionSet.java ^
 src\java\com\amd\aparapi\InstructionTransformer.java ^
 src\java\com\amd\aparapi\InstructionViewer.java ^
 src\java\com\amd\aparapi\JavaDevice.java ^
 src\java\com\amd\aparapi\Kernel.java ^
 src\java\com\amd\aparapi\KernelRunner.java ^
 src\java\com\amd\aparapi\KernelWriter.java ^
 src\java\com\amd\aparapi\MethodModel.java ^
 src\java\com\amd\aparapi\OpenCL.java ^
 src\java\com\amd\aparapi\OpenCLAdapter.java ^
 src\java\com\amd\aparapi\OpenCLArgDescriptor.java ^
 src\java\com\amd\aparapi\OpenCLDevice.java ^
 src\java\com\amd\aparapi\OpenCLJNI.java ^
 src\java\com\amd\aparapi\OpenCLKernel.java ^
 src\java\com\amd\aparapi\OpenCLMem.java ^
 src\java\com\amd\aparapi\OpenCLPlatform.java ^
 src\java\com\amd\aparapi\OpenCLProgram.java ^
 src\java\com\amd\aparapi\ProfileInfo.java ^
 src\java\com\amd\aparapi\Range.java ^
 src\java\com\amd\aparapi\RangeException.java ^
 src\java\com\amd\aparapi\UnsafeWrapper.java ^
 src\java\com\amd\aparapi\samples\mandel\Mandel.java 

%LAMBDA_JAVA_HOME%\bin\jar ^
  cf aparapi.jar^
  -C classes^
  com

%LAMBDA_JAVA_HOME%\bin\javah ^
 -classpath classes ^
 -d include ^
 -force  ^
 com.amd.aparapi.KernelRunner ^
 com.amd.aparapi.OpenCLJNI ^
 com.amd.aparapi.OpenCLArgDescriptor ^
 com.amd.aparapi.OpenCLMem 


"%MSVC_DIR%\vc\bin\amd64\cl.exe" ^
 "/nologo" ^
 "/TP" ^
 "/Ox" ^
 "/I%MSVC_DIR%\vc\include" ^
 "/I%MSVC_SDK_DIR%\include" ^
 "/I%LAMBDA_JAVA_HOME%\include" ^
 "/I%LAMBDA_JAVA_HOME%\include\win32" ^
 "/Iinclude" ^
 "/I%AMD_APP_SDK_DIR%\include" ^
 "src\cpp\aparapi.cpp" ^
 "src\cpp\config.cpp" ^
 "src\cpp\profileInfo.cpp" ^
 "src\cpp\arrayBuffer.cpp" ^
 "src\cpp\opencljni.cpp" ^
 "src\cpp\jniHelper.cpp" ^
 "src\cpp\clHelper.cpp" ^
 "/LD" ^
 "/link" ^
 "/libpath:%MSVC_DIR%\vc\lib\amd64" ^
 "/libpath:%MSVC_SDK_DIR%\lib\x64" ^
 "/libpath:%AMD_APP_SDK_DIR%\lib\x86_64" ^
 "OpenCL.lib" ^
 "/out:aparapi_x86_64.dll" 

endlocal 
