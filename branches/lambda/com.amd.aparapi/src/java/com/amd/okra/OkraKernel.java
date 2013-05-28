package com.amd.okra;

public class OkraKernel{

   static{
      OkraContext.loadOkraNativeLibrary();
   } // end static

   public OkraKernel(OkraContext _okraContext, String source, String entryName){
      this(_okraContext, source, entryName, null);  // create with null classRefsUsed
   }

   // Kernel creation which takes an classRefsUsed array parameter
   public OkraKernel(OkraContext _okraContext, String source, String entryName, Class<?>[] _classRefsUsed){
      okraContext = _okraContext;
      contextHandle = _okraContext.getContextHandle();
      kernelHandle = _okraContext.createKernelJNI(source, entryName);
      argsVecHandle = 0;
      classRefsUsed = _classRefsUsed;
      // register the whole range of heap memory in the device
      // we give it one object and it deduces the range
      okraContext.registerHeapMemory(new Object());
   }

   private long kernelHandle;
   private long contextHandle;  // used by the JNI side
   private long argsVecHandle;  // only used by JNI side
   private OkraContext okraContext;
   private Class<?> classRefsUsed[];

   public boolean isValid(){
      return (kernelHandle != 0);
   }

   // various methods for setting different types of args into the arg stack
   public native int pushFloatArg(float f);

   public native int pushIntArg(int i);

   public native int pushBooleanArg(boolean z);

   public native int pushByteArg(byte b);

   public native int pushLongArg(long j);

   public native int pushDoubleArg(double d);

   public native int pushIntArrayArg(int[] a);

   public native int pushFloatArrayArg(float[] a);

   public native int pushDoubleArrayArg(double[] a);

   public native int pushBooleanArrayArg(boolean[] a);

   public native int pushByteArrayArg(byte[] a);

   public native int pushLongArrayArg(long[] a);

   public native int clearArgs();

   private native int pushObjectArrayArgJNI(Object[] a);    // for possibly supporting oop array

   private native int pushObjectArgJNI(Object obj);

   public int pushObjectArg(Object obj){
      // since we registered the heap when okraContext was created,
      // we believe no further memory registration is needed here

      return pushObjectArgJNI(obj);
   }

   public int pushObjectArrayArg(Object[] a){    // for possibly supporting oop array
      // since we registered the heap when okraContext was created,
      // we believe no further memory registration is needed here

      return pushObjectArrayArgJNI(a);
   }

   // for the setLaunchAttributes calls, we are just assuming 1D support for now.
   // version that explicitly specifies numWorkItems and groupSize
   public native int setLaunchAttributes(int numWorkItems, int groupSize);

   // version that just specifies numWorkItems and we will pick a "best" groupSize
   public int setLaunchAttributes(int numWorkItems){
      return setLaunchAttributes(numWorkItems, 0);
   }

   // run a kernel and wait until complete
   private native int dispatchKernelWaitCompleteJNI();

   // the java version handles pushing any classRef parameters which were passed in when the kernel was created
   // since these are subject to movement by GC, they cannot be treated as true constants by the generated code
   public int dispatchKernelWaitComplete(){
      if(classRefsUsed != null && classRefsUsed.length > 0){
         for(Class<?> clazz : classRefsUsed){
            pushObjectArg(clazz);
         }
      }
      return dispatchKernelWaitCompleteJNI();
   }

   // if it is primitive, calls the appropriate push routine and
   // returns true else returns false
   private boolean pushPrimitiveArg(Class<?> argclass, Object arg){
      if(argclass.equals(Float.class)){
         pushFloatArg((float) arg);
      }else if(argclass.equals(Integer.class)){
         pushIntArg((int) arg);
      }else if(argclass.equals(Long.class)){
         pushLongArg((long) arg);
      }else if(argclass.equals(Double.class)){
         pushDoubleArg((double) arg);
      }else if(argclass.equals(Boolean.class)){
         pushBooleanArg((boolean) arg);
      }else if(argclass.equals(Byte.class)){
         pushByteArg((byte) arg);
      }else{
         return false;
      }
      //if we took one of the push paths, return true
      return true;
   }

   // a "convenience routine" if you know the arguments to match the
   // requirements of the graal compiler, all arrays will push the
   // simple reference, rather than calling pushXXXArrayArg which
   // ends up pushing the raw array data pointer.

   public int dispatchWithArgs(Object... args){
      clearArgs();
      for(Object arg : args){
         Class<?> argclass = arg.getClass();
         // System.out.println("argclass=" + argclass);
         if(!pushPrimitiveArg(argclass, arg)){
            // in this usage everything that is not a primitive is pushed as an "object"
            pushObjectArg(arg);
         }
      }
      return dispatchKernelWaitComplete();
   }

   // the following version would instead push arrays as old-aparapi-opencl style raw array data pointers
   public int dispatchWithArgsUsingRawArrays(Object... args){
      clearArgs();
      for(Object arg : args){
         Class<?> argclass = arg.getClass();
         // System.out.println("argclass=" + argclass);
         if(!pushPrimitiveArg(argclass, arg)){
            if(argclass.equals(float[].class)){
               pushFloatArrayArg((float[]) arg);
            }else if(argclass.equals(int[].class)){
               pushIntArrayArg((int[]) arg);
            }else if(argclass.equals(long[].class)){
               pushLongArrayArg((long[]) arg);
            }else if(argclass.equals(double[].class)){
               pushDoubleArrayArg((double[]) arg);
            }else if(argclass.equals(boolean[].class)){
               pushBooleanArrayArg((boolean[]) arg);
            }else if(argclass.equals(byte[].class)){
               pushByteArrayArg((byte[]) arg);
            }else if(arg instanceof Object[]){
               // System.out.println("array but not a primitive array for " + arg + ", pushing Object array");
               pushObjectArrayArg((Object[]) arg);
            }else{
               // System.out.println("no primitive or primitive array match for " + arg + ", pushing Object");
               // since we registered the heap when okraContext was created,
               // we believe no further memory registration is needed here
               pushObjectArg(arg);
            }
         }
      }
      return dispatchKernelWaitComplete();
   }

};

