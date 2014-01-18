package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 6/4/13
 * Time: 2:00 PM
 * To change this template use File | Settings | File Templates.
 */

abstract class HSAILOperand<R extends HSAILOperand<R,T>, T extends PrimitiveType>{
   protected T type;
   HSAILOperand(R original){
      type = original.type;
   }
   HSAILOperand(T _type){
      type = _type;
   }
   public abstract R cloneMe();
}

public abstract class HSAILRegister<R extends HSAILRegister<R,T>, T extends PrimitiveType> extends HSAILOperand<R,T>{
   int index;

   public boolean stack;

   HSAILRegister(R original){
      super(original);
       index = original.index;
       stack = original.stack;
   }

   HSAILRegister(int _index, T _type, boolean _stack){
      super(_type);
      index = _index;
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

    public abstract R cloneMe();
}

abstract class Reg_f64 <R extends Reg_f64<R>> extends HSAILRegister<R, f64>{
   Reg_f64(R original){
       super(original);
   }
   Reg_f64(int _index, boolean _stack){
      super(_index, PrimitiveType.f64, _stack);
   }
}

abstract class Reg_ref <R extends Reg_ref<R>>extends HSAILRegister<R, ref>{
    Reg_ref(R original){
        super(original);
    }
   Reg_ref(int _index, boolean _stack){
      super(_index, PrimitiveType.ref, _stack);
   }
}

abstract class Reg_u64 <R extends Reg_u64<R>>extends HSAILRegister<R, u64>{
    Reg_u64(R original){
        super(original);
    }
   Reg_u64(int _index, boolean _stack){
      super(_index, PrimitiveType.u64, _stack);
   }
}

abstract class Reg_s64  <R extends Reg_s64<R>>extends HSAILRegister<R, s64>{
    Reg_s64(R original){
        super(original);
    }
   Reg_s64(int _index, boolean _stack){
      super(_index, PrimitiveType.s64, _stack);
   }
}

abstract class Reg_s32  <R extends Reg_s32<R>>extends HSAILRegister<R, s32>{
    Reg_s32(R original){
        super(original);
    }
   Reg_s32(int _index, boolean _stack){
      super(_index, PrimitiveType.s32, _stack);
   }
}

abstract class Reg_s16  <R extends Reg_s16<R>>extends HSAILRegister<R, s16>{
    Reg_s16(R original){
        super(original);
    }
   Reg_s16(int _index, boolean _stack){
      super(_index, PrimitiveType.s16, _stack);
   }
}

abstract class Reg_u16 <R extends Reg_u16<R>>extends HSAILRegister<R, u16>{
    Reg_u16(R original){
        super(original);
    }
   Reg_u16(int _index, boolean _stack){
      super(_index, PrimitiveType.u16, _stack);
   }
}

abstract class Reg_s8 <R extends Reg_s8<R>>extends HSAILRegister<R, s8>{
    Reg_s8(R original){
        super(original);
    }
   Reg_s8(int _index, boolean _stack){
      super(_index, PrimitiveType.s8, _stack);
   }
}

abstract class Reg_f32 <R extends Reg_f32<R>>extends HSAILRegister<R, f32>{
   Reg_f32(R original){
      super(original);
   }
   Reg_f32(int _index, boolean _stack){
      super(_index, PrimitiveType.f32, _stack);
   }
}

class StackReg_f64 extends Reg_f64<StackReg_f64>{
    StackReg_f64(StackReg_f64 original){
        super(original);
    }
    @Override public StackReg_f64 cloneMe(){
        return(new StackReg_f64(this));
    }
   StackReg_f64(Instruction _from, int _stackBase, int _offset){
      super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _stackBase + _offset, true);
   }
}

class StackReg_f32 extends Reg_f32<StackReg_f32>{
    StackReg_f32(StackReg_f32 original){
        super(original);
    }
    @Override public StackReg_f32 cloneMe(){
        return(new StackReg_f32(this));
    }
   StackReg_f32(Instruction _from, int _stackBase, int _offset){
      super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _stackBase + _offset, true);
   }
}

class StackReg_s64 extends Reg_s64<StackReg_s64>{
    StackReg_s64(StackReg_s64 original){
        super(original);
    }
    @Override public StackReg_s64 cloneMe(){
        return(new StackReg_s64(this));
    }
   StackReg_s64(Instruction _from, int _stackBase,int _offset){
      super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals()+ _stackBase + _offset, true);
   }
}

class StackReg_u64 extends Reg_u64<StackReg_u64>{
    StackReg_u64(StackReg_u64 original){
        super(original);
    }
    @Override public StackReg_u64 cloneMe(){
        return(new StackReg_u64(this));
    }
   StackReg_u64(Instruction _from, int _stackBase,int _offset){
      super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _stackBase+ _offset, true);
   }
}

class StackReg_s32 extends Reg_s32<StackReg_s32>{
    StackReg_s32(StackReg_s32 original){
        super(original);
    }
    @Override public StackReg_s32 cloneMe(){
        return(new StackReg_s32(this));
    }
   StackReg_s32(Instruction _from, int _stackBase,int _offset){
      super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals()+ _stackBase + _offset, true);
   }
}

class StackReg_s16 extends Reg_s16<StackReg_s16>{
    StackReg_s16(StackReg_s16 original){
        super(original);
    }
    @Override public StackReg_s16 cloneMe(){
        return(new StackReg_s16(this));
    }
   StackReg_s16(Instruction _from, int _stackBase,int _offset){
      super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals()+ _stackBase + _offset, true);
   }
}

class StackReg_u16 extends Reg_u16<StackReg_u16>{
    StackReg_u16(StackReg_u16 original){
        super(original);
    }
    @Override public StackReg_u16 cloneMe(){
        return(new StackReg_u16(this));
    }
   StackReg_u16(Instruction _from, int _stackBase,int _offset){
      super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals()+ _stackBase + _offset, true);
   }
}

class StackReg_s8 extends Reg_s8<StackReg_s8>{
    StackReg_s8(StackReg_s8 original){
        super(original);
    }
    @Override public StackReg_s8 cloneMe(){
        return(new StackReg_s8(this));
    }
   StackReg_s8(Instruction _from, int _stackBase,int _offset){
      super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _stackBase+ _offset, true);
   }
}

class StackReg_ref extends Reg_ref<StackReg_ref>{
    StackReg_ref(StackReg_ref original){
        super(original);
    }
    @Override public StackReg_ref cloneMe(){
        return(new StackReg_ref(this));
    }
   StackReg_ref(Instruction _from, int _stackBase,int _offset){
      super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals()+ _stackBase + _offset, true);
   }
}

class VarReg_f64 extends Reg_f64<VarReg_f64>{
    VarReg_f64(VarReg_f64 original){
        super(original);
    }
    @Override public VarReg_f64 cloneMe(){
        return(new VarReg_f64(this));
    }
   VarReg_f64(Instruction _from, int _stackBase){
      super(_from.asLocalVariableAccessor().getLocalVariableTableIndex()+ _stackBase, false);
   }
    public VarReg_f64(int _index){
        super(_index, false);
    }
}

class VarReg_s64 extends Reg_s64<VarReg_s64>{
    VarReg_s64(VarReg_s64 original){
        super(original);
    }
    @Override public VarReg_s64 cloneMe(){
        return(new VarReg_s64(this));
    }
   VarReg_s64(Instruction _from, int _stackBase){
      super(_from.asLocalVariableAccessor().getLocalVariableTableIndex()+ _stackBase, false);
   }
    public VarReg_s64(int _index){
        super(_index, false);
    }
}

class VarReg_u64 extends Reg_u64<VarReg_u64>{
    VarReg_u64(VarReg_u64 original){
        super(original);
    }
    @Override public VarReg_u64 cloneMe(){
        return(new VarReg_u64(this));
    }
   VarReg_u64(Instruction _from, int _stackBase){
      super(_from.asLocalVariableAccessor().getLocalVariableTableIndex()+ _stackBase, false);
   }
}

class VarReg_ref extends Reg_ref<VarReg_ref>{
    VarReg_ref(VarReg_ref original){
        super(original);
    }
    @Override public VarReg_ref cloneMe(){
        return(new VarReg_ref(this));
    }
   VarReg_ref(Instruction _from, int _stackBase){
      super(_from.asLocalVariableAccessor().getLocalVariableTableIndex()+ _stackBase, false);
   }

   public VarReg_ref(int _index){
      super(_index, false);
   }
}

class VarReg_s32 extends Reg_s32<VarReg_s32>{
    VarReg_s32(VarReg_s32 original){
        super(original);
    }
    @Override public VarReg_s32 cloneMe(){
        return(new VarReg_s32(this));
    }
   VarReg_s32(Instruction _from, int _stackBase){
      super(_from.asLocalVariableAccessor().getLocalVariableTableIndex()+ _stackBase, false);
   }

   public VarReg_s32(int _index){
      super(_index, false);
   }
}

class VarReg_f32 extends Reg_f32<VarReg_f32>{
    VarReg_f32(VarReg_f32 original){
        super(original);
    }
    @Override public VarReg_f32 cloneMe(){
        return(new VarReg_f32(this));
    }
   VarReg_f32(Instruction _from, int _stackBase){
      super(_from.asLocalVariableAccessor().getLocalVariableTableIndex()+ _stackBase, false);
   }

   public VarReg_f32(int _index){
      super(_index, false);
   }
}

