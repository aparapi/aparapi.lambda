package com.amd.aparapi;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

public class  HSADevice extends Device<HSADevice> {

    static class CachedRunner {
        String hsail;
        Object instance;

        OkraRunner runner;
        boolean isStatic;
        Field[] capturedFields;
        Object[] args;
        int arg;
    }

    static Map<Class<?>, CachedRunner> map = new HashMap<Class<?>, CachedRunner>();

    public void dump(IntConsumer ic) {
        try{
        LambdaKernelCall lkc = new LambdaKernelCall(ic);

        ClassModel classModel = ClassModel.getClassModel(lkc.getLambdaKernelClass());

        ClassModel.ClassModelMethod method = classModel.getMethod(lkc.getLambdaMethodName(), lkc.getLambdaMethodSignature());


        HSAILRenderer renderer = new HSAILRenderer().setShowComments(true);

        HSAILMethod hsailMethod = HSAILMethod.getHSAILMethod(method);


        hsailMethod.render(renderer);
        System.out.println(renderer.toString());
        }catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        } catch (ClassParseException e) {
            e.printStackTrace();
        } catch (AparapiException e) {
            e.printStackTrace();
        }
    }
    



     CachedRunner getCachedRunner(Object lambda, int _extraArgs){
         try {
        CachedRunner cachedRunner = null;
        if (map.containsKey(lambda.getClass())) {
            cachedRunner = map.get(lambda.getClass());
        } else {
            cachedRunner = new CachedRunner();
            LambdaKernelCall lkc = new LambdaKernelCall(lambda);

                ClassModel classModel = ClassModel.getClassModel(lkc.getLambdaKernelClass());
                ClassModel.ClassModelMethod method = classModel.getMethod(lkc.getLambdaMethodName(), lkc.getLambdaMethodSignature());
                HSAILRenderer renderer = new HSAILRenderer().setShowComments(true);
                HSAILMethod hsailMethod = HSAILMethod.getHSAILMethod(method);
                hsailMethod.render(renderer);
                cachedRunner.hsail = renderer.toString();

            if (Config.enableShowGeneratedHSAIL || Config.enableShowGeneratedHSAILAndExit){
                System.out.println(cachedRunner.hsail);
                if (Config.enableShowGeneratedHSAILAndExit){
                    System.exit(1);
                }
            }
            cachedRunner.runner = new OkraRunner(cachedRunner.hsail);
            cachedRunner.isStatic = lkc.isStatic();
            if (!cachedRunner.isStatic) {
                cachedRunner.instance = lkc.getLambdaKernelThis();

            }
            cachedRunner.capturedFields= lkc.getLambdaCapturedFields();
            cachedRunner.args = new Object[cachedRunner.capturedFields.length+(cachedRunner.isStatic?0:1)+_extraArgs];


            map.put(lambda.getClass(), cachedRunner);
        }
            cachedRunner.arg=0;
            if (!cachedRunner.isStatic){
                cachedRunner.args[cachedRunner.arg++]=cachedRunner.instance;
            }
            try {
                for (Field f : cachedRunner.capturedFields) {
                    f.setAccessible(true);
                    Type type = f.getType();
                    if (type.equals(float.class)) {
                        cachedRunner.args[cachedRunner.arg++]= f.getFloat(lambda);
                    } else if (type.equals(int.class)) {
                        cachedRunner.args[cachedRunner.arg++]=f.getInt(lambda);
                    }else if (type.equals(long.class)) {
                        cachedRunner.args[cachedRunner.arg++]= f.getLong(lambda);
                    } else if (type.equals(double.class)) {
                        cachedRunner.args[cachedRunner.arg++]=f.getDouble(lambda);
                    } else if (type.equals(char.class)){
                        cachedRunner.args[cachedRunner.arg++]=f.getChar(lambda);
                    } else if (type.equals(boolean.class)){
                        cachedRunner.args[cachedRunner.arg++]=f.getBoolean(lambda);
                    } else if (type.equals(short.class)){
                        cachedRunner.args[cachedRunner.arg++]=f.getShort(lambda);
                    }else {
                        cachedRunner.args[cachedRunner.arg++]=f.get(lambda);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return(cachedRunner);

         } catch (AparapiException e) {
             e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         } catch (ClassNotFoundException e) {
             e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }
         return(null);
    }


    public void forEach(int from, int to,  IntConsumer ic) {
            CachedRunner cachedRunner = getCachedRunner(ic, 1);
            cachedRunner.args[cachedRunner.arg++]=0;
            cachedRunner.runner.run(from, to, cachedRunner.args);
    }

    public <T> void forEach(T[] _array,  Aparapi.ObjectConsumer<T> ic) {
        CachedRunner cachedRunner = getCachedRunner(ic, 2);
        cachedRunner.args[cachedRunner.arg++]=_array;
        cachedRunner.args[cachedRunner.arg++]=0;
        System.out.println("out = "+cachedRunner.hsail);
        System.exit(1);
        cachedRunner.runner.run(0,_array.length, cachedRunner.args);

    }



    public void forEach(int to, IntConsumer ic) {
        forEach(0, to, ic);
    }

    public void forEach(int _from, int _to, Aparapi.IntMapper intMapper, Aparapi.IntReducer intReducer){

    }
}
