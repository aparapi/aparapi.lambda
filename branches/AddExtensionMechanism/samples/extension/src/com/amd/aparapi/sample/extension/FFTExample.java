package com.amd.aparapi.sample.extension;

import com.amd.aparapi.AparapiExtensionImplementation;
import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Aparapi.*;
import com.amd.aparapi.AparapiExtension;
import com.amd.aparapi.Range;

public class FFTExample{

   public static class FFTImplementation extends AparapiExtensionImplementation{
      @OpenCL("{"//
            + ""//
            + "}")//
      public void forward(//
            @Global @ReadWrite float[] _data,//
            @Global @ReadWrite float[] _imaginary) {
         System.out.println("in forward " + getGlobalId(0));
         // java implementation 
      }

      @OpenCL("{"//
            + ""//
            + "}")//
      public void reverse(//
            @Global @ReadWrite float[] _data,//
            @Global @ReadWrite float[] _imaginary) {
         System.out.println("in reverse " + getGlobalId(0));
         // java implementation 
      }
   }

   @Wraps(FFTImplementation.class) interface FFT extends AparapiExtension{
      public void forward(Range _range, float[] _data, float[] _imaginary);

      public void reverse(Range _range, float[] _data, float[] _imaginary);
   }

   public static void main(String[] args) {
      float[] real = new float[1024];
      float[] imag = new float[1024];
      Range range = Range.create(real.length / 2);
      Aparapi.create(FFT.class).forward(range, real, imag);

   }

}
