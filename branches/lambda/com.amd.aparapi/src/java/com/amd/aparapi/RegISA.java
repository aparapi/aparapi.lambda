package com.amd.aparapi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/27/13
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegISA{
   public static abstract class Type{
      static final u1 u1 = new u1();
      static final u8 u8 = new u8();
      static final u16 u16 = new u16();
      static final u32 u32 = new u32();
      static final u64 u64 = new u64();
      static final s8 s8 = new s8();
      static final s16 s16 = new s16();
      static final s32 s32 = new s32();
      static final s64 s64 = new s64();

      static final f16 f16 = new f16();
      static final f32 f32 = new f32();
      static final f64 f64 = new f64();
      int bits;

      public int getBits(){
         return (bits);
      }

      String sizeType; // s,d

      public String getSizeType(){
         return (sizeType);
      }

      String type; // u,f,s

      public String getType(){
         return (type);
      }

      Type(int _bits, String _sizeType, String _type){
         bits = _bits;
         sizeType = _sizeType;
         type = _type;
      }

      public String getRegName(int i){
         return ("$" + sizeType + "" + i);
      }

      public String getTypeName(){
         return (type + bits);
      }

      public static Type forJavaType(TypeHelper.Type javaType){
         if(javaType.isArray()){
            return (u64);
         } else if(javaType.isInt()){
            return (s32);
         }else if(javaType.isFloat()){
            return (f32);
         } else if(javaType.isLong()){
            return (s64);
         }else if(javaType.isDouble()){
            return (f64);
         }else{
            throw new IllegalArgumentException("no mapping for "+javaType);

         }
      }


   }

   ;

   static class u1 extends Type{

      u1(){
         super(1, "?", "u");
      }
   }

   static class u8 extends Type{
      u8(){
         super(8, "?", "u");
      }

   }

   static class s8 extends Type{
      s8(){
         super(8, "?", "s");
      }
   }

   static class u16 extends Type{
      u16(){
         super(16, "?", "u");
      }
   }

   static class s16 extends Type{
      s16(){
         super(16, "?", "s");
      }
   }

   static class f16 extends Type{
      f16(){
         super(16, "?", "f");
      }
   }

   static class u32 extends Type{
      u32(){
         super(32, "s", "u");
      }
   }

   static class s32 extends Type{
      s32(){
         super(32, "s", "s");
      }
   }

   static class f32 extends Type{
      f32(){
         super(32, "s", "f");
      }
   }

   static class u64 extends Type{
      u64(){
         super(64, "d", "u");
      }
   }

   static class s64 extends Type{
      s64(){
         super(64, "d", "s");
      }
   }

   static class f64 extends Type{
      f64(){
         super(64, "d", "f");
      }
   }

   public abstract static class Reg<T extends Type> {
      int index;
      public T type;
      public boolean stack;

      Reg(int _index, T _type, boolean _stack){
         index = _index;
         type = _type;
         stack = _stack;
      }
      public boolean isStack(){
         return(stack);
      }

      @Override public boolean equals(Object _other){
         if (_other instanceof Reg){
            Reg otherReg = (Reg)_other;
            return(type.equals(otherReg.type) && index == otherReg.index);
         }
         return false;
      }

   }

   public abstract static class Reg_f64 extends Reg<f64>{
      Reg_f64(int _index, boolean _stack){
         super(_index, Type.f64, _stack);
      }
   }
   public abstract static class Reg_u64 extends Reg<u64>{
      Reg_u64(int _index, boolean _stack){
         super(_index, Type.u64, _stack);
      }
   }
   public abstract static class Reg_s64 extends Reg<s64>{
      Reg_s64(int _index, boolean _stack){
         super(_index, Type.s64, _stack);
      }
   }
   public abstract static class Reg_s32 extends Reg<s32>{
      Reg_s32(int _index, boolean _stack){
         super(_index, Type.s32, _stack);
      }
   }
   public abstract static class Reg_f32 extends Reg<f32>{
      Reg_f32(int _index, boolean _stack){
         super(_index, Type.f32, _stack);
      }
   }

   public static class StackReg_f64 extends Reg_f64{
      StackReg_f64(Instruction _from, int _offset){
         super(_from.getPreStackBase()+_from.getMethod().getCodeEntry().getMaxLocals()+_offset, true);
      }
   }

   public static class StackReg_f32 extends Reg_f32{
      StackReg_f32(Instruction _from, int _offset){
         super(_from.getPreStackBase()+_from.getMethod().getCodeEntry().getMaxLocals()+_offset, true);
      }
   }
   public static class StackReg_s64 extends Reg_s64{
      StackReg_s64(Instruction _from, int _offset){
         super(_from.getPreStackBase()+_from.getMethod().getCodeEntry().getMaxLocals()+_offset, true);
      }
   }

   public static class StackReg_s32 extends Reg_s32{
      StackReg_s32(Instruction _from, int _offset){
         super(_from.getPreStackBase()+_from.getMethod().getCodeEntry().getMaxLocals()+_offset, true);
      }
   }

   public static class StackReg_u64 extends Reg_u64{
      StackReg_u64(Instruction _from, int _offset){
         super(_from.getPreStackBase()+_from.getMethod().getCodeEntry().getMaxLocals()+_offset, true);
      }
   }
   public static class VarReg_f64 extends Reg_f64{
      VarReg_f64(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }
   }
   public static class VarReg_s64 extends Reg_s64{
      VarReg_s64(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }
   }
   public static class VarReg_u64 extends Reg_u64{
      VarReg_u64(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }

      public VarReg_u64(int _index){
         super(_index, false);
      }
   }
   public static class VarReg_s32 extends Reg_s32{
      VarReg_s32(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }

      public VarReg_s32(int _index){
         super(_index, false);
      }
   }
   public static class VarReg_f32 extends Reg_f32{
      VarReg_f32(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }
   }


   static abstract class RegInstruction{
      Instruction from;
      Reg[] dests=null;
      Reg[] sources=null;


      RegInstruction(Instruction _from, int _destCount, int _sourceCount){
         from = _from;
         dests = new Reg[_destCount];
         sources= new Reg[_sourceCount];
      }

      abstract void render(RegISARenderer r);

   }
   static abstract class RegInstructionWithDest<T extends Type> extends RegInstruction{


      RegInstructionWithDest(Instruction _from, Reg<T> _dest){
         super(_from, 1, 0);
         dests[0] = _dest;

      }
      Reg<T> getDest(){
         return((Reg<T>)dests[0]);
      }
   }
   static abstract class RegInstructionWithSrc<T extends Type> extends RegInstruction{

      RegInstructionWithSrc(Instruction _from, Reg<T> _src){
         super(_from, 0, 1);
         sources[0] = _src;
      }
      Reg<T> getSrc(){
         return((Reg<T>)sources[0]);
      }
   }

   static abstract class RegInstructionWithDestSrc<T extends Type> extends RegInstruction{

      RegInstructionWithDestSrc(Instruction _from, Reg<T> _dest, Reg<T> _src){
         super(_from, 1, 1);
         dests[0]=_dest;
         sources[0]=_src;
      }
      Reg<T> getDest(){
         return((Reg<T>)dests[0]);
      }
      Reg<T> getSrc(){
         return((Reg<T>)sources[0]);
      }
   }

   static class branch extends RegInstruction{
      String name;
      int pc;

      branch(Instruction _from, String _name, int _pc){
         super(_from, 0, 0);
         name = _name;
         pc = _pc;
      }

      @Override public void render(RegISARenderer r){
         r.append(name + " ");
         r.label(pc);
      }
   }

   static class put_field<T extends Type> extends RegInstructionWithSrc<T>{
      boolean isStatic;

      put_field(Instruction _from, Reg<T> _src){
         super(_from, _src);
         isStatic = (_from instanceof InstructionSet.I_PUTSTATIC);
      }

      @Override void render(RegISARenderer r){
         r.append("store_");
         if(isStatic){
            r.append("static_");
         }
         r.append("field_");
         String dotClassName = from.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName();
         String name = from.asFieldAccessor().getConstantPoolFieldEntry().getName();


         r.typeName(getSrc());
         if(!isStatic){
            r.separator().append("$d").append(getSrc().index);
         }
         r.space().append(dotClassName).dot().append(name).separator().regName(getSrc());


      }

   }

   static class get_field<T extends Type> extends RegInstructionWithDest<T>{

      boolean isStatic;

      get_field(Instruction _from, Reg<T> _dest){
         super(_from, _dest);
         isStatic = (_from instanceof InstructionSet.I_GETSTATIC);
      }

      @Override void render(RegISARenderer r){
         r.append("load_");
         if(isStatic){
            r.append("static_");
         }
         r.append("field_");
         String dotClassName = from.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName();
         String name = from.asFieldAccessor().getConstantPoolFieldEntry().getName();


         r.typeName(getDest()).space().regName(getDest());
         if(!isStatic){
            r.separator().append("$d").append(getDest().index);
         }
         r.separator().append(dotClassName).dot().append(name);

      }

   }

   static class call extends RegInstruction{

      call(Instruction _from){
         super(_from, 0, 0);
      }

      @Override void render(RegISARenderer r){
         String dotClassName = from.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName();
         String name = from.asMethodCall().getConstantPoolMethodEntry().getName();
         TypeHelper.ArgsAndReturnType argsAndReturnType = from.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType();


         TypeHelper.Type returnType = argsAndReturnType.getReturnType();


         if(returnType.isVoid()){
            r.append("call_").append("void").space().append("VOID");
         }else if(returnType.isInt()){
            r.append("call_").append("s64").space().append("$s").append(from.getPreStackBase()+from.getMethod().getCodeEntry().getMaxLocals());

         }else if(returnType.isDouble()){
            r.append("call_").append("f64").space().append("$d").append(from.getPreStackBase()+from.getMethod().getCodeEntry().getMaxLocals());


         }


         r.separator().append(dotClassName).dot().append(name).space();

         for(TypeHelper.Arg arg : argsAndReturnType.getArgs()){
            if(arg.getArgc() > 0){
               r.separator();
            }
            if(arg.isDouble()){
               r.append("$d").append(from.getPreStackBase()+from.getMethod().getCodeEntry().getMaxLocals() + arg.getArgc());
            }else if(arg.isFloat()){
               r.append("$s").append(from.getPreStackBase()+from.getMethod().getCodeEntry().getMaxLocals() + arg.getArgc());
            }else if(arg.isInt()){
               r.append("$s").append(from.getPreStackBase()+from.getMethod().getCodeEntry().getMaxLocals() + arg.getArgc());
            }else if(arg.isLong()){
               r.append("$d").append(from.getPreStackBase()+from.getMethod().getCodeEntry().getMaxLocals() + arg.getArgc());
            }
         }
      }
   }



   static class nyi extends RegInstruction{

      nyi(Instruction _from){
         super(_from, 0, 0);
      }



      @Override void render(RegISARenderer r){

         r.append("NYI ").i(from);

      }
   }

   static class ld_kernarg<T extends Type> extends RegInstructionWithDest<T>{


      ld_kernarg(Instruction _from, Reg<T> _dest){
         super(_from, _dest);
      }

      @Override void render(RegISARenderer r){
         r.append("ld_kernarg_").typeName(getDest()).space().regName(getDest()).separator().append("[%_arg").append(getDest().index).append("]");
      }
   }

   static abstract class binary_const<T extends Type, C> extends RegInstructionWithDestSrc<T>{
      C value;
      String op;

      binary_const(Instruction _from, String _op, Reg<T> _dest, Reg _src, C _value){
         super(_from, _dest, _src);
         value = _value;
         op = _op;
      }

      @Override void render(RegISARenderer r){
         r.append(op).typeName(getDest()).space().regName(getDest()).separator().regName(getSrc()).separator().append(value.toString());
      }
   }

   static class add_const<T extends Type, C> extends binary_const<T, C>{

      add_const(Instruction _from, Reg<T> _dest, Reg _src, C _value){
         super(_from, "add_", _dest, _src, _value);

      }

   }

   static class mul_const<T extends Type, C> extends binary_const<T, C>{

      mul_const(Instruction _from, Reg<T> _dest, Reg _src, C _value){
         super(_from, "mul_", _dest, _src, _value);

      }

   }


   static class cvt<T1 extends Type, T2 extends Type> extends RegInstruction{


      cvt(Instruction _from, Reg<T1> _dest, Reg<T2> _src){
         super(_from, 1, 1);
         dests[0] = _dest;
         sources[0]= _src;
      }

      Reg<T1> getDest(){
         return((Reg<T1>)dests[0]);
      }
      Reg<T2> getSrc(){
         return((Reg<T2>)sources[0]);
      }

      @Override void render(RegISARenderer r){
         r.append("cvt_").typeName(getDest()).append("_").typeName(getSrc()).space().regName(getDest()).separator().regName(getSrc());
      }
   }


   static class retvoid extends RegInstruction{

      retvoid(Instruction _from){
         super(_from, 0, 0);

      }

      @Override void render(RegISARenderer r){
         r.append("ret");
      }
   }
   static class ret<T extends Type> extends RegInstructionWithSrc<T>{

      ret(Instruction _from, Reg<T> _src){
         super(_from, _src);

      }

      @Override void render(RegISARenderer r){
         r.append("ret_").typeName(getSrc()).space().regName(getSrc());
      }
   }

   static class store<T extends Type> extends RegInstructionWithSrc<T>{

      Reg_u64 mem;

      store(Instruction _from, Reg_u64 _mem, Reg<T> _src){
         super(_from, _src);

         mem = _mem;
      }
      @Override void render(RegISARenderer r){
         r.append("st_global_").typeName(getSrc()).space(). append("["). regName(mem). append("+"). array_len_offset(). append("]").separator().regName(getSrc());
      }
   }



   static class load<T extends Type> extends RegInstructionWithDest<T>{
      Reg_u64 mem;


      load(Instruction _from, Reg<T> _dest, Reg_u64 _mem){
         super(_from, _dest);

         mem = _mem;
      }
      @Override void render(RegISARenderer r){
         r.append("ld_global" ).typeName(getDest()).space().regName(getDest()).separator(). append("["). regName(mem). append("+"). array_len_offset(). append("]");
      }
   }



   static final class mov<T extends Type> extends RegInstructionWithDestSrc{

      public mov(Instruction _from, Reg<T> _dest, Reg<T> _src){
         super(_from, _dest, _src);
      }

      @Override void render(RegISARenderer r){
         r.append("mov_").typeName(getDest()).space().regName(getDest()).separator().regName(getSrc());

      }
   }

   static abstract class binary<T extends Type> extends RegInstruction{

      String op;
      public binary(Instruction _from, String _op, Reg<T> _dest, Reg<T> _lhs, Reg<T> _rhs){
         super(_from, 1, 2);
         dests[0] = _dest;
         sources[0] = _lhs;
         sources[1] = _rhs;
         op = _op;
      }

         @Override void render(RegISARenderer r){
            r.append(op).typeName(getDest()).space().regName(getDest()).separator().regName(getLhs()).separator().regName(getRhs());
         }
      Reg<T> getDest(){
         return((Reg<T>)dests[0]);
      }
      Reg<T> getRhs(){
         return((Reg<T>)sources[1]);
      }
      Reg<T> getLhs(){
         return((Reg<T>)sources[0]);
      }

   }

  /* static abstract class binaryRegConst<T extends Type, C> extends RegInstruction{
      Reg<T> dest, lhs;
      C value;
      String op;

      public binaryRegConst(Instruction _from, String _op,  Reg<T> _dest, Reg<T> _lhs, C _value){
         super(_from);
         dest = _dest;
         lhs = _lhs;
         value = _value;
         op = _op;
      }
      @Override void render(RegISARenderer r){
         r.append(op).typeName(dest).space().regName(dest).separator().regName(lhs).separator().append(value.toString());
      }
   }

   static  class addConst<T extends Type, C> extends binaryRegConst<T, C>{

      public addConst(Instruction _from,   Reg<T> _dest, Reg<T> _lhs, C _value_rhs){
         super(_from, "add_", _dest, _lhs, _value_rhs);
      }
   }
   */

   static  class add<T extends Type> extends binary<T>{
      public add(Instruction _from,  Reg<T> _dest, Reg<T> _lhs, Reg<T>  _rhs){
         super(_from, "add_", _dest, _lhs, _rhs);
      }

   }

   static  class sub<T extends Type> extends binary<T>{
      public sub(Instruction _from,  Reg<T> _dest, Reg<T> _lhs, Reg<T>  _rhs){
         super(_from, "sub_", _dest, _lhs, _rhs);
      }

   }
   static  class div<T extends Type> extends binary<T>{
      public div(Instruction _from,  Reg<T> _dest, Reg<T> _lhs, Reg<T>  _rhs){
         super(_from, "div_", _dest, _lhs, _rhs);
      }

   }
   static  class mul<T extends Type> extends binary<T>{
      public mul(Instruction _from,  Reg<T> _dest, Reg<T> _lhs, Reg<T>  _rhs){
         super(_from, "mul_", _dest, _lhs, _rhs);
      }

   }
   static  class rem<T extends Type> extends binary<T>{
      public rem(Instruction _from,  Reg<T> _dest, Reg<T> _lhs, Reg<T>  _rhs){
         super(_from, "rem_", _dest, _lhs, _rhs);
      }

   }

   static class mov_const<T extends Type, V> extends RegInstructionWithDest<T>{

      V value;

      public mov_const(Instruction _from, Reg<T> _dest, V _value){
         super(_from, _dest);
         value = _value;
      }

      @Override void render(RegISARenderer r){
         r.append("mov_").typeName(getDest()).space().regName(getDest()).separator().append(value.toString());

      }
   }

   List<RegInstruction> instructions = new ArrayList<RegInstruction>();
   ClassModel.ClassModelMethod method;

   boolean optimizeMoves = false|| Config.enableOptimizeRegMoves;

   void add(RegInstruction _regInstruction){
      // before we add lets see if this is a redundant mov
      if (optimizeMoves && _regInstruction.sources != null && _regInstruction.sources.length>0){
         for (int regIndex = 0; regIndex <_regInstruction.sources.length; regIndex++){
            Reg r = _regInstruction.sources[regIndex];
            if (r.isStack()){
               // look up the list of reg instructions for the last mov which assigns to r
               int i=instructions.size();
               while((--i)>=0){
                  if (instructions.get(i) instanceof mov){
                     // we have found a move
                     mov candidateForRemoval = (mov)instructions.get(i);
                     if (candidateForRemoval.from.getBlock() == _regInstruction.from.getBlock()
                           && candidateForRemoval.getDest().isStack() && candidateForRemoval.getDest().equals(r)){
                        // so i may be a candidate if between i and instruction.size() i.dest() is not mutated
                        boolean mutated = false;
                        for (int x=i+1; !mutated && x<instructions.size(); x++){
                           if (instructions.get(x).dests.length>0 && instructions.get(x).dests[0].equals(candidateForRemoval.getSrc())){
                              mutated = true;
                           }
                        }
                        if (!mutated){
                        instructions.remove(i);
                        // removed mov
                        _regInstruction.sources[regIndex] = candidateForRemoval.getSrc();
                        break;
                        }
                     }
                  }
               }
            }
         }
      }

      instructions.add(_regInstruction);
   }



   RegISARenderer render(RegISARenderer r){
      r.append("version 1:0").nl();
      r.append("kernel &" + method.getName() + "(");
      int argOffset = method.isStatic() ? 0 : 1;
      if(!method.isStatic()){
         r.nl().pad(3).append("kernarg_u64 %_arg0");
      }

      for(TypeHelper.Arg arg : method.argsAndReturnType.getArgs()){
         if((method.isStatic() && arg.getArgc() == 0)){
            r.nl();
         }else{
            r.separator().nl();
         }

         Type type = Type.forJavaType(arg);

         r.pad(3).append("kernarg_").append(type.getTypeName()).append(" %_arg" + (arg.getArgc() + argOffset));

      }
      r.nl().pad(3).append("){").nl();

      java.util.Set<Instruction> s = new java.util.HashSet<Instruction>();

      for(RegInstruction i : instructions){
         if(!(i instanceof ld_kernarg) && !s.contains(i.from)){
            s.add(i.from);
            if (i.from.isBranchTarget()){

            r.label(i.from.getThisPC());
            r.nl();
            }
            r.nl().pad(1).append("// ").mark().append(i.from.getThisPC()).relpad(2).space().i(i.from).nl();
         }
         r.pad(9);
         i.render(r);
         r.semicolon();

         r.nl();
      }
      r.append("};");
      return (r);
   }

   public Reg getRegOfLastWriteToIndex(int _index){

       int idx=instructions.size();
       while (--idx>=0){
           RegInstruction i = instructions.get(idx);
           if (i.dests != null){
               for (Reg d:i.dests){
                  if (d.index == _index){
                      return(d);
                  }
               }
           }
       }


       return(null);
   }

   public void addmov(Instruction _i, Type _type, int _from, int _to){
       if (_type.equals(Type.u64) ||_type.getBits()==32 ){
           if (_type.equals(Type.u64)){
               add(new mov<u64>(_i, new StackReg_u64(_i, _to), new StackReg_u64(_i, _from)));
           }else if (_type.equals(Type.s32)){
               add(new mov<s32>(_i, new StackReg_s32(_i, _to), new StackReg_s32(_i, _from)));
           } else{
               throw new IllegalStateException (" unknown type 1 type for first of DUP2");
           }

       }else{
           throw new IllegalStateException (" unknown type 2 type for DUP2");
       }
   }

   public Reg addmov(Instruction _i, int _from, int _to){
       Reg r= getRegOfLastWriteToIndex(_i.getPreStackBase()+_i.getMethod().getCodeEntry().getMaxLocals()+_from);
       addmov(_i, r.type, _from, _to);
       return(r);
   }
   private void add(Instruction _i){

      switch(_i.getByteCode()){

         case ACONST_NULL:
            add(new nyi(_i));
            break;
         case ICONST_M1:
         case ICONST_0:
         case ICONST_1:
         case ICONST_2:
         case ICONST_3:
         case ICONST_4:
         case ICONST_5:
         case BIPUSH:
         case SIPUSH:
            add(new mov_const<s32, Integer>(_i, new StackReg_s32(_i, 0), _i.asIntegerConstant().getValue()));
            break;
         case LCONST_0:
         case LCONST_1:
            add(new mov_const<s64, Long>(_i, new StackReg_s64(_i, 0), _i.asLongConstant().getValue()));
            break;
         case FCONST_0:
         case FCONST_1:
         case FCONST_2:
            add(new mov_const<f32, Float>(_i,  new StackReg_f32(_i, 0), _i.asFloatConstant().getValue()));
         break;
         case DCONST_0:
         case DCONST_1:
            add(new mov_const<f64, Double>(_i,  new StackReg_f64(_i, 0), _i.asDoubleConstant().getValue()));
            break;
         // case BIPUSH: moved up
         // case SIPUSH: moved up

         case LDC:
         case LDC_W:
         case LDC2_W:{
            InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant) _i;

            ClassModel.ConstantPool.ConstantEntry e = (ClassModel.ConstantPool.ConstantEntry) cpe.getConstantPoolEntry();
            if(e instanceof ClassModel.ConstantPool.DoubleEntry){
               add(new mov_const<f64, Double>(_i, new StackReg_f64(_i, 0), ((ClassModel.ConstantPool.DoubleEntry) e).getValue()));
            }else if(e instanceof ClassModel.ConstantPool.FloatEntry){
               add(new mov_const<f32, Float>(_i, new StackReg_f32(_i, 0), ((ClassModel.ConstantPool.FloatEntry) e).getValue()));
            }else if(e instanceof ClassModel.ConstantPool.IntegerEntry){
               add(new mov_const<s32, Integer>(_i, new StackReg_s32(_i, 0), ((ClassModel.ConstantPool.IntegerEntry) e).getValue()));
            }else if(e instanceof ClassModel.ConstantPool.LongEntry){
               add(new mov_const<s64, Long>(_i, new StackReg_s64(_i, 0), ((ClassModel.ConstantPool.LongEntry) e).getValue()));

            }

         }
         break;
         // case LLOAD: moved down
         // case FLOAD: moved down
         // case DLOAD: moved down
         //case ALOAD: moved down
         case ILOAD:
         case ILOAD_0:
         case ILOAD_1:
         case ILOAD_2:
         case ILOAD_3:
            add(new mov<s32>(_i, new StackReg_s32(_i, 0), new VarReg_s32(_i)));

            break;
         case LLOAD:
         case LLOAD_0:
         case LLOAD_1:
         case LLOAD_2:
         case LLOAD_3:
            add(new mov<s64>(_i, new StackReg_s64(_i, 0),new VarReg_s64(_i)));
            break;
         case FLOAD:
         case FLOAD_0:
         case FLOAD_1:
         case FLOAD_2:
         case FLOAD_3:
            add(new mov<f32>(_i, new StackReg_f32(_i, 0), new VarReg_f32(_i)));
            break;
         case DLOAD:
         case DLOAD_0:
         case DLOAD_1:
         case DLOAD_2:
         case DLOAD_3:
            add(new mov<f64>(_i,  new StackReg_f64(_i, 0),new VarReg_f64(_i)));
            break;
         case ALOAD:
         case ALOAD_0:
         case ALOAD_1:
         case ALOAD_2:
         case ALOAD_3:
            add(new mov<u64>(_i, new StackReg_u64(_i, 0), new VarReg_u64(_i)));
            break;
         case IALOAD:{
            add(new cvt<u64, s32>(_i, new StackReg_u64(_i, 1), new StackReg_s32(_i, 1)));  // index converted to 64 bit
            add(new mul_const<u64, Long>(_i, new StackReg_u64(_i, 1), new StackReg_u64(_i, 1),  (long) 4));
            add(new add<u64>(_i, new StackReg_u64(_i, 1), new StackReg_u64(_i, 0), new StackReg_u64(_i, 1)));
            add(new load<s32>(_i, new StackReg_s32(_i, 0), new StackReg_u64(_i, 1)));
         }


         break;
         case LALOAD:
            add(new nyi(_i));
            break;
         case FALOAD:
            add(new nyi(_i));
            break;
         case DALOAD:
            add(new nyi(_i));
            break;
         case AALOAD:
            add(new nyi(_i));
            break;
         case BALOAD:
            add(new nyi(_i));
            break;
         case CALOAD:
            add(new nyi(_i));
            break;
         case SALOAD:
            add(new nyi(_i));
            break;
         //case ISTORE: moved down
         // case LSTORE:  moved down
         //case FSTORE: moved down
         //case DSTORE:  moved down
         // case ASTORE: moved down
         case ISTORE:
         case ISTORE_0:
         case ISTORE_1:
         case ISTORE_2:
         case ISTORE_3:
            add(new mov<s32>(_i, new VarReg_s32(_i), new StackReg_s32(_i, 0)));

            break;
         case LSTORE:
         case LSTORE_0:
         case LSTORE_1:
         case LSTORE_2:
         case LSTORE_3:
            add(new mov<s64>(_i, new VarReg_s64(_i), new StackReg_s64(_i, 0)));

            break;
         case FSTORE:
         case FSTORE_0:
         case FSTORE_1:
         case FSTORE_2:
         case FSTORE_3:
            add(new mov<f32>(_i, new VarReg_f32(_i), new StackReg_f32(_i, 0)));
            break;
         case DSTORE:
         case DSTORE_0:
         case DSTORE_1:
         case DSTORE_2:
         case DSTORE_3:
            add(new mov<f64>(_i, new VarReg_f64(_i), new StackReg_f64(_i, 0)));
            break;
         case ASTORE:
         case ASTORE_0:
         case ASTORE_1:
         case ASTORE_2:
         case ASTORE_3:
            add(new mov<u64>(_i, new VarReg_u64(_i), new StackReg_u64(_i, 0)));

            break;
         case IASTORE:

         {

                /*

                cvt_u64_s32 $d(index), $s(index)
                mul_u64 $d(index), $d(index), (sizeof array element);
                add_u64 $d(index), $d(index), $d{array};
                st_global_s32 $s3, [$d6 + 24];
                 */
            add(new cvt<u64, s32>(_i, new StackReg_u64(_i, 1), new StackReg_s32(_i, 1)));
            add(new mul_const<u64, Long>(_i, new StackReg_u64(_i, 1), new StackReg_u64(_i, 1), (long)4));
            add(new add<u64>(_i,  new StackReg_u64(_i, 1),  new StackReg_u64(_i, 0), new StackReg_u64(_i, 1)));  ;
            add(new store<s32>(_i,new StackReg_u64(_i, 1), new StackReg_s32(_i, 2)));

         }
         break;
         case LASTORE:
            add(new nyi(_i));
            break;
         case FASTORE:
            add(new nyi(_i));
            break;
         case DASTORE:
            add(new nyi(_i));
            break;
         case AASTORE:
            add(new nyi(_i));
            break;
         case BASTORE:
            add(new nyi(_i));
            break;
         case CASTORE:
            add(new nyi(_i));
            break;
         case SASTORE:
            add(new nyi(_i));
            break;
         case POP:
            add(new nyi(_i));
            break;
         case POP2:
            add(new nyi(_i));
            break;
         case DUP:
            add(new nyi(_i));
            break;
         case DUP_X1:
            add(new nyi(_i));
            break;
         case DUP_X2:  {
            // Reg r = getRegOfLastWriteToIndex(_i.getPreStackBase()+_i.getMethod().getCodeEntry().getMaxLocals()+3);
            // addmov(_i, 2, 4);
             addmov(_i, 2, 3);
             addmov(_i, 1, 2);
             addmov(_i, 0, 1);
             addmov(_i, 3, 0);
           // add(new mov<s32>(_i, new StackReg_s32(_i, 3), new StackReg_s32(_i, 2)));
           // add(new mov<s32>(_i, new StackReg_s32(_i, 2),new StackReg_s32(_i, 1)));

           // add(new mov<s32>(_i, new StackReg_s32(_i, 1), new StackReg_s32(_i, 0)));

           // add(new mov<s32>(_i, new StackReg_s32(_i, 0), new StackReg_s32(_i, 3)));
         }
            break;
         case DUP2:  {
            // DUP2 is problematic. DUP2 either dups top two items or one depending on the 'type' of the stack items.
            // To complicate this further HSA large model wants object/mem references to be 64 bits (type 2 in Java) whereas
             // in java object/array refs are 32 bits (type 1).
            addmov(_i, 0,  2);
            addmov(_i, 1,  3);
            }
            break;
         case DUP2_X1:
            add(new nyi(_i));
            break;
         case DUP2_X2:
            add(new nyi(_i));
            break;
         case SWAP:
            add(new nyi(_i));
            break;
         case IADD:
            add(new add<s32>(_i, new StackReg_s32(_i, 0), new StackReg_s32(_i, 0), new StackReg_s32(_i, 1)));


            break;
         case LADD:
            add(new add<s64>(_i, new StackReg_s64(_i, 0), new StackReg_s64(_i, 0), new StackReg_s64(_i, 1)));


            break;
         case FADD:
            add(new add<f32>(_i, new StackReg_f32(_i, 0), new StackReg_f32(_i, 0), new StackReg_f32(_i, 1)));
            break;
         case DADD:
            add(new add<f64>(_i, new StackReg_f64(_i, 0), new StackReg_f64(_i, 0), new StackReg_f64(_i, 1)));

            break;
         case ISUB:
            add(new sub<s32>(_i, new StackReg_s32(_i, 0), new StackReg_s32(_i, 0), new StackReg_s32(_i, 1)));

            break;
         case LSUB:
            add(new sub<s64>(_i, new StackReg_s64(_i, 0), new StackReg_s64(_i, 0), new StackReg_s64(_i, 1)));

            break;
         case FSUB:
            add(new sub<f32>(_i, new StackReg_f32(_i, 0), new StackReg_f32(_i, 0), new StackReg_f32(_i, 1)));

            break;
         case DSUB:
            add(new sub<f64>(_i, new StackReg_f64(_i, 0), new StackReg_f64(_i, 0), new StackReg_f64(_i, 1)));

            break;
         case IMUL:
            add(new mul<s32>(_i, new StackReg_s32(_i, 0), new StackReg_s32(_i, 0), new StackReg_s32(_i, 1)));

            break;
         case LMUL:
            add(new mul<s64>(_i, new StackReg_s64(_i, 0), new StackReg_s64(_i, 0), new StackReg_s64(_i, 1)));

            break;
         case FMUL:
            add(new mul<f32>(_i, new StackReg_f32(_i, 0), new StackReg_f32(_i, 0), new StackReg_f32(_i, 1)));

            break;
         case DMUL:
            add(new mul<f64>(_i, new StackReg_f64(_i, 0), new StackReg_f64(_i, 0), new StackReg_f64(_i, 1)));
            break;
         case IDIV:
            add(new div<s32>(_i, new StackReg_s32(_i, 0), new StackReg_s32(_i, 0), new StackReg_s32(_i, 1)));

            break;
         case LDIV:
            add(new div<s64>(_i, new StackReg_s64(_i, 0), new StackReg_s64(_i, 0), new StackReg_s64(_i, 1)));

            break;
         case FDIV:
            add(new div<f32>(_i, new StackReg_f32(_i, 0), new StackReg_f32(_i, 0), new StackReg_f32(_i, 1)));

            break;
         case DDIV:
            add(new div<f64>(_i, new StackReg_f64(_i, 0), new StackReg_f64(_i, 0), new StackReg_f64(_i, 1)));

            break;
         case IREM:
            add(new rem<s32>(_i, new StackReg_s32(_i, 0), new StackReg_s32(_i, 0), new StackReg_s32(_i, 1)));

            break;
         case LREM:
            add(new rem<s64>(_i, new StackReg_s64(_i, 0), new StackReg_s64(_i, 0), new StackReg_s64(_i, 1)));

            break;
         case FREM:
            add(new rem<f32>(_i, new StackReg_f32(_i, 0), new StackReg_f32(_i, 0), new StackReg_f32(_i, 1)));

            break;
         case DREM:
            add(new rem<f64>(_i, new StackReg_f64(_i, 0), new StackReg_f64(_i, 0), new StackReg_f64(_i, 1)));

            break;
         case INEG:
            add(new nyi(_i));
            break;
         case LNEG:
            add(new nyi(_i));
            break;
         case FNEG:
            add(new nyi(_i));
            break;
         case DNEG:
            add(new nyi(_i));
            break;
         case ISHL:
            add(new nyi(_i));
            break;
         case LSHL:
            add(new nyi(_i));
            break;
         case ISHR:
            add(new nyi(_i));
            break;
         case LSHR:
            add(new nyi(_i));
            break;
         case IUSHR:
            add(new nyi(_i));
            break;
         case LUSHR:
            add(new nyi(_i));
            break;
         case IAND:
            add(new nyi(_i));
            break;
         case LAND:
            add(new nyi(_i));
            break;
         case IOR:
            add(new nyi(_i));
            break;
         case LOR:
            add(new nyi(_i));
            break;
         case IXOR:
            add(new nyi(_i));
            break;
         case LXOR:
            add(new nyi(_i));
            break;
         case IINC:
            add(new add_const<s32, Integer>(_i, new VarReg_s32(_i),  new VarReg_s32(_i),  ((InstructionSet.I_IINC) _i).getDelta()));

            break;
         case I2L:
            add(new cvt<s64, s32>(_i, new StackReg_s64(_i, 0), new StackReg_s32(_i, 0)));
            break;
         case I2F:
            add(new cvt<f32, s32>(_i, new StackReg_f32(_i, 0), new StackReg_s32(_i, 0)));
            break;
         case I2D:
            add(new cvt<f64, s32>(_i, new StackReg_f64(_i, 0), new StackReg_s32(_i, 0)));
            break;
         case L2I:
            add(new nyi(_i));
            break;
         case L2F:
            add(new nyi(_i));
            break;
         case L2D:
            add(new nyi(_i));
            break;
         case F2I:
            add(new nyi(_i));
            break;
         case F2L:
            add(new nyi(_i));
            break;
         case F2D:
            add(new nyi(_i));
            break;
         case D2I:
            add(new nyi(_i));
            break;
         case D2L:
            add(new nyi(_i));
            break;
         case D2F:
            add(new nyi(_i));
            break;
         case I2B:
            add(new nyi(_i));
            break;
         case I2C:
            add(new nyi(_i));
            break;
         case I2S:
            add(new nyi(_i));
            break;
         case LCMP:
            add(new nyi(_i));
            break;
         case FCMPL:
            add(new nyi(_i));
            break;
         case FCMPG:
            add(new nyi(_i));
            break;
         case DCMPL:
            add(new nyi(_i));
            break;
         case DCMPG:
            add(new nyi(_i));
            break;
         case IFEQ:
         case IFNE:
         case IFLT:
         case IFGE:
         case IFGT:
         case IFLE:
         case IF_ICMPEQ:
         case IF_ICMPNE:
         case IF_ICMPLT:
         case IF_ICMPGE:
         case IF_ICMPGT:
         case IF_ICMPLE:
         case IF_ACMPEQ:
         case IF_ACMPNE:
         case GOTO:
         case IFNULL:
         case IFNONNULL:
         case GOTO_W:
            add(new branch(_i, _i.getByteCode().getName(), _i.asBranch().getAbsolute()));
            break;
         case JSR:
            add(new nyi(_i));
            break;
         case RET:
            add(new nyi(_i));
            break;
         case TABLESWITCH:
            add(new nyi(_i));
            break;
         case LOOKUPSWITCH:
            add(new nyi(_i));
            break;
         case IRETURN:
            add(new ret<s32>(_i, new StackReg_s32(_i, 0)));
            break;
         case LRETURN:
            add(new ret<s64>(_i, new StackReg_s64(_i, 0)));
            break;
         case FRETURN:
            add(new ret<f32>(_i, new StackReg_f32(_i, 0)));
            break;
         case DRETURN:
            add(new ret<f64>(_i, new StackReg_f64(_i, 0)));
            break;
         case ARETURN:
            add(new nyi(_i));
            break;
         case RETURN:
            add(new retvoid(_i));
            break;
         case GETSTATIC:
         case GETFIELD:{
            TypeHelper.Type type = _i.asFieldAccessor().getConstantPoolFieldEntry().getType();
            if(type.isArray()){
               add(new get_field<u64>(_i, new StackReg_u64(_i, 0)));
            }else if(type.isInt()){
               add(new get_field<s32>(_i, new StackReg_s32(_i, 0)));
            }else if(type.isFloat()){
               add(new get_field<f32>(_i, new StackReg_f32(_i, 0)));
            }
         }
         break;
         case PUTSTATIC:
         case PUTFIELD:{
            TypeHelper.Type type = _i.asFieldAccessor().getConstantPoolFieldEntry().getType();
            if(type.isArray()){
               add(new put_field<u64>(_i, new StackReg_u64(_i, 0)));
            }else if(type.isInt()){
               add(new put_field<s32>(_i, new StackReg_s32(_i, 0)));
            }else if(type.isFloat()){
               add(new put_field<f32>(_i, new StackReg_f32(_i, 0)));
            }

         }
         break;
         case INVOKEVIRTUAL:
         case INVOKESPECIAL:
         case INVOKESTATIC:
         case INVOKEINTERFACE:
         case INVOKEDYNAMIC:
            add(new call(_i));
            break;
         case NEW:
            add(new nyi(_i));
            break;
         case NEWARRAY:
            add(new nyi(_i));
            break;
         case ANEWARRAY:
            add(new nyi(_i));
            break;
         case ARRAYLENGTH:
            add(new nyi(_i));
            break;
         case ATHROW:
            add(new nyi(_i));
            break;
         case CHECKCAST:
            add(new nyi(_i));
            break;
         case INSTANCEOF:
            add(new nyi(_i));
            break;
         case MONITORENTER:
            add(new nyi(_i));
            break;
         case MONITOREXIT:
            add(new nyi(_i));
            break;
         case WIDE:
            add(new nyi(_i));
            break;
         case MULTIANEWARRAY:
            add(new nyi(_i));
            break;
         case JSR_W:
            add(new nyi(_i));
            break;

      }

   }

   RegISA(ClassModel.ClassModelMethod _method){
      method = _method;
      for(Instruction i : method.getInstructions()){
         if(i.getThisPC() == 0){
            int argOffset = 0;
            if(!method.isStatic()){
               add(new ld_kernarg(i, new VarReg_u64(0)));
               argOffset++;
            }
            for(TypeHelper.Arg arg : method.argsAndReturnType.getArgs()){
               if(arg.isArray()){
                  add(new ld_kernarg(i, new VarReg_u64(arg.getArgc() + argOffset)));
               }else if(arg.isInt()){
                  add(new ld_kernarg(i, new VarReg_s32(arg.getArgc() + argOffset)));


               }
            }
         }
         add(i);
      }
   }
}
