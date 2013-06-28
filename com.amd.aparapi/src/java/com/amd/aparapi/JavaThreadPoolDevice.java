package com.amd.aparapi;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.IntConsumer;

public class JavaThreadPoolDevice extends Device{
   void wait(CyclicBarrier barrier){
      try{
         barrier.await();
      }catch(InterruptedException ex){
      }catch(BrokenBarrierException ex){
      }
   }

   public Device forEach(int _range, final IntConsumer _intConsumer){
      final int threads = Runtime.getRuntime().availableProcessors();
      final CyclicBarrier barrier = new CyclicBarrier(threads + 1);
      for(int t = 0; t < threads; t++){
         int finalt = t;
         new Thread(() -> {
            for(int x = finalt * (_range / threads); x < (finalt + 1) * (_range / threads); x++){
               _intConsumer.accept(x);
            }
            wait(barrier);
         }).start();
      }
      wait(barrier);
      return (this);
   }

}
