/*
   Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
   following conditions are met:

   Redistributions of source code must retain the above copyright notice, this list of conditions and the following
   disclaimer. 

   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution. 

   Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
   derived from this software without specific prior written permission. 

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
   SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

   If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
   laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 
   through 774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of
   the EAR, you hereby certify that, except pursuant to a license granted by the United States Department of Commerce
   Bureau of Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export 
   Administration Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in 
   Country Groups D:1, E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) 
   export to Country Groups D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced
   direct product is subject to national security controls as identified on the Commerce Control List (currently 
   found in Supplement 1 to Part 774 of EAR).  For the most current Country Group listings, or for additional 
   information about the EAR or your obligations under those regulations, please refer to the U.S. Bureau of Industry
   and Security’s website at http://www.bis.doc.gov/. 
   */

#include "aparapi.h"
#include "jniHelper.h"
#include "com_amd_aparapi_JNI.h"

APARAPI_JAVA(jobject, JNI, createCompilationUnit)(JNIEnv *jenv, jobject jobj, jobject contextInstance, jstring source) {
   cl_context context = (cl_context) JNIHelper::callLong(jenv, contextInstance, "getContextId", "()J"); 
   jobject deviceInstance = JNIHelper::callObject(jenv, contextInstance, "getDevice", "()Lcom/amd/aparapi/Device;");
   cl_device_id deviceId = (cl_device_id) JNIHelper::callLong(jenv, deviceInstance, "getDeviceId", "()J"); 
   jobject platformInstance = JNIHelper::callObject(jenv, deviceInstance, "getPlatform", "()Lcom/amd/aparapi/Platform;");
   cl_platform_id platformId = (cl_platform_id) JNIHelper::callLong(jenv, platformInstance, "getPlatformId", "()J"); 
   cl_device_type deviceType;
   cl_int status = CL_SUCCESS;
   clGetDeviceInfo(deviceId, CL_DEVICE_TYPE,  sizeof(deviceType), &deviceType, NULL);
   fprintf(stderr, "device[%d] CL_DEVICE_TYPE = %x\n", deviceId, deviceType);

   const char *sourceChars = jenv->GetStringUTFChars(source, NULL);
   size_t sourceSize[] = { strlen(sourceChars) };
   cl_program program = clCreateProgramWithSource(context, 1, &sourceChars, sourceSize, &status); 
   jenv->ReleaseStringUTFChars(source, sourceChars);

   status = clBuildProgram(program, 1, &deviceId, NULL, NULL, NULL);

   jobject compilationUnitInstance = NULL;
   jstring log = NULL;
   if(status == CL_BUILD_PROGRAM_FAILURE) {
      cl_int logStatus;
      size_t buildLogSize = 0;
      status = clGetProgramBuildInfo(program, deviceId, CL_PROGRAM_BUILD_LOG, buildLogSize, NULL, &buildLogSize);
      char * buildLog = new char[buildLogSize];
      memset(buildLog, 0, buildLogSize);
      status = clGetProgramBuildInfo (program, deviceId, CL_PROGRAM_BUILD_LOG, buildLogSize, buildLog, NULL);

      fprintf(stderr, "clBuildProgram failed");
      fprintf(stderr, "\n************************************************\n");
      fprintf(stderr, "%s", buildLog);
      fprintf(stderr, "\n************************************************\n\n\n");
      log =  jenv->NewStringUTF(buildLog); 
      delete buildLog;
   }else{
   cl_command_queue_properties queue_props = CL_QUEUE_PROFILING_ENABLE;
   cl_command_queue queue = clCreateCommandQueue(context, deviceId, queue_props, &status);

      compilationUnitInstance = JNIHelper::createInstance(jenv, "com/amd/aparapi/CompilationUnit", "(JJLcom/amd/aparapi/Context;Ljava/lang/String;Ljava/lang/String;)V", (jlong)program, (jlong)queue, contextInstance, source, log);
   }
   return(compilationUnitInstance);
}
APARAPI_JAVA(jobject, JNI, createContext)(JNIEnv *jenv, jobject jobj, jobject deviceInstance) {
   jobject platformInstance = JNIHelper::callObject(jenv, deviceInstance, "getPlatform", "()Lcom/amd/aparapi/Platform;");
   cl_platform_id platformId = (cl_platform_id) JNIHelper::callLong(jenv, platformInstance, "getPlatformId", "()J"); 
   cl_device_id deviceId = (cl_device_id) JNIHelper::callLong(jenv, deviceInstance, "getDeviceId", "()J"); 
   cl_device_type deviceType;
   cl_int status = CL_SUCCESS;
   clGetDeviceInfo(deviceId, CL_DEVICE_TYPE,  sizeof(deviceType), &deviceType, NULL);
   fprintf(stderr, "device[%d] CL_DEVICE_TYPE = %x\n", deviceId, deviceType);

   jobject contextInstance = NULL;

   cl_context_properties cps[3] = { CL_CONTEXT_PLATFORM, (cl_context_properties)platformId, 0 };
   cl_context_properties* cprops = (NULL == platformId) ? NULL : cps;
   cl_context context = clCreateContextFromType( cprops, deviceType, NULL, NULL, &status);
   if (status == CL_SUCCESS){
      contextInstance = JNIHelper::createInstance(jenv, "com/amd/aparapi/Context", "(JLcom/amd/aparapi/Device;)V", (jlong)context, deviceInstance);
   }
   return(contextInstance);
}

APARAPI_JAVA(jobject, JNI, createKernelEntrypoint)(JNIEnv *jenv, jobject jobj, jobject compilationUnitInstance, jstring name) {
   jobject contextInstance = JNIHelper::callObject(jenv, compilationUnitInstance, "getContext", "()Lcom/amd/aparapi/Context;");
   cl_context context = (cl_context) JNIHelper::callLong(jenv, contextInstance, "getContextId", "()J"); 
   cl_program program = (cl_program) JNIHelper::callLong(jenv, compilationUnitInstance, "getProgramId", "()J"); 
   jobject deviceInstance = JNIHelper::callObject(jenv, contextInstance, "getDevice", "()Lcom/amd/aparapi/Device;");
   jobject platformInstance = JNIHelper::callObject(jenv, deviceInstance, "getPlatform", "()Lcom/amd/aparapi/Platform;");
   cl_platform_id platformId = (cl_platform_id) JNIHelper::callLong(jenv, platformInstance, "getPlatformId", "()J"); 
   cl_device_id deviceId = (cl_device_id) JNIHelper::callLong(jenv, deviceInstance, "getDeviceId", "()J"); 
   cl_device_type deviceType;
   cl_int status = CL_SUCCESS;
   clGetDeviceInfo(deviceId, CL_DEVICE_TYPE,  sizeof(deviceType), &deviceType, NULL);
   fprintf(stderr, "device[%d] CL_DEVICE_TYPE = %x\n", deviceId, deviceType);
  const char *nameChars = jenv->GetStringUTFChars(name, NULL);

   cl_kernel kernel = clCreateKernel(program, "run", &status);
  jenv->ReleaseStringUTFChars(name, nameChars);

   jobject kernelEntrypointInstance = NULL;

   if (status == CL_SUCCESS){
      kernelEntrypointInstance = JNIHelper::createInstance(jenv, "com/amd/aparapi/KernelEntrypoint", "(JLcom/amd/aparapi/CompilationUnit;)V", (jlong)kernel, compilationUnitInstance);
   }
   return(kernelEntrypointInstance);

}

APARAPI_JAVA(jobject, JNI, getPlatforms)(JNIEnv *jenv, jobject jobj) {
   jobject platformListInstance = JNIHelper::createInstance(jenv, "java/util/ArrayList", "()V");
   cl_int status = CL_SUCCESS;
   cl_uint platformc;

   status = clGetPlatformIDs(0, NULL, &platformc);
   fprintf(stderr, "There are %d platforms\n", platformc);
   cl_platform_id* platformIds = new cl_platform_id[platformc];
   status = clGetPlatformIDs(platformc, platformIds, NULL);

   if (status == CL_SUCCESS){
      for (unsigned platformIdx = 0; platformIdx < platformc; ++platformIdx) {
         char platformVersionName[512];
         status = clGetPlatformInfo(platformIds[platformIdx], CL_PLATFORM_VERSION, sizeof(platformVersionName), platformVersionName, NULL);
         if (!strncmp(platformVersionName, "OpenCL 1.1", 10)) { 
            char platformVendorName[512];  
            status = clGetPlatformInfo(platformIds[platformIdx], CL_PLATFORM_VENDOR, sizeof(platformVendorName), platformVendorName, NULL);
            fprintf(stderr, "platform vendor    %d %s\n", platformIdx, platformVendorName); 
            fprintf(stderr, "platform version %d %s\n", platformIdx, platformVersionName); 
            jobject platformInstance = JNIHelper::createInstance(jenv, "com/amd/aparapi/Platform", "(JLjava/lang/String;Ljava/lang/String;)V", 
                  (jlong)platformIds[platformIdx],
                  jenv->NewStringUTF(platformVersionName), 
                  jenv->NewStringUTF(platformVendorName));
            JNIHelper::callVoid(jenv, platformListInstance, "add", "(Ljava/lang/Object;)Z", platformInstance);

            cl_uint deviceIdc;
            cl_device_type requestedDeviceType =CL_DEVICE_TYPE_CPU |CL_DEVICE_TYPE_GPU ;
            status = clGetDeviceIDs(platformIds[platformIdx], requestedDeviceType, 0, NULL, &deviceIdc);
            if (status == CL_SUCCESS && deviceIdc >0 ){
               cl_device_id* deviceIds = new cl_device_id[deviceIdc];
               status = clGetDeviceIDs(platformIds[platformIdx], requestedDeviceType, deviceIdc, deviceIds, NULL);
               if (status == CL_SUCCESS){
                  for (unsigned deviceIdx=0; deviceIdx<deviceIdc; deviceIdx++){

                     cl_device_type deviceType;
                     status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_TYPE,  sizeof(deviceType), &deviceType, NULL);
                     jobject deviceTypeEnumInstance = JNIHelper::getStaticFieldObjectValue(jenv, "com/amd/aparapi/Device$TYPE", "UNKNOWN", "Lcom/amd/aparapi/Device$TYPE;");
                     fprintf(stderr, "device[%d] CL_DEVICE_TYPE = ", deviceIdx);
                     if (deviceType & CL_DEVICE_TYPE_DEFAULT) {
                        deviceType &= ~CL_DEVICE_TYPE_DEFAULT;
                        fprintf(stderr, "Default ");
                     }
                     if (deviceType & CL_DEVICE_TYPE_CPU) {
                        deviceType &= ~CL_DEVICE_TYPE_CPU;
                        fprintf(stderr, "CPU ");
                        deviceTypeEnumInstance = JNIHelper::getStaticFieldObjectValue(jenv, "com/amd/aparapi/Device$TYPE", "CPU", "Lcom/amd/aparapi/Device$TYPE;");
                     }
                     if (deviceType & CL_DEVICE_TYPE_GPU) {
                        deviceType &= ~CL_DEVICE_TYPE_GPU;
                        fprintf(stderr, "GPU ");
                        deviceTypeEnumInstance = JNIHelper::getStaticFieldObjectValue(jenv, "com/amd/aparapi/Device$TYPE", "GPU", "Lcom/amd/aparapi/Device$TYPE;");
                     }
                     if (deviceType & CL_DEVICE_TYPE_ACCELERATOR) {
                        deviceType &= ~CL_DEVICE_TYPE_ACCELERATOR;
                        fprintf(stderr, "Accelerator ");
                     }
                     fprintf(stderr, "(0x%llx) ", deviceType);
                     fprintf(stderr, "\n");


                     jobject deviceInstance = JNIHelper::createInstance(jenv, "com/amd/aparapi/Device", "(Lcom/amd/aparapi/Platform;JLcom/amd/aparapi/Device$TYPE;)V",
                           platformInstance, 
                           (jlong)deviceIds[deviceIdx],
                           deviceTypeEnumInstance);
                     JNIHelper::callVoid(jenv, platformInstance, "add", "(Lcom/amd/aparapi/Device;)V", deviceInstance);


                     cl_uint maxComputeUnits;
                     status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_COMPUTE_UNITS,  sizeof(maxComputeUnits), &maxComputeUnits, NULL);
                     //fprintf(stderr, "device[%d] CL_DEVICE_MAX_COMPUTE_UNITS = %u\n", deviceIdx, maxComputeUnits);
                     JNIHelper::callVoid(jenv, deviceInstance, "setMaxComputeUnits", "(I)V",  maxComputeUnits);



                     cl_uint maxWorkItemDimensions;
                     status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS,  sizeof(maxWorkItemDimensions), &maxWorkItemDimensions, NULL);
                     //fprintf(stderr, "device[%d] CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS = %u\n", deviceIdx, maxWorkItemDimensions);
                     JNIHelper::callVoid(jenv, deviceInstance, "setMaxWorkItemDimensions", "(I)V",  maxWorkItemDimensions);

                     size_t *maxWorkItemSizes = new size_t[maxWorkItemDimensions];
                     status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_WORK_ITEM_SIZES,  sizeof(size_t)*maxWorkItemDimensions, maxWorkItemSizes, NULL);
                     for (unsigned dimIdx=0; dimIdx<maxWorkItemDimensions; dimIdx++){
                        //fprintf(stderr, "device[%d] dim[%d] = %d\n", deviceIdx, dimIdx, maxWorkItemSizes[dimIdx]);
                        JNIHelper::callVoid(jenv, deviceInstance, "setMaxWorkItemSize", "(II)V", dimIdx,maxWorkItemSizes[dimIdx]);
                     }

                     size_t maxWorkGroupSize;
                     status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_MAX_WORK_GROUP_SIZE,  sizeof(maxWorkGroupSize), &maxWorkGroupSize, NULL);
                     //fprintf(stderr, "device[%d] CL_DEVICE_MAX_GROUP_SIZE = %u\n", deviceIdx, maxWorkGroupSize);
                     JNIHelper::callVoid(jenv, deviceInstance, "setMaxWorkGroupSize", "(I)V",  maxWorkGroupSize);

                     cl_ulong globalMemSize;
                     status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_GLOBAL_MEM_SIZE,  sizeof(globalMemSize), &globalMemSize, NULL);
                     //fprintf(stderr, "device[%d] CL_DEVICE_GLOBAL_MEM_SIZE = %lu\n", deviceIdx, globalMemSize);
                     JNIHelper::callVoid(jenv, deviceInstance, "setGlobalMemSize", "(J)V",  globalMemSize);

                     cl_ulong localMemSize;
                     status = clGetDeviceInfo(deviceIds[deviceIdx], CL_DEVICE_LOCAL_MEM_SIZE,  sizeof(localMemSize), &localMemSize, NULL);
                     //fprintf(stderr, "device[%d] CL_DEVICE_LOCAL_MEM_SIZE = %lu\n", deviceIdx, localMemSize);
                     JNIHelper::callVoid(jenv, deviceInstance, "setLocalMemSize", "(J)V",  localMemSize);
                  }

               }
            }
         }

      }
   }
   return (platformListInstance);
}

