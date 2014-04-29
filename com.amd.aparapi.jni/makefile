targets: clean mkdirs javah agent lib

OPENCL_OBJS=\
  src/cpp/aparapi.o\
  src/cpp/config.o\
  src/cpp/profileInfo.o\
  src/cpp/arrayBuffer.o\
  src/cpp/opencljni.o\
  src/cpp/jniHelper.o\
  src/cpp/clHelper.o

AGENT_OBJS=\
  src/cpp/classtools.o\
  src/cpp/agent.o

OBJS=\
  ${OPENCL_OBJS}\
  ${AGENT_OBJS}\

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
  -Iinclude

OCL_INCLUDES=\
  ${INCLUDES}\
  -I${OCL_INCLUDE}

clean: 
	rm -rf include dist *.dll *.so ${OBJS}

javah:
	javah -cp ../com.amd.aparapi/dist/aparapi.jar -d include -force ${CLASSES}

mkdirs:
	mkdir include
	mkdir dist
   

agent: ${AGENT_OBJS}
	g++ -shared -o dist/libaparapi_agent_x86_64.so ${AGENT_OBJS} 

lib: ${OPENCL_OBJS}
	g++ -shared -o dist/libaparapi_opencl_x86_64.so ${OPENCL_OBJS} 

cltest: src/cpp/cltest.o
	g++ -o dist/cltest src/cpp/cltest.o -L${OCL_LIB} -lOpenCL

.cpp.o:
	g++ -c -O3 -fPIC -DCL_USE_DEPRECATED_OPENCL_1_1_APIS  ${OCL_INCLUDES} $< -o $@
