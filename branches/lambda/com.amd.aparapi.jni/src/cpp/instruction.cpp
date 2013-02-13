#define INSTRUCTION_CPP
#include "instruction.h"

#include <string.h>


PCStack::PCStack(unsigned _size)
   : index(0), size(_size+1), values(new int[_size+1]) {
      for (int i=0; i<size; i++){
         values[i]=-1;
      }
   }
PCStack::~PCStack(){
   if (size){
      delete[] values;
   }
}

int PCStack::pop(){
   int retValue = -1;
   if (index>0){
      retValue = values[--index];
      values[index] = -1;
   }
   return(retValue);
}
int PCStack::peek(){
   int retValue = -1;
   if (index>0){
      retValue = values[index-1];
   }
   return(retValue);
}
void PCStack::push(int _value){
   if ((index+1)<size){
      values[index++]=_value;
   }
}
int PCStack::get(unsigned _index){
   int retValue = -1;
   if (_index < size){
      retValue=values[_index];
   }
   return(retValue);
}
unsigned PCStack::getSize(){
   return(size);
}
unsigned PCStack::getIndex(){
   return(index);
}

u4_t Instruction::getPC(){
   return(pc);
}
ByteCode *Instruction::getByteCode(){
   return(byteCode);
}

Instruction::Instruction(ConstantPoolEntry** _constantPool, ByteBuffer *_codeByteBuffer, PCStack *_pcStack, s4_t _prevPc){
   prevPc = _prevPc;
   stackBase = _pcStack->getIndex();
   pc = _codeByteBuffer->getOffset();
   byte_t byte= _codeByteBuffer->u1();
   byteCode = &bytecode[byte];
   bool wide = false;
   if (byteCode->bytecode == I_WIDE){
      wide = true;
      byte_t byte= _codeByteBuffer->u1();
      byteCode = &bytecode[byte];
   }
   switch(byteCode->immSpec){
      case ImmSpec_NONE:
         break;
      case ImmSpec_NONE_const_null:
         break;
      case ImmSpec_NONE_const_m1:
         break;
      case ImmSpec_NONE_const_0:
         break;
      case ImmSpec_NONE_const_1:
         break;
      case ImmSpec_NONE_const_2:
         break;
      case ImmSpec_NONE_const_3:
         break;
      case ImmSpec_NONE_const_4:
         break;
      case ImmSpec_NONE_const_5:
         break;
      case ImmSpec_NONE_lvti_0:
         break;
      case ImmSpec_NONE_lvti_1:
         break;
      case ImmSpec_NONE_lvti_2:
         break;
      case ImmSpec_NONE_lvti_3:
         break;
      case ImmSpec_Blvti:
         if (wide){
            immSpec_Blvti.lvti=_codeByteBuffer->u2();
         }else{
            immSpec_Blvti.lvti=_codeByteBuffer->u1();
         }
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
         if (wide){
            immSpec_BlvtiBconst.lvti= _codeByteBuffer->u2();
            immSpec_BlvtiBconst.value= _codeByteBuffer->s2();
         }else{
            immSpec_BlvtiBconst.lvti= _codeByteBuffer->u1();
            immSpec_BlvtiBconst.value= _codeByteBuffer->u1(); //s1()? TODO:
         }
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

   nextPc = _codeByteBuffer->getOffset();
   length = nextPc-pc;

   switch(byteCode->popSpec){
      case PopSpec_NONE:
         break;
      case PopSpec_A:
         popSpec_A.a = _pcStack->pop();
         break;
      case PopSpec_AI:
         popSpec_AI.i = _pcStack->pop();
         popSpec_AI.a = _pcStack->pop();
         break;
      case PopSpec_AII:
         popSpec_AII.i2 = _pcStack->pop();
         popSpec_AII.i1 = _pcStack->pop();
         popSpec_AII.a = _pcStack->pop();
         break;
      case PopSpec_AIL:
         popSpec_AIL.l = _pcStack->pop();
         popSpec_AIL.i = _pcStack->pop();
         popSpec_AIL.a = _pcStack->pop();
         break;
      case PopSpec_AIF:
         popSpec_AIF.f = _pcStack->pop();
         popSpec_AIF.i = _pcStack->pop();
         popSpec_AIF.a = _pcStack->pop();
         break;
      case PopSpec_AID:
         popSpec_AID.d = _pcStack->pop();
         popSpec_AID.i = _pcStack->pop();
         popSpec_AID.a = _pcStack->pop();
         break;
      case PopSpec_AIO:
         popSpec_AIO.o = _pcStack->pop();
         popSpec_AIO.i = _pcStack->pop();
         popSpec_AIO.a = _pcStack->pop();
         break;
      case PopSpec_AIB:
         popSpec_AIB.b = _pcStack->pop();
         popSpec_AIB.i = _pcStack->pop();
         popSpec_AIB.a = _pcStack->pop();
         break;
      case PopSpec_AIC:
         popSpec_AIC.c = _pcStack->pop();
         popSpec_AIC.i = _pcStack->pop();
         popSpec_AIC.a = _pcStack->pop();
         break;
      case PopSpec_AIS:
         popSpec_AIS.s = _pcStack->pop();
         popSpec_AIS.i = _pcStack->pop();
         popSpec_AIS.a = _pcStack->pop();
         break;
      case PopSpec_II :
         popSpec_II.i2 = _pcStack->pop();
         popSpec_II.i1 = _pcStack->pop();
         break;
      case PopSpec_III:
         popSpec_III.i3 = _pcStack->pop();
         popSpec_III.i2 = _pcStack->pop();
         popSpec_III.i1 = _pcStack->pop();
         break;
      case PopSpec_IIII:
         popSpec_IIII.i4 = _pcStack->pop();
         popSpec_IIII.i3 = _pcStack->pop();
         popSpec_IIII.i2 = _pcStack->pop();
         popSpec_IIII.i1 = _pcStack->pop();
         break;
      case PopSpec_L:
         popSpec_L.l = _pcStack->pop();
         break;
      case PopSpec_LI:
         popSpec_LI.i = _pcStack->pop();
         popSpec_LI.l = _pcStack->pop();
         break;
      case PopSpec_LL:
         popSpec_LL.l2 = _pcStack->pop();
         popSpec_LL.l1 = _pcStack->pop();
         break;
      case PopSpec_F:
         popSpec_F.f = _pcStack->pop();
         break;
      case PopSpec_FF:
         popSpec_FF.f2 = _pcStack->pop();
         popSpec_FF.f1 = _pcStack->pop();
         break;
      case PopSpec_OO:
         popSpec_OO.o2 = _pcStack->pop();
         popSpec_OO.o1 = _pcStack->pop();
         break;
      case PopSpec_RA:
         popSpec_RA.a = _pcStack->pop();
         popSpec_RA.r = _pcStack->pop();
         break;
      case PopSpec_O:
         popSpec_O.o = _pcStack->pop();
         break;
      case PopSpec_I:
         popSpec_I.i = _pcStack->pop();
         break;
      case PopSpec_D:
         popSpec_D.d = _pcStack->pop();
         break;
      case PopSpec_DD:
         popSpec_DD.d2 = _pcStack->pop();
         popSpec_DD.d1 = _pcStack->pop();
         break;
      case PopSpec_OFSIG:
         popSpec_OFSIG.v = _pcStack->pop();
         popSpec_OFSIG.o = _pcStack->pop();
         break;
      case PopSpec_FSIG:
         popSpec_FSIG.v = _pcStack->pop();
         break;
      case PopSpec_UNKNOWN:
         break;
      case PopSpec_MSIG:
         {
            MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            popSpec_MSIG.argc = method->getArgCount(_constantPool);
            if (popSpec_MSIG.argc>0){
               popSpec_MSIG.args = new u4_t[popSpec_MSIG.argc];
               for (int i=popSpec_MSIG.argc-1; i>=0;  i--){
                  popSpec_MSIG.args[i] = _pcStack->pop();
               }
            }else{
               popSpec_MSIG.args = NULL;
            }
         }
         break;
      case PopSpec_OMSIG:
         {
            MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            popSpec_OMSIG.argc = method->getArgCount(_constantPool);
            if ( popSpec_OMSIG.argc>0){
               popSpec_OMSIG.args = new u4_t[popSpec_OMSIG.argc];
               for (int i=popSpec_OMSIG.argc-1; i>=0; i--){
                  popSpec_OMSIG.args[i] = _pcStack->pop();
               }
            }else{
               popSpec_OMSIG.args = NULL;

            }
            popSpec_OMSIG.o = _pcStack->pop();
         }
         break;
   }


   switch(byteCode->pushSpec){
      case PushSpec_NONE:
         break;
      case PushSpec_N:
         _pcStack->push(pc);
         break;
      case PushSpec_I:
         _pcStack->push(pc);
         break;
      case PushSpec_L:
         _pcStack->push(pc);
         break;
      case PushSpec_F:
         _pcStack->push(pc);
         break;
      case PushSpec_D:
         _pcStack->push(pc);
         break;
      case PushSpec_O:
         _pcStack->push(pc);
         break;
      case PushSpec_A:
         _pcStack->push(pc);
         break;
      case PushSpec_RA:
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_IorForS:
         _pcStack->push(pc);
         break;
      case PushSpec_LorD:
         _pcStack->push(pc);
         break;
      case PushSpec_II:
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_III:
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_IIII:
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_IIIII:
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_IIIIII:
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         _pcStack->push(pc);
         break;
      case PushSpec_FSIG:
         {
            FieldConstantPoolEntry* field = (FieldConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            _pcStack->push(pc);
         }

         break;
      case PushSpec_MSIG:
         {
            MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            int retc = method->getRetCount(_constantPool);
            if (retc>0){
               _pcStack->push(pc);
            } else {
            }
         }

         break;
      case PushSpec_UNKNOWN:
         break;
   }


}

Instruction::~Instruction(){
   switch(byteCode->pushSpec){
      case PopSpec_MSIG:
         if (popSpec_MSIG.argc>0){
            delete [] popSpec_MSIG.args;
         }
         break;
      case PopSpec_OMSIG:
         if ( popSpec_OMSIG.argc>0){
            delete [] popSpec_OMSIG.args;
         }
         break;
   }
}


void Instruction::writeRegForm(FILE *_file, ConstantPoolEntry **_constantPool, int _maxLocals, LocalVariableTableAttribute *_localVariableTableAttribute){
   fprintf(_file, "%4d %-14s ", pc, (char*)byteCode->name);
   switch(byteCode->ldSpec){
      case LDSpec_I:
         switch(byteCode->immSpec){ 
            case ImmSpec_NONE_lvti_0:
               fprintf(_file, "i32_0");
               break;
            case ImmSpec_NONE_lvti_1:
               fprintf(_file, "i32_1");
               break;
            case ImmSpec_NONE_lvti_2:
               fprintf(_file, "i32_2");
               break;
            case ImmSpec_NONE_lvti_3:
               fprintf(_file, "i32_3");
               break;
            case ImmSpec_Blvti:
               fprintf(_file, "i32_%d", immSpec_Blvti.lvti);
               break;
            default:
               fprintf(_file, "i32_WHAT?");
               break;
         }
         break;
      case LDSpec_F:
         switch(byteCode->immSpec){ 
            case ImmSpec_NONE_lvti_0:
               fprintf(_file, "f32_0");
               break;
            case ImmSpec_NONE_lvti_1:
               fprintf(_file, "f32_1");
               break;
            case ImmSpec_NONE_lvti_2:
               fprintf(_file, "f32_2");
               break;
            case ImmSpec_NONE_lvti_3:
               fprintf(_file, "f32_3");
               break;
            case ImmSpec_Blvti:
               fprintf(_file, "f32_%d", immSpec_Blvti.lvti);
               break;
            default:
               fprintf(_file, "f32_WHAT?");
               break;
         }
         break;

      case LDSpec_D:
         switch(byteCode->immSpec){ 
            case ImmSpec_NONE_lvti_0:
               fprintf(_file, "f64_0");
               break;
            case ImmSpec_NONE_lvti_1:
               fprintf(_file, "f64_1");
               break;
            case ImmSpec_NONE_lvti_2:
               fprintf(_file, "f64_2");
               break;
            case ImmSpec_NONE_lvti_3:
               fprintf(_file, "f64_3");
               break;
            case ImmSpec_Blvti:
               fprintf(_file, "f64_%d", immSpec_Blvti.lvti);
               break;
            default:
               fprintf(_file, "f64_WHAT?");
               break;
         }
         break;

      case LDSpec_L:
         switch(byteCode->immSpec){ 
            case ImmSpec_NONE_lvti_0:
               fprintf(_file, "i64_0");
               break;
            case ImmSpec_NONE_lvti_1:
               fprintf(_file, "i64_1");
               break;
            case ImmSpec_NONE_lvti_2:
               fprintf(_file, "i64_2");
               break;
            case ImmSpec_NONE_lvti_3:
               fprintf(_file, "i64_3");
               break;
            case ImmSpec_Blvti:
               fprintf(_file, "i64_%d", immSpec_Blvti.lvti);
               break;
            default:
               fprintf(_file, "f64_WHAT?");
               break;
         }
         break;
      case LDSpec_A:
         switch(byteCode->immSpec){ 
            case ImmSpec_NONE_lvti_0:
               fprintf(_file, "obj_0");
               break;
            case ImmSpec_NONE_lvti_1:
               fprintf(_file, "obj_1");
               break;
            case ImmSpec_NONE_lvti_2:
               fprintf(_file, "obj_2");
               break;
            case ImmSpec_NONE_lvti_3:
               fprintf(_file, "obj_3");
               break;
            case ImmSpec_Blvti:
               fprintf(_file, "obj_%d", immSpec_Blvti.lvti);
               break;
            default:
               fprintf(_file, "obj_WHAT?");
               break;
         }
         break;
      default:
         //fprintf(_file, "?");
         break;
   }

   int popBase = stackBase - getPopCount(_constantPool) + _maxLocals;
   int pushBase = popBase;

   switch(byteCode->popSpec){
      case PopSpec_NONE:
         // fprintf(_file, " NONE");
         break;
      case PopSpec_A:
         fprintf(_file, " aref_%d", popBase);
         break;
      case PopSpec_AI:
         fprintf(_file, " aref_%d, i32_%d", popBase, popBase+1);
         break;
      case PopSpec_AII:
         fprintf(_file, " aref_%d, i32_%d, i32_%d", popBase, popBase+1, popBase+2);
         break;
      case PopSpec_AIL:
         fprintf(_file, " aref_%d, i32_%d, i64_%d", popBase, popBase+1, popBase+2);
         break;
      case PopSpec_AIF:
         fprintf(_file, " aref_%d, i32_%d, f32_%d", popBase, popBase+1, popBase+2);
         break;
      case PopSpec_AID:
         fprintf(_file, " aref_%d, i32_%d, f64_%d", popBase, popBase+1, popBase+2);
         break;
      case PopSpec_AIO:
         fprintf(_file, " aref_%d, i32_%d, obj_%d", popBase, popBase+1, popBase+2);
         break;
      case PopSpec_AIB:
         fprintf(_file, " aref_%d, i32_%d, i8_%d", popBase, popBase+1, popBase+2);
         break;
      case PopSpec_AIC:
         fprintf(_file, " aref_%d, i32_%d, u16_%d", popBase, popBase+1, popBase+2);
         break;
      case PopSpec_AIS:
         fprintf(_file, " aref_%d, i32_%d, s16_%d", popBase, popBase+1, popBase+2);
         break;
      case PopSpec_II :
         fprintf(_file, " i32_%d, i32_%d", popBase, popBase+1);
         break;
      case PopSpec_III:
         fprintf(_file, " i32_%d, i32_%d, i32_%d", popBase, popBase+1, popBase+2);
         break;
      case PopSpec_IIII:
         fprintf(_file, " i32_%d, i32_%d, i32_%d, i32_%d", popBase, popBase+1, popBase+2, popBase+3);
         break;
      case PopSpec_L:
         fprintf(_file, " i64_%d", popBase);
         break;
      case PopSpec_LI:
         fprintf(_file, " i64_%d, i32_%d", popBase, popBase+1);
         break;
      case PopSpec_LL:
         fprintf(_file, " i64_%d, i64_%d", popBase, popBase+1);
         break;
      case PopSpec_F:
         fprintf(_file, " f32_%d", popBase);
         break;
      case PopSpec_FF:
         fprintf(_file, " f32_%d, f32_%d", popBase, popBase+1);
         break;
      case PopSpec_OO:
         fprintf(_file, " obj_%d, obj_%d", popBase, popBase+1);
         break;
      case PopSpec_RA:
         fprintf(_file, " ((R)%d, (A)%d", popBase, popBase+1);
         break;
      case PopSpec_O:
         fprintf(_file, " obj_%d", popBase);
         break;
      case PopSpec_I:
         fprintf(_file, " i32_%d", popBase);
         break;
      case PopSpec_D:
         fprintf(_file, " f64_%d", popBase);
         break;
      case PopSpec_DD:
         fprintf(_file, " f64_%d, f64_%d", popBase, popBase+1);
         break;
      case PopSpec_OFSIG:
         break;
      case PopSpec_FSIG:
         break;
      case PopSpec_UNKNOWN:
         break;
      case PopSpec_MSIG:
         {
            if (popSpec_MSIG.argc==0){
               fprintf(_file, "call ()");
            }else{
               fprintf(_file, "call (");
               for (int i=0; i<popSpec_MSIG.argc; i++){
                  if (i>0){
                     fprintf(_file, " ,");
                  }
                  fprintf(_file, "arg_%d ", popBase+i);
               }
               fprintf(_file, ")");
            }

         }
         break;
      case PopSpec_OMSIG:
         {
            fprintf(_file, "call (");
            fprintf(_file, " obj_%d", popBase);
            if (popSpec_OMSIG.argc>0){
               for (int i=0; i<popSpec_OMSIG.argc; i++){
                  fprintf(_file, ", arg_%d", popBase+1+i);
               }
            }
            fprintf(_file, ")");
         }
         break;
   }
   fprintf(_file, " -> ");

   switch(byteCode->pushSpec){
      case PushSpec_NONE:
         //fprintf(_file, " NONE");
         break;
      case PushSpec_N:
         fprintf(_file, " n?_%d", pushBase);
         break;
      case PushSpec_I:
         fprintf(_file, " i32_%d", pushBase);
         break;
      case PushSpec_L:
         fprintf(_file, " i64_%d", pushBase);
         break;
      case PushSpec_F:
         fprintf(_file, " f32_%d", pushBase);
         break;
      case PushSpec_D:
         fprintf(_file, " f64_%d", pushBase);
         break;
      case PushSpec_O:
         fprintf(_file, " obj_%d", pushBase);
         break;
      case PushSpec_A:
         fprintf(_file, " aref_%d", pushBase);
         break;
      case PushSpec_RA:
         fprintf(_file, " R_%d, A_%d", pushBase, pushBase+1);
         break;
      case PushSpec_IorForS:
         fprintf(_file, " (i32|f32|i16)_%d", pushBase);
         break;
      case PushSpec_LorD:
         fprintf(_file, " (i64|f64)_%d", pushBase);
         break;
      case PushSpec_II:
         fprintf(_file, " i32_%d, i32_%d", pushBase, pushBase+1);
         break;
      case PushSpec_III:
         fprintf(_file, " i32_%d, i32_%d, i32_%d", pushBase, pushBase+1, pushBase+2);
         break;
      case PushSpec_IIII:
         fprintf(_file, " i32_%d, i32_%d, i32_%d, i32_%d", pushBase, pushBase+1, pushBase+2, pushBase+3);
         break;
      case PushSpec_IIIII:
         fprintf(_file, " i32_%d, i32_%d, i32_%d, i32_%d, i32_%d", pushBase, pushBase+1, pushBase+2, pushBase+3, pushBase+4);
         break;
      case PushSpec_IIIIII:
         fprintf(_file, " i32_%d, i32_%d, i32_%d, i32_%d, i32_%d, i32_%d", pushBase, pushBase+1, pushBase+2, pushBase+3, pushBase+4, pushBase+5);
         break;
      case PushSpec_FSIG:
         {
            FieldConstantPoolEntry* field = (FieldConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            fprintf(_file, " fieldType_%d", pushBase);
         }

         break;
      case PushSpec_MSIG:
         {
            MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            int retc = method->getRetCount(_constantPool);
            if (retc>0){
               fprintf(_file, " methodRetType_%d", pushBase);
            } else {
            }
         }

         break;
      case PushSpec_UNKNOWN:
         break;
   }

   switch(byteCode->stSpec){
      case STSpec_I:
         switch(byteCode->immSpec){ 
            case ImmSpec_NONE_lvti_0:
               fprintf(_file, "i32_0");
               break;
            case ImmSpec_NONE_lvti_1:
               fprintf(_file, "i32_1");
               break;
            case ImmSpec_NONE_lvti_2:
               fprintf(_file, "i32_2");
               break;
            case ImmSpec_NONE_lvti_3:
               fprintf(_file, "i32_3");
               break;
            case ImmSpec_Blvti:
               fprintf(_file, "i32_%d", immSpec_Blvti.lvti);
               break;
            default:
               fprintf(_file, "i32_WHAT?");
               break;
         }
         break;
      case STSpec_F:
         switch(byteCode->immSpec){ 
            case ImmSpec_NONE_lvti_0:
               fprintf(_file, "f32_0");
               break;
            case ImmSpec_NONE_lvti_1:
               fprintf(_file, "f32_1");
               break;
            case ImmSpec_NONE_lvti_2:
               fprintf(_file, "f32_2");
               break;
            case ImmSpec_NONE_lvti_3:
               fprintf(_file, "f32_3");
               break;
            case ImmSpec_Blvti:
               fprintf(_file, "f32_%d", immSpec_Blvti.lvti);
               break;
            default:
               fprintf(_file, "f32_WHAT?");
               break;
         }
         break;

      case STSpec_D:
         switch(byteCode->immSpec){ 
            case ImmSpec_NONE_lvti_0:
               fprintf(_file, "f64_0");
               break;
            case ImmSpec_NONE_lvti_1:
               fprintf(_file, "f64_1");
               break;
            case ImmSpec_NONE_lvti_2:
               fprintf(_file, "f64_2");
               break;
            case ImmSpec_NONE_lvti_3:
               fprintf(_file, "f64_3");
               break;
            case ImmSpec_Blvti:
               fprintf(_file, "f64_%d", immSpec_Blvti.lvti);
               break;
            default:
               fprintf(_file, "f64_WHAT?");
               break;
         }
         break;
      case STSpec_L:
         switch(byteCode->immSpec){ 
            case ImmSpec_NONE_lvti_0:
               fprintf(_file, "i64_0");
               break;
            case ImmSpec_NONE_lvti_1:
               fprintf(_file, "i64_1");
               break;
            case ImmSpec_NONE_lvti_2:
               fprintf(_file, "i64_2");
               break;
            case ImmSpec_NONE_lvti_3:
               fprintf(_file, "i64_3");
               break;
            case ImmSpec_Blvti:
               fprintf(_file, "i64_%d", immSpec_Blvti.lvti);
               break;
            default:
               fprintf(_file, "i64_WHAT?");
               break;
         }
         break;
      case STSpec_A:
         switch(byteCode->immSpec){ 
            case ImmSpec_NONE_lvti_0:
               fprintf(_file, "obj_0");
               break;
            case ImmSpec_NONE_lvti_1:
               fprintf(_file, "obj_1");
               break;
            case ImmSpec_NONE_lvti_2:
               fprintf(_file, "obj_2");
               break;
            case ImmSpec_NONE_lvti_3:
               fprintf(_file, "obj_3");
               break;
            case ImmSpec_Blvti:
               fprintf(_file, "obj_%d", immSpec_Blvti.lvti);
               break;
            default:
               fprintf(_file, "obj_WHAT?");
               break;
         }
         break;
      default:
         //fprintf(_file, "?");
         break;
   }
}

void Instruction::write(FILE *_file, ConstantPoolEntry **_constantPool, LocalVariableTableAttribute *_localVariableTableAttribute){
   fprintf(_file, "%4d %-14s ", pc, (char*)byteCode->name);
   fprintf(_file, "%4d ", stackBase);
   int popCount = getPopCount(_constantPool);
   fprintf(_file, "%4d ", popCount);
   int pushCount = getPushCount(_constantPool);
   fprintf(_file, "%4d ", pushCount);
   fprintf(_file, "%4d ", pushCount-popCount);

   switch(byteCode->immSpec){
      case ImmSpec_NONE:
         break;
      case ImmSpec_NONE_const_null:
         fprintf(_file, " NULL");
         break;
      case ImmSpec_NONE_const_m1:
         fprintf(_file, " -1");
         break;
      case ImmSpec_NONE_const_0:
         fprintf(_file, " 0");
         break;
      case ImmSpec_NONE_const_1:
         fprintf(_file, " 1");
         break;
      case ImmSpec_NONE_const_2:
         fprintf(_file, " 2");
         break;
      case ImmSpec_NONE_const_3:
         fprintf(_file, " 3");
         break;
      case ImmSpec_NONE_const_4:
         fprintf(_file, " 4");
         break;
      case ImmSpec_NONE_const_5:
         fprintf(_file, " 5");
         break;
      case ImmSpec_NONE_lvti_0:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 0, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_1:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 1, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_2:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 2, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_3:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 3, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_Blvti:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, immSpec_Blvti.lvti, _constantPool);
            fprintf(_file, " %s", varName);
         }
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
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, immSpec_BlvtiBconst.lvti, _constantPool);
            fprintf(_file, " %s", varName);
         }
         fprintf(_file, " %d", immSpec_BlvtiBconst.value);
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

   switch(byteCode->popSpec){
      case PopSpec_NONE:
         fprintf(_file, " <-- NONE");
         break;
      case PopSpec_A:
         fprintf(_file, " <-- pop ((array)%d)", popSpec_A.a);
         break;
      case PopSpec_AI:
         fprintf(_file, " <-- pop ((array)%d, (int)%d)", popSpec_AI.a, popSpec_AI.i);
         break;
      case PopSpec_AII:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (int)%d)", popSpec_AII.a, popSpec_AII.i1, popSpec_AII.i2);
         break;
      case PopSpec_AIL:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (long)%d)", popSpec_AIL.a, popSpec_AIL.i, popSpec_AIL.l);
         break;
      case PopSpec_AIF:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (float)%d)", popSpec_AIF.a, popSpec_AIF.i, popSpec_AIF.f);
         break;
      case PopSpec_AID:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (double)%d)", popSpec_AID.a, popSpec_AID.i, popSpec_AID.d);
         break;
      case PopSpec_AIO:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (object)%d)", popSpec_AIO.a, popSpec_AIO.i, popSpec_AIO.o);
         break;
      case PopSpec_AIB:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (byte)%d)", popSpec_AIB.a, popSpec_AIB.i, popSpec_AIB.b);
         break;
      case PopSpec_AIC:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (char)%d)", popSpec_AIC.a, popSpec_AIC.i, popSpec_AIC.c);
         break;
      case PopSpec_AIS:
         fprintf(_file, " <-- pop ((array)%d, (int)%d, (short)%d)", popSpec_AIS.a, popSpec_AIS.i, popSpec_AIS.s);
      case PopSpec_II :
         fprintf(_file, " <-- ((int)%d, (int)%d)", popSpec_II.i1, popSpec_II.i2);
         break;
      case PopSpec_III:
         fprintf(_file, " <-- ((int)%d, (int)%d, (int)%d)", popSpec_III.i1, popSpec_III.i2, popSpec_III.i3);
         break;
      case PopSpec_IIII:
         fprintf(_file, " <-- ((int)%d, (int)%d, (int)%d, (int)%d)", popSpec_IIII.i1, popSpec_IIII.i2, popSpec_IIII.i3, popSpec_IIII.i4);
         break;
      case PopSpec_L:
         fprintf(_file, " <-- ((long)%d", popSpec_L.l);
         break;
      case PopSpec_LI:
         fprintf(_file, " <-- ((long)%d, (int)%d)", popSpec_LI.l, popSpec_LI.i);
         break;
      case PopSpec_LL:
         fprintf(_file, " <-- ((long)%d, (long)%d)", popSpec_LL.l1, popSpec_LL.l2);
         break;
      case PopSpec_F:
         fprintf(_file, " <-- ((float)%d)", popSpec_F.f);
         break;
      case PopSpec_FF:
         fprintf(_file, " <-- ((float)%d, (float)%d)", popSpec_FF.f1, popSpec_FF.f2);
         break;
      case PopSpec_OO:
         fprintf(_file, " <-- ((object)%d, (object)%d)", popSpec_OO.o1, popSpec_OO.o2);
         break;
      case PopSpec_RA:
         fprintf(_file, " <-- ((R)%d, (A)%d)", popSpec_RA.r, popSpec_RA.a);
         break;
      case PopSpec_O:
         fprintf(_file, " <-- ((object)%d)", popSpec_O.o);
         break;
      case PopSpec_I:
         fprintf(_file, " <-- ((int)%d)", popSpec_I.i);
         break;
      case PopSpec_D:
         fprintf(_file, " <-- ((double)%d)", popSpec_D.d);
         break;
      case PopSpec_DD:
         fprintf(_file, " <-- ((double)%d, (double)%d)", popSpec_DD.d1, popSpec_DD.d2);
         break;
      case PopSpec_OFSIG:
         break;
      case PopSpec_FSIG:
         break;
      case PopSpec_UNKNOWN:
         break;
      case PopSpec_MSIG:
         {
            fprintf(_file, " <-- ");
            if (popSpec_MSIG.argc==0){
               fprintf(_file, "NONE");
            }else{
               fprintf(_file, "(");
               for (int i=0; i<popSpec_MSIG.argc; i++){
                  if (i>0){
                     fprintf(_file, " ,");
                  }
                  fprintf(_file, "%d ", popSpec_MSIG.args[i]);
               }
               fprintf(_file, ")");
            }

         }
         break;
      case PopSpec_OMSIG:
         {
            fprintf(_file, " <- (%d", popSpec_OMSIG.o);
            if (popSpec_OMSIG.argc>0){
               for (int i=0; i<popSpec_OMSIG.argc; i++){
                  fprintf(_file, ", %d", popSpec_OMSIG.args[i]);
               }
            }
            fprintf(_file, ")");
         }
         break;
   }
}


void Instruction::treeWrite(FILE *_file, Instruction **_instructions, int _codeLength, int _depth, ConstantPoolEntry **_constantPool, LocalVariableTableAttribute *_localVariableTableAttribute, int _rootPc){
   for (int i=0; i<_depth; i++){
      fprintf(_file, "   ");
   }
   fprintf(_file, "%4d %-10s", pc, (char*)byteCode->name);

   switch(byteCode->immSpec){
      case ImmSpec_NONE:
         break;
      case ImmSpec_NONE_const_null:
         fprintf(_file, " NULL");
         break;
      case ImmSpec_NONE_const_m1:
         fprintf(_file, " -1");
         break;
      case ImmSpec_NONE_const_0:
         fprintf(_file, " 0");
         break;
      case ImmSpec_NONE_const_1:
         fprintf(_file, " 1");
         break;
      case ImmSpec_NONE_const_2:
         fprintf(_file, " 2");
         break;
      case ImmSpec_NONE_const_3:
         fprintf(_file, " 3");
         break;
      case ImmSpec_NONE_const_4:
         fprintf(_file, " 4");
         break;
      case ImmSpec_NONE_const_5:
         fprintf(_file, " 5");
         break;
      case ImmSpec_NONE_lvti_0:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 0, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_1:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 1, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_2:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 2, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_NONE_lvti_3:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, 3, _constantPool);
            fprintf(_file, " %s", varName);
         }
         break;
      case ImmSpec_Blvti:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, immSpec_Blvti.lvti, _constantPool);
            fprintf(_file, " %s", varName);
         }
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
                  fprintf(_file, " %f", ((FloatConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case INTEGER:
                  fprintf(_file, " %d", ((IntegerConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case DOUBLE:
                  fprintf(_file, " %lf", ((DoubleConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               case LONG:
                  fprintf(_file, " %ld", ((LongConstantPoolEntry*)constantPoolEntry)->getValue());
                  break;
               default:
                  fprintf(_file, " constant pool #%d", immSpec_Bcpci.cpci);
                  break;
            }
            break;
         }
      case ImmSpec_Bconst:
         fprintf(_file, " %d", immSpec_Bconst.value);
         break;
      case ImmSpec_Sconst:
         fprintf(_file, " %d", immSpec_Sconst.value);
         break;
      case ImmSpec_Spc:
         fprintf(_file, " %d", immSpec_Spc.pc);
         break;
      case ImmSpec_Scpfi:
         {
            FieldConstantPoolEntry* fieldConstantPoolEntry = (FieldConstantPoolEntry*)_constantPool[immSpec_Scpfi.cpfi];
            NameAndTypeConstantPoolEntry* nameAndTypeConstantPoolEntry = (NameAndTypeConstantPoolEntry*)_constantPool[fieldConstantPoolEntry->getNameAndTypeIndex()];
            UTF8ConstantPoolEntry* nameConstantPoolEntry = (UTF8ConstantPoolEntry*)_constantPool[nameAndTypeConstantPoolEntry->getNameIndex()];

            fprintf(_file, " %s", nameConstantPoolEntry->getUTF8());
            break;
         }
      case ImmSpec_ScpmiBB:
         break;
      case ImmSpec_BlvtiBconst:
         if (_localVariableTableAttribute !=  NULL){
            char *varName = _localVariableTableAttribute->getLocalVariableName(pc +length, immSpec_BlvtiBconst.lvti, _constantPool);
            fprintf(_file, " %s", varName);
         }
         fprintf(_file, " %d", immSpec_BlvtiBconst.value);
         break;
      case ImmSpec_Scpmi:
         {
            MethodConstantPoolEntry* methodConstantPoolEntry = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            NameAndTypeConstantPoolEntry* nameAndTypeConstantPoolEntry = (NameAndTypeConstantPoolEntry*)_constantPool[methodConstantPoolEntry->getNameAndTypeIndex()];
            UTF8ConstantPoolEntry* nameConstantPoolEntry = (UTF8ConstantPoolEntry*)_constantPool[nameAndTypeConstantPoolEntry->getNameIndex()];
            fprintf(_file, " %s", nameConstantPoolEntry->getUTF8());
            break;
         }
      case ImmSpec_ScpciBdim:
         break;
      case ImmSpec_Ipc:
         fprintf(_file, " %d", immSpec_Ipc.pc);
         break;
      case ImmSpec_UNKNOWN:
         break;

   }


   //write(_file, _constantPool, _localVariableTableAttribute);
   fprintf(_file, "\n");
   switch(byteCode->popSpec){
      case PopSpec_NONE:
         break;
      case PopSpec_A:
         _instructions[popSpec_A.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AI:
         _instructions[popSpec_AI.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AI.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AII:
         _instructions[popSpec_AII.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AII.i1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AII.i2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIL:
         _instructions[popSpec_AIL.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIL.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIL.l]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIF:
         _instructions[popSpec_AIF.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIF.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIF.f]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AID:
         _instructions[popSpec_AID.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AID.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AID.d]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIO:
         _instructions[popSpec_AIO.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIO.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIO.o]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIB:
         _instructions[popSpec_AIB.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIB.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIB.b]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIC:
         _instructions[popSpec_AIC.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIC.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIC.c]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_AIS:
         _instructions[popSpec_AIS.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIS.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_AIS.s]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_II :
         _instructions[popSpec_II.i1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_II.i2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_III:
         _instructions[popSpec_III.i1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_III.i2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_III.i3]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_IIII:
         _instructions[popSpec_IIII.i1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_IIII.i2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_IIII.i3]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_IIII.i4]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_L:
         _instructions[popSpec_L.l]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_LI:
         _instructions[popSpec_LI.l]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_LI.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_LL:
         _instructions[popSpec_LL.l1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_LL.l2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_F:
         _instructions[popSpec_F.f]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_FF:
         _instructions[popSpec_FF.f1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_FF.f2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_OO:
         _instructions[popSpec_OO.o1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_OO.o2]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_RA:
         _instructions[popSpec_RA.r]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_RA.a]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_O:
         _instructions[popSpec_O.o]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_I:
         _instructions[popSpec_I.i]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_D:
         _instructions[popSpec_D.d]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_DD:
         _instructions[popSpec_DD.d1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         _instructions[popSpec_DD.d1]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         break;
      case PopSpec_OFSIG:
         break;
      case PopSpec_FSIG:
         break;
      case PopSpec_UNKNOWN:
         break;
      case PopSpec_MSIG:
         if (popSpec_MSIG.argc!=0){
            for (int i=0; i<popSpec_MSIG.argc; i++){
               _instructions[popSpec_MSIG.args[i]]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
            }
         }
         break;
      case PopSpec_OMSIG:
         _instructions[popSpec_OMSIG.o]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
         if (popSpec_OMSIG.argc>0){
            for (int i=0; i<popSpec_OMSIG.argc; i++){
               _instructions[popSpec_OMSIG.args[i]]->treeWrite(_file, _instructions, _codeLength, _depth+1, _constantPool, _localVariableTableAttribute, _rootPc);
            }
         }
         break;
   }
   if (nextPc < _rootPc){
      Instruction *nextInstruction =  _instructions[nextPc];
      if (nextInstruction->getByteCode()->pushSpec == PushSpec_NONE && nextInstruction->getByteCode()->popSpec == PopSpec_NONE){
         for (int i=0; i<_depth; i++){
            fprintf(_file, "   ");
         }
         fprintf(_file, " (");
         nextInstruction->treeWrite(_file, _instructions, _codeLength, _depth, _constantPool, _localVariableTableAttribute, _rootPc);
         for (int i=0; i<_depth; i++){
            fprintf(_file, "   ");
         }
         fprintf(_file, " )");
      }
   }
}

s4_t Instruction::getPrevPC(){
   return(prevPc);
}

u4_t Instruction::getNextPC(){
   return(nextPc);
}

u2_t Instruction::getStackBase(){
   return(stackBase);
}

void Decoder::list(u1_t* buf, u4_t len){
   fprintf(stdout, "inside list\n");
}

int Instruction::getPopCount(ConstantPoolEntry **_constantPool){
   int count = 0;
   switch(byteCode->popSpec){
      case PopSpec_NONE:
      case PopSpec_UNKNOWN:
         count = 0;
         break;
      case PopSpec_A:
      case PopSpec_L:
      case PopSpec_F:
      case PopSpec_O:
      case PopSpec_I:
      case PopSpec_D:
         count = 1;
         break;
      case PopSpec_AI:
      case PopSpec_II :
      case PopSpec_LI:
      case PopSpec_LL:
      case PopSpec_FF:
      case PopSpec_OO:
      case PopSpec_OFSIG:
      case PopSpec_FSIG:
      case PopSpec_RA:
      case PopSpec_DD:
         count = 2;
         break;
      case PopSpec_AII:
      case PopSpec_AIL:
      case PopSpec_AIF:
      case PopSpec_AID:
      case PopSpec_AIO:
      case PopSpec_AIB:
      case PopSpec_AIC:
      case PopSpec_AIS:
      case PopSpec_III:
         count = 3;
         break;
      case PopSpec_IIII:
         count = 4;
         break;
      case PopSpec_MSIG:
         count = popSpec_MSIG.argc;
         break;
      case PopSpec_OMSIG:
         count = popSpec_MSIG.argc+1;
         break;
   }
   return(count);
}

int Instruction::getPushCount(ConstantPoolEntry **_constantPool){
   int count=0;
   switch(byteCode->pushSpec){
      case PushSpec_UNKNOWN:
      case PushSpec_NONE:
         count = 0;
         break;
      case PushSpec_N:
      case PushSpec_I:
      case PushSpec_L:
      case PushSpec_F:
      case PushSpec_D:
      case PushSpec_O:
      case PushSpec_A:
      case PushSpec_IorForS:
      case PushSpec_LorD:
      case PushSpec_FSIG:
         count = 1;
         break;
      case PushSpec_RA:
      case PushSpec_II:
         count = 2;
         break;
      case PushSpec_III:
         count = 3;
         break;
      case PushSpec_IIII:
         count = 4;
         break;
      case PushSpec_IIIII:
         count = 5;
         break;
      case PushSpec_IIIIII:
         count = 6;
         break;
      case PushSpec_MSIG:
         {
            MethodConstantPoolEntry* method = (MethodConstantPoolEntry*)_constantPool[immSpec_Scpmi.cpmi];
            count = method->getRetCount(_constantPool);
         }
         break;
   }
   return(count);

}
