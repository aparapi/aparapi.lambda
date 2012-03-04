package com.amd.opencl;

import java.util.List;


public class CompilationUnit{
   private long programId;

   private long queueId;

   private Context context;

   private String source;

   private String log;

   CompilationUnit(long _programId, long _queueId, Context _context, String _source, String _log) {
      programId = _programId;
      queueId = _queueId;
      context = _context;
      source = _source;
      log = _log;
   }

   Context getContext() {
      return (context);
   }

   long getProgramId() {
      return (programId);
   }

   public KernelEntrypoint createKernelEntrypoint(String _kernelName, List<KernelEntrypoint.Arg> args) {
     return(OpenCLJNI.getJNI().createKernelEntrypoint(this, _kernelName, args));
   }
}
