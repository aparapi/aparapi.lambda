package com.amd.aparapi;

import java.util.Arrays;

/**
 * 
 * A representation of 1, 2 or 3 dimensional range of execution. 
 * 
 * This class uses factory methods to allow one, two or three dimensional ranges to be created. 
 * <br/>
 * For a Kernel operating over the linear range 0..1024 without a specified groups size we would create a one dimensional <code>Range</code> using 
 * <blockquote><pre>Range.create(1024);</pre></blockquote>
 * To request the same linear range but with a groupSize of 64 (range must be a multiple of group size!) we would use
 * <blockquote><pre>Range.create(1024,64);</pre></blockquote>
 * To request a two dimensional range over a grid (0..width)x(0..height) where width==512 and height=256 we would use
 * <blockquote><pre>
 * int width=512;
 * int height=256;
 * Range.create2D(width,height)
 * </pre></blockquote>
 * Again the above does not specify the group size.  One will be chosen for you. If you want to specify the groupSize (say 16x8; 16 wide by 8 high) use
 * <blockquote><pre>
 * int width=512;
 * int height=256;
 * int groupWidth=16;
 * int groupHeight=8;
 * Range.create2D(width, height, groupWidth, groupHeight);
 * </pre></blockquote>
 * Finally we can request a three dimensional range using 
 * <blockquote><pre>
 * int width=512;
 * int height=256;
 * int depth=8;
 * Range.create3D(width, height, depth);
 * </pre></blockquote>
 * And can specify a group size using 
 * <blockquote><pre>
 *  int width=512;
 *  int height=256;
 *  int depth=8;
 *  int groupWidth=8;
 *  int groupHeight=4;
 *  int groupDepth=2
 *  Range.create3D(width, height, depth, groupWidth, groupHeight, groupDepth);
 * </pre></blockquote>
 */
public class Range{
   @KernelRunner.UsedByJNICode private int globalSize_0 = 1;

   @KernelRunner.UsedByJNICode private int localSize_0 = 1;

   @KernelRunner.UsedByJNICode private int globalSize_1 = 1;

   @KernelRunner.UsedByJNICode private int localSize_1 = 1;

   @KernelRunner.UsedByJNICode private int globalSize_2 = 1;

   @KernelRunner.UsedByJNICode private int localSize_2 = 1;

   @KernelRunner.UsedByJNICode private int dims;

   @KernelRunner.UsedByJNICode private boolean valid;

   @KernelRunner.UsedByJNICode private boolean localIsDerived = false;

   /**
    * Get the localSize (of the group) given the requested dimension
    * 
    * @param _dim 0=width, 1=height, 2=depth
    * @return The size of the group give the requested dimension
    */
   public int getLocalSize(int _dim) {
      return (_dim == 0 ? localSize_0 : (_dim == 1 ? localSize_1 : localSize_2));
   }

   /**
    * Get the globalSize (of the range) given the requested dimension
    * 
    * @param _dim 0=width, 1=height, 2=depth
    * @return The size of the group give the requested dimension
    */
   public int getGlobalSize(int _dim) {
      return (_dim == 0 ? globalSize_0 : (_dim == 1 ? globalSize_1 : globalSize_2));
   }

   private static final int THREADS_PER_CORE = 16;

   private static final int MAX_OPENCL_GROUP_SIZE = 256;

   private static final int MAX_GROUP_SIZE = Math.max(Runtime.getRuntime().availableProcessors() * THREADS_PER_CORE,
         MAX_OPENCL_GROUP_SIZE);

   /** 
    * Create a one dimensional range <code>0.._globalWidth</code> which is processed in groups of size _localWidth.
    * <br/>
    * Note that for this range to be valid : </br> <strong><code>_globalWidth > 0 && _localWidth > 0 && _localWidth < MAX_GROUP_SIZE && _globalWidth % _localWidth==0</code></strong>
    * 
    * @param _globalWidth the overall range we wish to process
    * @param _localWidth the size of the group we wish to process.
    * @return A new Range with the requested dimensions
    */
   public static Range create(int _globalWidth, int _localWidth) {
      Range range = new Range();
      range.dims = 1;
      range.globalSize_0 = _globalWidth;
      range.localSize_0 = _localWidth;
      range.valid = range.localSize_0 > 0 && range.localSize_0 < MAX_GROUP_SIZE && range.globalSize_0 % range.localSize_0 == 0;
      return (range);
   }

   private static int[] getFactors(int value) {
      int factors[] = new int[MAX_GROUP_SIZE];
      int idx = 0;
      for (int i = 1; i <= MAX_GROUP_SIZE; i++) {
         if (value % i == 0) {
            factors[idx++] = i;
         }
      }
      return (Arrays.copyOf(factors, idx));
   }

   /** 
    * Create a one dimensional range 0.._globalWidth with an undefined group size.
    * <br/>
    * Note that for this range to be valid :- </br> <strong><code>_globalWidth > 0 </code></strong>
    * <br/>
    * The groupsize will be chosen such that _localWidth > 0 && _localWidth < MAX_GROUP_SIZE && _globalWidth % _localWidth==0 is true
    * 
    * @param _globalWidth the overall range we wish to process
    * @return A new Range with the requested dimensions
    */
   public static Range create(int _globalWidth) {
      Range withoutLocal = create(_globalWidth, 1);
      withoutLocal.localIsDerived = true;
      int[] factors = getFactors(withoutLocal.globalSize_0);

      withoutLocal.localSize_0 = factors[factors.length - 1];

      withoutLocal.valid = withoutLocal.localSize_0 > 0 && withoutLocal.localSize_0 < MAX_GROUP_SIZE
            && withoutLocal.globalSize_0 % withoutLocal.localSize_0 == 0;
      return (withoutLocal);
   }

   /** 
    * Create a two dimensional range 0.._globalWidth x 0.._globalHeight using a group which is _localWidth x _localHeight in size.
    * <br/>
    * Note that for this range to be valid  _globalWidth > 0 &&  _globalHeight >0 && _localWidth>0 && _localHeight>0 && _localWidth*_localHeight < MAX_GROUP_SIZE && _globalWidth%_localWidth==0 && _globalHeight%_localHeight==0.
    * 
    * @param _globalWidth the overall range we wish to process
    * @return
    */
   public static Range create2D(int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {
      Range range = new Range();
      range.dims = 2;
      range.globalSize_0 = _globalWidth;
      range.localSize_0 = _localWidth;
      range.globalSize_1 = _globalHeight;
      range.localSize_1 = _localHeight;
      range.valid = range.localSize_0 > 0 && range.localSize_1 > 0 && range.localSize_0 * range.localSize_1 < MAX_GROUP_SIZE
            && range.globalSize_0 % range.localSize_0 == 0 && range.globalSize_1 % range.localSize_1 == 0;

      return (range);
   }

   public static Range create2D(int _globalWidth, int _globalHeight) {
      Range withoutLocal = create2D(_globalWidth, _globalHeight, 1, 1);
      withoutLocal.localIsDerived = true;
      int[] widthFactors = getFactors(_globalWidth);
      int[] heightFactors = getFactors(_globalHeight);

      withoutLocal.localSize_0 = 1;
      withoutLocal.localSize_1 = 1;
      int max = 1;
      int spread = 0;
      for (int w : widthFactors) {
         for (int h : heightFactors) {
            int size = w * h;
            if (size > MAX_GROUP_SIZE) {
               break;
            }

            if (size > max) {
               max = size;
               spread = Math.abs(w - h);
               withoutLocal.localSize_0 = w;
               withoutLocal.localSize_1 = h;
            } else if (size == max) {
               int localSpread = Math.abs(w - h);
               if (localSpread < spread) {
                  spread = localSpread;
                  withoutLocal.localSize_0 = w;
                  withoutLocal.localSize_1 = h;
               }
            }
         }
      }

      withoutLocal.valid = withoutLocal.localSize_0 > 0 && withoutLocal.localSize_1 > 0
            && withoutLocal.localSize_0 * withoutLocal.localSize_1 < MAX_GROUP_SIZE
            && withoutLocal.globalSize_0 % withoutLocal.localSize_0 == 0
            && withoutLocal.globalSize_1 % withoutLocal.localSize_1 == 0;

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
      range.valid = range.localSize_0 > 0 && range.localSize_1 > 0 && range.localSize_2 > 0
            && range.localSize_0 * range.localSize_1 * range.localSize_2 < MAX_GROUP_SIZE
            && range.globalSize_0 % range.localSize_0 == 0 && range.globalSize_1 % range.localSize_1 == 0
            && range.globalSize_2 % range.localSize_2 == 0;

      return (range);
   }

   public static Range create3D(int _globalWidth, int _globalHeight, int _globalDepth) {
      Range withoutLocal = create3D(_globalWidth, _globalHeight, _globalDepth, 1, 1, 1);
      withoutLocal.localIsDerived = true;
      int[] widthFactors = getFactors(_globalWidth);
      int[] heightFactors = getFactors(_globalHeight);
      int[] depthFactors = getFactors(_globalDepth);

      withoutLocal.localSize_0 = 1;
      withoutLocal.localSize_1 = 1;
      withoutLocal.localSize_2 = 1;
      int max = 1;
      int spread = 0;
      for (int w : widthFactors) {
         for (int h : heightFactors) {
            for (int d : depthFactors) {
               int size = w * h * d;
               if (size > MAX_GROUP_SIZE) {
                  break;
               }
               if (size > max) {
                  max = size;
                  spread = Math.max(Math.max(w, h), d) - Math.min(Math.min(w, h), d); // can this be optimized?
                  withoutLocal.localSize_0 = w;
                  withoutLocal.localSize_1 = h;
                  withoutLocal.localSize_2 = d;
               } else if (size == max) {
                  int localSpread = Math.max(Math.max(w, h), d) - Math.min(Math.min(w, h), d); // can this be optimized?
                  if (localSpread < spread) {
                     spread = localSpread;
                     withoutLocal.localSize_0 = w;
                     withoutLocal.localSize_1 = h;
                     withoutLocal.localSize_2 = d;
                  }
               }
            }
         }
      }

      withoutLocal.valid = withoutLocal.localSize_0 > 0 && withoutLocal.localSize_1 > 0 && withoutLocal.localSize_2 > 0
            && withoutLocal.localSize_0 * withoutLocal.localSize_1 * withoutLocal.localSize_2 < MAX_GROUP_SIZE
            && withoutLocal.globalSize_0 % withoutLocal.localSize_0 == 0
            && withoutLocal.globalSize_1 % withoutLocal.localSize_1 == 0
            && withoutLocal.globalSize_2 % withoutLocal.localSize_2 == 0;

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

   public boolean isValid() {
      return (valid);
   }

}
