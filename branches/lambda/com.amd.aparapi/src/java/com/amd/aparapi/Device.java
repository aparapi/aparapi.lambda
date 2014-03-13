package com.amd.aparapi;

import com.amd.aparapi.OpenCLDevice.DeviceComparitor;
import com.amd.aparapi.OpenCLDevice.DeviceSelector;

import java.util.function.IntConsumer;

public abstract class Device<T extends Device>{



    static public class IntRange{
       int from;
       int to;
       public ParallelIntRange parallel(){
          return(new ParallelIntRange(this));
       }
       IntRange(int _from, int _to){
          from =_from;
          to =_to;
       }
       public void forEach(IntConsumer _ic){

           Device.seq().forEach(from, to, _ic);
       }
    }

    static public class ParallelIntRange {
       IntRange intRange;
       ParallelIntRange(IntRange _intRange){
          intRange = _intRange;
       }
       public void forEach(IntConsumer _ic){
          Device.hsa().forEach(intRange.from, intRange.to, _ic);
       }
    }

    static public interface ObjectConsumer<T>{
       void accept(T t);
    }

     static public class ParallelArrayRange<T> {
       ArrayRange<T> arrayRange;
       ParallelArrayRange(ArrayRange _arrayRange){
          arrayRange = _arrayRange;
       }
       public void forEach(ObjectConsumer<T> _oc){
          for (T i:arrayRange.arr){
             _oc.accept(i);
          }
       }
    }

    static public class ArrayRange<T>{
       T[] arr;
       ArrayRange(T[] _arr){
          arr = _arr;
       }
       public ParallelArrayRange<T> parallel(){
          return(new ParallelArrayRange(this));
       }
       public void forEach(ObjectConsumer<T> _oc){
          for (T i :arr){
             _oc.accept(i);
          }
       }
    }

    static public <T> ArrayRange<T> range(T[] _arr){
       return((ArrayRange<T>)new ArrayRange(_arr));
    }

    static public IntRange range(int _from, int _to){
       return(new IntRange(_from, _to));
    }
    static public IntRange range(int _to){
        return(new IntRange(0, _to));
    }

    static void foo(){
       range(new String[0]);
    }
   
    public static Device getByName(String _deviceName) {
        TYPE type = TYPE.valueOf(_deviceName.toUpperCase());
        return(_deviceName.equals("hsa")?Device.hsa():
                (_deviceName.equals("jtp")?Device.jtp():
                        (_deviceName.equals("seq")?Device.seq():
                                (_deviceName.equals("hyb")?Device.hyb():
                                        (_deviceName.equals("best")?Device.best():
                                                 null)
                                )
                        )
                )
        );
    }

    static public enum TYPE{
      UNKNOWN,
      GPU,
      CPU,
      JTP,
      HSA,
      SEQ,
      HYB
   }

   ;

   public static Device best(){
      return (OpenCLDevice.select(new DeviceComparitor(){
         @Override
         public OpenCLDevice select(OpenCLDevice _deviceLhs, OpenCLDevice _deviceRhs){
            if(_deviceLhs.getType() != _deviceRhs.getType()){
               if(_deviceLhs.getType() == TYPE.GPU){
                  return (_deviceLhs);
               }else{
                  return (_deviceRhs);
               }
            }
            if(_deviceLhs.getMaxComputeUnits() > _deviceRhs.getMaxComputeUnits()){
               return (_deviceLhs);
            }else{
               return (_deviceRhs);
            }

         }
      }));
   }

   public static Device first(final Device.TYPE _type){
      return (OpenCLDevice.select(new DeviceSelector(){
         @Override
         public OpenCLDevice select(OpenCLDevice _device){
            return (_device.getType() == _type ? _device : null);
         }
      }));
   }

   public static Device firstGPU(){
      return (first(Device.TYPE.GPU));
   }

   public static Device firstCPU(){
      return (first(Device.TYPE.CPU));

   }

   public static JavaThreadPoolDevice jtp(){
      return (new JavaThreadPoolDevice());

   }

   public static JavaSequentialDevice seq(){
        return (new JavaSequentialDevice());

    }

    public static HybridDevice hyb(){
        return (new HybridDevice());

    }


   public static HSADevice hsa(){
      return (new HSADevice());

   }

   protected TYPE type = TYPE.UNKNOWN;

   protected int maxWorkGroupSize;

   protected int maxWorkItemDimensions;

   protected int[] maxWorkItemSize = new int[]{
         0,
         0,
         0
   };

   public TYPE getType(){
      return type;
   }

   public void setType(TYPE type){
      this.type = type;
   }

   public int getMaxWorkItemDimensions(){
      return maxWorkItemDimensions;
   }

   public void setMaxWorkItemDimensions(int _maxWorkItemDimensions){
      maxWorkItemDimensions = _maxWorkItemDimensions;
   }

   public int getMaxWorkGroupSize(){
      return maxWorkGroupSize;
   }

   public void setMaxWorkGroupSize(int _maxWorkGroupSize){
      maxWorkGroupSize = _maxWorkGroupSize;
   }

   public int[] getMaxWorkItemSize(){
      return maxWorkItemSize;
   }

   public void setMaxWorkItemSize(int[] maxWorkItemSize){
      this.maxWorkItemSize = maxWorkItemSize;
   }

   public Range createRange(int _globalWidth){
      return (Range.create(this, _globalWidth));
   }

   public Range createRange(int _globalWidth, int _localWidth){
      return (Range.create(this, _globalWidth, _localWidth));
   }

   public Range createRange2D(int _globalWidth, int _globalHeight){
      return (Range.create2D(this, _globalWidth, _globalHeight));
   }

   public Range createRange2D(int _globalWidth, int _globalHeight, int _localWidth, int _localHeight){
      return (Range.create2D(this, _globalWidth, _globalHeight, _localWidth, _localHeight));
   }

   public Range createRange3D(int _globalWidth, int _globalHeight, int _globalDepth){
      return (Range.create3D(this, _globalWidth, _globalHeight, _globalDepth));
   }

   public Range createRange3D(int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth, int _localHeight,
                              int _localDepth){
      return (Range.create3D(this, _globalWidth, _globalHeight, _globalDepth, _localWidth, _localHeight, _localDepth));
   }

    public abstract  void forEach(int range, IntConsumer ic);

   static HSADevice hsaDevice;
   public synchronized static  void hsaForEach(int range, IntConsumer ic){
      if (hsaDevice == null ){
          hsaDevice = (HSADevice)hsa();

      }
      hsaDevice.forEach(range, ic);
   }

    static HybridDevice hybDevice;
    public synchronized static  void hybForEach(int range, IntConsumer ic){
        if (hybDevice == null ){
            hybDevice = (HybridDevice)hyb();

        }
        hybDevice.forEach(range, ic);
    }
    public synchronized static  void hybForEach(int range, float gpuShare, IntConsumer ic){
        if (hybDevice == null ){
            hybDevice = (HybridDevice)hyb();

        }
        hybDevice.forEach(range, gpuShare, ic);
    }

    static JavaSequentialDevice javaSequentialDevice;
    public synchronized static  void seqForEach(int range, IntConsumer ic){
        if (javaSequentialDevice == null ){
            javaSequentialDevice = (JavaSequentialDevice)seq();

        }
        javaSequentialDevice.forEach(range, ic);
    }

    static JavaThreadPoolDevice javaThreadPoolDevice;
    public synchronized static  void jtpForEach(int range, IntConsumer ic){
        if (javaThreadPoolDevice == null ){
            javaThreadPoolDevice = (JavaThreadPoolDevice)jtp();

        }
        javaThreadPoolDevice.forEach(range, ic);
    }
}
