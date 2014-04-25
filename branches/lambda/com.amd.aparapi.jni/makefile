targets: agent

OBJS=\
  src/cpp/aparapi.o\
  src/cpp/config.o\
  src/cpp/profileInfo.o\
  src/cpp/arrayBuffer.o\
  src/cpp/opencljni.o\
  src/cpp/jniHelper.o\
  src/cpp/clHelper.o\
  src/cpp/classtools.o\
  src/cpp/instruction.o\
  src/cpp/agent.o

CLASSES=\
  com.amd.aparapi.OpenCLRunner\
  com.amd.aparapi.LambdaRunner\
  com.amd.aparapi.KernelRunner\
  com.amd.aparapi.OpenCLJNI\
  com.amd.aparapi.OpenCLArgDescriptor\
  com.amd.aparapi.OpenCLMem

INCLUDES=\
  -I${JAVA_HOME}/include\
  -I${JAVA_HOME}/include/linux\
  -Iinclude\
  -I${OCL_INCLUDE}

clean: 
	rm -rf include dist *.dll *.so ${OBJS}

javah:
	mkdir include
	javah -cp ../com.amd.aparapi/dist/aparapi.jar -d include -force ${CLASSES}

agent: clean javah ${OBJS}
	mkdir dist
	g++ -shared -o dist/libaparapi_x86_64.so ${OBJS} -L${OCL_LIB} -lOpenCL

cltest: src/cpp/cltest.o
	g++ -o dist/cltest src/cpp/cltest.o -L${OCL_LIB} -lOpenCL

.cpp.o:
	g++ -c -O3 -fPIC -DCL_USE_DEPRECATED_OPENCL_1_1_APIS  ${INCLUDES} $< -o $@
