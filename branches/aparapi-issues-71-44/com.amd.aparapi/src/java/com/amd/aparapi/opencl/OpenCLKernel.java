package com.amd.aparapi.opencl;

import java.util.List;

import com.amd.aparapi.jni.OpenCLJNI;

public class OpenCLKernel {

   private final OpenCLArgDescriptor[] args;

   private final long kernelId;

   private final OpenCLProgram program;

   private final String name;

   OpenCLKernel(long _kernelId, OpenCLProgram _program, String _name, List<OpenCLArgDescriptor> _args) {
      kernelId = _kernelId;
      program = _program;
      name = _name;
      args = _args.toArray(new OpenCLArgDescriptor[0]);
      for (final OpenCLArgDescriptor arg : args) {
         arg.kernel = this;
      }
   }

   public String getName() {
      return name;
   }

   public void invoke(Object[] _args) {
      OpenCLJNI.getInstance().invoke(this, _args);
   }
}
