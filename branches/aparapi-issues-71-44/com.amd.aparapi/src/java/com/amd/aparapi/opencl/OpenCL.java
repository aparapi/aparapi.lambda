package com.amd.aparapi.opencl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface OpenCL<T> {

   public static final String CL_KHR_FP64 = "cl_khr_fp64";

   public static final String CL_KHR_SELECT_FPROUNDING_MODE = "cl_khr_select_fprounding_mode";

   public static final String CL_KHR_GLOBAL_INT32_BASE_ATOMICS = "cl_khr_global_int32_base_atomics";

   public static final String CL_KHR_GLOBAL_INT32_EXTENDED_ATOMICS = "cl_khr_global_int32_extended_atomics";

   public static final String CL_KHR_LOCAL_INT32_BASE_ATOMICS = "cl_khr_local_int32_base_atomics";

   public static final String CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS = "cl_khr_local_int32_extended_atomics";

   public static final String CL_KHR_INT64_BASE_ATOMICS = "cl_khr_int64_base_atomics";

   public static final String CL_KHR_INT64_EXTENDED_ATOMICS = "cl_khr_int64_extended_atomics";

   public static final String CL_KHR_3D_IMAGE_WRITES = "cl_khr_3d_image_writes";

   public static final String CL_KHR_BYTE_ADDRESSABLE_SUPPORT = "cl_khr_byte_addressable_store";

   public static final String CL_KHR_FP16 = "cl_khr_fp16";

   public static final String CL_KHR_GL_SHARING = "cl_khr_gl_sharing";

   public @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   @interface Put {
   }

   public @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   @interface Get {
   }

   public @Target(ElementType.TYPE)
   @Retention(RetentionPolicy.RUNTIME)
   @interface Source {
      String value();

   }

   public @Target(ElementType.TYPE)
   @Retention(RetentionPolicy.RUNTIME)
   @interface Resource {
      String value();
   }

   public @Target(ElementType.METHOD)
   @Retention(RetentionPolicy.RUNTIME)
   @interface Kernel {
      String value();
   }

   public @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   @interface Arg {
      String value();
   }

   public @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   @interface GlobalReadWrite {
      String value();
   }

   public @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   @interface GlobalReadOnly {
      String value();
   }

   public @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   @interface GlobalWriteOnly {
      String value();
   }

   public @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   @interface Local {
      String value();
   }

   public @Target(ElementType.PARAMETER)
   @Retention(RetentionPolicy.RUNTIME)
   @interface Constant {
      String value();
   }

   public T put(float[] array);

   public T put(int[] array);

   public T put(short[] array);

   public T put(byte[] array);

   public T put(char[] array);

   public T put(boolean[] array);

   public T put(double[] array);

   public T get(float[] array);

   public T get(int[] array);

   public T get(short[] array);

   public T get(char[] array);

   public T get(boolean[] array);

   public T get(double[] array);

   public T get(byte[] array);

   public T begin();

   public T end();
}
