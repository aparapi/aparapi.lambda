package com.amd.opencl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amd.aparapi.Range;

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
      return (OpenCLJNI.getJNI().createContext(this));
   }

   public static class OpenCLInvocationHandler<T extends OpenCL<T>> implements InvocationHandler{
      private Map<String, KernelEntrypoint> map;

      public OpenCLInvocationHandler(Map<String, KernelEntrypoint> _map) {
         map = _map;
      }

      @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         KernelEntrypoint kernel = map.get(method.getName());
         if (kernel != null) {
            // we have a kernel entrypoint bound. 
            System.out.println("would invoke opencl for "+kernel.getName());
            kernel.invoke(args);
         }else{
            // put or get from OpenCL interface. 
           // System.out.println("in " + method.getName());
           // for (Method m : proxy.getClass().getDeclaredMethods()) {
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
           // }
         }
         return proxy;
      }

   }

  
   public <T extends OpenCL<T>> T create(Class<T> _interface) {

      StringBuilder sourceBuilder = new StringBuilder();
      Map<String, List<Arg>> kernels = new HashMap<String, List<Arg>>();
      for (Method m : _interface.getDeclaredMethods()) {

         for (Annotation a : m.getAnnotations()) {
            //  System.out.println("   annotation "+a);
           // System.out.println("   annotation type " + a.annotationType());
            if (a instanceof OpenCL.Source) {
               OpenCL.Source openCL = (OpenCL.Source) a;
               sourceBuilder.append(openCL.value()).append("\n");
            }
            if (a instanceof OpenCL.Kernel) {
               sourceBuilder.append("__kernel void " + m.getName() + "(");
               Annotation[][] parameterAnnotations = m.getParameterAnnotations();
               Class<?>[] parameterTypes = m.getParameterTypes();
               List<Arg> args = new ArrayList<Arg>();
               boolean first = true;
               for (int arg = 0; arg < parameterTypes.length; arg++) {
                  if (parameterTypes[arg].isAssignableFrom(Range.class)) {

                  } else {

                     long bits = 0;
                     if (first) {
                        first = false;
                     } else {
                        sourceBuilder.append(",");
                     }
                     sourceBuilder.append("\n   ");
                     String name = null;
                     for (Annotation pa : parameterAnnotations[arg]) {
                        if (pa instanceof OpenCL.GlobalReadOnly) {
                           name = ((OpenCL.GlobalReadOnly) pa).value();
                           bits |= OpenCLJNI.GLOBAL_BIT |OpenCLJNI.READONLY_BIT ;
                        } else if (pa instanceof OpenCL.GlobalWriteOnly) {
                           name = ((OpenCL.GlobalWriteOnly) pa).value();
                           bits |= OpenCLJNI.GLOBAL_BIT|OpenCLJNI.WRITEONLY_BIT ;
                        } else if (pa instanceof OpenCL.GlobalReadWrite) {
                           name = ((OpenCL.GlobalReadWrite) pa).value();
                           bits |= OpenCLJNI.GLOBAL_BIT|OpenCLJNI.READWRITE_BIT ;
                        } else if (pa instanceof OpenCL.Local) {
                           name = ((OpenCL.Local) pa).value();
                           bits |= OpenCLJNI.LOCAL_BIT;
                        } else if (pa instanceof OpenCL.Constant) {
                           name = ((OpenCL.Constant) pa).value();
                           bits |= OpenCLJNI.CONST_BIT|OpenCLJNI.READONLY_BIT ;
                        }

                     }
                     if (parameterTypes[arg].isAssignableFrom(float[].class)) {
                        bits |= OpenCLJNI.FLOAT_BIT | OpenCLJNI.ARRAY_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(int[].class)) {
                        bits |= OpenCLJNI.INT_BIT | OpenCLJNI.ARRAY_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(double[].class)) {
                        bits |= OpenCLJNI.DOUBLE_BIT | OpenCLJNI.ARRAY_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(short[].class)) {
                        bits |= OpenCLJNI.SHORT_BIT | OpenCLJNI.ARRAY_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(long[].class)) {
                        bits |= OpenCLJNI.LONG_BIT | OpenCLJNI.ARRAY_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(float.class)) {
                        bits |= OpenCLJNI.FLOAT_BIT | OpenCLJNI.PRIMITIVE_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(int.class)) {
                        bits |= OpenCLJNI.INT_BIT | OpenCLJNI.PRIMITIVE_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(double.class)) {
                        bits |= OpenCLJNI.DOUBLE_BIT | OpenCLJNI.PRIMITIVE_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(short.class)) {
                        bits |= OpenCLJNI.SHORT_BIT | OpenCLJNI.PRIMITIVE_BIT;
                     } else if (parameterTypes[arg].isAssignableFrom(long.class)) {
                        bits |= OpenCLJNI.LONG_BIT | OpenCLJNI.PRIMITIVE_BIT;
                     }
                     if (name == null){
                        throw new IllegalStateException("no name!");
                     }
                     Arg kernelArg = new Arg(name, bits);
                     args.add(kernelArg);

                     sourceBuilder.append(kernelArg);
                  }
               }
               sourceBuilder.append(")");
               OpenCL.Kernel kernel = (OpenCL.Kernel) a;
               sourceBuilder.append(kernel.value());
               kernels.put(m.getName(), args);
            }
         }
      }
    

    

      String source = sourceBuilder.toString();
      System.out.println("opencl{\n" + source + "\n}opencl");

      Context context = createContext();
      CompilationUnit compilationUnit = context.createCompilationUnit(source);

      Map<String, KernelEntrypoint> map = new HashMap<String, KernelEntrypoint>();
      for (String name : kernels.keySet()) {     
         map.put(name, compilationUnit.createKernelEntrypoint(name,  kernels.get(name)));
      }
      
      OpenCLInvocationHandler<T> invocationHandler = new OpenCLInvocationHandler<T>(map);
      T instance = (T) Proxy.newProxyInstance(Device.class.getClassLoader(), new Class[] {
         _interface,
         OpenCL.class
   }, invocationHandler);
      return instance;

   }

   public static <T extends OpenCL<T>> T firstGPU(Class<T> _interface) {
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
