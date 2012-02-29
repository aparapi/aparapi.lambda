package com.amd.aparapi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class Aparapi{

   public enum AccessType {
      READONLY,
      READWRITE,
      WRITEONLY
   }

   public enum BufferType {
      LOCAL,
      GLOBAL,
      CONSTANT
   }

   public @Retention(RetentionPolicy.RUNTIME) @interface OpenCL {
      String value() default "";

   }

   public @Retention(RetentionPolicy.RUNTIME) @interface Extension {
      String value() default "";

   }

   public @Retention(RetentionPolicy.RUNTIME) @interface ReadOnly {
      String value() default "";

   }

   public @Retention(RetentionPolicy.RUNTIME) @interface ReadWrite {
      String value() default "";

   }

   public @Retention(RetentionPolicy.RUNTIME) @interface WriteOnly {
      String value() default "";

   }

   public @Retention(RetentionPolicy.RUNTIME) @interface Access {
      AccessType value() default AccessType.READWRITE;

   }

   public @Retention(RetentionPolicy.RUNTIME) @interface Buffer {
      BufferType value() default BufferType.GLOBAL;

   }

   public @Retention(RetentionPolicy.RUNTIME) @interface Wraps {
      Class<?> value();

   }

   public static class AparapiExtensionInvocationHandler<T extends AparapiExtensionImplementation> implements InvocationHandler{
      T implementation;

      public AparapiExtensionInvocationHandler(T _implementation) {
         implementation = _implementation;
      }

      @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

         for (Method m : implementation.getClass().getDeclaredMethods()) {

            if (method.getName().equals(m.getName())) {
               // strip the zeroth arg
               Object[] delegatedArgs = Arrays.copyOfRange(args, 1, args.length);
               implementation.setRange((Range)args[0]);
               for (implementation.globalId[0]=0; implementation.globalId[0]<implementation.range.getGlobalSize(0); implementation.globalId[0]++){
                  m.invoke(implementation, delegatedArgs);
               }
            }
         }

         return null;
      }

   }

   public static <T extends AparapiExtension, TI extends AparapiExtensionImplementation> T create(Class<T> _interface) {
      T instance = null;

      Wraps wraps = _interface.getAnnotation(Wraps.class);
      if (wraps != null) {
         Class<TI> typeWeWrap = (Class<TI>) wraps.value();

         try {
            AparapiExtensionImplementation implementationInstance = (AparapiExtensionImplementation) typeWeWrap.newInstance();
            AparapiExtensionInvocationHandler invocationHandler = new AparapiExtensionInvocationHandler(implementationInstance);

            instance = (T) Proxy.newProxyInstance(Aparapi.class.getClassLoader(), new Class[] {
               _interface
            }, invocationHandler);
         } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
        

      }

      return instance;
   }
}
