package com.amd.aparapi;

import com.amd.okra.OkraContext;
import com.amd.okra.OkraKernel;

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
   public void run(int _from, int _to,   Object... args){


      k.setLaunchAttributes(_to-_from, 0);
      k.dispatchWithArgs(args);
   }


}
