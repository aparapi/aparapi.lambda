package com.amd.aparapi.sample.extension;

import com.amd.opencl.CompilationUnit;
import com.amd.opencl.Context;
import com.amd.opencl.Device;
import com.amd.opencl.KernelEntrypoint;
import com.amd.opencl.Platform;

public class SquareExample{
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
      
     
   }

}
