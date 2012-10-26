
#ifndef KERNEL_ARG_H
#define KERNEL_ARG_H

#include "common.h"
#include "jniHelper.h"
#include "arrayBuffer.h"
#include "com_amd_aparapi_internal_jni_KernelRunnerJNI.h"
#include "config.h"

class JNIContext;

class KernelArg{
   private:
      static jclass argClazz;
      static jfieldID nameFieldID;
      static jfieldID typeFieldID; 
      static jfieldID sizeInBytesFieldID;
      static jfieldID numElementsFieldID;
   public:
      static jfieldID javaArrayFieldID; 
   public:
      JNIContext *jniContext;  
      jobject argObj;    // the Java KernelRunner.KernelArg object that we are mirroring.
      jobject javaArg;   // global reference to the corresponding java KernelArg object we grabbed our own global reference so that the object won't be collected until we dispose!
      char *name;        // used for debugging printfs
      jint type;         // a bit mask determining the type of this arg

      ArrayBuffer *arrayBuffer;

      KernelArg(JNIEnv *jenv, JNIContext *jniContext, jobject argObj); // Uses JNIContext so cant inline here see below

      ~KernelArg(){
      }

      void unpinAbort(JNIEnv *jenv){
         arrayBuffer->unpinAbort(jenv);
      }
      void unpinCommit(JNIEnv *jenv){
         arrayBuffer->unpinCommit(jenv);
      }
      void unpin(JNIEnv *jenv){
         //if  (value.ref.isPinned == JNI_FALSE){		 
         //     fprintf(stdout, "why are we unpinning buffer %s! isPinned = JNI_TRUE\n", name);
         //}
         if (isMutableByKernel()){
            // we only need to commit if the buffer has been written to
            // we use mode=0 in that case (rather than JNI_COMMIT) because that frees any copy buffer if it exists
            // in most cases this array will have been pinned so this will not be an issue
            unpinCommit(jenv);
         }else {
            // fast path for a read_only buffer
            unpinAbort(jenv);
         }
      }
      void pin(JNIEnv *jenv){
         arrayBuffer->pin(jenv);
      }

      int isArray(){
         return(type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_ARRAY);
      }
      int isReadByKernel(){
         return(type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_READ);
      }
      int isMutableByKernel(){
         return(type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_WRITE);
      }
      int isExplicit(){
         return(type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_EXPLICIT);
      }
      int usesArrayLength(){
         return(type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_ARRAYLENGTH);
      }
      int isExplicitWrite(){
         return(type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_EXPLICIT_WRITE);
      }
      int isImplicit(){
         return(!isExplicit());
      }
      int isPrimitive(){
         return(type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_PRIMITIVE);
      }
      int isGlobal(){
         return(type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_GLOBAL);
      }
      int isFloat(){
         return(type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_FLOAT);
      }
      int isLong(){
         return (type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_LONG);
      }
      int isInt(){
         return (type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_INT);
      }
      int isDouble(){
         return (type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_DOUBLE);
      }
      int isBoolean(){
         return (type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_BOOLEAN);
      }
      int isByte(){
         return (type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_BYTE);
      }
      int isShort(){
         return (type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_SHORT);
      }
      int isLocal(){
         return (type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_LOCAL);
      }
      int isStatic(){
         return (type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_STATIC);
      }
      int isConstant(){
         return (type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_CONSTANT);
      }
      int isAparapiBuf(){
         return (type&com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_APARAPI_BUF);
      }
      int isBackedByArray(){
         return ( (isArray() && (isGlobal() || isConstant())));
      }
      int needToEnqueueRead(){
         return(((isArray() && isGlobal()) || ((isAparapiBuf()&&isGlobal()))) && (isImplicit()&&isMutableByKernel()));
      }
      int needToEnqueueWrite(){
         return ((isImplicit()&&isReadByKernel())||(isExplicit()&&isExplicitWrite()));
      }
      void syncType(JNIEnv* jenv){
         type = jenv->GetIntField(javaArg, typeFieldID);
      }
      void syncSizeInBytes(JNIEnv* jenv){
         arrayBuffer->lengthInBytes = jenv->GetIntField(javaArg, sizeInBytesFieldID);
      }
      void syncJavaArrayLength(JNIEnv* jenv){
         arrayBuffer->length = jenv->GetIntField(javaArg, numElementsFieldID);
      }
      void clearExplicitBufferBit(JNIEnv* jenv){
         type &= ~com_amd_aparapi_internal_jni_KernelRunnerJNI_ARG_EXPLICIT_WRITE;
         jenv->SetIntField(javaArg, typeFieldID,type );
      }

      void syncValue(JNIEnv *jenv); // Uses JNIContext so can't inline here we below.  
      cl_int setLocalBufferArg(JNIEnv *jenv, int argIdx, int argPos); // Uses JNIContext so can't inline here we below.  
      cl_int setPrimitiveArg(JNIEnv *jenv, int argIdx, int argPos ); // Uses JNIContext so can't inline here we below.  
};


#endif // KERNEL_ARG_H
