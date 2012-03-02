package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Range;
import com.amd.opencl.Device;
import com.amd.opencl.OpenCLBinding;

public class SquareExample{

 
   interface Squarer extends OpenCLBinding<Squarer>{
      @OpenCL(""//
            + "__kernel void square("//
            + "        __global float* in,"//
            + "        __global float* out){"//
            + "    const size_t id = get_global_id(0);"//
            + "    out[id] = in[id]*in[id];"//
            + "}")//
      public Squarer square(Range _range, @GlobalReadOnly("in") float[] in, @GlobalWriteOnly("out") float[] out);
   }
   
   public static void main(String[] args) {
      
      int size = 1024;
      float[] in = new float[size];
      for (int i = 0; i < size; i++) {
         in[i] = i;
      }
      float[] out = new float[size];

      Device device = Device.getFirstGPUDevice();
      Squarer squarer = device.create(Squarer.class);
      squarer.put(in).square(Range.create(size), in, out).get(out);

   }

}
