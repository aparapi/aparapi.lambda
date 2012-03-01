package com.amd.aparapi;

import java.util.List;

public interface OpenCL{
   List<Platform> getPlatforms();

   Context createContext(Device device);

   CompilationUnit createCompilationUnit(Context context, String source);

   KernelEntrypoint createKernelEntrypoint(CompilationUnit cu, String kernelName);

}
