package com.amd.opencl;

import java.util.List;


public class JNI implements OpenCL{
   {
      Runtime.getRuntime().loadLibrary("aparapi_x86");
   }

   @Override native public List<Platform> getPlatforms();

   @Override native public Context createContext(Device device);

   @Override native public CompilationUnit createCompilationUnit(Context context, String source);

   @Override native public KernelEntrypoint createKernelEntrypoint(CompilationUnit cu, String kernelName);

}
