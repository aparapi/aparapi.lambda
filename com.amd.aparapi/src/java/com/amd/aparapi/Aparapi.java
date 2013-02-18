package com.amd.aparapi;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.function.IntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Aparapi{
   
   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   static void wait(CyclicBarrier barrier){
      try {
         barrier.await(); 
      } catch (InterruptedException ex) { 
      } catch (BrokenBarrierException ex) { 
      }
   }

   static public void forEachJava(int jobSize, final IntConsumer _intFunctionSAM) {
    final int threads = Runtime.getRuntime().availableProcessors();
    final CyclicBarrier barrier = new CyclicBarrier(threads+1);
    for (int t=0; t<threads; t++){
       int finalt = t;
       new Thread(()->{
             for (int x=finalt*(jobSize/threads); x<(finalt+1)*(jobSize/threads); x++){
        	   _intFunctionSAM.accept(x);
             }   
             Aparapi.wait(barrier);
       }).start();
    }   
    wait(barrier);
      
   }
   
   
   static final ConcurrentHashMap<Class, KernelRunner> kernels = new ConcurrentHashMap<Class, KernelRunner>();
   static final ConcurrentHashMap<Class, Boolean> haveGoodKernel = new ConcurrentHashMap<Class, Boolean>();
   
   
   static public void forEach(int jobSize, IntConsumer intFunctionSAM) {
      
      // Note it is a new Block object each time
      
      KernelRunner kernelRunner = kernels.get(intFunctionSAM.getClass());
      Boolean haveKernel = haveGoodKernel.get(intFunctionSAM.getClass());
      
      try {

         if ((kernelRunner == null) && (haveKernel == null)) {
            kernelRunner = new KernelRunner(intFunctionSAM);
         }

         if ((kernelRunner != null) && (kernelRunner.getRunnable() == true)) {
            boolean success = kernelRunner.execute(intFunctionSAM, Range.create(jobSize), 1);
            if (success == true) {
               kernels.put(intFunctionSAM.getClass(), kernelRunner);
               haveGoodKernel.put(intFunctionSAM.getClass(), true);
            }
            kernelRunner.setRunnable(success);

         } else {
            forEachJava(jobSize, intFunctionSAM);
         }

         return;
         
      } catch (AparapiException e) {
         System.err.println(e);
         e.printStackTrace();
         
         if (logger.isLoggable(Level.FINE)) {
            logger.fine("Kernel failed, try to revert to java.");
         }

         haveGoodKernel.put(intFunctionSAM.getClass(), false);
         
         if (kernelRunner != null) {
            kernelRunner.setRunnable(false);
         }
      }
      
      if (logger.isLoggable(Level.FINE)) {
         logger.fine("Running java.");
      }
      
      forEachJava(jobSize, intFunctionSAM);
   }
}

