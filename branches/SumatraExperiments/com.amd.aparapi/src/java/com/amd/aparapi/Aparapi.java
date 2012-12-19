package com.amd.aparapi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.invoke.InnerClassLambdaMetafactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BrokenBarrierException;
import java.util.function.IntBlock;
import java.util.HashMap;
import java.util.List;

import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.InstructionSet.AccessField;
import com.amd.aparapi.InstructionSet.MethodCall;
import com.amd.aparapi.InstructionSet.VirtualMethodCall;

public class Aparapi{
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

/*
   static public void forEach(int width, int height, KernelII kernel){
      final int threads = Runtime.getRuntime().availableProcessors();
      final CyclicBarrier barrier = new CyclicBarrier(threads+1);
      for (int t=0; t<threads; t++){
         final int finalt = t;
         new Thread(()->{
            for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
               for (int y=0; y<height; y++){
                  kernel.run(x,y);
               }
            }
            wait(barrier);
         }).start();
      }
      wait(barrier);

   }
   static public void forEach(int width, KernelI kernel){
      final int threads = Runtime.getRuntime().availableProcessors();
      final CyclicBarrier barrier = new CyclicBarrier(threads+1);
      for (int t=0; t<threads; t++){
         final int finalt = t;
         new Thread(()->{
            for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
               kernel.run(x);
            }
            wait(barrier);
         }).start();
      }
      wait(barrier);
   }
*/
   
   static class LambaKernelCall {
      IntBlock block;
      String   lambdaKernelSource;
      String   lambdaMethodName;
      Field[] lambdaCapturedFields;
      Object[] lambdaCapturedArgs;
      String lambdaMethodSignature;
      
      public String     getLambdaKernelSource()    { return lambdaKernelSource; }
      public Object     getLambdaKernelThis()      { return lambdaCapturedArgs[0]; }
      public String     getLambdaMethodName()      { return lambdaMethodName; }
      public String     getLambdaMethodSignature()      { return lambdaMethodSignature; }
      //public Object[]   getLambdaCapturedArgs()    { return lambdaCapturedArgs; }
      //public Object[]   getLambdaReferenceArgs()   { return lambdaReferencedFields; }
      
      public Field[]   getLambdaCapturedFields()    { return lambdaCapturedFields; }
      
      public LambaKernelCall(IntBlock _block) throws AparapiException { 
         block = _block;
         
         // Try to do reflection on the block
         Class bc = block.getClass();
         System.out.println("# block class:" + bc);
         
         // The first field is "this" for the lambda call if the lambda
         // is not static, the later fields are captured values which will 
         // become lambda call parameters
         Field[] bcf = bc.getDeclaredFields();
         lambdaCapturedArgs = new Object[bcf.length];

         Field[] allBlockClassFields = block.getClass().getDeclaredFields();
         
         Field[] capturedFieldsWithoutThis = new Field[ allBlockClassFields.length - 1 ];
         for(int i=1; i<allBlockClassFields.length; i++) {
            capturedFieldsWithoutThis[i-1] = allBlockClassFields[i];
         }
         
         lambdaCapturedFields = capturedFieldsWithoutThis;

         
         try {
            for (int i=0; i<bcf.length; i++) {
            	
               // Since Block members are private have to use Unsafe here
               Class currFieldType = bcf[i].getType();
               long offset = UnsafeWrapper.objectFieldOffset(bcf[i]);

               if (currFieldType.isPrimitive() == false) {
            	   lambdaCapturedArgs[i] = UnsafeWrapper.getObject(block, offset);
               } else if (currFieldType.getName().equals("float")) {
            	   lambdaCapturedArgs[i] = UnsafeWrapper.getFloat(block, offset);
               } else if (currFieldType.getName().equals("int")) {
            	   lambdaCapturedArgs[i] = UnsafeWrapper.getInt(block, offset);
               } else if (currFieldType.getName().equals("long")) {
            	   lambdaCapturedArgs[i] = UnsafeWrapper.getLong(block, offset);

                  // No getDouble ??   
                  //} else if (currFieldType.getName().equals("double")) {
                  //   lambdaArgs[i] = UnsafeWrapper.getDouble(block, offset);
               }

               System.out.println("# Lambda arg type: " + currFieldType + "  " + bcf[i].getName() + " = " + lambdaCapturedArgs[i]);            
            }
         } catch (Exception e) {
            System.out.println("Problem getting Block args");
            e.printStackTrace();
         }
         
         
//         Method[] bcm = bc.getDeclaredMethods();
//         for (Method m : bcm) {
//            System.out.println("# block class method:" + m);
//         }

         // This is the Class containing the lambda method        
         Class lc = getLambdaKernelThis().getClass();
         //Method[] lcm = lc.getDeclaredMethods();
         //for (Method x : lcm) {
         //   System.out.println("# lambda class method:" + x);
         //}

         // The class name is created with the "/" style delimiters
         String bcNameWithSlashes = bc.getName().replace('.', '/');
         ByteArrayInputStream blockClassStream = new ByteArrayInputStream(InnerClassLambdaMetafactory.getBytesForClassName(bcNameWithSlashes));
         ClassModel blockModel = new ClassModel(blockClassStream);

         // We know we are calling an IntBlock lambda with signature "(I)V"
         MethodModel acceptModel = blockModel.getMethodModel("accept", "(I)V");

         List<MethodCall> acceptCallSites = acceptModel.getMethodCalls();
         assert acceptCallSites.size() == 1 : "Should only have one call site in this method";

         
         //VirtualMethodCall vCall = (VirtualMethodCall) acceptCallSites.get(0);
         MethodCall vCall = acceptCallSites.get(0);
         MethodEntry lambdaCallTarget = vCall.getConstantPoolMethodEntry();
         lambdaMethodName = lambdaCallTarget.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
         lambdaMethodSignature = lambdaCallTarget.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();

         System.out.println("call target = " + 
        		 lambdaCallTarget.getClassEntry().getNameUTF8Entry().getUTF8() + 
        		 " " + lambdaMethodName + " " + lambdaMethodSignature);

         String lcNameWithSlashes = lc.getName().replace('.', '/');
         assert lcNameWithSlashes.equals(lambdaCallTarget.getClassEntry().getNameUTF8Entry().getUTF8()) : 
            "lambda target class name does not match arg in block object";
         
      }
   }
  
   static public void forEachJava(int jobSize, IntBlock block) {

      // Single threaded solution
      //for (int i=0; i<jobSize; i++) {
      //   block.accept(i);
      //}
      
      
    final int width = jobSize;
    final int threads = Runtime.getRuntime().availableProcessors();
    final CyclicBarrier barrier = new CyclicBarrier(threads+1);
    for (int t=0; t<threads; t++){
       final int finalt = t;
       new Thread(()->{
          for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
             block.accept(x);
          }   
          wait(barrier);
       }).start();
    }   
    wait(barrier);
      
   }
   
   
   static final ConcurrentHashMap<Class, KernelRunner> kernels = new ConcurrentHashMap<Class, KernelRunner>();
   
   
   static public void forEach(int jobSize, IntBlock block) {
      
      // Note it is a new Block object each time
      
      KernelRunner kernelRunner = kernels.get(block.getClass());
      
      try {

         if (kernelRunner == null) {
            //LambaKernelCall call = new LambaKernelCall(block);
            kernelRunner = new KernelRunner(block);
            kernels.put(block.getClass(), kernelRunner);
         }
         
         boolean success = kernelRunner.execute(block, Range.create(jobSize), 1);

      } catch (AparapiException e) {
         System.out.println(e);
         e.printStackTrace();

         //forEachJava(jobSize, block);
         
         System.exit(-1);
      }
      
      
//      final int width = intArray.length;
//      final int threads = Runtime.getRuntime().availableProcessors();
//      final CyclicBarrier barrier = new CyclicBarrier(threads+1);
//      for (int t=0; t<threads; t++){
//         final int finalt = t;
//         new Thread(()->{
//            for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
//               block.accept(intArray[x]);
//            }   
//            wait(barrier);
//         }).start();
//      }   
//      wait(barrier);
   }
/*
   static public void forEach(int[][] intArray, KernelIII kernel){
      final int width = intArray.length;
      final int threads = Runtime.getRuntime().availableProcessors();
      final CyclicBarrier barrier = new CyclicBarrier(threads+1);
      for (int t=0; t<threads; t++){
         final int finalt = t;
         new Thread(()->{
            for (int x=finalt*(width/threads); x<(finalt+1)*(width/threads); x++){
               int[] arr = intArray[x];
               int arrLen = arr.length;
               for (int y=0; y<arrLen; y++){
                  kernel.run(x,y, arr[y]);
               }
            }
            wait(barrier);
         }).start();
      }
      wait(barrier);
   }
*/   
}

