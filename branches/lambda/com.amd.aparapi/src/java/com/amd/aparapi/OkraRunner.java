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
   public void run(String _hsail, int _size, Object... args){
      OkraContext context = new OkraContext();
      OkraKernel k = new OkraKernel(context, _hsail, "&run");
      k.setLaunchAttributes(_size);
      k.dispatchWithArgs(args);


   }


}
