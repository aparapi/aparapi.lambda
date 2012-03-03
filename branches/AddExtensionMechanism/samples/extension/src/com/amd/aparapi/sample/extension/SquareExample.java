package com.amd.aparapi.sample.extension;

import com.amd.aparapi.Range;
import com.amd.opencl.Device;
import com.amd.opencl.OpenCLBinding;

public class SquareExample{

   interface Squarer extends OpenCLBinding<Squarer>{
      @OpenCL(//
            "const size_t id = get_global_id(0);"//
            + "out[id] = in[id]*in[id];"//
            )
      public Squarer square(//
            Range _range,//
            @GlobalReadOnly("in") @Put float[] in,//
            @GlobalWriteOnly("out") @Get float[] out);
   }
   
   interface Doubler extends OpenCLBinding<Doubler>{
      @OpenCL(//
            "const size_t id = get_global_id(0);"//
            + "arr[id] *=2"//
            )
      public Doubler doublit(//
            Range _range,//
            @GlobalReadOnly("arr") float[] arr);
   }

   public static void main(String[] args) {

      int size = 1024;
      float[] in = new float[size];
      for (int i = 0; i < size; i++) {
         in[i] = i;
      }
      float[] out = new float[size];
      Range range= Range.create(size);
      
      Squarer squarer = Device.firstGPU(Squarer.class);
      squarer.square(range, in, out);
      
      Doubler doubler = Device.firstGPU(Doubler.class);
      doubler//
        .put(out)//
        .doublit(range, out)//
        .doublit(range, out)//
        .doublit(range, out)//
        .doublit(range, out)//
        .get(out);
      
        

   }

}
