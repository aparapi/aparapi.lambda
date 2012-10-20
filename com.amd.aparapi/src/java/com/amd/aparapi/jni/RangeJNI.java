package com.amd.aparapi.jni;

import com.amd.aparapi.util.Annotations.UsedByJNICode;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public class RangeJNI {

   public static final int THREADS_PER_CORE = 16;

   public static final int MAX_OPENCL_GROUP_SIZE = 256;

   public static final int MAX_GROUP_SIZE = Math.max(Runtime.getRuntime().availableProcessors() * THREADS_PER_CORE, MAX_OPENCL_GROUP_SIZE);

   @UsedByJNICode
   private int globalSize_0 = 1;

   @UsedByJNICode
   private int localSize_0 = 1;

   @UsedByJNICode
   private int globalSize_1 = 1;

   @UsedByJNICode
   private int localSize_1 = 1;

   @UsedByJNICode
   private int globalSize_2 = 1;

   @UsedByJNICode
   private int localSize_2 = 1;

   @UsedByJNICode
   private int dims;

   @UsedByJNICode
   private boolean valid = true;

   @UsedByJNICode
   private boolean localIsDerived = false;

   private int maxWorkGroupSize;

   private int[] maxWorkItemSize = new int[] {
         MAX_GROUP_SIZE,
         MAX_GROUP_SIZE,
         MAX_GROUP_SIZE
   };

   /**
    * Default constructor
    */
   public RangeJNI() {

   }

   /**
    * @return the globalSize_0
    */
   public int getGlobalSize_0() {
      return globalSize_0;
   }

   /**
    * @param globalSize_0
    *          the globalSize_0 to set
    */
   public void setGlobalSize_0(int globalSize_0) {
      this.globalSize_0 = globalSize_0;
   }

   /**
    * @return the localSize_0
    */
   public int getLocalSize_0() {
      return localSize_0;
   }

   /**
    * @param localSize_0
    *          the localSize_0 to set
    */
   public void setLocalSize_0(int localSize_0) {
      this.localSize_0 = localSize_0;
   }

   /**
    * @return the globalSize_1
    */
   public int getGlobalSize_1() {
      return globalSize_1;
   }

   /**
    * @param globalSize_1
    *          the globalSize_1 to set
    */
   public void setGlobalSize_1(int globalSize_1) {
      this.globalSize_1 = globalSize_1;
   }

   /**
    * @return the localSize_1
    */
   public int getLocalSize_1() {
      return localSize_1;
   }

   /**
    * @param localSize_1
    *          the localSize_1 to set
    */
   public void setLocalSize_1(int localSize_1) {
      this.localSize_1 = localSize_1;
   }

   /**
    * @return the globalSize_2
    */
   public int getGlobalSize_2() {
      return globalSize_2;
   }

   /**
    * @param globalSize_2
    *          the globalSize_2 to set
    */
   public void setGlobalSize_2(int globalSize_2) {
      this.globalSize_2 = globalSize_2;
   }

   /**
    * @return the localSize_2
    */
   public int getLocalSize_2() {
      return localSize_2;
   }

   /**
    * @param localSize_2
    *          the localSize_2 to set
    */
   public void setLocalSize_2(int localSize_2) {
      this.localSize_2 = localSize_2;
   }

   /**
    * @return the dims
    */
   public int getDims() {
      return dims;
   }

   /**
    * @param dims
    *          the dims to set
    */
   public void setDims(int dims) {
      this.dims = dims;
   }

   /**
    * @return the valid
    */
   public boolean isValid() {
      return valid;
   }

   /**
    * @param valid
    *          the valid to set
    */
   public void setValid(boolean valid) {
      this.valid = valid;
   }

   /**
    * @return the localIsDerived
    */
   public boolean isLocalIsDerived() {
      return localIsDerived;
   }

   /**
    * @param localIsDerived
    *          the localIsDerived to set
    */
   public void setLocalIsDerived(boolean localIsDerived) {
      this.localIsDerived = localIsDerived;
   }

   /**
    * @return the maxWorkGroupSize
    */
   public int getMaxWorkGroupSize() {
      return maxWorkGroupSize;
   }

   /**
    * @param maxWorkGroupSize
    *          the maxWorkGroupSize to set
    */
   public void setMaxWorkGroupSize(int maxWorkGroupSize) {
      this.maxWorkGroupSize = maxWorkGroupSize;
   }

   /**
    * @return the maxWorkItemSize
    */
   public int[] getMaxWorkItemSize() {
      return maxWorkItemSize;
   }

   /**
    * @param maxWorkItemSize
    *          the maxWorkItemSize to set
    */
   public void setMaxWorkItemSize(int[] maxWorkItemSize) {
      this.maxWorkItemSize = maxWorkItemSize;
   }
}
