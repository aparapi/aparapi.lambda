package com.amd.opencl;

import java.util.List;

public class OpenCLJNI{
   static {
      Runtime.getRuntime().loadLibrary("aparapi_x86");
   }
   static final OpenCLJNI jni = new OpenCLJNI();
   static OpenCLJNI getJNI(){
      return(jni);
   }
   public final static long INT_BIT = 1 << 0;

   public final static long FLOAT_BIT = 1 << 1;

   public final static long DOUBLE_BIT = 1 << 2;

   public final static long SHORT_BIT = 1 << 3;

   public final static long ARRAY_BIT = 1 << 4;

   public final static long GLOBAL_BIT = 1 << 5;

   public final static long LOCAL_BIT = 1 << 6;

   public final static long CONST_BIT = 1 << 7;

   public final static long PRIMITIVE_BIT = 1 << 8;

   public final static long LONG_BIT = 1 << 9;

   public final static long READONLY_BIT = 1 << 10;

   public final static long WRITEONLY_BIT = 1 << 11;

   public final static long READWRITE_BIT = 1 << 12;

   native public List<Platform> getPlatforms();

   native public Context createContext(Device device);

   native public CompilationUnit createCompilationUnit(Context context, String source);

   native public KernelEntrypoint createKernelEntrypoint(CompilationUnit cu, String kernelName, List<Arg> args );


   native public void invoke(KernelEntrypoint kernelEntrypoint, Object[] args);

}
