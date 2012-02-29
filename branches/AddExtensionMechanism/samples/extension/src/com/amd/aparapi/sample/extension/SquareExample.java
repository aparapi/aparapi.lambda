package com.amd.aparapi.sample.extension;

import com.amd.aparapi.AparapiExtensionImplementation;
import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Aparapi.*;
import com.amd.aparapi.AparapiExtension;
import com.amd.aparapi.Range;

public class SquareExample{

   public static class SquareImplementation extends AparapiExtensionImplementation{
      @OpenCL(""//
            + "_out[getGlobalId(0)]= _in[getGlobalId(0)]*_in[getGlobalId(0)];"//
            + "")//
      public void square(//
            @Buffer(BufferType.GLOBAL) @Access(AccessType.READONLY) float[] _in,//
            @Buffer(BufferType.GLOBAL) @Access(AccessType.WRITEONLY) float[] _out) {
         _out[getGlobalId(0)] = _in[getGlobalId(0)] * _in[getGlobalId(0)];
      }

   }

   @Wraps(SquareImplementation.class) interface Square extends AparapiExtension{
      public void square(Range _range, float[] _data, float[] _imaginary);
   }

   public static void main(String[] args) {
      float[] in = new float[32];
      float[] out = new float[in.length];
      for (int i = 0; i < in.length; i++) {
         in[i] = i;
      }
      Range range = Range.create(in.length);
      Aparapi.create(Square.class).square(range, in, out);
      for (float f : out) {
         System.out.println(f);
      }

   }

}
