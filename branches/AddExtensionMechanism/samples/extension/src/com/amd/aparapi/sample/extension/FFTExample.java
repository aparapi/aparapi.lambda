package com.amd.aparapi.sample.extension;

import com.amd.aparapi.AparapiExtensionImplementation;
import com.amd.aparapi.Aparapi;
import com.amd.aparapi.CompilationUnit;
import com.amd.aparapi.Context;
import com.amd.aparapi.Device;
import com.amd.aparapi.JNI;
import com.amd.aparapi.KernelEntrypoint;
import com.amd.aparapi.Platform;
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
    
      Device device = null;
      for (Platform p : Platform.getPlatforms()) {
         //System.out.println(p);
         for (Device d : p.getDevices()) {
            //System.out.println(d);
            if (d.getType() == Device.TYPE.GPU) {
               device = d;
            }
         }
      }
      System.out.println(device);
      Context context = device.createContext();

      String source = "" //
            + "__kernel void square("//
            + "        __global float* input,"//
            + "        __global float* output){"//
            + "    const size_t id = get_global_id(0);"//
            + "    output[id] = input[id]*input[id];"//
            + "}";
      ;

      CompilationUnit compilationUnit = context.createCompilationUnit(source);

      KernelEntrypoint kernelEntrypoint = compilationUnit.createKernelEntrypoint("run");
      int size = 1024;
      float[] input = new float[size];
      for (int i=0;i<size; i++){
         input[i]=i;
      }
      float[] output = new float[size];
      
//kernelEntrypoint.args(input,output);
//kernelEntypoint.put(input);
//kernelEntrypint.run(Range.create(size));
//kernelEntypoint.get(output);
      
      if (context != null) {
         float[] real = new float[1024];
         float[] imag = new float[1024];
         Range range = Range.create(real.length / 2);
         Aparapi.create(FFT.class).forward(range, real, imag);
      }
   }

}
