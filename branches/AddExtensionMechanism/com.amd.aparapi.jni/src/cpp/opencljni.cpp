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

#include "common.h"
#include "jniHelper.h"
#include "clHelper.h"

#define OPENCLJNI_SOURCE
#include "opencljni.h"

#include "com_amd_opencl_OpenCLJNI.h"

#define JNI_JAVA(type, className, methodName) JNIEXPORT type JNICALL Java_com_amd_opencl_##className##_##methodName
#define isset(bits, token) (((bits) & com_amd_opencl_OpenCLJNI_##token##_BIT) ==com_amd_opencl_OpenCLJNI_##token##_BIT) 
#define set(bits, token) (bits) |= com_amd_opencl_OpenCLJNI_##token##_BIT 
#define reset(bits, token) (bits) &= ~com_amd_opencl_OpenCLJNI_##token##_BIT 

class Device{
   public:
      static jobject getPlatformInstance(JNIEnv *jenv, jobject deviceInstance){
         return(JNIHelper::getInstanceFieldObject(jenv, deviceInstance, "platform", "Lcom/amd/opencl/Platform;"));
      } 
      static cl_device_id getDeviceId(JNIEnv *jenv, jobject deviceInstance){
         return((cl_device_id)JNIHelper::getInstanceFieldLong(jenv, deviceInstance, "deviceId"));
      }
};
class Platform{
   public:
      static cl_platform_id getPlatformId(JNIEnv *jenv, jobject platformInstance){
         return((cl_platform_id)JNIHelper::getInstanceFieldLong(jenv, platformInstance, "platformId"));
      }
};
class Program{
   public:
      static jobject create(JNIEnv *jenv, cl_program program, cl_command_queue queue, cl_context context, jobject deviceInstance, jstring source, jstring log){
         return(JNIHelper::createInstance(jenv, "com/amd/opencl/Program", "(JJJLcom/amd/opencl/Device;Ljava/lang/String;Ljava/lang/String;)V", (jlong)program, (jlong)queue, (jlong)context, deviceInstance, source, log));
      }
      static cl_context getContext(JNIEnv *jenv, jobject programInstance){
         return((cl_context) JNIHelper::getInstanceFieldLong(jenv, programInstance, "contextId")); 
      }
      static cl_program getProgram(JNIEnv *jenv, jobject programInstance){
         return((cl_program) JNIHelper::getInstanceFieldLong(jenv, programInstance, "programId")); 
      }
      static cl_command_queue getCommandQueue(JNIEnv *jenv, jobject programInstance){
         return((cl_command_queue)JNIHelper::getInstanceFieldLong(jenv, programInstance, "queueId"));
      }
};
class Kernel{
   public:
      static jobject create(JNIEnv *jenv, cl_kernel kernel, jobject programInstance, jstring name, jobject args){
         return(JNIHelper::createInstance(jenv, "com/amd/opencl/Kernel", "(JLcom/amd/opencl/Program;Ljava/lang/String;Ljava/util/List;)V", (jlong)kernel, programInstance, name, args));
      }

      static cl_kernel getKernel(JNIEnv *jenv, jobject kernelInstance){
         return((cl_kernel) JNIHelper::getInstanceFieldLong(jenv, kernelInstance, "kernelId"));
      }

      static jobject getProgramInstance(JNIEnv *jenv, jobject kernelInstance){
         return(JNIHelper::getInstanceFieldObject(jenv, kernelInstance, "program", "Lcom/amd/opencl/Program;"));
      }

      static jobjectArray getArgsArray(JNIEnv *jenv, jobject kernelInstance){
         return(reinterpret_cast<jobjectArray> (JNIHelper::getInstanceFieldObject(jenv, kernelInstance, "args", "[Lcom/amd/opencl/Arg;")));
      }
};

class Mem{
   public:
      static jobject create(JNIEnv *jenv){
         return(JNIHelper::createInstance(jenv, "Lcom/amd/opencl/Mem;", "()V"));
      }
      static jlong getBits(JNIEnv *jenv, jobject memInstance){
         return(JNIHelper::getInstanceFieldLong(jenv, memInstance, "bits"));
      }
      static void setBits(JNIEnv *jenv, jobject memInstance, jlong bits){
         JNIHelper::setInstanceFieldLong(jenv, memInstance, "bits", bits);
      }
      static void *getAddress(JNIEnv *jenv, jobject memInstance){
         return((void*)JNIHelper::getInstanceFieldLong(jenv, memInstance, "address"));
      }
      static void setAddress(JNIEnv *jenv, jobject memInstance, void *address){
         JNIHelper::setInstanceFieldLong(jenv, memInstance, "address", (jlong)address);
      }
      static void setInstance(JNIEnv *jenv, jobject memInstance, jobject instance){
         JNIHelper::setInstanceFieldObject(jenv, memInstance, "instance", "Ljava/lang/Object;", instance);
      }
      static void setSizeInBytes(JNIEnv *jenv, jobject memInstance, jint sizeInBytes){
         JNIHelper::setInstanceFieldInt(jenv, memInstance, "sizeInBytes", sizeInBytes);
      }
      static size_t getSizeInBytes(JNIEnv *jenv, jobject memInstance){
         return((size_t)JNIHelper::getInstanceFieldInt(jenv, memInstance, "sizeInBytes"));
      }
      static jobject getInstance(JNIEnv *jenv, jobject memInstance){
         return(JNIHelper::getInstanceFieldObject(jenv, memInstance, "instance", "Ljava/lang/Object;"));
      }

      static cl_mem getMem(JNIEnv *jenv, jobject memInstance, jlong bits){
         cl_mem mem = 0;

         if (isset(bits, READONLY)){
            mem = (cl_mem)JNIHelper::getInstanceFieldLong(jenv, memInstance, "readOnlyMemId");
         } else if (isset(bits, READWRITE)){
            mem = (cl_mem)JNIHelper::getInstanceFieldLong(jenv, memInstance, "readWriteMemId");
         } else if (isset(bits, WRITEONLY)){
            mem = (cl_mem)JNIHelper::getInstanceFieldLong(jenv, memInstance, "writeOnlyMemId");
         }
         return(mem);
      }

      static void setMem(JNIEnv *jenv, jobject memInstance, jlong bits, cl_mem mem){
         if (isset(bits, READONLY)){
            JNIHelper::setInstanceFieldLong(jenv, memInstance, "readOnlyMemId", (jlong)mem);
         } else if (isset(bits, READWRITE)){
            JNIHelper::setInstanceFieldLong(jenv, memInstance, "readWriteMemId", (jlong)mem);
         } else if (isset(bits, WRITEONLY)){
            JNIHelper::setInstanceFieldLong(jenv, memInstance, "writeOnlyMemId", (jlong)mem);
         }
      }

};

class Arg{
   public:
      static jlong getBits(JNIEnv *jenv, jobject argInstance){
         return(JNIHelper::getInstanceFieldLong(jenv, argInstance, "bits"));
      }
      static void setBits(JNIEnv *jenv, jobject argInstance, jlong bits){
         JNIHelper::setInstanceFieldLong(jenv, argInstance, "bits", bits);
      }
      static jobject getMemInstance(JNIEnv *jenv, jobject argInstance){
         return(JNIHelper::getInstanceFieldObject(jenv, argInstance, "memVal", "Lcom/amd/opencl/Mem;"));
      }
      static void setMemInstance(JNIEnv *jenv, jobject argInstance, jobject memInstance){
         JNIHelper::setInstanceFieldObject(jenv, argInstance, "memVal", "Lcom/amd/opencl/Mem;",memInstance);
      }
};
class Range{
   public:
      static jint getDims(JNIEnv *jenv, jobject rangeInstance){
         return(JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "dims"));
      }

      static void fill(JNIEnv *jenv, jobject rangeInstance, jint dims, size_t* offsets, size_t* globalDims, size_t* localDims){
         if (dims >0){
            offsets[0]= 0;
            localDims[0]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "localSize_0"); 
            globalDims[0]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "globalSize_0"); 
            if (dims >1){
               offsets[1]= 0;
               localDims[1]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "localSize_1"); 
               globalDims[1]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "globalSize_1"); 
               if (dims >2){
                  offsets[2]= 0;
                  localDims[2]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "localSize_2"); 
                  globalDims[2]=JNIHelper::getInstanceFieldInt(jenv, rangeInstance, "globalSize_2"); 
               }
            }
         }
      }
};

JNI_JAVA(jobject, OpenCLJNI, createProgram)
   (JNIEnv *jenv, jobject jobj, jobject deviceInstance, jstring source) {

      jobject platformInstance = Device::getPlatformInstance(jenv, deviceInstance);
      cl_platform_id platformId = Platform::getPlatformId(jenv, platformInstance);
      cl_device_id deviceId = Device::getDeviceId(jenv, deviceInstance);
      cl_int status = CL_SUCCESS;
      cl_device_type deviceType;
      clGetDeviceInfo(deviceId, CL_DEVICE_TYPE,  sizeof(deviceType), &deviceType, NULL);
      //fprintf(stderr, "device[%d] CL_DEVICE_TYPE = %x\n", deviceId, deviceType);


      cl_context_properties cps[3] = { CL_CONTEXT_PLATFORM, (cl_context_properties)platformId, 0 };
      cl_context_properties* cprops = (NULL == platformId) ? NULL : cps;
      cl_context context = clCreateContextFromType( cprops, deviceType, NULL, NULL, &status);


      jstring log=NULL;
      cl_program program = CLHelper::compile(jenv, context, 1, &deviceId, source, &log, &status);
      cl_command_queue queue = NULL;
      if(status == CL_SUCCESS) {
         cl_command_queue_properties queue_props = CL_QUEUE_PROFILING_ENABLE;
         queue = clCreateCommandQueue(context, deviceId, queue_props, &status);
      }else{
         fprintf(stderr, "queue creation seems to have failed\n");

      }
      jobject programInstance = Program::create(jenv, program, queue, context, deviceInstance, source, log);

      return(programInstance);
   }

JNI_JAVA(jobject, OpenCLJNI, createKernel)
   (JNIEnv *jenv, jobject jobj, jobject programInstance, jstring name, jobject args) {
      cl_context context = Program::getContext(jenv, programInstance);
      cl_program program = Program::getProgram(jenv, programInstance); 
      cl_int status = CL_SUCCESS;
      const char *nameChars = jenv->GetStringUTFChars(name, NULL);
      fprintf(stderr, "tring to extract kernel '%s'\n", nameChars);
      cl_kernel kernel = clCreateKernel(program, nameChars, &status);
      jenv->ReleaseStringUTFChars(name, nameChars);

      if (kernel == NULL){
         fprintf(stderr, "kernel is null!\n");
      }

      jobject kernelInstance = NULL;
      if (status == CL_SUCCESS){
         kernelInstance = Kernel::create(jenv, kernel, programInstance, name, args);
      }else{
         fprintf(stderr, "kernel creation seems to have failed\n");
      }
      return(kernelInstance);

   }


void describeBits(JNIEnv *jenv, jlong bits){
   fprintf(stderr, " %lx ", bits);
   if (isset(bits, READONLY)){
      fprintf(stderr, "readonly ");
   }
   if (isset(bits, WRITEONLY)){
      fprintf(stderr, "writeonly ");
   }
   if (isset(bits, READWRITE)){
      fprintf(stderr, "readwrite ");
   }
   if (isset(bits, ARRAY)){
      fprintf(stderr, "array ");
   }
   if (isset(bits, PRIMITIVE)){
      fprintf(stderr, "primitive ");
   }
   if (isset(bits, FLOAT)){
      fprintf(stderr, "float ");
   }
   if (isset(bits, SHORT)){
      fprintf(stderr, "short ");
   }
   if (isset(bits, LONG)){
      fprintf(stderr, "long ");
   }
   if (isset(bits, DOUBLE)){
      fprintf(stderr, "double ");
   }
   if (isset(bits, INT)){
      fprintf(stderr, "int ");
   }
   if (isset(bits, COPY)){
      fprintf(stderr, "copy ");
   }
   if (isset(bits, GLOBAL)){
      fprintf(stderr, "global ");
   }
   if (isset(bits, LOCAL)){
      fprintf(stderr, "local ");
   }
   if (isset(bits, DIRTY)){
      fprintf(stderr, "dirty ");
   }
   if (isset(bits, ENQUEUED)){
      fprintf(stderr, "enqueued ");
   }
   if (isset(bits, ARG)){
      fprintf(stderr, "arg ");
   }
}

void describeArg(JNIEnv *jenv, jint argIndex, jobject argDef, jobject arg){
   jlong bits = Arg::getBits(jenv, argDef);
   fprintf(stderr, " %d ", argIndex);
   describeBits(jenv, bits);
   fprintf(stderr, "\n");
}

cl_uint bitsToOpenCLMask(jlong bits ){
   cl_uint mask =  CL_MEM_USE_HOST_PTR;
   if (isset(bits, READONLY)){
      mask|= CL_MEM_READ_ONLY;
   }else if (isset(bits, READWRITE)){
      mask|= CL_MEM_READ_WRITE;
   } else if (isset(bits, WRITEONLY)){
      mask|= CL_MEM_WRITE_ONLY;
   }
   return(mask);
}

jsize getArraySizeInBytes(JNIEnv *jenv, jarray array, jlong bits){
   jsize arrayLen = jenv->GetArrayLength(array);
   jsize sizeInBytes = 0;
   if (isset(bits, FLOAT) || isset(bits, INT)){
      sizeInBytes = arrayLen *4;
   }else if (isset(bits, DOUBLE) || isset(bits, LONG)){
      sizeInBytes = arrayLen *8;
   }else if (isset(bits, SHORT)){
      sizeInBytes = arrayLen *2;
   }
   fprintf(stderr, "size of new Mem arg is %d\n", sizeInBytes);
   return(sizeInBytes);
}

void *pin(JNIEnv *jenv, jarray array, jlong *bits){
   jboolean isCopy;
   void *ptr = jenv->GetPrimitiveArrayCritical(array,&isCopy); 
   if (bits != NULL){
      if(0){describeBits(jenv, *bits);fprintf(stderr, " before  \n");}
      if (isCopy){
         set(*bits, COPY);
      }else{
         reset(*bits, COPY);
      }
      if(0){describeBits(jenv, *bits);fprintf(stderr, " after \n");}
   }
   return(ptr);
}

void unpin(JNIEnv *jenv, jarray array, void *ptr, jlong *bits){
   if (isset(*bits, WRITEONLY)){
      jenv->ReleasePrimitiveArrayCritical(array, ptr,JNI_ABORT);
   }else{
      jenv->ReleasePrimitiveArrayCritical(array, ptr, 0);
   }
}


jobject createMem(JNIEnv *jenv, cl_context context,  jlong bits, jarray array){
   jsize sizeInBytes = getArraySizeInBytes(jenv, array, bits);

   cl_int status = CL_SUCCESS;
   void *ptr = pin(jenv, array, &bits); 
   cl_mem mem=clCreateBuffer(context, bitsToOpenCLMask(bits),  sizeInBytes, ptr, &status);
   if (status != CL_SUCCESS){
      fprintf(stderr, "buffer creation failed!\n");
   }

   jobject memInstance = Mem::create(jenv);
   fprintf(stderr, "created a new mem object!\n");
   Mem::setAddress(jenv, memInstance, ptr);
   Mem::setInstance(jenv, memInstance, array);
   Mem::setSizeInBytes(jenv, memInstance, sizeInBytes);
   Mem::setBits(jenv, memInstance, bits);
   Mem::setMem(jenv, memInstance, bits, mem);
   fprintf(stderr, "initiated mem object!\n");
   return(memInstance);
}

#define setPrimitiveArgField(jenv, kernel, argIndex, instance, clType, type, format)\
   clType value = JNIHelper::getInstanceField##type##(jenv, instance, "value");\
status = clSetKernelArg(kernel, argIndex, sizeof(value), (void *)&(value));          \
if (status != CL_SUCCESS) {\
   fprintf(stderr, "error setting arg %d " format " %s!\n",  argIndex, value, CLHelper::errString(status));\
}else{\
   if(0)fprintf(stderr, "set arg  %d to " format "\n", argIndex, value);\
}


void putArgs(JNIEnv *jenv, cl_context context, cl_kernel kernel, cl_command_queue commandQueue, cl_event *events, jint *eventc, jint argIndex, jobject argDef, jobject arg){
   if(0){
      fprintf(stderr, "putArgs ");
      describeArg(jenv, argIndex, argDef, arg);
   }
   cl_int status = CL_SUCCESS;
   jlong bits = Arg::getBits(jenv, argDef);
   if (isset(bits, ARRAY)){ // global check?
      jobject memInstance = Arg::getMemInstance(jenv, argDef);
      if (memInstance == NULL){
         // first call?
         memInstance = createMem(jenv, context, bits, (jarray)arg);
         Arg::setMemInstance(jenv, argDef, memInstance);
      }else{
         // check of bits == memInstance.bits
         // we need to pin it
         jboolean isCopy;
         void *ptr  =  pin(jenv, (jarray)arg,&bits); 
         void *oldPtr = Mem::getAddress(jenv, memInstance);
         if (ptr !=oldPtr){
            fprintf(stderr, "ptr moved from %lx to %lx\n", oldPtr, ptr);
            cl_mem mem = Mem::getMem(jenv, memInstance, bits);
            status = clReleaseMemObject(mem); 
            memInstance = createMem(jenv, context, bits, (jarray)arg);
            Arg::setMemInstance(jenv, argDef, memInstance);
         }
         Arg::setBits(jenv, argDef, bits);
      }
      cl_mem mem = Mem::getMem(jenv, memInstance, bits);
      cl_int status = CL_SUCCESS;
      if (isset(bits, READONLY)|isset(bits, READWRITE)){ // kernel reads this so enqueue a write
         void *ptr= Mem::getAddress(jenv, memInstance);
         size_t sizeInBytes= Mem::getSizeInBytes(jenv, memInstance);
         jlong memBits = Mem::getBits(jenv, memInstance);
         set(memBits, ENQUEUED);
         Mem::setBits(jenv, memInstance, memBits);
         status = clEnqueueWriteBuffer(commandQueue, mem, CL_FALSE, 0, sizeInBytes, ptr, *eventc, (*eventc)==0?NULL:events, &events[*eventc]);
         if (status != CL_SUCCESS) {
            fprintf(stderr, "error enqueuing write %s!\n",  CLHelper::errString(status));
         }else{
            if(0)fprintf(stderr, "enqueued write eventc = %d!\n", *eventc);
         }
         (*eventc)++;
      }
      status = clSetKernelArg(kernel, argIndex, sizeof(cl_mem), (void *)&(mem));          
      if (status != CL_SUCCESS) {
         fprintf(stderr, "error setting arg %d %s!\n",  argIndex, CLHelper::errString(status));
      }else{
         if(0)fprintf(stderr, "set arg  = %d!\n", argIndex);
      }
   }else if (isset(bits, PRIMITIVE)){
      if (isset(bits, INT)){
         cl_int value = JNIHelper::getInstanceFieldInt(jenv, arg, "value");
         status = clSetKernelArg(kernel, argIndex, sizeof(value), (void *)&(value));          
         if (status != CL_SUCCESS) {
            fprintf(stderr, "error setting int arg %d %d %s!\n",  argIndex, value, CLHelper::errString(status));
         }else{
            if(0)fprintf(stderr, "set arg  = %d to %d!\n", argIndex, value);
         }
      }else if (isset(bits, FLOAT)){
         cl_float value = JNIHelper::getInstanceFieldFloat(jenv, arg, "value");
         status = clSetKernelArg(kernel, argIndex, sizeof(value), (void *)&(value));          
         if (status != CL_SUCCESS) {
            fprintf(stderr, "error setting int arg %d %f %s!\n",  argIndex, value, CLHelper::errString(status));
         }else{
            if(0)fprintf(stderr, "set arg  = %d to %f!\n", argIndex, value);
         }

      }else if (isset(bits, DOUBLE)){
         cl_double value = JNIHelper::getInstanceFieldDouble(jenv, arg, "value");
         status = clSetKernelArg(kernel, argIndex, sizeof(value), (void *)&(value));          
         if (status != CL_SUCCESS) {
            fprintf(stderr, "error setting double arg %d %lf %s!\n",  argIndex, value, CLHelper::errString(status));
         }else{
            if(0)fprintf(stderr, "set arg  = %d to %lf!\n", argIndex, value);
         }

      }
   }
}




void getArgs(JNIEnv *jenv, cl_context context, cl_command_queue commandQueue, cl_event *events, jint *eventc, jint argIndex, jobject argDef, jobject arg){
   if (0){
      fprintf(stderr, "post ");
      describeArg(jenv, argIndex, argDef, arg);
   }
   jlong bits = Arg::getBits(jenv, argDef);
   if (isset(bits, ARRAY)){
      jobject memInstance = Arg::getMemInstance(jenv, argDef);
      if (memInstance == NULL){
         fprintf(stderr, "mem instance not set\n");
      }else{
         if(0)fprintf(stderr, "retrieved mem instance\n");
      }
      void *ptr= Mem::getAddress(jenv, memInstance);
      if (isset(bits, WRITEONLY)|isset(bits, READWRITE)){

         cl_mem mem = Mem::getMem(jenv, memInstance, bits);

         size_t sizeInBytes= Mem::getSizeInBytes(jenv, memInstance);
         if (0){
            fprintf(stderr, "about to enqueu read eventc = %d!\n", *eventc);
         }
         cl_int status = clEnqueueReadBuffer(commandQueue, mem, CL_FALSE, 0, sizeInBytes, ptr ,*eventc, (*eventc)==0?NULL:events, &events[*eventc]);
         if (status != CL_SUCCESS) {
            fprintf(stderr, "error enqueuing read %s!\n",  CLHelper::errString(status));
         }else{
            if (0){
               fprintf(stderr, "enqueued read eventc = %d!\n", *eventc);
            }
         }
         (*eventc)++;
      }

      jobject arrayInstance = Mem::getInstance(jenv, memInstance);

      unpin(jenv, (jarray)arrayInstance, ptr, &bits);


      reset(bits, ENQUEUED);
      reset(bits, COPY);
      Mem::setBits(jenv, memInstance, bits);
   }
}

JNI_JAVA(void, OpenCLJNI, invoke)
   (JNIEnv *jenv, jobject jobj, jobject kernelInstance, jobjectArray argArray) {


      cl_kernel kernel = Kernel::getKernel(jenv, kernelInstance);
      jobject programInstance = Kernel::getProgramInstance(jenv, kernelInstance);
      jobjectArray argDefsArray = Kernel::getArgsArray(jenv, kernelInstance);

      cl_context context =Program::getContext(jenv, programInstance);
      cl_command_queue commandQueue = Program::getCommandQueue(jenv, programInstance);


      // walk through the args creating buffers when needed 
      // we use the bitfields to determine which is which
      // note that argArray[0] is the range then 1,2,3 etc matches argDefsArray[0,1,2]
      jsize argc = jenv->GetArrayLength(argDefsArray);
      if (0) fprintf(stderr, "argc = %d\n", argc);
      jint reads=0;
      jint writes=0;
      for (jsize argIndex=0; argIndex<argc; argIndex++){
         jobject argDef = jenv->GetObjectArrayElement(argDefsArray, argIndex);
         jlong bits = Arg::getBits(jenv, argDef);
         if (isset(bits, READONLY)){
            reads++;
         }
         if (isset(bits, READWRITE)){
            reads++;
            writes++;
         }
         if (isset(bits, WRITEONLY)){
            writes++;
         }
      }

      if (0) fprintf(stderr, "reads=%d writes=%d\n", reads, writes);
      cl_event * events= new cl_event[reads+writes+1];

      jint eventc =0;

      for (jsize argIndex=0; argIndex<argc; argIndex++){
         jobject argDef = jenv->GetObjectArrayElement(argDefsArray, argIndex);
         jobject arg = jenv->GetObjectArrayElement(argArray, argIndex+1);
         putArgs(jenv, context, kernel, commandQueue, events, &eventc, argIndex, argDef, arg);
      }

      jobject rangeInstance = jenv->GetObjectArrayElement(argArray, 0);
      jint dims = Range::getDims(jenv, rangeInstance);

      size_t *offsets = new size_t[dims];
      size_t *globalDims = new size_t[dims];
      size_t *localDims = new size_t[dims];
      Range::fill(jenv, rangeInstance, dims, offsets, globalDims, localDims);

      cl_int status = CL_SUCCESS;

      if(0) fprintf(stderr, "Exec %d\n", eventc);
      status = clEnqueueNDRangeKernel(
            commandQueue,
            kernel,
            dims,
            offsets,
            globalDims,
            localDims,
            eventc, // count Of events to wait for
            eventc==0?NULL:events, // address of events to wait for
            &events[eventc]);
      if (status != CL_SUCCESS) {
         fprintf(stderr, "error enqueuing execute %s !\n", CLHelper::errString(status));
      }else{
         if (0) fprintf(stderr, "success enqueuing execute eventc= %d !\n", eventc);
      }
      eventc++;

      for (jsize argIndex=0; argIndex<argc; argIndex++){
         jobject argDef = jenv->GetObjectArrayElement(argDefsArray, argIndex);
         jobject arg = jenv->GetObjectArrayElement(argArray, argIndex+1);
         getArgs(jenv, context, commandQueue, events, &eventc, argIndex, argDef, arg);
      }
      status = clWaitForEvents(eventc, events);
      if (status != CL_SUCCESS) {
         fprintf(stderr, "error waiting for events !\n");
      }
   }

JNI_JAVA(jobject, OpenCLJNI, getPlatforms)
   (JNIEnv *jenv, jobject jobj) {
      jobject platformListInstance = JNIHelper::createInstance(jenv, "java/util/ArrayList", "()V");
      cl_int status = CL_SUCCESS;
      cl_uint platformc;

      status = clGetPlatformIDs(0, NULL, &platformc);
      //fprintf(stderr, "There are %d platforms\n", platformc);
      cl_platform_id* platformIds = new cl_platform_id[platformc];
      status = clGetPlatformIDs(platformc, platformIds, NULL);

      if (status == CL_SUCCESS){
         for (unsigned platformIdx = 0; platformIdx < platformc; ++platformIdx) {
            char platformVersionName[512];
            status = clGetPlatformInfo(platformIds[platformIdx], CL_PLATFORM_VERSION, sizeof(platformVersionName), platformVersionName, NULL);
            if (!strncmp(platformVersionName, "OpenCL 1.1", 10)) { 
               char platformVendorName[512];  
               status = clGetPlatformInfo(platformIds[platformIdx], CL_PLATFORM_VENDOR, sizeof(platformVendorName), platformVendorName, NULL);
               //fprintf(stderr, "platform vendor    %d %s\n", platformIdx, platformVendorName); 
               //fprintf(stderr, "platform version %d %s\n", platformIdx, platformVersionName); 
               jobject platformInstance = JNIHelper::createInstance(jenv, "com/amd/opencl/Platform", "(JLjava/lang/String;Ljava/lang/String;)V", 
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
                        jobject deviceTypeEnumInstance = JNIHelper::getStaticFieldObject(jenv, "com/amd/opencl/Device$TYPE", "UNKNOWN", "Lcom/amd/opencl/Device$TYPE;");
                        //fprintf(stderr, "device[%d] CL_DEVICE_TYPE = ", deviceIdx);
                        if (deviceType & CL_DEVICE_TYPE_DEFAULT) {
                           deviceType &= ~CL_DEVICE_TYPE_DEFAULT;
                           //  fprintf(stderr, "Default ");
                        }
                        if (deviceType & CL_DEVICE_TYPE_CPU) {
                           deviceType &= ~CL_DEVICE_TYPE_CPU;
                           //fprintf(stderr, "CPU ");
                           deviceTypeEnumInstance = JNIHelper::getStaticFieldObject(jenv, "com/amd/opencl/Device$TYPE", "CPU", "Lcom/amd/opencl/Device$TYPE;");
                        }
                        if (deviceType & CL_DEVICE_TYPE_GPU) {
                           deviceType &= ~CL_DEVICE_TYPE_GPU;
                           //fprintf(stderr, "GPU ");
                           deviceTypeEnumInstance = JNIHelper::getStaticFieldObject(jenv, "com/amd/opencl/Device$TYPE", "GPU", "Lcom/amd/opencl/Device$TYPE;");
                        }
                        if (deviceType & CL_DEVICE_TYPE_ACCELERATOR) {
                           deviceType &= ~CL_DEVICE_TYPE_ACCELERATOR;
                           //fprintf(stderr, "Accelerator ");
                        }
                        //fprintf(stderr, "(0x%llx) ", deviceType);
                        //fprintf(stderr, "\n");


                        jobject deviceInstance = JNIHelper::createInstance(jenv, "com/amd/opencl/Device", "(Lcom/amd/opencl/Platform;JLcom/amd/opencl/Device$TYPE;)V",
                              platformInstance, 
                              (jlong)deviceIds[deviceIdx],
                              deviceTypeEnumInstance);
                        JNIHelper::callVoid(jenv, platformInstance, "add", "(Lcom/amd/opencl/Device;)V", deviceInstance);


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

