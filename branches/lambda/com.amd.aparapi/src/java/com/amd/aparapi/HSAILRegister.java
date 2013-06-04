package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 6/4/13
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */


   public abstract class HSAILRegister<T extends PrimitiveType>{
      int index;
      public T type;
      public boolean stack;

      HSAILRegister(int _index, T _type, boolean _stack){
         index = _index;
         type = _type;
         stack = _stack;
      }

      public boolean isStack(){
         return (stack);
      }

      @Override
      public boolean equals(Object _other){
         if(_other instanceof HSAILRegister){
            HSAILRegister otherReg = (HSAILRegister) _other;
            return (type.equals(otherReg.type) && index == otherReg.index);
         }
         return false;
      }

   }

    abstract  class Reg_f64 extends HSAILRegister<f64>{
      Reg_f64(int _index, boolean _stack){
         super(_index, PrimitiveType.f64, _stack);
      }
   }

    abstract  class Reg_ref extends HSAILRegister<ref>{
      Reg_ref(int _index, boolean _stack){
         super(_index, PrimitiveType.ref, _stack);
      }
   }

    abstract  class Reg_u64 extends HSAILRegister<u64>{
      Reg_u64(int _index, boolean _stack){
         super(_index, PrimitiveType.u64, _stack);
      }
   }

    abstract  class Reg_s64 extends HSAILRegister<s64>{
      Reg_s64(int _index, boolean _stack){
         super(_index, PrimitiveType.s64, _stack);
      }
   }

    abstract  class Reg_s32 extends HSAILRegister<s32>{
      Reg_s32(int _index, boolean _stack){
         super(_index, PrimitiveType.s32, _stack);
      }
   }

    abstract  class Reg_f32 extends HSAILRegister<f32>{
      Reg_f32(int _index, boolean _stack){
         super(_index, PrimitiveType.f32, _stack);
      }
   }

     class StackReg_f64 extends Reg_f64{
      StackReg_f64(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
      }
   }

     class StackReg_f32 extends Reg_f32{
      StackReg_f32(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
      }
   }

     class StackReg_s64 extends Reg_s64{
      StackReg_s64(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
      }
   }

     class StackReg_u64 extends Reg_u64{
      StackReg_u64(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
      }
   }

     class StackReg_s32 extends Reg_s32{
      StackReg_s32(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
      }
   }

     class StackReg_ref extends Reg_ref{
      StackReg_ref(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
      }
   }

     class VarReg_f64 extends Reg_f64{
      VarReg_f64(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }
   }

     class VarReg_s64 extends Reg_s64{
      VarReg_s64(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }
   }

     class VarReg_u64 extends Reg_u64{
      VarReg_u64(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }
   }

     class VarReg_ref extends Reg_ref{
      VarReg_ref(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }

      public VarReg_ref(int _index){
         super(_index, false);
      }
   }

     class VarReg_s32 extends Reg_s32{
      VarReg_s32(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }

      public VarReg_s32(int _index){
         super(_index, false);
      }
   }

     class VarReg_f32 extends Reg_f32{
      VarReg_f32(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }

      public VarReg_f32(int _index){
         super(_index, false);
      }
   }

