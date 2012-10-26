
#include "Range.h"

jclass Range::rangeClazz = (jclass)0;
jfieldID  Range::globalSize_0_FieldID=0;
jfieldID  Range::globalSize_1_FieldID=0;
jfieldID  Range::globalSize_2_FieldID=0;
jfieldID  Range::localSize_0_FieldID=0;
jfieldID  Range::localSize_1_FieldID=0;
jfieldID  Range::localSize_2_FieldID=0;
jfieldID  Range::dimsFieldID=0;
jfieldID  Range::localIsDerivedFieldID=0; 

Range::Range(JNIEnv *jenv, jobject range):
         range(range),
         dims(0),
         offsets(NULL),
         globalDims(NULL),
         localDims(NULL){
   if (rangeClazz ==NULL){
      jclass rangeClazz = jenv->GetObjectClass(range); 
      globalSize_0_FieldID = jenv->GetFieldID(rangeClazz, "globalSize_0", "I"); ASSERT_FIELD(globalSize_0_);
      globalSize_1_FieldID = jenv->GetFieldID(rangeClazz, "globalSize_1", "I"); ASSERT_FIELD(globalSize_1_);
      globalSize_2_FieldID = jenv->GetFieldID(rangeClazz, "globalSize_2", "I"); ASSERT_FIELD(globalSize_2_);
      localSize_0_FieldID = jenv->GetFieldID(rangeClazz, "localSize_0", "I"); ASSERT_FIELD(localSize_0_);
      localSize_1_FieldID = jenv->GetFieldID(rangeClazz, "localSize_1", "I"); ASSERT_FIELD(localSize_1_);
      localSize_2_FieldID = jenv->GetFieldID(rangeClazz, "localSize_2", "I"); ASSERT_FIELD(localSize_2_);
      dimsFieldID = jenv->GetFieldID(rangeClazz, "dims", "I"); ASSERT_FIELD(dims);
      localIsDerivedFieldID = jenv->GetFieldID(rangeClazz, "localIsDerived", "Z"); ASSERT_FIELD(localIsDerived);
   }
   dims = jenv->GetIntField(range, dimsFieldID);
   localIsDerived = jenv->GetBooleanField(range, localIsDerivedFieldID);
   if (dims >0){
      //fprintf(stderr, "native range dims == %d\n", dims);
      offsets = new size_t[dims];
      globalDims = new size_t[dims];
      localDims = new size_t[dims];
      offsets[0]= 0;
      localDims[0]= jenv->GetIntField(range, localSize_0_FieldID);
      //fprintf(stderr, "native range localSize_0 == %d\n", localDims[0]);
      globalDims[0]= jenv->GetIntField(range, globalSize_0_FieldID);
      //fprintf(stderr, "native range globalSize_0 == %d\n", globalDims[0]);
      if (dims >1){
         offsets[1]= 0;
         localDims[1]= jenv->GetIntField(range, localSize_1_FieldID);
         //fprintf(stderr, "native range localSize_1 == %d\n", localDims[1]);
         globalDims[1]= jenv->GetIntField(range, globalSize_1_FieldID);
         //fprintf(stderr, "native range globalSize_1 == %d\n", globalDims[1]);
         if (dims >2){
            offsets[2]= 0;
            localDims[2]= jenv->GetIntField(range, localSize_2_FieldID);
            //fprintf(stderr, "native range localSize_2 == %d\n", localDims[2]);
            globalDims[2]= jenv->GetIntField(range, globalSize_2_FieldID);
            //fprintf(stderr, "native range globalSize_2 == %d\n", globalDims[2]);
         }
      }

   }
}


Range::~Range(){
   if (offsets!= NULL){
      delete offsets;
   }
   if (globalDims!= NULL){
      delete globalDims;
   }
   if (localDims!= NULL){
      delete localDims;
   }
}


