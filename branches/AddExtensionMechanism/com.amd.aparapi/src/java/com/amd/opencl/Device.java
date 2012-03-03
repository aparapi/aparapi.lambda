package com.amd.opencl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.AparapiExtension;
import com.amd.aparapi.AparapiExtensionImplementation;
import com.amd.aparapi.Range;
import com.amd.aparapi.Aparapi.AparapiExtensionInvocationHandler;
import com.amd.aparapi.Aparapi.Wraps;

public class Device{
   static public enum TYPE {
      UNKNOWN,
      GPU,
      CPU
   };

   private Platform platform;

   private long deviceId;

   private TYPE type = TYPE.UNKNOWN;

   private int maxComputeUnits;

   private int maxWorkItemDimensions;

   private long localMemSize;

   private long globalMemSize;

   private int maxWorkGroupSize;

   private int[] maxWorkItemSize = new int[] {
         0,
         0,
         0
   };

   Device(Platform _platform, long _deviceId, TYPE _type) {
      platform = _platform;
      deviceId = _deviceId;
      type = _type;

   }

   public TYPE getType() {
      return type;
   }

   public void setType(TYPE type) {
      this.type = type;
   }

   public int getMaxComputeUnits() {
      return maxComputeUnits;
   }

   public void setMaxComputeUnits(int _maxComputeUnits) {
      maxComputeUnits = _maxComputeUnits;
   }

   public int getMaxWorkItemDimensions() {
      return maxWorkItemDimensions;
   }

   public void setMaxWorkItemDimensions(int _maxWorkItemDimensions) {
      maxWorkItemDimensions = _maxWorkItemDimensions;
   }

   public long getLocalMemSize() {
      return localMemSize;
   }

   public void setLocalMemSize(long _localMemSize) {
      localMemSize = _localMemSize;
   }

   public long getGlobalMemSize() {
      return globalMemSize;
   }

   public void setGlobalMemSize(long _globalMemSize) {
      globalMemSize = _globalMemSize;
   }

   public int getMaxWorkGroupSize() {
      return maxWorkGroupSize;
   }

   public void setMaxWorkGroupSize(int _maxWorkGroupSize) {
      maxWorkGroupSize = _maxWorkGroupSize;
   }

   public int[] getMaxWorkItemSize() {
      return maxWorkItemSize;
   }

   public void setMaxWorkItemSize(int[] maxWorkItemSize) {
      this.maxWorkItemSize = maxWorkItemSize;
   }

   public String toString() {
      StringBuilder s = new StringBuilder("{");
      boolean first = true;
      for (int workItemSize : maxWorkItemSize) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }
         s.append(workItemSize);
      }
      s.append("}");
      return ("Device " + deviceId + "\n  type:" + type + "\n  maxComputeUnits=" + maxComputeUnits + "\n  maxWorkItemDimensions="
            + maxWorkItemDimensions + "\n  maxWorkItemSizes=" + s + "\n  maxWorkWorkGroupSize=" + maxWorkGroupSize
            + "\n  globalMemSize=" + globalMemSize + "\n  localMemSize=" + localMemSize);
   }

   void setMaxWorkItemSize(int _dim, int _value) {
      maxWorkItemSize[_dim] = _value;
   }

   public long getDeviceId() {
      return (deviceId);
   }

   public Platform getPlatform() {
      return (platform);
   }

   public Context createContext() {
      return (JNIFactory.getJNI().createContext(this));
   }

   public static class OpenCLInvocationHandler<T extends OpenCLBinding<T>> implements InvocationHandler{

      @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      //   System.out.println("in " + method.getName());
         for (Method m : proxy.getClass().getDeclaredMethods()) {
           // System.out.println("  found " + m.getName());

            // for (Annotation a:method.getAnnotations()){
            //   System.out.println("   annotation "+a);
            //   System.out.println("   annotation type "+a.annotationType());
            // }
            // if (method.getName().equals(m.getName())) {
            // strip the zeroth arg
            //   Object[] delegatedArgs = Arrays.copyOfRange(args, 1, args.length);
            //   implementation.setRange((Range) args[0]);
            //   for (implementation.globalId[0] = 0; implementation.globalId[0] < implementation.range.getGlobalSize(0); implementation.globalId[0]++) {
            //     m.invoke(implementation, delegatedArgs);
            //  }
            //}
         }
         return proxy;
      }

   }

   public static class Arg{
      public static int INT_BIT = 1 << 0;

      public static int FLOAT_BIT = 1 << 1;

      public static int DOUBLE_BIT = 1 << 2;

      public static int SHORT_BIT = 1 << 3;

      public static int ARRAY_BIT = 1 << 4;

      public static int GLOBAL_BIT = 1 << 5;

      public static int LOCAL_BIT = 1 << 6;

      public static int CONST_BIT = 1 << 7;

      public static int PRIMITIVE_BIT = 1 << 8;

      public static int LONG_BIT = 1 << 8;

      private String name;

      private int bits;

      public Arg(String _name, int _bits) {
         name = _name;
         bits = _bits;
      }

      public String toString() {
         StringBuilder argBuilder = new StringBuilder();
         if ((bits & GLOBAL_BIT) == GLOBAL_BIT) {
            argBuilder.append("__global ");
         } else if ((bits & LOCAL_BIT) == LOCAL_BIT) {
            argBuilder.append("__local ");
         } else if ((bits & CONST_BIT) == CONST_BIT) {
            argBuilder.append("__constant ");
         }
         if ((bits & FLOAT_BIT) == FLOAT_BIT) {
            argBuilder.append("float ");
         } else if ((bits & INT_BIT) == INT_BIT) {
            argBuilder.append("int ");
         } else if ((bits & SHORT_BIT) == SHORT_BIT) {
            argBuilder.append("short ");
         } else if ((bits & DOUBLE_BIT) == DOUBLE_BIT) {
            argBuilder.append("double ");
         } else if ((bits & LONG_BIT) == LONG_BIT) {
            argBuilder.append("long ");
         }

         if ((bits & ARRAY_BIT) == ARRAY_BIT) {
            argBuilder.append("*");
         }
         argBuilder.append(name);
         return (argBuilder.toString());
      }

   }

   public <T extends OpenCLBinding<T>> T create(Class<T> _interface) {

      StringBuilder sourceBuilder = new StringBuilder();
      for (Method m : _interface.getDeclaredMethods()) {

         for (Annotation a : m.getAnnotations()) {
            //  System.out.println("   annotation "+a);
            System.out.println("   annotation type " + a.annotationType());
            if (a instanceof OpenCLBinding.OpenCL) {
               OpenCLBinding.OpenCL openCL = (OpenCLBinding.OpenCL) a;
               sourceBuilder.append(openCL.value()).append("\n");
            }
            if (a instanceof OpenCLBinding.Kernel) {
               sourceBuilder.append("__kernel void " + m.getName() + "(");
               Annotation[][] parameterAnnotations = m.getParameterAnnotations();
               Class<?>[] parameterTypes = m.getParameterTypes();
               List<Arg> args = new ArrayList<Arg>();
               boolean first = true;
               for (int arg = 0; arg < parameterTypes.length; arg++) {
                  if (parameterTypes[arg].isAssignableFrom(Range.class)) {

                  } else {

                     int argBits = 0;
                     if (first) {
                        first = false;
                     } else {
                        sourceBuilder.append(",");
                     }
                     sourceBuilder.append("\n   ");
                     String name = null;
                     for (Annotation pa : parameterAnnotations[arg]) {
                        if (pa instanceof OpenCLBinding.GlobalReadOnly) {
                           name = ((OpenCLBinding.GlobalReadOnly) pa).value();
                           argBits |= Arg.GLOBAL_BIT;
                        } else if (pa instanceof OpenCLBinding.GlobalWriteOnly) {
                           name = ((OpenCLBinding.GlobalWriteOnly) pa).value();
                           argBits |= Arg.GLOBAL_BIT;
                        } else if (pa instanceof OpenCLBinding.GlobalReadWrite) {
                           name = ((OpenCLBinding.GlobalReadWrite) pa).value();
                           argBits |= Arg.GLOBAL_BIT;
                        } else if (pa instanceof OpenCLBinding.Local) {
                           name = ((OpenCLBinding.Local) pa).value();
                           argBits |= Arg.LOCAL_BIT;
                        } else if (pa instanceof OpenCLBinding.Constant) {
                           name = ((OpenCLBinding.Constant) pa).value();
                           argBits |= Arg.CONST_BIT;
                        }

                     }
                     if (parameterTypes[arg].isAssignableFrom(float[].class)) {
                        argBits |= Arg.FLOAT_BIT | Arg.ARRAY_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(int[].class)) {
                        argBits |= Arg.INT_BIT | Arg.ARRAY_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(double[].class)) {
                        argBits |= Arg.DOUBLE_BIT | Arg.ARRAY_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(short[].class)) {
                        argBits |= Arg.SHORT_BIT | Arg.ARRAY_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(long[].class)) {
                        argBits |= Arg.LONG_BIT | Arg.ARRAY_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(float.class)) {
                        argBits |= Arg.FLOAT_BIT | Arg.PRIMITIVE_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(int.class)) {
                        argBits |= Arg.INT_BIT | Arg.PRIMITIVE_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(double.class)) {
                        argBits |= Arg.DOUBLE_BIT | Arg.PRIMITIVE_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(short.class)) {
                        argBits |= Arg.SHORT_BIT | Arg.PRIMITIVE_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(long.class)) {
                        argBits |= Arg.LONG_BIT | Arg.PRIMITIVE_BIT;
                     }
                     Arg kernelArg = new Arg(name, argBits);
                     args.add(kernelArg);
                     sourceBuilder.append(kernelArg);
                  }
               }
               sourceBuilder.append(")");
               OpenCLBinding.Kernel kernel = (OpenCLBinding.Kernel) a;
               sourceBuilder.append(kernel.value());
            }
         }
      }
      OpenCLInvocationHandler<T> invocationHandler = new OpenCLInvocationHandler<T>();

      T instance = (T) Proxy.newProxyInstance(Device.class.getClassLoader(), new Class[] {
            _interface,
            OpenCLBinding.class
      }, invocationHandler);

      String source = sourceBuilder.toString();
      System.out.println("opencl{\n" + source + "\n}opencl");

      Context context = createContext();
      CompilationUnit compilationUnit = context.createCompilationUnit(source);

      KernelEntrypoint kernelEntrypoint = compilationUnit.createKernelEntrypoint("run");

      return instance;

   }

   public static <T extends OpenCLBinding<T>> T firstGPU(Class<T> _interface) {
      Device device = null;
      for (Platform p : Platform.getPlatforms()) {
         //System.out.println(p);
         for (Device d : p.getDevices()) {
            //System.out.println(d);
            if (d.getType() == Device.TYPE.GPU) {
               device = d;
               break;
            }
         }
         if (device != null) {
            break;
         }
      }
      return (device.create(_interface));

   }

}
