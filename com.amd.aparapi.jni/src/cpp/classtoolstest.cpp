
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
   //MethodInfo *methodInfo = classInfo->getMethodInfo((char*)"getCount", (char*)"(FF)I");
   MethodInfo *methodInfo = classInfo->getMethodInfo((char*)"run", (char*)"()V");
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
      Instruction *instruction = new Instruction(classInfo->getConstantPool(), codeByteBuffer, maxStack, stackMap, &stackSize);
      instruction[instruction->getPC()] = instruction;
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
