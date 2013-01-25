
#include <stdio.h>
#include <stdlib.h>

#ifndef __APPLE__
#include <malloc.h>
#endif

#include <sys/types.h>
#ifndef _WIN32
#include <unistd.h>
#endif

#include "classtools.h"
#include "instruction.h"

int main(int argc, char **argv){
   FILE *classFile = fopen("Main.class", "rb");
   if (classFile == NULL){fputs ("OPen error",stderr); exit (1);}
   fseek(classFile, 0 , SEEK_END);
   long size = ftell(classFile);
   rewind (classFile);

   // allocate memory to contain the whole file:
   char *buffer = new char[size];
   if (buffer == NULL) {fputs ("Memory error",stderr); exit (2);}

   // copy the file into the buffer:
   size_t result = fread (buffer,1,size, classFile);
   fclose (classFile);
   if (result != size) {fputs ("Reading error",stderr); exit (3);}

   fprintf(stdout, "read %ld bytes\n", size);

   ByteBuffer *byteBuffer = new ByteBuffer((byte_t*)buffer, size);

   ClassInfo *classInfo = new ClassInfo(byteBuffer);
   MethodInfo *methodInfo = classInfo->getMethodInfo((char*)"getCount", (char*)"(FF)I");
   CodeAttribute *codeAttribute = methodInfo->getCodeAttribute();
   ByteBuffer *codeByteBuffer = codeAttribute->getCodeByteBuffer();

   u2_t maxStack = codeAttribute->getMaxStack();
   u4_t *stackMap = new u4_t[maxStack];
   u2_t stackSize=0;
   for (int i=0; i<maxStack; i++){
      stackMap[i]=-1;
   }

   Instruction** instructions = new Instruction*[codeByteBuffer->getLen()];
   for (unsigned i=0; i< codeByteBuffer->getLen(); i++){
      instructions[i] = NULL;
   }

   while (!codeByteBuffer->empty()){
      Instruction *instruction = new Instruction(codeByteBuffer);
      instructions[instruction->getPC()] = instruction;

      switch(instruction->getByteCode()->pushSpec){
         case PushSpec_NONE:
            break;
         case PushSpec_N:
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_I:
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_L:
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_F:
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_D:
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_O:
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_A:
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_RA:
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_IorForS:
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_LorD:
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_II:
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_III:
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_IIII:
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_IIIII:
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_IIIIII:
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            stackMap[stackSize++] = instruction->getPC();
            break;
         case PushSpec_UNKNOWN:
            break;
      }
      switch(instruction->getByteCode()->popSpec){
         case PopSpec_NONE:
            break;
         case PopSpec_A:
            stackSize--;
            break;
         case PopSpec_AI:
            stackSize-=2;
            break;
         case PopSpec_AII:
            stackSize-=3;
            break;
         case PopSpec_AIL:
            stackSize-=3;
            break;
         case PopSpec_AIF:
            stackSize-=3;
            break;
         case PopSpec_AID:
            stackSize-=3;
            break;
         case PopSpec_AIO:
            stackSize-=3;
            break;
         case PopSpec_AIB:
            stackSize-=3;
            break;
         case PopSpec_AIC:
            stackSize-=3;
            break;
         case PopSpec_AIS:
            stackSize-=3;
            break;
         case PopSpec_II :
            stackSize-=2;
            break;
         case PopSpec_III:
            stackSize-=3;
            break;
         case PopSpec_IIII:
            stackSize-=4;
            break;
         case PopSpec_L:
            stackSize--;
            break;
         case PopSpec_LI:
            stackSize-=2;
            break;
         case PopSpec_LL:
            stackSize-=2;
            break;
         case PopSpec_F:
            stackSize--;
            break;
         case PopSpec_FF:
            stackSize-=2;
            break;
         case PopSpec_OO:
            stackSize-=2;
            break;
         case PopSpec_RA:
            stackSize-=2;
            break;
         case PopSpec_O:
            stackSize--;
            break;
         case PopSpec_I:
            stackSize--;
            break;
         case PopSpec_D:
            stackSize--;
            break;
         case PopSpec_DD:
            stackSize-=2;
            break;
         case PopSpec_OUNKNOWN:
            break;
         case PopSpec_UNKNOWN:
            break;
         case PopSpec_ARGS:
            break;
         case PopSpec_OARGS:
            break;
      }


      fprintf(stdout, "|");
      for (int i=0; i<maxStack; i++){
         fprintf(stdout, "%3d|", stackMap[i]);
      }
      fprintf(stdout, " : ");
      instruction->write(stdout, classInfo->getConstantPool());
      fprintf(stdout, "\n");
   }

   delete byteBuffer;
   delete[] buffer; 
   delete[] stackMap;
   delete classInfo;

   /* the whole file is now loaded in the memory buffer. */

   // terminate

}
