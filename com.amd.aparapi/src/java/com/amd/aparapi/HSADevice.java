package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.IntConsumer;

public class HSADevice extends Device{

   public Device forEach(int size, IntConsumer ic){
      try{
         LambdaKernelCall lkc = new LambdaKernelCall(ic);
         // System.out.println("lambda method " + lkc.getLambdaMethodName());
         //  System.out.println("lambda method sig " + lkc.getLambdaMethodSignature());
         // System.out.println("lambda method class "+lkc.getLambdaKernelClass());
         ClassModel classModel = ClassModel.getClassModel(lkc.getLambdaKernelClass());
         ClassModel.ClassModelMethod method = classModel.getMethod(lkc.getLambdaMethodName(), lkc.getLambdaMethodSignature());

         OkraRunner runner = new OkraRunner();
         HSAILRenderer renderer = new HSAILRenderer().setShowComments(true);
         HSAILMethod.getHSAILMethod(method).renderEntryPoint(renderer);
         System.out.println(renderer.toString());

         List<Object> args = new ArrayList<Object>();
         if (!lkc.isStatic()){
            args.add(lkc.getLambdaKernelThis());
         }
         for(Field f : lkc.getLambdaCapturedFields()){
            String name = f.getName();
             System.out.println("name "+name);
            Object v = lkc.unsafeGetFieldRefFromObject(ic,name);     // Sometimes this fails if capture is not effectively final!
             if (v != null){
                args.add(v);
             } else{
                 v=v;
             }
         }
         args.add(0);

         runner.run(renderer.toString(), size, args.toArray(new Object[0]));


      }catch(AparapiException e){
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }catch(ClassNotFoundException e){
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }


      return (this);

   }
}
