package com.amd.opencl;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public interface OpenCL<T> {
 
   public @Retention(RetentionPolicy.RUNTIME)
   @interface Put {
   }
   public @Retention(RetentionPolicy.RUNTIME)
   @interface Get {
   }
   
   public @Retention(RetentionPolicy.RUNTIME)
   @interface Source {
      String value();
   }
   public @Retention(RetentionPolicy.RUNTIME)
   @interface Kernel {
      String value();
   }
   
   public @Retention(RetentionPolicy.RUNTIME)
   @interface GlobalReadWrite {
      String value();
   }

   public @Retention(RetentionPolicy.RUNTIME)
   @interface GlobalReadOnly {
      String value();
   }

   public @Retention(RetentionPolicy.RUNTIME)
   @interface GlobalWriteOnly {
      String value();
   }

   public @Retention(RetentionPolicy.RUNTIME)
   @interface Local {
      String value();
   }

   public @Retention(RetentionPolicy.RUNTIME)
   @interface Constant {
      String value();
   }

   public T put(float[] array);

   public T put(int[] array);

   public T put(short[] array);

   public T put(char[] array);

   public T put(boolean[] array);

   public T put(double[] array);

   public T get(float[] array);

   public T get(int[] array);

   public T get(short[] array);

   public T get(char[] array);

   public T get(boolean[] array);

   public T get(double[] array);
}
