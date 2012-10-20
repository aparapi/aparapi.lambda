package com.amd.aparapi.opencl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.jni.OpenCLJNI;

public class OpenCLProgram extends OpenCLJNI {

   private final long programId;

   private final long queueId;

   private final long contextId;

   private final OpenCLDevice device;

   private final String source;

   private final String log;

   OpenCLProgram(long _programId, long _queueId, long _contextId, OpenCLDevice _device, String _source, String _log) {
      programId = _programId;
      queueId = _queueId;
      contextId = _contextId;
      device = _device;
      source = _source;
      log = _log;
   }

   public static OpenCLProgram createProgram(OpenCLDevice context, String openCLSource) {
      return createProgram(context, openCLSource);
   }

   public OpenCLDevice getDevice() {
      return device;
   }

   public OpenCLKernel createKernel(String _kernelName, List<OpenCLArgDescriptor> args) {
      return (createKernel(this, _kernelName, args));
   }

   private final Map<Object, OpenCLMem> instanceToMem = new HashMap<Object, OpenCLMem>();

   private final Map<Long, OpenCLMem> addressToMem = new HashMap<Long, OpenCLMem>();

   public synchronized OpenCLMem getMem(Object _instance, long _address) {
      OpenCLMem mem = instanceToMem.get(_instance);

      if (mem == null) {
         mem = addressToMem.get(_instance);
         if (mem != null) {
            System.out.println("object has been moved, we need to remap the buffer");
            remap(this, mem, _address);
         }
      }

      return (mem);
   }

   public synchronized void add(OpenCLMem _mem) {
      instanceToMem.put(_mem.instance, _mem);
      addressToMem.put(_mem.address, _mem);
   }

   public synchronized void remapped(OpenCLMem _mem, long _oldAddress) {
      addressToMem.remove(_oldAddress);
      addressToMem.put(_mem.address, _mem);
   }
}
