
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
   char *buffer = (char*) malloc (sizeof(char)*size);
   if (buffer == NULL) {fputs ("Memory error",stderr); exit (2);}

   // copy the file into the buffer:
   size_t result = fread (buffer,1,size, classFile);
   fclose (classFile);
   if (result != size) {fputs ("Reading error",stderr); exit (3);}

   fprintf(stdout, "read %ld bytes\n", size);

   ByteBuffer byteBuffer((byte_t*)buffer, size);

   ClassInfo classInfo(&byteBuffer);
   MethodInfo *methodInfo = classInfo.getMethodInfo((char*)"getCount", (char*)"(FF)I");
   CodeAttribute *codeAttribute = methodInfo->getCodeAttribute();
   ByteBuffer *codeByteBuffer = codeAttribute->getCodeByteBuffer();

   Instruction** instructions = new Instruction*[codeByteBuffer->getLen()];
   for (unsigned i=0; i< codeByteBuffer->getLen(); i++){
      instructions[i] = NULL;
   }

   while (!codeByteBuffer->empty()){
      Instruction *instruction = new Instruction(codeByteBuffer);
      instructions[instruction->getPC()] = instruction;
      instruction->write(stdout, classInfo.getConstantPool());
   }

   //   delete methodInfo;

   /* the whole file is now loaded in the memory buffer. */

   // terminate

}
