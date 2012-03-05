package com.amd.opencl;

import java.util.List;

public class KernelEntrypoint{
   private Arg[] args;

   private long kernelId;

   private CompilationUnit compilationUnit;

   private String name;

   KernelEntrypoint(long _kernelId, CompilationUnit _compilationUnit, String _name, List<Arg> _args) {
      kernelId = _kernelId;
      compilationUnit = _compilationUnit;
      name = _name;
      args = _args.toArray(new Arg[0]);
   }

   public CompilationUnit getCompilationUnit() {
      return (compilationUnit);

   }

   public String getName() {
      return name;
   }

   public void invoke(Object[] _args) {
      OpenCLJNI.getJNI().invoke(this, _args);

   }

}
