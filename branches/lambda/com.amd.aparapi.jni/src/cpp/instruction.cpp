#define INSTRUCTION_CPP
#include "instruction.h"

#include <string.h>

u4_t Instruction::getPC(){
   return(pc);
}
ByteCode *Instruction::getByteCode(){
   return(byteCode);
}

Instruction::Instruction(ConstantPoolEntry** _constantPool, ByteBuffer *_codeByteBuffer, u2_t _maxStack, u4_t *_stackMap, u2_t *_stackSize ){
   stackBase = *_stackSize;
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

   switch(byteCode->pushSpec){
      case PushSpec_NONE:
         break;
      case PushSpec_N:
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_I:
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_L:
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_F:
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_D:
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_O:
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_A:
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_RA:
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_IorForS:
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_LorD:
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_II:
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_III:
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_IIII:
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_IIIII:
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_IIIIII:
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         _stackMap[(*_stackSize)++] = pc;
         break;
      case PushSpec_UNKNOWN:
         break;
   }
   switch(byteCode->popSpec){
      case PopSpec_NONE:
         break;
      case PopSpec_A:
         popSpec_A.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_AI:
         popSpec_AI.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AI.i = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_AII:
         popSpec_AII.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AII.i1 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AII.i2 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_AIL:
         popSpec_AIL.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIL.i = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIL.l = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_AIF:
         popSpec_AIF.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIF.i = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIF.f = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_AID:
         popSpec_AID.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AID.i = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AID.d = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_AIO:
         popSpec_AIO.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIO.i = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIO.o = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_AIB:
         popSpec_AIB.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIB.i = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIB.b = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_AIC:
         popSpec_AIC.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIC.i = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIC.c = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_AIS:
         popSpec_AIS.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIS.i = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_AIS.s = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_II :
         popSpec_II.i1 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_II.i2 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_III:
         popSpec_III.i1 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_III.i2 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_III.i3 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_IIII:
         popSpec_IIII.i1 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_IIII.i2 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_IIII.i3 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_IIII.i4 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_L:
         popSpec_L.l = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_LI:
         popSpec_LI.l = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_LI.i = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_LL:
         popSpec_LL.l1 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_LL.l2 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_F:
         popSpec_F.f = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_FF:
         popSpec_FF.f1 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_FF.f2 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_OO:
         popSpec_OO.o1 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_OO.o2 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_RA:
         popSpec_RA.r = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_RA.a = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_O:
         popSpec_O.o = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_I:
         popSpec_I.i = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_D:
         popSpec_D.d = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_DD:
         popSpec_DD.d1 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         popSpec_DD.d2 = _stackMap[--(*_stackSize)];
         _stackMap[*_stackSize] = -1;
         break;
      case PopSpec_OUNKNOWN:
         break;
      case PopSpec_UNKNOWN:
         break;
      case PopSpec_ARGS:
         {
           MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
           int argc = method->getArgCount(_constantPool);
         }
         break;
      case PopSpec_OARGS:
         {
           MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
           int argc = method->getArgCount(_constantPool);
         }
         break;
   }


}

Instruction::~Instruction(){
}


void Instruction::write(FILE *_file, ConstantPoolEntry **_constantPool){
   fprintf(_file, "%4d %-10s", pc, (char*)byteCode->name);
   switch(byteCode->immSpec){
      case ImmSpec_NONE:
         break;
      case ImmSpec_Blvti:
         fprintf(_file, " %d", immSpec_Blvti.lvti);
         break;
      case ImmSpec_Bcpci:
      case ImmSpec_Scpci:
         {
            int cpi = 0;
            if (byteCode->immSpec == ImmSpec_Bcpci){
               cpi = immSpec_Bcpci.cpci;
            }else{
               cpi = immSpec_Scpci.cpci;
            }
            ConstantPoolEntry* constantPoolEntry = _constantPool[cpi];
            switch (constantPoolEntry->getConstantPoolType()){
               case FLOAT:
                  fprintf(_file, " FLOAT %f", ((FloatConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case INTEGER:
                  fprintf(_file, " INTEGER %d", ((IntegerConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case DOUBLE:
                  fprintf(_file, " DOUBLE %lf", ((DoubleConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case LONG:
                  fprintf(_file, " LONG %ld", ((LongConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               default:
                  fprintf(_file, " constant pool #%d", immSpec_Bcpci.cpci);
                  break;
            }
            break;
         }
      case ImmSpec_Bconst:
         fprintf(_file, " byte %d", immSpec_Bconst.value);
         break;
      case ImmSpec_Sconst:
         fprintf(_file, " short %d", immSpec_Sconst.value);
         break;
      case ImmSpec_Spc:
         fprintf(_file, " %d", immSpec_Spc.pc);
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
         fprintf(_file, " %d", immSpec_Ipc.pc);
         break;
      case ImmSpec_UNKNOWN:
         break;

   }
}


void Decoder::list(u1_t* buf, u4_t len){
   fprintf(stdout, "inside list\n");
}




