package com.amd.aparapi;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.function.IntConsumer;

public class HSADevice extends Device{
    // TODO: We need to cache somewhere to avoid creating HSAIl each time
   static Map<IntConsumer, String> map = new HashMap<IntConsumer, String>();
    static Map<IntConsumer, Object[]> mapArg = new HashMap<IntConsumer, Object[]>();
    static Map<IntConsumer, OkraRunner> mapRunner = new HashMap<IntConsumer, OkraRunner>();
   public Device forEach(int size, IntConsumer ic){
      try{
          String source = null;
          Object[] args;
          OkraRunner runner;
         if (map.containsKey( ic)){
            System.out.println("seen!");
            source = map.get(ic);
             args = mapArg.get(ic);
             runner = mapRunner.get(ic);
          }else{
         LambdaKernelCall lkc = new LambdaKernelCall(ic);
         // System.out.println("lambda method " + lkc.getLambdaMethodName());
         //  System.out.println("lambda method sig " + lkc.getLambdaMethodSignature());
         // System.out.println("lambda method class "+lkc.getLambdaKernelClass());
         ClassModel classModel = ClassModel.getClassModel(lkc.getLambdaKernelClass());
         ClassModel.ClassModelMethod method = classModel.getMethod(lkc.getLambdaMethodName(), lkc.getLambdaMethodSignature());

         runner = new OkraRunner();
         HSAILRenderer renderer = new HSAILRenderer().setShowComments(true);
         HSAILMethod.getHSAILMethod(method, null).renderEntryPoint(renderer);
         System.out.println(renderer.toString());

         List<Object> argList = new ArrayList<Object>();
         if (!lkc.isStatic()){
            argList.add(lkc.getLambdaKernelThis());
         }
             try {
         for(Field f : lkc.getLambdaCapturedFields()){
            f.setAccessible(true);
            String name = f.getName();
            Type type = f.getType();
            if (type.equals(float.class)){

                    argList.add(f.getFloat(ic));

            } else
             if (type.equals(int.class)){

                 argList.add(f.getInt(ic));

             } else {
            // if (type.equals(Object.class)){

                 argList.add(f.get(ic));

             }

             System.out.println("name " + name);

         }
             } catch (IllegalAccessException e) {
                 e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
             }
         argList.add(0); // for the gid!
         source = renderer.toString();
             args =   argList.toArray(new Object[0]);
             for (Object o:args){
                 System.out.println(o+" "+o.getClass().isPrimitive());
             }
         map.put(ic, source);
             mapArg.put(ic, args );
             mapRunner.put(ic, runner);
         }

         runner.run(source, size, args);


      }catch(AparapiException e){
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }catch(ClassNotFoundException e){
         e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }


      return (this);

   }
}
