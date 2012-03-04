package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Range;
import com.amd.opencl.Device;
import com.amd.opencl.OpenCL;

public class SquareExample{

   interface Squarer extends OpenCL<Squarer>{
      @Kernel("{\n"//
            + "  const size_t id = get_global_id(0);\n"//
            + "  out[id] = in[id]*in[id];\n"//
            + "}\n")//
      public Squarer square(//
            Range _range,//
            @GlobalReadOnly("in") float[] in,//
            @GlobalWriteOnly("out") float[] out);
   }

   interface Doubler extends OpenCL<Doubler>{
      @Kernel("{\n"//
            + "  const size_t id = get_global_id(0);\n"//
            + "  arr[id] *=2;\n"//
            + "}\n")//
      public Doubler doublit(//
            Range _range,//
            @GlobalReadWrite("arr") float[] arr,
            @Constant("index") int[] index );
   }

   public static void main(String[] args) {

      int size = 1024;
      int[] v = new int[size];
      float[] in = new float[size];
      for (int i = 0; i < size; i++) {
         in[i] = i;
      }
      float[] out = new float[size];
      Range range = Range.create(size);

      Squarer squarer = Device.firstGPU(Squarer.class);
      squarer.square(range, in, out);

      Doubler doubler = Device.firstGPU(Doubler.class);
      doubler//
            .put(out)//
            .doublit(range, out, v)//
            .doublit(range, out, v)//
            .doublit(range, out, v)//
            .doublit(range, out, v)//
            .get(out);

   }

}
