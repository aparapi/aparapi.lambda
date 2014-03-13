package com.amd.aparapi;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.IntConsumer;

public class JavaSequentialDevice extends Device{
    @Override public void forEach(int _to, final IntConsumer _intConsumer){

        forEach(0, _to, _intConsumer);
    }

   public void forEach(int _from, int _to, final IntConsumer _intConsumer){

      for(int t = _from; t < _to; t++){
         _intConsumer.accept(t);
      }
   }

}
