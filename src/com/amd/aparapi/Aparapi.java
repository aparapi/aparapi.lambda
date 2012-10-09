package com.amd.aparapi;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class Aparapi{
   public interface KernelI {
      void run(int x);
   }
   public interface KernelII {
      void run(int x, int y);
   }

   static void waitForIt(CyclicBarrier barrier){
      try {
         barrier.await(); 
      } catch (InterruptedException ex) { 
      } catch (BrokenBarrierException ex) { 
      }
   }
   static public void forEach(int width, int height, KernelII kernel){
      final int threads = Runtime.getRuntime().availableProcessors();
      final CyclicBarrier barrier = new CyclicBarrier(threads+1, ()-> { System.out.println("done!");});
      for (int t=0; t<threads; t++){
         final int finalt = t;
         new Thread(()->{
            for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
               for (int y=0; y<height; y++){
                  kernel.run(x,y);
               }
            }
            waitForIt(barrier);
         }).start();
      }
      waitForIt(barrier);

   }
   static public void forEach(int width, KernelI kernel){
      for (int x=0; x<width; x++){
         kernel.run(x);
      }
   }
   static public void forEach(int[] intArray, KernelII kernel){
      for (int i=0; i<intArray.length; i++){
         kernel.run(i,intArray[i]);
      }
   }
}

