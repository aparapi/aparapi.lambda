package com.amd.aparapi.jni;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amd.aparapi.Config;
import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.opencl.OpenCLArgDescriptor;
import com.amd.aparapi.opencl.OpenCLKernel;
import com.amd.aparapi.opencl.OpenCLMem;
import com.amd.aparapi.opencl.OpenCLPlatform;
import com.amd.aparapi.opencl.OpenCLProgram;

public class OpenCLJNI {

   private static final Logger logger = Logger.getLogger(Config.getLoggerName());

   /**
    * Be careful changing the name/type of this field as it is referenced from JNI code.
    */
   public @interface UsedByJNICode {

   }

   public static final String CL_KHR_FP64 = "cl_khr_fp64";

   public static final String CL_KHR_SELECT_FPROUNDING_MODE = "cl_khr_select_fprounding_mode";

   public static final String CL_KHR_GLOBAL_INT32_BASE_ATOMICS = "cl_khr_global_int32_base_atomics";

   public static final String CL_KHR_GLOBAL_INT32_EXTENDED_ATOMICS = "cl_khr_global_int32_extended_atomics";

   public static final String CL_KHR_LOCAL_INT32_BASE_ATOMICS = "cl_khr_local_int32_base_atomics";

   public static final String CL_KHR_LOCAL_INT32_EXTENDED_ATOMICS = "cl_khr_local_int32_extended_atomics";

   public static final String CL_KHR_INT64_BASE_ATOMICS = "cl_khr_int64_base_atomics";

   public static final String CL_KHR_INT64_EXTENDED_ATOMICS = "cl_khr_int64_extended_atomics";

   public static final String CL_KHR_3D_IMAGE_WRITES = "cl_khr_3d_image_writes";

   public static final String CL_KHR_BYTE_ADDRESSABLE_SUPPORT = "cl_khr_byte_addressable_store";

   public static final String CL_KHR_FP16 = "cl_khr_fp16";

   public static final String CL_KHR_GL_SHARING = "cl_khr_gl_sharing";

   private static boolean openCLAvailable = false;

   static {
      final String arch = System.getProperty("os.arch");
      logger.fine("arch = " + arch);
      String aparapiLibraryName = null;

      if (arch.equals("amd64") || arch.equals("x86_64")) {
         aparapiLibraryName = "aparapi_x86_64";
      } else if (arch.equals("x86") || arch.equals("i386")) {
         aparapiLibraryName = "aparapi_x86";
      } else {
         logger.warning("Expected property os.arch to contain amd64, x86_64, x86 or i386 but instead found " + arch
               + " as a result we don't know which aparapi to attempt to load.");
      }
      if (aparapiLibraryName != null) {
         logger.fine("attempting to load aparapi shared lib " + aparapiLibraryName);

         try {
            Runtime.getRuntime().loadLibrary(aparapiLibraryName);
            openCLAvailable = true;
         } catch (final UnsatisfiedLinkError e) {
            logger.log(Level.SEVERE, "Check your environment. Failed to load aparapi native library " + aparapiLibraryName
                  + " or possibly failed to locate opencl native library (opencl.dll/opencl.so)." +
                  " Ensure that both are in your PATH (windows) or in LD_LIBRARY_PATH (linux).");
         }
      }
   }

   private final static OpenCLJNI instance = new OpenCLJNI();

   public static OpenCLJNI getInstance() {
      return instance;
   }

   native public List<OpenCLPlatform> getPlatforms();

   native public OpenCLProgram createProgram(OpenCLDevice context, String openCLSource);

   native public OpenCLKernel createKernel(OpenCLProgram program, String kernelName, List<OpenCLArgDescriptor> args);

   native public void invoke(OpenCLKernel openCLKernel, Object[] args);

   native public void remap(OpenCLProgram program, OpenCLMem mem, long address);

   native public void getMem(OpenCLProgram program, OpenCLMem mem);

   public boolean isOpenCLAvailable() {
      return (openCLAvailable);
   }
}
