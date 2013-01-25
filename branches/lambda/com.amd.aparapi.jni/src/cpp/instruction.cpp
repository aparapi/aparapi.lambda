#define INSTRUCTION_CPP
#include "instruction.h"

#include <string.h>

u4_t Instruction::getPC(){
   return(pc);
}
ByteCode *Instruction::getByteCode(){
   return(byteCode);
}

Instruction::Instruction(ByteBuffer *_codeByteBuffer){
   pc = _codeByteBuffer->getOffset();
   byte_t byte= _codeByteBuffer->u1();
   byteCode = &bytecode[byte];
   switch(byteCode->immSpec){
      case ImmSpec_NONE:
         break;
      case ImmSpec_Blvti:
         immSpec_Blvti.lvti=_codeByteBuffer->u1();
         break;
      case ImmSpec_Bcpci:
         immSpec_Bcpci.cpci=_codeByteBuffer->u1();
         break;
      case ImmSpec_Scpci:
         immSpec_Scpci.cpci= _codeByteBuffer->u2();
         break;
      case ImmSpec_Bconst:
         immSpec_Bconst.value= _codeByteBuffer->u1();
         break;
      case ImmSpec_Sconst:
         immSpec_Sconst.value= _codeByteBuffer->u2();
         break;
      case ImmSpec_IorForS:
         immSpec_IorForS.i= _codeByteBuffer->u4();
         break;
      case ImmSpec_Spc:
         immSpec_Spc.pc= _codeByteBuffer->s2() +pc;
         break;
      case ImmSpec_Scpfi:
         immSpec_Scpfi.cpfi= _codeByteBuffer->u2();
         break;
      case ImmSpec_ScpmiBB:
         immSpec_ScpmiBB.cpmi= _codeByteBuffer->u2();
         immSpec_ScpmiBB.b1= _codeByteBuffer->u1();
         immSpec_ScpmiBB.b2= _codeByteBuffer->u1();
         break;
      case ImmSpec_BlvtiBconst:
         immSpec_BlvtiBconst.lvti= _codeByteBuffer->u1();
         immSpec_BlvtiBconst.value= _codeByteBuffer->u1();
         break;
      case ImmSpec_Scpmi:
         immSpec_Scpmi.cpmi= _codeByteBuffer->u2();
         break;
      case ImmSpec_ScpciBdim:
         immSpec_ScpciBdim.cpci= _codeByteBuffer->u2();
         immSpec_ScpciBdim.dim= _codeByteBuffer->u1();
         break;
      case ImmSpec_Ipc:
         immSpec_Ipc.pc= _codeByteBuffer->s4()+pc;
         break;
      case ImmSpec_UNKNOWN:
         break;

   }
}


void Instruction::write(FILE *_file, ConstantPoolEntry **_constantPool){
   fprintf(stderr, "%d %s", pc, (char*)byteCode->name);
   switch(byteCode->immSpec){
      case ImmSpec_NONE:
         break;
      case ImmSpec_Blvti:
         break;
      case ImmSpec_Bcpci:
         {
         ConstantPoolEntry* constantPoolEntry = _constantPool[immSpec_Bcpci.cpci];
         fprintf(stderr, " #%d", immSpec_Bcpci.cpci);
         //switch (constantPoolEntry->){
         //}
         break;
         }
      case ImmSpec_Scpci:
         break;
      case ImmSpec_Bconst:
         break;
      case ImmSpec_Sconst:
         break;
      case ImmSpec_IorForS:
         break;
      case ImmSpec_Spc:
         fprintf(stderr, " %d", immSpec_Spc.pc);
         break;
      case ImmSpec_Scpfi:
         break;
      case ImmSpec_ScpmiBB:
         break;
      case ImmSpec_BlvtiBconst:
         break;
      case ImmSpec_Scpmi:
         break;
      case ImmSpec_ScpciBdim:
         break;
      case ImmSpec_Ipc:
         fprintf(stderr, " %d", immSpec_Ipc.pc);
         break;
      case ImmSpec_UNKNOWN:
         break;

   }
   fprintf(stderr, "\n");
}


void Decoder::list(u1_t* buf, u4_t len){
   fprintf(stdout, "inside list\n");
}




