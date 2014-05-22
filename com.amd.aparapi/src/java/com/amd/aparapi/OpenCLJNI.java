package com.amd.aparapi;

import java.util.List;
import java.util.logging.Logger;

public class OpenCLJNI{
   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   static boolean openCLAvailable = false;
   static final OpenCLJNI jni = new OpenCLJNI();

   public static synchronized  OpenCLJNI getOpenCLJNI(){

         String arch = System.getProperty("os.arch");
         logger.fine("arch = "+arch);

         String libName = null;
         try{

            if (arch.equals("amd64") || arch.equals("x86_64")){
               libName = "aparapi_opencl_x86_64";
               logger.fine("attempting to array_load shared lib "+libName);
               System.loadLibrary(libName);
               openCLAvailable = true;
            }else if (arch.equals("x86") || arch.equals("i386")){
               libName = "aparapi_opencl_x86";
               logger.fine("attempting to array_load shared lib "+libName);
               System.loadLibrary(libName);
               openCLAvailable = true;
            }else{
               logger.warning("Expected property os.arch to contain amd64 or x86 but found "+arch
                     +" don't know which library to array_load.");

            }
         }catch (UnsatisfiedLinkError e){
            logger.warning("Check your environment. Failed to array_load aparapi native library "
                  +libName
                  +" or possibly failed to locate opencl native library (opencl.dll/opencl.so). Ensure that both are in your PATH (windows) or in LD_LIBRARY_PATH (linux).");
         }


      return (jni);
   }

   native public List<OpenCLPlatform> getPlatforms();

   native public OpenCLProgram createProgram(OpenCLDevice context, String openCLSource);

   native public OpenCLKernel createKernel(OpenCLProgram program, String kernelName, List<OpenCLArgDescriptor> args);

   native public void invoke(OpenCLKernel openCLKernel, Object[] args);

   native public void remap(OpenCLProgram program, OpenCLMem mem, long address);

   native public void getMem(OpenCLProgram program, OpenCLMem mem);


   public boolean isOpenCLAvailable(){
      return (openCLAvailable);
   }

}
