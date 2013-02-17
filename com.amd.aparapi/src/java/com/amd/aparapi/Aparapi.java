package com.amd.aparapi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.function.IntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
//import java.util.List;

import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.InstructionSet.AccessField;
import com.amd.aparapi.InstructionSet.MethodCall;
import com.amd.aparapi.InstructionSet.VirtualMethodCall;

public class Aparapi{
   
   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   public interface KernelI {
      void run(int x);
   }
   public interface KernelII {
      void run(int x, int y);
   }
   public interface KernelIII {
      void run(int x, int y, int id);
   }

   public interface KernelSAM {
      void run();
   }

   static void wait(CyclicBarrier barrier){
      try {
         barrier.await(); 
      } catch (InterruptedException ex) { 
      } catch (BrokenBarrierException ex) { 
      }
   }

   static public void forEachJava(int jobSize, final IntFunction _intFunctionSAM) {
    final int width = jobSize;
    final int threads = Runtime.getRuntime().availableProcessors();
    final CyclicBarrier barrier = new CyclicBarrier(threads+1);
    for (int t=0; t<threads; t++){
       final int finalt = t;
       new Thread(new Runnable(){ 
    	  public void run(){
             for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
        	   _intFunctionSAM.apply(x);
             }   
             Aparapi.wait(barrier);
    	  }
       }).start();
    }   
    wait(barrier);
      
   }
   
   
   static final ConcurrentHashMap<Class, KernelRunner> kernels = new ConcurrentHashMap<Class, KernelRunner>();
   static final ConcurrentHashMap<Class, Boolean> haveGoodKernel = new ConcurrentHashMap<Class, Boolean>();
   
   
   static public void forEach(int jobSize, IntFunction intFunctionSAM) {
      
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

