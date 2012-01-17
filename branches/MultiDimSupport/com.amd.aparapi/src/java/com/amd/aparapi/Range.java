package com.amd.aparapi;

public class Range{
   @KernelRunner.UsedByJNICode private int globalSize_0 = 1;

   @KernelRunner.UsedByJNICode private int localSize_0 = 1;

   @KernelRunner.UsedByJNICode private int globalSize_1 = 1;

   @KernelRunner.UsedByJNICode private int localSize_1 = 1;

   @KernelRunner.UsedByJNICode private int globalSize_2 = 1;

   @KernelRunner.UsedByJNICode private int localSize_2 = 1;

   @KernelRunner.UsedByJNICode private boolean valid;

   @KernelRunner.UsedByJNICode private int dims;

   @KernelRunner.UsedByJNICode private boolean localIsDerived = false;

   public int getLocalSize(int _dim) {
      return (_dim == 0 ? localSize_0 : (_dim == 1 ? localSize_1 : localSize_2));
   }

   public boolean isValid() {
      return (valid);
   }

   public int getGlobalSize(int _dim) {
      return (_dim == 0 ? globalSize_0 : (_dim == 1 ? globalSize_1 : globalSize_2));
   }

   private static final int THREADS_PER_CORE = 16;

   private static final int MAX_OPENCL_GROUP_SIZE = 128;

   private static final int MAX_GROUP_SIZE = Math.max(Runtime.getRuntime().availableProcessors() * THREADS_PER_CORE,
         MAX_OPENCL_GROUP_SIZE);

   public static Range create(int _globalWidth, int _localWidth) {
      Range range = new Range();
      range.dims = 1;
      range.globalSize_0 = _globalWidth;
      range.localSize_0 = _localWidth;
      return (range);
   }

   public static Range create(int _globalWidth) {

      Range withoutLocal = create(_globalWidth, 1);
      withoutLocal.localIsDerived = true;
      withoutLocal.localSize_0 = MAX_GROUP_SIZE;
      while (withoutLocal.globalSize_0 % withoutLocal.localSize_0 != 0) {
         withoutLocal.localSize_0--;
      }
      return (withoutLocal);
   }

   public static Range create2D(int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {

      Range range = new Range();
      range.dims = 2;
      range.globalSize_0 = _globalWidth;
      range.localSize_0 = _localWidth;
      range.globalSize_1 = _globalHeight;
      range.localSize_1 = _localHeight;
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
            if (withoutLocal.dims > 1 && lh <= withoutLocal.globalSize_1) {
               lh++;
            }

         }
         count++;
         if (withoutLocal.globalSize_0 % lw == 0 && withoutLocal.globalSize_1 % lh == 0) {
            withoutLocal.localSize_0 = lw;
            withoutLocal.localSize_1 = lh;

         }
      } while (lw * lh <= MAX_GROUP_SIZE);
      return (withoutLocal);
   }

   public static Range create3D(int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth, int _localHeight,
         int _localDepth) {
      Range range = new Range();
      range.dims = 3;
      range.globalSize_0 = _globalWidth;
      range.localSize_0 = _localWidth;
      range.globalSize_1 = _globalHeight;
      range.localSize_1 = _localHeight;
      range.globalSize_2 = _globalDepth;
      range.localSize_2 = _localDepth;
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
            if (withoutLocal.dims > 1 && lh <= withoutLocal.globalSize_1) {
               lh++;
            }

         } else if (count % 3 == 2) {
            if (withoutLocal.dims > 2 && lw <= withoutLocal.globalSize_2) {
               ld++;
            }
         }
         count++;
         if (withoutLocal.globalSize_0 % lw == 0 && withoutLocal.globalSize_1 % lh == 0 && withoutLocal.globalSize_2 % ld == 0) {
            withoutLocal.localSize_0 = lw;
            withoutLocal.localSize_1 = lh;
            withoutLocal.localSize_2 = ld;
         }
      } while (lw * lh * ld <= MAX_GROUP_SIZE);
      return (withoutLocal);

   }

   public int getDims() {
      return (dims);
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();

      switch (dims) {
         case 1:

            sb.append("global:" + globalSize_0 + " local:" + (localIsDerived ? "(derived)" : "") + localSize_0);

            break;
         case 2:
            sb.append("2D(global:" + globalSize_0 + "x" + globalSize_1 + " local:" + (localIsDerived ? "(derived)" : "")
                  + localSize_0 + "x" + localSize_1 + ")");
            break;
         case 3:
            sb.append("3D(global:" + globalSize_0 + "x" + globalSize_1 + "x" + globalSize_2 + " local:"
                  + (localIsDerived ? "(derived)" : "") + localSize_0 + "x" + localSize_1 + "x" + localSize_0 + ")");
            break;

      }
      return (sb.toString());
   }

   public int getNumGroups(int _dim) {
      return (_dim == 0 ? (globalSize_0 / localSize_0) : (_dim == 1 ? (globalSize_1 / localSize_1) : (globalSize_2 / localSize_2)));
   }

   public int getWorkGroupSize() {
      return localSize_0 * localSize_1 * localSize_2;
   }

}
