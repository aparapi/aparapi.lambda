package com.amd.opencl;

public class Context{
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
     return(JNIFactory.getJNI().createCompilationUnit(this, _source));
   }

}
