
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

   s4_t prevPc=-1;

   while (!codeByteBuffer->empty()){
      Instruction *instruction = new Instruction(classInfo->getConstantPool(), codeByteBuffer, maxStack, stackMap, &stackSize, prevPc);
      prevPc = instruction->getPC();
      instructions[prevPc] = instruction;
      fprintf(stdout, "|");
      for (int i=0; i<maxStack; i++){
         fprintf(stdout, "%3d|", stackMap[i]);
      }
      fprintf(stdout, " : ");
      instruction->write(stdout, classInfo->getConstantPool(), codeAttribute->getLocalVariableTableAttribute());
      fprintf(stdout, "\n");
   }

   Instruction *instruction = instructions[0];
   while (instruction != NULL){
      if (instruction->getPrevPC() >=0 && instruction->getStackBase()==0 && instructions[instruction->getPrevPC()]->getStackBase()==1){
         fprintf(stdout, "-8<-\n");
         instructions[instruction->getPrevPC()]->treeWrite(stdout, instructions, 0, classInfo->getConstantPool(), codeAttribute->getLocalVariableTableAttribute());
         fprintf(stdout, "->8-\n");
      }
      fprintf(stdout, " stackBase = %2d :", instruction->getStackBase());
      instruction->write(stdout, classInfo->getConstantPool(), codeAttribute->getLocalVariableTableAttribute());
      fprintf(stdout, "\n");
      u4_t nextPc = instruction->getNextPC();
      if (nextPc < codeByteBuffer->getLen()){
         instruction = instructions[nextPc];
      }else{
         instruction = NULL;
      }
   }



   delete byteBuffer;
   delete[] buffer; 
   delete[] stackMap;
   delete classInfo;

   /* the whole file is now loaded in the memory buffer. */

   // terminate

}
