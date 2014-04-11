package com.amd.aparapi;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public void dump(Aparapi.IntTerminal ic) {
        try{
        LambdaKernelCall lkc = new LambdaKernelCall(ic);

        ClassModel classModel = ClassModel.getClassModel(lkc.getLambdaKernelClass());

        ClassModel.ClassModelMethod method = classModel.getMethod(lkc.getLambdaMethodName(), lkc.getLambdaMethodSignature());


        HSAILRenderer renderer = new HSAILRenderer().setShowComments(true);

        HSAILMethod hsailMethod = HSAILMethod.getHSAILMethod(method, ic);


        hsailMethod.render(renderer, ic);
        System.out.println(renderer.toString());
        }catch(ClassNotFoundException cnfe){
            cnfe.printStackTrace();
        } catch (ClassParseException e) {
            e.printStackTrace();
        } catch (AparapiException e) {
            e.printStackTrace();
        }
    }
    



     CachedRunner getCachedRunner(Aparapi.Lambda lambda, int _extraArgs){
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
                HSAILMethod hsailMethod = HSAILMethod.getHSAILMethod(method, lambda);
                hsailMethod.render(renderer, lambda);
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


    public void forEach(int from, int to,  Aparapi.IntTerminal ic) {
            CachedRunner cachedRunner = getCachedRunner(ic, 1);

        // The args will be the captured args followed by the fake 'id' arg which is passed to the kernel
        // but subsequently clobbered by generated HSAIL
        //
        // int arr[]; ///
        // so for Device.hsa().forEach(size, id -> arr[id]=id);
        //
        // Args will be arr + id
        //
        // The HSAIL will be
        //
        // kernel &run(
        //    kernarg_u64 %_arg0,  // arr
        //    kernarg_s32 %_arg2   // id
        // ){
        //   ld_kernarg_u64 $d0, [%_arg0]; // arr
        //   ld_kernarg_s32 $s2, [%_arg2]; // id  is 0 here
        //   workitemabsid_s32 $s2, 0;     // <- sets id to workitem from the device
        //   ...
        // }
        //
        // Note that until we implement range offsets (forEach(from, to, IntTerminal)) Id will always be
        // last and we will send '0', this '0' will be clobbered in the HSAIL.
        // This saves us having to append args or create var slots.  We already have a var slot which
        // remains in scope until the end of the lambda method.
        //
        // To support offsets I suggest we pass the offset via id and then add workitemabsid
        //
        // kernel &run(
        //    kernarg_u64 %_arg0,  // arr
        //    kernarg_s32 %_arg2   // id
        // ){
        //   ld_kernarg_u64 $d0, [%_arg0]; // arr
        //   ld_kernarg_s32 $s2, [%_arg2]; // id  is 0 here
        //   workitemabsid_s32 $s3, 0;     // <- sets id to workitem from the device
        //   add_b32 $s3,$s2,$s3;          // add passed id to workitem to start offset
        //   ...
        // }
        //
        // This also allows us to batch from Aparapi
        //
        // forEach(0, 1024, IntTerminal) can be mapped to forEach(0, 512, IntTerminal)+forEach(0, 512, IntTerminal)
        //

            cachedRunner.args[cachedRunner.arg++]=from;
            cachedRunner.runner.run(from, to, cachedRunner.args);
    }


    public <T> void forEach(T[] _array,   Aparapi.ObjectTerminal<T> ic) {
        forEach(_array, _array.length, ic);


    }
    public <T> void forEach(T[] _array, int _len,  Aparapi.ObjectTerminal<T> ic) {
        CachedRunner cachedRunner = getCachedRunner(ic, 1);
        cachedRunner.args[cachedRunner.arg++]=_array;
        // We pass the array as the last arg.  The generated HSAIL *knows* to replace this with _array[workitemabsid]


        cachedRunner.runner.run(0,_len, cachedRunner.args);

    }



    public <T> void forEach(ArrayList<T> _arrayList,  Aparapi.ObjectTerminal<T> ic) {
        CachedRunner cachedRunner = getCachedRunner(ic, 1);
        try{
        Field f = ArrayList.class.getDeclaredField("elementData");
            f.setAccessible(true);

        cachedRunner.args[cachedRunner.arg++]=f.get(_arrayList);
        // We pass the array as the last arg.  The generated HSAIL *knows* to replace this with _array[workitemabsid]


        cachedRunner.runner.run(0,_arrayList.size(), cachedRunner.args);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void forEach(int to, Aparapi.IntTerminal ic) {
        forEach(0, to, ic);
    }

    public void forEach(int _from, int _to, Aparapi.Int2IntMapper intMapper, Aparapi.IntReducer intReducer){

    }
}
