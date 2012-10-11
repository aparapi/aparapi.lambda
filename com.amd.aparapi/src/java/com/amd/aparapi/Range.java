package com.amd.aparapi;

import java.util.Arrays;

import com.amd.aparapi.device.Device;
import com.amd.aparapi.jni.RangeJNI;

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
public class Range {

   private final RangeJNI rangeJNI = new RangeJNI();

   private Device device = null;

   public Range(Device _device, int _dims) {
      device = _device;
      rangeJNI.setDims(_dims);

      if (device != null) {
         rangeJNI.setMaxWorkItemSize(device.getMaxWorkItemSize());
         rangeJNI.setMaxWorkGroupSize(device.getMaxWorkGroupSize());
      } else {
         rangeJNI.setMaxWorkGroupSize(RangeJNI.MAX_GROUP_SIZE);
      }
   }

   /** 
    * Create a one dimensional range <code>0.._globalWidth</code> which is processed in groups of size _localWidth.
    * <br/>
    * Note that for this range to be valid : </br> <strong><code>_globalWidth > 0 && _localWidth > 0 && _localWidth < MAX_GROUP_SIZE && _globalWidth % _localWidth==0</code></strong>
    * 
    * @param _globalWidth the overall range we wish to process
    * @param _localWidth the size of the group we wish to process.
    * @return A new Range with the requested dimensions
    */
   public static Range create(Device _device, int _globalWidth, int _localWidth) {
      final Range range = new Range(_device, 1);
      final RangeJNI rangeJNI = range.getRangeJNI();

      rangeJNI.setGlobalSize_0(_globalWidth);
      rangeJNI.setLocalSize_0(_localWidth);

      rangeJNI.setValid((rangeJNI.getLocalSize_0() > 0) && (rangeJNI.getLocalSize_0() <= rangeJNI.getMaxWorkItemSize()[0])
            && (rangeJNI.getLocalSize_0() <= rangeJNI.getMaxWorkGroupSize()) && ((rangeJNI.getGlobalSize_0() % rangeJNI.getLocalSize_0()) == 0));

      return (range);
   }

   /**
    * Determine the set of factors for a given value.
    * @param _value The value we wish to factorize. 
    * @param _max an upper bound on the value that can be chosen
    * @return and array of factors of _value
    */

   private static int[] getFactors(int _value, int _max) {
      final int factors[] = new int[RangeJNI.MAX_GROUP_SIZE];
      int factorIdx = 0;

      for (int possibleFactor = 1; possibleFactor <= _max; possibleFactor++) {
         if ((_value % possibleFactor) == 0) {
            factors[factorIdx++] = possibleFactor;
         }
      }

      return (Arrays.copyOf(factors, factorIdx));
   }

   /** 
    * Create a one dimensional range <code>0.._globalWidth</code> with an undefined group size.
    * <br/>
    * Note that for this range to be valid :- </br> <strong><code>_globalWidth > 0 </code></strong>
    * <br/>
    * The groupsize will be chosen such that _localWidth > 0 && _localWidth < MAX_GROUP_SIZE && _globalWidth % _localWidth==0 is true
    * 
    * We extract the factors of _globalWidth and choose the highest value.
    * 
    * @param _globalWidth the overall range we wish to process
    * @return A new Range with the requested dimensions
    */
   public static Range create(Device _device, int _globalWidth) {
      final Range withoutLocal = create(_device, _globalWidth, 1);
      final RangeJNI withoutLocalJNI = withoutLocal.getRangeJNI();

      if (withoutLocalJNI.isValid()) {
         withoutLocalJNI.setLocalIsDerived(true);
         final int[] factors = getFactors(withoutLocalJNI.getGlobalSize_0(), withoutLocalJNI.getMaxWorkItemSize()[0]);

         withoutLocalJNI.setLocalSize_0(factors[factors.length - 1]);

         withoutLocalJNI.setValid((withoutLocalJNI.getLocalSize_0() > 0) && (withoutLocalJNI.getLocalSize_0() <= withoutLocalJNI.getMaxWorkItemSize()[0])
               && (withoutLocalJNI.getLocalSize_0() <= withoutLocalJNI.getMaxWorkGroupSize())
               && ((withoutLocalJNI.getGlobalSize_0() % withoutLocalJNI.getLocalSize_0()) == 0));
      }

      return (withoutLocal);
   }

   public static Range create(int _globalWidth, int _localWidth) {
      final Range range = create(null, _globalWidth, _localWidth);

      return (range);
   }

   public static Range create(int _globalWidth) {
      final Range range = create(null, _globalWidth);

      return (range);
   }

   /** 
    * Create a two dimensional range 0.._globalWidth x 0.._globalHeight using a group which is _localWidth x _localHeight in size.
    * <br/>
    * Note that for this range to be valid  _globalWidth > 0 &&  _globalHeight >0 && _localWidth>0 && _localHeight>0 && _localWidth*_localHeight < MAX_GROUP_SIZE && _globalWidth%_localWidth==0 && _globalHeight%_localHeight==0.
    * 
    *  @param _globalWidth the overall range we wish to process
    * @return
    */
   public static Range create2D(Device _device, int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {
      final Range range = new Range(_device, 2);
      final RangeJNI rangeJNI = range.getRangeJNI();

      rangeJNI.setGlobalSize_0(_globalWidth);
      rangeJNI.setLocalSize_0(_localWidth);
      rangeJNI.setGlobalSize_1(_globalHeight);
      rangeJNI.setLocalSize_1(_localHeight);

      rangeJNI.setValid((rangeJNI.getLocalSize_0() > 0) && (rangeJNI.getLocalSize_1() > 0) && (rangeJNI.getLocalSize_0() <= rangeJNI.getMaxWorkItemSize()[0])
            && (rangeJNI.getLocalSize_1() <= rangeJNI.getMaxWorkItemSize()[1]) && ((rangeJNI.getLocalSize_0() * rangeJNI.getLocalSize_1()) <= rangeJNI.getMaxWorkGroupSize())
            && ((rangeJNI.getGlobalSize_0() % rangeJNI.getLocalSize_0()) == 0) && ((rangeJNI.getGlobalSize_1() % rangeJNI.getLocalSize_1()) == 0));

      return (range);
   }

   /** 
    * Create a two dimensional range <code>0.._globalWidth * 0.._globalHeight</code> choosing suitable values for <code>localWidth</code> and <code>localHeight</code>.
    * <p>
    * Note that for this range to be valid  <code>_globalWidth > 0 &&  _globalHeight >0 && _localWidth>0 && _localHeight>0 && _localWidth*_localHeight < MAX_GROUP_SIZE && _globalWidth%_localWidth==0 && _globalHeight%_localHeight==0</code>.
    * 
    * <p>
    * To determine suitable values for <code>_localWidth</code> and <code>_localHeight</code> we extract the factors for <code>_globalWidth</code> and <code>_globalHeight</code> and then 
    * find the largest product ( <code><= MAX_GROUP_SIZE</code>) with the lowest perimeter.
    * 
    * <p>
    * For example for <code>MAX_GROUP_SIZE</code> of 16 we favor 4x4 over 1x16.
    * 
    * @param _globalWidth the overall range we wish to process
    * @return
    */
   public static Range create2D(Device _device, int _globalWidth, int _globalHeight) {
      final Range withoutLocal = create2D(_device, _globalWidth, _globalHeight, 1, 1);
      final RangeJNI withoutLocalJNI = withoutLocal.getRangeJNI();

      if (withoutLocalJNI.isValid()) {
         withoutLocalJNI.setLocalIsDerived(true);
         final int[] widthFactors = getFactors(_globalWidth, withoutLocalJNI.getMaxWorkItemSize()[0]);
         final int[] heightFactors = getFactors(_globalHeight, withoutLocalJNI.getMaxWorkItemSize()[1]);

         withoutLocalJNI.setLocalSize_0(1);
         withoutLocalJNI.setLocalSize_1(1);
         int max = 1;
         int perimeter = 0;

         for (final int w : widthFactors) {
            for (final int h : heightFactors) {
               final int size = w * h;
               if (size > withoutLocalJNI.getMaxWorkGroupSize()) {
                  break;
               }

               if (size > max) {
                  max = size;
                  perimeter = w + h;
                  withoutLocalJNI.setLocalSize_0(w);
                  withoutLocalJNI.setLocalSize_1(h);
               } else if (size == max) {
                  final int localPerimeter = w + h;
                  if (localPerimeter < perimeter) {// is this the shortest perimeter so far
                     perimeter = localPerimeter;
                     withoutLocalJNI.setLocalSize_0(w);
                     withoutLocalJNI.setLocalSize_1(h);
                  }
               }
            }
         }

         withoutLocalJNI.setValid((withoutLocalJNI.getLocalSize_0() > 0) && (withoutLocalJNI.getLocalSize_1() > 0)
               && (withoutLocalJNI.getLocalSize_0() <= withoutLocalJNI.getMaxWorkItemSize()[0])
               && (withoutLocalJNI.getLocalSize_1() <= withoutLocalJNI.getMaxWorkItemSize()[1])
               && ((withoutLocalJNI.getLocalSize_0() * withoutLocalJNI.getLocalSize_1()) <= withoutLocalJNI.getMaxWorkGroupSize())
               && ((withoutLocalJNI.getGlobalSize_0() % withoutLocalJNI.getLocalSize_0()) == 0)
               && ((withoutLocalJNI.getGlobalSize_1() % withoutLocalJNI.getLocalSize_1()) == 0));
      }

      return (withoutLocal);
   }

   public static Range create2D(int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {
      final Range range = create2D(null, _globalWidth, _globalHeight, _localWidth, _localHeight);

      return (range);
   }

   public static Range create2D(int _globalWidth, int _globalHeight) {
      final Range range = create2D(null, _globalWidth, _globalHeight);

      return (range);
   }

   /** 
    * Create a two dimensional range <code>0.._globalWidth * 0.._globalHeight *0../_globalDepth</code> 
    * in groups defined by  <code>localWidth</code> * <code>localHeight</code> * <code>localDepth</code>.
    * <p>
    * Note that for this range to be valid  <code>_globalWidth > 0 &&  _globalHeight >0 _globalDepth >0 && _localWidth>0 && _localHeight>0 && _localDepth>0 && _localWidth*_localHeight*_localDepth < MAX_GROUP_SIZE && _globalWidth%_localWidth==0 && _globalHeight%_localHeight==0 && _globalDepth%_localDepth==0</code>.
    * 
    * @param _globalWidth the width of the 3D grid we wish to process
    * @param _globalHieght the height of the 3D grid we wish to process
    * @param _globalDepth the depth of the 3D grid we wish to process
    * @param _localWidth the width of the 3D group we wish to process
    * @param _localHieght the height of the 3D group we wish to process
    * @param _localDepth the depth of the 3D group we wish to process
    * @return
    */
   public static Range create3D(Device _device, int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth, int _localHeight, int _localDepth) {
      final Range range = new Range(_device, 3);
      final RangeJNI rangeJNI = range.getRangeJNI();

      rangeJNI.setGlobalSize_0(_globalWidth);
      rangeJNI.setLocalSize_0(_localWidth);
      rangeJNI.setGlobalSize_1(_globalHeight);
      rangeJNI.setLocalSize_1(_localHeight);
      rangeJNI.setGlobalSize_2(_globalDepth);
      rangeJNI.setLocalSize_2(_localDepth);
      rangeJNI.setValid((rangeJNI.getLocalSize_0() > 0) && (rangeJNI.getLocalSize_1() > 0) && (rangeJNI.getLocalSize_2() > 0)
            && ((rangeJNI.getLocalSize_0() * rangeJNI.getLocalSize_1() * rangeJNI.getLocalSize_2()) <= rangeJNI.getMaxWorkGroupSize())
            && (rangeJNI.getLocalSize_0() <= rangeJNI.getMaxWorkItemSize()[0]) && (rangeJNI.getLocalSize_1() <= rangeJNI.getMaxWorkItemSize()[1])
            && (rangeJNI.getLocalSize_2() <= rangeJNI.getMaxWorkItemSize()[2]) && ((rangeJNI.getGlobalSize_0() % rangeJNI.getLocalSize_0()) == 0)
            && ((rangeJNI.getGlobalSize_1() % rangeJNI.getLocalSize_1()) == 0) && ((rangeJNI.getGlobalSize_2() % rangeJNI.getLocalSize_2()) == 0));

      return (range);
   }

   /** 
    * Create a three dimensional range <code>0.._globalWidth * 0.._globalHeight *0../_globalDepth</code> 
    * choosing suitable values for <code>localWidth</code>, <code>localHeight</code> and <code>localDepth</code>.
    * <p>
     * Note that for this range to be valid  <code>_globalWidth > 0 &&  _globalHeight >0 _globalDepth >0 && _localWidth>0 && _localHeight>0 && _localDepth>0 && _localWidth*_localHeight*_localDepth < MAX_GROUP_SIZE && _globalWidth%_localWidth==0 && _globalHeight%_localHeight==0 && _globalDepth%_localDepth==0</code>.
    * 
    * <p>
    * To determine suitable values for <code>_localWidth</code>,<code>_localHeight</code> and <code>_lodalDepth</code> we extract the factors for <code>_globalWidth</code>,<code>_globalHeight</code> and <code>_globalDepth</code> and then 
    * find the largest product ( <code><= MAX_GROUP_SIZE</code>) with the lowest perimeter.
    * 
    * <p>
    * For example for <code>MAX_GROUP_SIZE</code> of 64 we favor 4x4x4 over 1x16x16.
    * 
    * @param _globalWidth the width of the 3D grid we wish to process
    * @param _globalHieght the height of the 3D grid we wish to process
    * @param _globalDepth the depth of the 3D grid we wish to process
    * @return
    */
   public static Range create3D(Device _device, int _globalWidth, int _globalHeight, int _globalDepth) {
      final Range withoutLocal = create3D(_device, _globalWidth, _globalHeight, _globalDepth, 1, 1, 1);
      final RangeJNI withoutLocalJNI = withoutLocal.getRangeJNI();

      if (withoutLocalJNI.isValid()) {
         withoutLocalJNI.setLocalIsDerived(true);

         final int[] widthFactors = getFactors(_globalWidth, withoutLocalJNI.getMaxWorkItemSize()[0]);
         final int[] heightFactors = getFactors(_globalHeight, withoutLocalJNI.getMaxWorkItemSize()[1]);
         final int[] depthFactors = getFactors(_globalDepth, withoutLocalJNI.getMaxWorkItemSize()[2]);

         withoutLocalJNI.setLocalSize_0(1);
         withoutLocalJNI.setLocalSize_1(1);
         withoutLocalJNI.setLocalSize_2(1);
         int max = 1;
         int perimeter = 0;

         for (final int w : widthFactors) {
            for (final int h : heightFactors) {
               for (final int d : depthFactors) {
                  final int size = w * h * d;
                  if (size > withoutLocalJNI.getMaxWorkGroupSize()) {
                     break;
                  }

                  if (size > max) {
                     max = size;
                     perimeter = w + h + d;
                     withoutLocalJNI.setLocalSize_0(w);
                     withoutLocalJNI.setLocalSize_1(h);
                     withoutLocalJNI.setLocalSize_2(d);
                  } else if (size == max) {
                     final int localPerimeter = w + h + d;
                     if (localPerimeter < perimeter) { // is this the shortest perimeter so far
                        perimeter = localPerimeter;
                        withoutLocalJNI.setLocalSize_0(w);
                        withoutLocalJNI.setLocalSize_1(w);
                        withoutLocalJNI.setLocalSize_2(d);
                     }
                  }
               }
            }
         }

         withoutLocalJNI.setValid((withoutLocalJNI.getLocalSize_0() > 0) && (withoutLocalJNI.getLocalSize_1() > 0) && (withoutLocalJNI.getLocalSize_2() > 0)
               && ((withoutLocalJNI.getLocalSize_0() * withoutLocalJNI.getLocalSize_1() * withoutLocalJNI.getLocalSize_2()) <= withoutLocalJNI.getMaxWorkGroupSize())
               && (withoutLocalJNI.getLocalSize_0() <= withoutLocalJNI.getMaxWorkItemSize()[0])
               && (withoutLocalJNI.getLocalSize_1() <= withoutLocalJNI.getMaxWorkItemSize()[1])
               && (withoutLocalJNI.getLocalSize_2() <= withoutLocalJNI.getMaxWorkItemSize()[2])
               && ((withoutLocalJNI.getGlobalSize_0() % withoutLocalJNI.getLocalSize_0()) == 0)
               && ((withoutLocalJNI.getGlobalSize_1() % withoutLocalJNI.getLocalSize_1()) == 0)
               && ((withoutLocalJNI.getGlobalSize_2() % withoutLocalJNI.getLocalSize_2()) == 0));
      }

      return (withoutLocal);
   }

   public static Range create3D(int _globalWidth, int _globalHeight, int _globalDepth) {
      final Range range = create3D(null, _globalWidth, _globalHeight, _globalDepth);

      return (range);
   }

   public static Range create3D(int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth, int _localHeight, int _localDepth) {
      final Range range = create3D(null, _globalWidth, _globalHeight, _globalDepth, _localWidth, _localHeight, _localDepth);
      return (range);
   }

   /**
    * Override {@link #toString()}
    */
   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();

      switch (rangeJNI.getDims()) {
         case 1:
            sb.append("global:" + rangeJNI.getGlobalSize_0() + " local:" + (rangeJNI.isLocalIsDerived() ? "(derived)" : "") + rangeJNI.getLocalSize_0());
            break;
         case 2:
            sb.append("2D(global:" + rangeJNI.getGlobalSize_0() + "x" + rangeJNI.getGlobalSize_1() + " local:" + (rangeJNI.isLocalIsDerived() ? "(derived)" : "")
                  + rangeJNI.getLocalSize_0() + "x" + rangeJNI.getLocalSize_1() + ")");
            break;
         case 3:
            sb.append("3D(global:" + rangeJNI.getGlobalSize_0() + "x" + rangeJNI.getGlobalSize_1() + "x" + rangeJNI.getGlobalSize_2() + " local:"
                  + (rangeJNI.isLocalIsDerived() ? "(derived)" : "") + rangeJNI.getLocalSize_0() + "x" + rangeJNI.getLocalSize_1() + "x"
                  + rangeJNI.getLocalSize_0() + ")");
            break;
      }

      return (sb.toString());
   }

   /**
    * Get the localSize (of the group) given the requested dimension
    * 
    * @param _dim 0=width, 1=height, 2=depth
    * @return The size of the group give the requested dimension
    */
   public int getLocalSize(int _dim) {
      return (_dim == 0 ? rangeJNI.getLocalSize_0() : (_dim == 1 ? rangeJNI.getLocalSize_1() : rangeJNI.getLocalSize_2()));
   }

   /**
    * Get the globalSize (of the range) given the requested dimension
    * 
    * @param _dim 0=width, 1=height, 2=depth
    * @return The size of the group give the requested dimension
    */
   public int getGlobalSize(int _dim) {
      return (_dim == 0 ? rangeJNI.getGlobalSize_0() : (_dim == 1 ? rangeJNI.getGlobalSize_1() : rangeJNI.getGlobalSize_2()));
   }

   /**
    * Get the number of groups for the given dimension. 
    * 
    * <p>
    * This will essentially return globalXXXX/localXXXX for the given dimension (width, height, depth)
    * @param _dim The dim we are interested in 0, 1 or 2
    * @return the number of groups for the given dimension. 
    */

   public int getNumGroups(int _dim) {
      return (_dim == 0 ? (rangeJNI.getGlobalSize_0() / rangeJNI.getLocalSize_0()) : (_dim == 1 ? (rangeJNI.getGlobalSize_1() / rangeJNI.getLocalSize_1()) : (rangeJNI.getGlobalSize_2() / rangeJNI.getLocalSize_2())));
   }

   /**
    * 
    * @return The product of all valid localSize dimensions
    */
   public int getWorkGroupSize() {
      return rangeJNI.getLocalSize_0() * rangeJNI.getLocalSize_1() * rangeJNI.getLocalSize_2();
   }

   public Device getDevice() {
      return (device);
   }

   /**
    * @return the rangeJNI
    */
   protected RangeJNI getRangeJNI() {
      return rangeJNI;
   }

   /**
    * Get the number of dims for this Range.  
    * 
    * @return 0, 1 or 2 for one dimensional, two dimensional and three dimensional range respectively.
    */
   public int getDims() {
      return rangeJNI.getDims();
   }
}
