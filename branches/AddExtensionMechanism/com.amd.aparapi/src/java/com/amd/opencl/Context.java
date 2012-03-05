package com.amd.opencl;

import java.util.HashMap;
import java.util.Map;

public class Context{
   
   private Map<Long, Mem>  memIdToMem = new HashMap<Long,Mem>();
   private Map<Long, Mem>  ptrIdToMem = new HashMap<Long,Mem>();
   
   public Mem getMemByMemId(long memId){
      return(memIdToMem.get(memId));
   }
   public Mem getMemByPtrId(long ptrId){
      return(ptrIdToMem.get(ptrId));
   }
   
   public void add(Mem _mem){
      memIdToMem.put(_mem.memId, _mem);
      ptrIdToMem.put(_mem.memId, _mem);
   }
  
   private long contextId;

   public long getContextId() {
      return contextId;
   }

   private Device device;

   public Device getDevice() {
      return device;
   }

   Context(long _contextId, Device _device) {
      contextId = _contextId;
      device = _device;
   }

   public String toString() {

      return ("Context " + contextId + "\n  device:" + device);
   }

   public CompilationUnit createCompilationUnit(String _source) {
     return(OpenCLJNI.getJNI().createCompilationUnit(this, _source));
   }

}
