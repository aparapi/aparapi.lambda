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

   private Map<Long, Mem> memIdToMem = new HashMap<Long, Mem>();

   private Map<Long, Mem> ptrIdToMem = new HashMap<Long, Mem>();

   public Mem getMemByMemId(long memId) {
      return (memIdToMem.get(memId));
   }

   public Mem getMemByPtrId(long ptrId) {
      return (ptrIdToMem.get(ptrId));
   }

   public void add(Mem _mem) {
      memIdToMem.put(_mem.memId, _mem);
      ptrIdToMem.put(_mem.memId, _mem);
   }

}
