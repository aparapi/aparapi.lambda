package com.amd.aparapi.jni;

import java.util.List;

import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.opencl.OpenCLArgDescriptor;
import com.amd.aparapi.opencl.OpenCLKernel;
import com.amd.aparapi.opencl.OpenCLMem;
import com.amd.aparapi.opencl.OpenCLPlatform;
import com.amd.aparapi.opencl.OpenCLProgram;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 */
public abstract class OpenCLJNI {

   protected native List<OpenCLPlatform> getPlatforms();

   protected native OpenCLProgram createProgram(OpenCLDevice context, String openCLSource);

   protected native OpenCLKernel createKernel(OpenCLProgram program, String kernelName, List<OpenCLArgDescriptor> args);

   protected native void invoke(OpenCLKernel openCLKernel, Object[] args);

   protected native void remap(OpenCLProgram program, OpenCLMem mem, long address);

   protected native void getMem(OpenCLProgram program, OpenCLMem mem);
}
