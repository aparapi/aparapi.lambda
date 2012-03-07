package com.amd.opencl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Program{
   private long programId;

   private long queueId;

   private long contextId;

   private Device device;

   private String source;

   private String log;

   Program(long _programId, long _queueId, long _contextId, Device _device, String _source, String _log) {
      programId = _programId;
      queueId = _queueId;
      contextId = _contextId;
      device = _device;
      source = _source;
      log = _log;
   }

   public Device getDevice() {
      return device;
   }

   public Kernel createKernel(String _kernelName, List<Arg> args) {
      return (OpenCLJNI.getJNI().createKernel(this, _kernelName, args));
   }

   private Map<Object, Mem> instanceToMem = new HashMap<Object, Mem>();

   private Map<Long, Mem> addressToMem = new HashMap<Long, Mem>();


   public synchronized Mem getMem(Object _instance, long _address) {
      Mem mem = instanceToMem.get(_instance);
      if (mem == null){
         mem = addressToMem.get(_instance);
         if (mem != null){
            System.out.println("object has been moved, we need to remap the buffer");
            OpenCLJNI.getJNI().remap(this, mem, _address);
         }
      }
      return(mem);
   }
   
   public synchronized void add(Mem _mem) {
    
      instanceToMem.put(_mem.instance, _mem);
      addressToMem.put(_mem.address, _mem);
   }
   
   public synchronized void remaped(Mem _mem, long _oldAddress){
      addressToMem.remove(_oldAddress);
      addressToMem.put(_mem.address, _mem);
   }

}
