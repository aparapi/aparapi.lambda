package com.amd.aparapi;

public class Range implements Cloneable{
   @KernelRunner.UsedByJNICode private int globalWidth = 1;

   @KernelRunner.UsedByJNICode private int localWidth = 1;

   @KernelRunner.UsedByJNICode private int globalHeight = 1;

   @KernelRunner.UsedByJNICode private int localHeight = 1;

   @KernelRunner.UsedByJNICode private int globalDepth = 1;

   @KernelRunner.UsedByJNICode private int localDepth = 1;

   @KernelRunner.UsedByJNICode private boolean valid;

   @KernelRunner.UsedByJNICode private int dims;

   @KernelRunner.UsedByJNICode private boolean localIsDerived = false;

   private Range() {
   }

   Range(int _dims) {
      this();
      dims = _dims;
   }

   public int getGlobalWidth() {
      return globalWidth;
   }

   public int getLocalWidth() {
      return localWidth;
   }

   public int getGlobalHeight() {
      return globalHeight;
   }

   public int getLocalHeight() {
      return localHeight;
   }

   public void setValid(boolean _valid) {
      valid = _valid;
   }

   public boolean isValid() {
      return (valid);
   }

   public int getGlobalDepth() {
      return globalDepth;
   }

   public int getLocalDepth() {
      return localDepth;
   }

   public int getGroupSize() {
      return (localWidth * localHeight * localDepth);
   }

   public int getNumGroups() {
      return ((globalWidth * globalHeight * globalDepth) / getGroupSize());
   }

   private static final int THREADS_PER_CORE = 16;

   private static final int MAX_OPENCL_GROUP_SIZE = 128;

   private static final int MAX_GROUP_SIZE = Math.max(Runtime.getRuntime().availableProcessors() * THREADS_PER_CORE,
         MAX_OPENCL_GROUP_SIZE);

   public static Range create(int _globalWidth, int _localWidth) {
      Range range = new Range(1);
      range.globalWidth = _globalWidth;
      range.localWidth = _localWidth;
      return (range);
   }

   public static Range create(int _globalWidth) {

      Range withoutLocal = create(_globalWidth, 1);
      withoutLocal.localIsDerived = true;
      withoutLocal.localWidth = MAX_GROUP_SIZE;
      while (withoutLocal.globalWidth % withoutLocal.localWidth != 0) {
         withoutLocal.localWidth--;
      }
      return (withoutLocal);
   }

   public static Range create2D(int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {
      Range range = new Range(2);
      range.globalWidth = _globalWidth;
      range.localWidth = _localWidth;
      range.globalHeight = _globalHeight;
      range.localHeight = _localHeight;
      return (range);
   }

   public static Range create2D(int _globalWidth, int _globalHeight) {
      Range withoutLocal = create2D(_globalWidth, _globalHeight, 1, 1);
      withoutLocal.localIsDerived = true;
      // groupSize must match these constraints
      // groupsize = localWidth * localHeight
      // groupSize <= maxgroupSize; 
      // globalWidth%localWidth=0;
      // globalHeight%localHeight=0;

      // So start at 1,1
      // keep incrementing localWidth and  localHeight  until we violate the rules noting match cases as we find them

      int lw = 1;
      int lh = 1;

      int count = 0;
      do {
         if (count % 2 == 0) {
            lw++;
         } else if (count % 2 == 1) {
            if (withoutLocal.dims > 1 && lh < withoutLocal.globalHeight) {
               lh++;
            }

         }
         count++;
         if (withoutLocal.globalWidth % lw == 0 && withoutLocal.globalHeight % lh == 0) {
            withoutLocal.localWidth = lw;
            withoutLocal.localHeight = lh;

         }
      } while (lw * lh < MAX_GROUP_SIZE);

      return (withoutLocal);
   }

   public static Range create3D(int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth, int _localHeight,
         int _localDepth) {
      Range range = new Range(3);
      range.globalWidth = _globalWidth;
      range.localWidth = _localWidth;
      range.globalHeight = _globalHeight;
      range.localHeight = _localHeight;
      range.globalDepth = _globalDepth;
      range.localDepth = _localDepth;
      return (range);
   }

   public static Range create3D(int _globalWidth, int _globalHeight, int _globalDepth) {
      Range withoutLocal = create3D(_globalWidth, _globalHeight, _globalDepth, 1, 1, 1);
      withoutLocal.localIsDerived = true;
      // groupSize must match these constraints
      // groupsize = localWidth * localHeight *localDepth;
      // groupSize <= maxgroupSize; 
      // globalWidth%localWidth=0;
      // globalHeight%localHeight=0;
      // globalDepth%localHeight=0;

      // So start at 1,1,1
      // keep incrementing localWidth, localHeight and localDepth until we violate the rules noting match cases as we find them

      int lw = 1;
      int lh = 1;
      int ld = 1;
      int count = 0;
      do {
         if (count % 3 == 0) {
            lw++;
         } else if (count % 3 == 1) {
            if (withoutLocal.dims > 1 && lh < withoutLocal.globalHeight) {
               lh++;
            }

         } else if (count % 3 == 2) {
            if (withoutLocal.dims > 2 && lw < withoutLocal.globalDepth) {
               ld++;
            }
         }
         count++;
         if (withoutLocal.globalWidth % lw == 0 && withoutLocal.globalHeight % lh == 0 && withoutLocal.globalDepth % ld == 0) {
            withoutLocal.localWidth = lw;
            withoutLocal.localHeight = lh;
            withoutLocal.localDepth = ld;
         }
      } while (lw * lh * ld < MAX_GROUP_SIZE);

      return (withoutLocal);

   }

   public int getDims() {
      return (dims);
   }

   @Override protected Object clone() {
      try {
         Range worker = (Range) super.clone();

         return worker;
      } catch (CloneNotSupportedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return (null);
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();

      switch (dims) {
         case 1:

            sb.append("global:" + globalWidth + " local:" + (localIsDerived ? "(derived)" : "") + localWidth);

            break;
         case 2:
            sb.append("2D(global:" + globalWidth + "x" + globalHeight + " local:" + (localIsDerived ? "(derived)" : "")
                  + localWidth + "x" + localHeight + ")");
            break;
         case 3:
            sb.append("3D(global:" + globalWidth + "x" + globalHeight + "x" + globalDepth + " local:"
                  + (localIsDerived ? "(derived)" : "") + localWidth + "x" + localHeight + "x" + localWidth + ")");
            break;

      }
      return (sb.toString());
   }

   public void setLocalWidth(int _localWidth) {
      localWidth = _localWidth;

   }

   public void setLocalHeight(int _localHeight) {
      localHeight = _localHeight;

   }

}
