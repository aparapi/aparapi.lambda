#include "KernelArg.h"
#include "JNIContext.h"


jclass KernelArg::argClazz=(jclass)0;
jfieldID KernelArg::nameFieldID=0;
jfieldID KernelArg::typeFieldID=0; 
jfieldID KernelArg::javaArrayFieldID=0; 
jfieldID KernelArg::sizeInBytesFieldID=0;
jfieldID KernelArg::numElementsFieldID=0; 

KernelArg::KernelArg(JNIEnv *jenv, JNIContext *jniContext, jobject argObj):
   jniContext(jniContext),
   argObj(argObj){
      javaArg = jenv->NewGlobalRef(argObj);   // save a global ref to the java Arg Object
      if (argClazz == 0){
         jclass c = jenv->GetObjectClass(argObj); 
         nameFieldID = jenv->GetFieldID(c, "name", "Ljava/lang/String;"); ASSERT_FIELD(name);
         typeFieldID = jenv->GetFieldID(c, "type", "I"); ASSERT_FIELD(type);
         javaArrayFieldID = jenv->GetFieldID(c, "javaArray", "Ljava/lang/Object;"); ASSERT_FIELD(javaArray);
         sizeInBytesFieldID = jenv->GetFieldID(c, "sizeInBytes", "I"); ASSERT_FIELD(sizeInBytes);
         numElementsFieldID = jenv->GetFieldID(c, "numElements", "I"); ASSERT_FIELD(numElements);
         argClazz  = c;
      }
      type = jenv->GetIntField(argObj, typeFieldID);
      jstring nameString  = (jstring)jenv->GetObjectField(argObj, nameFieldID);
      const char *nameChars = jenv->GetStringUTFChars(nameString, NULL);
#ifdef _WIN32
      name=_strdup(nameChars);
#else
      name=strdup(nameChars);
#endif
      jenv->ReleaseStringUTFChars(nameString, nameChars);
      if (isArray()){
         arrayBuffer= new ArrayBuffer();
      }
   }

cl_int KernelArg::setLocalBufferArg(JNIEnv *jenv, int argIdx, int argPos){
   if (config->isVerbose()){
       fprintf(stderr, "ISLOCAL, clSetKernelArg(jniContext->kernel, %d, %d, NULL);\n", argIdx, (int) arrayBuffer->lengthInBytes);
   }
   return(clSetKernelArg(jniContext->kernel, argPos, (int)arrayBuffer->lengthInBytes, NULL));
}

cl_int KernelArg::setPrimitiveArg(JNIEnv *jenv, int argIdx, int argPos){
   cl_int status = CL_SUCCESS;
   if (isFloat()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "F");
         jfloat f = jenv->GetStaticFloatField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive float '%s' index=%d pos=%d value=%f\n",
                 name, argIdx, argPos, f); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jfloat), &f);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "F");
         jfloat f = jenv->GetFloatField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive float '%s' index=%d pos=%d value=%f\n",
                 name, argIdx, argPos, f); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jfloat), &f);
      }
   }else if (isInt()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "I");
         jint i = jenv->GetStaticIntField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive int '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, i); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jint), &i);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "I");
         jint i = jenv->GetIntField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive int '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, i); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jint), &i);
      }
   }else if (isBoolean()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "Z");
         jboolean z = jenv->GetStaticBooleanField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive boolean '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, z); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jboolean), &z);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "Z");
         jboolean z = jenv->GetBooleanField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive boolean '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, z); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jboolean), &z);
      }
   }else if (isByte()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "B");
         jbyte b = jenv->GetStaticByteField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive byte '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, b); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jbyte), &b);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "B");
         jbyte b = jenv->GetByteField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive byte '%s' index=%d pos=%d value=%d\n",
                 name, argIdx, argPos, b); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jbyte), &b);
      }
   }else if (isLong()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "J");
         jlong j = jenv->GetStaticLongField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive long '%s' index=%d pos=%d value=%ld\n",
                 name, argIdx, argPos, j); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jlong), &j);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "J");
         jlong j = jenv->GetLongField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive long '%s' index=%d pos=%d value=%ld\n",
                 name, argIdx, argPos, j); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jlong), &j);
      }
   }else if (isDouble()){
      if (isStatic()){
         jfieldID fieldID = jenv->GetStaticFieldID(jniContext->kernelClass, name, "D");
         jdouble d  = jenv->GetStaticDoubleField(jniContext->kernelClass, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg static primitive long '%s' index=%d pos=%d value=%lf\n",
                 name, argIdx, argPos, d); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jdouble), &d);
      }else{
         jfieldID fieldID = jenv->GetFieldID(jniContext->kernelClass, name, "D");
         jdouble d = jenv->GetDoubleField(jniContext->kernelObject, fieldID);
         if (config->isVerbose()){
            fprintf(stderr, "clSetKernelArg primitive long '%s' index=%d pos=%d value=%lf\n",
                 name, argIdx, argPos, d); 
         }
         status = clSetKernelArg(jniContext->kernel, argPos, sizeof(jdouble), &d);
      }
   }
   return status;
}

