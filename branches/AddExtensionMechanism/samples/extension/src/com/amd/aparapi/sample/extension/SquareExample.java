package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Range;
import com.amd.opencl.Device;
import com.amd.opencl.OpenCL;

public class SquareExample{
  
 
   interface Squarer extends OpenCL<Squarer>{
      @Kernel("{\n"//
            + "  const size_t id = get_global_id(0);\n"//
            + "  out[id] = in[id]*in[id];\n"//
            + "}\n")
      //
      public Squarer square(//
            Range _range,//
            @GlobalReadOnly("in") float[] in,//
            @GlobalWriteOnly("out") float[] out);
   }
   
   @OpenCL.Resource("squarer.cl")
   interface SquarerWithResource extends OpenCL<Squarer>{
   
      public Squarer square(//
            Range _range,//
            @GlobalReadOnly("in") float[] in,//
            @GlobalWriteOnly("out") float[] out);
   }
 
   @OpenCL.Source("__kernel void (\n" //
         + "          __global float *in,\n"//
         + "          __global float *out){\n"//
         + "   const size_t id = get_global_id(0);\n"//
         + "   out[id] = in[id]*in[id];\n"//
         + "}\n")
   interface SquarerWithSource extends OpenCL<Squarer>{
   
      public Squarer square(//
            Range _range,//
            @GlobalReadOnly("in") float[] in,//
            @GlobalWriteOnly("out") float[] out);
   }


   public static void main(String[] args) {

      int size = 32;
      int[] v = new int[size];
      float[] in = new float[size];
      for (int i = 0; i < size; i++) {
         in[i] = i;
      }
      float[] out = new float[size];
      Range range = Range.create(size);

      Squarer squarer = Device.firstGPU(Squarer.class);
      squarer.square(range, in, out);

      for (int i = 0; i < size; i++) {
         System.out.println(in[i] + " " + out[i]);
      }
      
      squarer.square(range, out, in);
      
      for (int i = 0; i < size; i++) {
         System.out.println(in[i] + " " + out[i]);
      }
   }

}
