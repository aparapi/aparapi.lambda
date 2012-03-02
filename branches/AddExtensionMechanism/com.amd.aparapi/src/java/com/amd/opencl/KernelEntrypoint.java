package com.amd.opencl;


public class KernelEntrypoint{
   private long kernelId;

   private CompilationUnit compilationUnit;

   KernelEntrypoint(long _kernelId, CompilationUnit _compilationUnit) {
      kernelId = _kernelId;
      compilationUnit = _compilationUnit;

   }

}
