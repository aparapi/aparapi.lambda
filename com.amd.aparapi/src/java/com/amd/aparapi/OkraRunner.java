package com.amd.aparapi;

import com.amd.okra.OkraContext;
import com.amd.okra.OkraKernel;
import java.util.function.IntConsumer;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 5/22/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class OkraRunner{
    static boolean first = true;
    OkraContext context;
    OkraKernel k;
    OkraRunner(String _hsail){
        if (first){
           // must be called before constructing first OkraKernelBelow.
           OkraContext.setCoherence(Config.enableSetOKRACoherence);
           first = false;
        }
        context = new OkraContext();
        k = new OkraKernel(context, _hsail, "&run");
    }
   public void run(int _size, int _offset,   Object... args){
      args[args.length-1]=_offset;
      // The args will be the captured args followed by the fake 'id' arg which is passed to the kernel
      // but subsequently clobbered by generated HSAIL
      //
      // int arr[]; ///
      // so for Device.hsa().forEach(size, id -> arr[id]=id);
      //
      // Args will be arr + id
      //
      // The HSAIL will be 
      //
      // kernel &run(
      //    kernarg_u64 %_arg0,  // arr
      //    kernarg_s32 %_arg2   // id
      // ){
      //   ld_kernarg_u64 $d0, [%_arg0]; // arr
      //   ld_kernarg_s32 $s2, [%_arg2]; // id  is 0 here
      //   workitemabsid_s32 $s2, 0;     // <- sets id to workitem from the device
      //   ...
      // }
      // 
      // Note that until we implement range offsets (forEach(from, to, IntConsumer)) Id will always be 
      // last and we will send '0', this '0' will be clobbered in the HSAIL. 
      // This saves us having to append args or create var slots.  We already have a var slot which
      // remains in scope until the end of the lambda method. 
      //
      // To support offsets I suggest we pass the offset via id and then add workitemabsid
      //
      // kernel &run(
      //    kernarg_u64 %_arg0,  // arr
      //    kernarg_s32 %_arg2   // id
      // ){
      //   ld_kernarg_u64 $d0, [%_arg0]; // arr
      //   ld_kernarg_s32 $s2, [%_arg2]; // id  is 0 here
      //   workitemabsid_s32 $s3, 0;     // <- sets id to workitem from the device
      //   add_b32 $s3,$s2,$s3;          // add passed id to workitem to start offset
      //   ...
      // }
      //
      // This also allows us to batch from Aparapi
      //
      // forEach(0, 1024, IntConsumer) can be mapped to forEach(0, 512, IntConsumer)+forEach(0, 512, IntConsumer)
      //
      k.setLaunchAttributes(_size, 0);
      k.dispatchWithArgs(args);
   }


}
