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


   public abstract static class Reg<T extends PrimitiveType>{
      int index;
      public T type;
      public boolean stack;

      Reg(int _index, T _type, boolean _stack){
         index = _index;
         type = _type;
         stack = _stack;
      }

      public boolean isStack(){
         return (stack);
      }

      @Override
      public boolean equals(Object _other){
         if(_other instanceof Reg){
            Reg otherReg = (Reg) _other;
            return (type.equals(otherReg.type) && index == otherReg.index);
         }
         return false;
      }

   }

   public abstract static class Reg_f64 extends Reg<f64>{
      Reg_f64(int _index, boolean _stack){
         super(_index, PrimitiveType.f64, _stack);
      }
   }

   public abstract static class Reg_ref extends Reg<ref>{
      Reg_ref(int _index, boolean _stack){
         super(_index, PrimitiveType.ref, _stack);
      }
   }

   public abstract static class Reg_s64 extends Reg<s64>{
      Reg_s64(int _index, boolean _stack){
         super(_index, PrimitiveType.s64, _stack);
      }
   }

   public abstract static class Reg_s32 extends Reg<s32>{
      Reg_s32(int _index, boolean _stack){
         super(_index, PrimitiveType.s32, _stack);
      }
   }

   public abstract static class Reg_f32 extends Reg<f32>{
      Reg_f32(int _index, boolean _stack){
         super(_index, PrimitiveType.f32, _stack);
      }
   }

   public static class StackReg_f64 extends Reg_f64{
      StackReg_f64(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
      }
   }

   public static class StackReg_f32 extends Reg_f32{
      StackReg_f32(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
      }
   }

   public static class StackReg_s64 extends Reg_s64{
      StackReg_s64(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
      }
   }

   public static class StackReg_s32 extends Reg_s32{
      StackReg_s32(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
      }
   }

   public static class StackReg_ref extends Reg_ref{
      StackReg_ref(Instruction _from, int _offset){
         super(_from.getPreStackBase() + _from.getMethod().getCodeEntry().getMaxLocals() + _offset, true);
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

   public static class VarReg_ref extends Reg_ref{
      VarReg_ref(Instruction _from){
         super(_from.asLocalVariableAccessor().getLocalVariableTableIndex(), false);
      }

      public VarReg_ref(int _index){
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

      public VarReg_f32(int _index){
         super(_index, false);
      }
   }

   static class State{
      int s32[];
      float f32[];
      long s64[];
      double f64[];
      Object ref[];
      PrimitiveType typeInfo[];
      int maxLocals;

      State(int _maxReg, int _maxLocals){
         maxLocals = _maxLocals;

         s32 = new int[_maxReg];
         f32 = new float[_maxReg];
         s64 = new long[_maxReg];
         f64 = new double[_maxReg];
         ref = new Object[_maxReg];
         typeInfo = new PrimitiveType[_maxReg];
         for(int i = 0; i < _maxReg; i++){
            typeInfo[i] = PrimitiveType.none;
         }
      }

      void s32Set(int _index, int _value){
         s32[_index] = _value;
         typeInfo[_index] = PrimitiveType.s32;
      }

      void f32Set(int _index, float _value){
         f32[_index] = _value;
         typeInfo[_index] = PrimitiveType.f32;
      }

      void s64Set(int _index, long _value){
         s64[_index] = _value;
         typeInfo[_index] = PrimitiveType.s64;
      }

      void f64Set(int _index, double _value){
         f64[_index] = _value;
         typeInfo[_index] = PrimitiveType.f64;
      }

      void refSet(int _index, Object _value){
         ref[_index] = _value;
         typeInfo[_index] = PrimitiveType.ref;
      }

      int s32Get(int _index){
         int value = 0;
         if(typeInfo[_index] == PrimitiveType.s32){
            value = s32[_index];
            if(_index >= maxLocals){
               //clobber
               typeInfo[_index] = PrimitiveType.none;
            }
         }else{
            throw new IllegalStateException("invalid type for reg " + _index);
         }
         return (value);
      }

      float f32Get(int _index){
         float value = 0;
         if(typeInfo[_index] == PrimitiveType.f32){
            value = f32[_index];
            if(_index >= maxLocals){
               //clobber
               typeInfo[_index] = PrimitiveType.none;
            }
         }else{
            throw new IllegalStateException("invalid type for reg " + _index);
         }
         return (value);
      }

      double f64Get(int _index){
         double value = 0;
         if(typeInfo[_index] == PrimitiveType.f64){
            value = f64[_index];
            if(_index >= maxLocals){
               //clobber
               typeInfo[_index] = PrimitiveType.none;
            }
         }else{
            throw new IllegalStateException("invalid type for reg " + _index);
         }
         return (value);
      }

      long s64Get(int _index){
         long value = 0;
         if(typeInfo[_index] == PrimitiveType.s64){
            value = s64[_index];
            if(_index >= maxLocals){
               //clobber
               typeInfo[_index] = PrimitiveType.none;
            }
         }else{
            throw new IllegalStateException("invalid type for reg " + _index);
         }
         return (value);
      }

      Object refGet(int _index){
         Object value = 0;
         if(typeInfo[_index] == PrimitiveType.ref){
            value = ref[_index];
            if(_index >= maxLocals){
               //clobber
               typeInfo[_index] = PrimitiveType.none;
            }
         }else{
            throw new IllegalStateException("invalid type for reg " + _index);
         }
         return (value);
      }


   }

   static class Delta{

   }


   static abstract class RegInstruction{
      Instruction from;
      Reg[] dests = null;
      Reg[] sources = null;


      RegInstruction(Instruction _from, int _destCount, int _sourceCount){
         from = _from;
         dests = new Reg[_destCount];
         sources = new Reg[_sourceCount];
      }

      abstract public Delta execute(State state);

      abstract void render(RegISARenderer r);

   }

   static abstract class RegInstructionWithDest<T extends PrimitiveType> extends RegInstruction{


      RegInstructionWithDest(Instruction _from, Reg<T> _dest){
         super(_from, 1, 0);
         dests[0] = _dest;

      }

      Reg<T> getDest(){
         return ((Reg<T>) dests[0]);
      }
   }

   static abstract class RegInstructionWithSrc<T extends PrimitiveType> extends RegInstruction{

      RegInstructionWithSrc(Instruction _from, Reg<T> _src){
         super(_from, 0, 1);
         sources[0] = _src;
      }

      Reg<T> getSrc(){
         return ((Reg<T>) sources[0]);
      }
   }
   static abstract class RegInstructionWithSrcSrc<T extends PrimitiveType> extends RegInstruction{

      RegInstructionWithSrcSrc(Instruction _from, Reg<T> _src_lhs, Reg<T> _src_rhs){
         super(_from, 0, 2);
         sources[0] = _src_lhs;
         sources[1] = _src_rhs;
      }

      Reg<T> getSrcLhs(){
         return ((Reg<T>) sources[0]);
      }
      Reg<T> getSrcRhs(){
         return ((Reg<T>) sources[1]);
      }
   }

   static abstract class RegInstructionWithDestSrcSrc<D extends PrimitiveType, T extends PrimitiveType> extends RegInstruction{

      RegInstructionWithDestSrcSrc(Instruction _from, Reg<D> _dest, Reg<T> _src_lhs, Reg<T> _src_rhs){
         super(_from, 1, 2);
         dests[0] = _dest;
         sources[0] = _src_lhs;
         sources[1] = _src_rhs;
      }
      Reg<D> getDest(){
         return ((Reg<D>) dests[0]);
      }
      Reg<T> getSrcLhs(){
         return ((Reg<T>) sources[0]);
      }
      Reg<T> getSrcRhs(){
         return ((Reg<T>) sources[1]);
      }
   }

   static abstract class RegInstructionWithDestSrc<T extends PrimitiveType> extends RegInstruction{

      RegInstructionWithDestSrc(Instruction _from, Reg<T> _dest, Reg<T> _src){
         super(_from, 1, 1);
         dests[0] = _dest;
         sources[0] = _src;
      }

      Reg<T> getDest(){
         return ((Reg<T>) dests[0]);
      }

      Reg<T> getSrc(){
         return ((Reg<T>) sources[0]);
      }
   }

   static class branch extends RegInstructionWithSrc<s32>{
      String name;
      int pc;

      branch(Instruction _from, Reg<s32> _src, String _name, int _pc){
         super(_from, _src);
         name = _name;
         pc = _pc;
      }

      @Override
      public Delta execute(State state){
         System.out.println("branch " + name);
         return (null);
      }

      @Override
      public void render(RegISARenderer r){
         r.append(name + " ");
         r.label(pc);
      }
   }

   static class cmp_s32_const_0 extends RegInstructionWithSrc<s32>{
      String type;

      cmp_s32_const_0(Instruction _from, String _type, Reg_s32 _src){
         super(_from, _src);
         type = _type;
      }

      @Override
      public Delta execute(State state){
         System.out.println("cmp_s32_const_0 " + type);
         return (null);
      }

      @Override
      public void render(RegISARenderer r){
         r.append("cmp_").append(type).append("_b1_").typeName(getSrc()).space().append("$c1").separator().regName(getSrc()).separator().append("0");

      }
   }

   static class cmp_s32 extends RegInstructionWithSrcSrc<s32>{
      String type;

      cmp_s32(Instruction _from, String _type, Reg_s32 _srcLhs, Reg_s32 _srcRhs){
         super(_from, _srcLhs, _srcRhs);
         type = _type;
      }

      @Override
      public Delta execute(State state){
         System.out.println("cmp_s32 " + type);
         return (null);
      }

      @Override
      public void render(RegISARenderer r){
         r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs()).separator().regName(getSrcRhs());

      }
   }

   static class cmp<T extends PrimitiveType> extends RegInstructionWithSrcSrc<T>{
      String type;

      cmp(Instruction _from, String _type,  Reg<T> _srcLhs, Reg<T> _srcRhs){
         super(_from, _srcLhs, _srcRhs);
         type = _type;
      }

      @Override
      public Delta execute(State state){
         System.out.println("cmp " + type);
         return (null);
      }

      @Override
      public void render(RegISARenderer r){
         r.append("cmp_").append(type).append("u").append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs()).separator().regName(getSrcRhs());

      }
   }

   static class cbr extends RegInstruction{
      int pc;

      cbr(Instruction _from, int _pc){
         super(_from, 0, 0);
         pc = _pc;
      }

      @Override
      public Delta execute(State state){
         System.out.println("cbr " + pc);
         return (null);
      }

      @Override
      public void render(RegISARenderer r){
         r.append("cbr").space().append("$c1").separator().label(pc);

      }
   }

   static class brn extends RegInstruction{
      int pc;

      brn(Instruction _from, int _pc){
         super(_from, 0, 0);
         pc = _pc;
      }

      @Override
      public Delta execute(State state){
         System.out.println("brn " + pc);
         return (null);
      }

      @Override
      public void render(RegISARenderer r){
         r.append("brn").space().label(pc);

      }
   }

   static class put_field<T extends PrimitiveType> extends RegInstructionWithSrc<T>{
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

      @Override
      public Delta execute(State state){
         System.out.println("put_field ");
         return (null);
      }

   }

   static class get_field<T extends PrimitiveType> extends RegInstructionWithDest<T>{

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

      @Override
      public Delta execute(State state){
         System.out.println("get_field ");
         return (null);
      }
   }

   static class call extends RegInstruction{

      call(Instruction _from){
         super(_from, 0, 0);
      }

      @Override void render(RegISARenderer r){
         String dotClassName = from.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName();
         String name = from.asMethodCall().getConstantPoolMethodEntry().getName();
         TypeHelper.JavaMethodArgsAndReturnType argsAndReturnType = from.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType();


         TypeHelper.JavaType returnType = argsAndReturnType.getReturnType();


         if(returnType.isVoid()){
            r.append("call_").append("void").space().append("VOID");
         }else if(returnType.isInt()){
            r.append("call_").append("s64").space().append("$s").append(from.getPreStackBase() + from.getMethod().getCodeEntry().getMaxLocals());

         }else if(returnType.isDouble()){
            r.append("call_").append("f64").space().append("$d").append(from.getPreStackBase() + from.getMethod().getCodeEntry().getMaxLocals());


         }


         r.separator().append(dotClassName).dot().append(name).space();

         for(TypeHelper.JavaMethodArg arg : argsAndReturnType.getArgs()){
            if(arg.getArgc() > 0){
               r.separator();
            }
            if(arg.getJavaType().isDouble()){
               r.append("$d").append(from.getPreStackBase() + from.getMethod().getCodeEntry().getMaxLocals() + arg.getArgc());
            }else if(arg.getJavaType().isFloat()){
               r.append("$s").append(from.getPreStackBase() + from.getMethod().getCodeEntry().getMaxLocals() + arg.getArgc());
            }else if(arg.getJavaType().isInt()){
               r.append("$s").append(from.getPreStackBase() + from.getMethod().getCodeEntry().getMaxLocals() + arg.getArgc());
            }else if(arg.getJavaType().isLong()){
               r.append("$d").append(from.getPreStackBase() + from.getMethod().getCodeEntry().getMaxLocals() + arg.getArgc());
            }
         }
      }

      @Override
      public Delta execute(State state){
         System.out.println("call ");
         return (null);
      }
   }


   static class nyi extends RegInstruction{

      nyi(Instruction _from){
         super(_from, 0, 0);
      }

      @Override
      public Delta execute(State state){
         System.out.println("nyi ");
         return (null);
      }

      @Override void render(RegISARenderer r){

         r.append("NYI ").i(from);

      }
   }

   static class ld_kernarg<T extends PrimitiveType> extends RegInstructionWithDest<T>{


      ld_kernarg(Instruction _from, Reg<T> _dest){
         super(_from, _dest);
      }

      @Override void render(RegISARenderer r){
         r.append("ld_kernarg_").typeName(getDest()).space().regName(getDest()).separator().append("[%_arg").append(getDest().index).append("]");
      }

      @Override
      public Delta execute(State state){
         System.out.println("ld_kernarg ");
         return (null);
      }
   }

   static abstract class binary_const<T extends PrimitiveType, C> extends RegInstructionWithDestSrc<T>{
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

      @Override
      public Delta execute(State state){
         System.out.println(op + " ");
         return (null);
      }
   }

   static class add_const<T extends PrimitiveType, C> extends binary_const<T, C>{

      add_const(Instruction _from, Reg<T> _dest, Reg _src, C _value){
         super(_from, "add_", _dest, _src, _value);

      }

   }

   static class mul_const<T extends PrimitiveType, C> extends binary_const<T, C>{

      mul_const(Instruction _from, Reg<T> _dest, Reg _src, C _value){
         super(_from, "mul_", _dest, _src, _value);

      }

   }


   static class cvt<T1 extends PrimitiveType, T2 extends PrimitiveType> extends RegInstruction{


      cvt(Instruction _from, Reg<T1> _dest, Reg<T2> _src){
         super(_from, 1, 1);
         dests[0] = _dest;
         sources[0] = _src;
      }

      Reg<T1> getDest(){
         return ((Reg<T1>) dests[0]);
      }

      Reg<T2> getSrc(){
         return ((Reg<T2>) sources[0]);
      }

      @Override void render(RegISARenderer r){
         r.append("cvt_").typeName(getDest()).append("_").typeName(getSrc()).space().regName(getDest()).separator().regName(getSrc());
      }

      @Override
      public Delta execute(State state){
         System.out.println("cvt ");
         return (null);
      }
   }


   static class retvoid extends RegInstruction{

      retvoid(Instruction _from){
         super(_from, 0, 0);

      }

      @Override void render(RegISARenderer r){
         r.append("ret");
      }

      @Override
      public Delta execute(State state){
         System.out.println("ret ");
         return (null);
      }
   }

   static class ret<T extends PrimitiveType> extends RegInstructionWithSrc<T>{

      ret(Instruction _from, Reg<T> _src){
         super(_from, _src);

      }

      @Override void render(RegISARenderer r){
         r.append("ret_").typeName(getSrc()).space().regName(getSrc());
      }

      @Override
      public Delta execute(State state){
         System.out.println("ret ");
         return (null);
      }
   }

   static class store<T extends PrimitiveType> extends RegInstructionWithSrc<T>{

      Reg_ref mem;

      store(Instruction _from, Reg_ref _mem, Reg<T> _src){
         super(_from, _src);

         mem = _mem;
      }

      @Override void render(RegISARenderer r){
        // r.append("st_global_").typeName(getSrc()).space().append("[").regName(mem).append("+").array_len_offset().append("]").separator().regName(getSrc());
         r.append("st_global_").typeName(getSrc()).space().regName(getSrc()).separator().append("[").regName(mem).append("+").array_base_offset().append("]") ;
      }

      @Override
      public Delta execute(State state){
         System.out.println("store ");
         return (null);
      }
   }


   static class load<T extends PrimitiveType> extends RegInstructionWithDest<T>{
      Reg_ref mem;


      load(Instruction _from, Reg<T> _dest, Reg_ref _mem){
         super(_from, _dest);

         mem = _mem;
      }

      @Override void render(RegISARenderer r){
         r.append("ld_global_").typeName(getDest()).space().regName(getDest()).separator().append("[").regName(mem).append("+").array_base_offset().append("]");
      }

      @Override
      public Delta execute(State state){
         System.out.println("load ");
         return (null);
      }
   }


   static final class mov<T extends PrimitiveType> extends RegInstructionWithDestSrc{

      public mov(Instruction _from, Reg<T> _dest, Reg<T> _src){
         super(_from, _dest, _src);
      }

      @Override void render(RegISARenderer r){
         r.append("mov_").movTypeName(getDest()).space().regName(getDest()).separator().regName(getSrc());

      }

      @Override
      public Delta execute(State state){
         System.out.println("mov ");
         return (null);
      }
   }

   static abstract class binary<T extends PrimitiveType> extends RegInstruction{

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
         return ((Reg<T>) dests[0]);
      }

      Reg<T> getRhs(){
         return ((Reg<T>) sources[1]);
      }

      Reg<T> getLhs(){
         return ((Reg<T>) sources[0]);
      }

      @Override
      public Delta execute(State state){
         System.out.println(op + " ");
         return (null);
      }

   }

  /* static abstract class binaryRegConst<T extends JavaType, C> extends RegInstruction{
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

   static  class addConst<T extends JavaType, C> extends binaryRegConst<T, C>{

      public addConst(Instruction _from,   Reg<T> _dest, Reg<T> _lhs, C _value_rhs){
         super(_from, "add_", _dest, _lhs, _value_rhs);
      }
   }
   */

   static class add<T extends PrimitiveType> extends binary<T>{
      public add(Instruction _from, Reg<T> _dest, Reg<T> _lhs, Reg<T> _rhs){
         super(_from, "add_", _dest, _lhs, _rhs);
      }

   }

   static class sub<T extends PrimitiveType> extends binary<T>{
      public sub(Instruction _from, Reg<T> _dest, Reg<T> _lhs, Reg<T> _rhs){
         super(_from, "sub_", _dest, _lhs, _rhs);
      }

   }

   static class div<T extends PrimitiveType> extends binary<T>{
      public div(Instruction _from, Reg<T> _dest, Reg<T> _lhs, Reg<T> _rhs){
         super(_from, "div_", _dest, _lhs, _rhs);
      }

   }

   static class mul<T extends PrimitiveType> extends binary<T>{
      public mul(Instruction _from, Reg<T> _dest, Reg<T> _lhs, Reg<T> _rhs){
         super(_from, "mul_", _dest, _lhs, _rhs);
      }

   }

   static class rem<T extends PrimitiveType> extends binary<T>{
      public rem(Instruction _from, Reg<T> _dest, Reg<T> _lhs, Reg<T> _rhs){
         super(_from, "rem_", _dest, _lhs, _rhs);
      }

   }

   static class mov_const<T extends PrimitiveType, V> extends RegInstructionWithDest<T>{

      V value;

      public mov_const(Instruction _from, Reg<T> _dest, V _value){
         super(_from, _dest);
         value = _value;
      }

      @Override void render(RegISARenderer r){
         r.append("mov_").movTypeName(getDest()).space().regName(getDest()).separator().append(value.toString());

      }

      @Override
      public Delta execute(State state){
         System.out.println("mov const ");
         return (null);
      }
   }

   List<RegInstruction> instructions = new ArrayList<RegInstruction>();
   ClassModel.ClassModelMethod method;

   boolean optimizeMoves = false || Config.enableOptimizeRegMoves;

   void add(RegInstruction _regInstruction){
      // before we add lets see if this is a redundant mov
      if(optimizeMoves && _regInstruction.sources != null && _regInstruction.sources.length > 0){
         for(int regIndex = 0; regIndex < _regInstruction.sources.length; regIndex++){
            Reg r = _regInstruction.sources[regIndex];
            if(r.isStack()){
               // look up the list of reg instructions for the last mov which assigns to r
               int i = instructions.size();
               while((--i) >= 0){
                  if(instructions.get(i) instanceof mov){
                     // we have found a move
                     mov candidateForRemoval = (mov) instructions.get(i);
                     if(candidateForRemoval.from.getBlock() == _regInstruction.from.getBlock()
                           && candidateForRemoval.getDest().isStack() && candidateForRemoval.getDest().equals(r)){
                        // so i may be a candidate if between i and instruction.size() i.dest() is not mutated
                        boolean mutated = false;
                        for(int x = i + 1; !mutated && x < instructions.size(); x++){
                           if(instructions.get(x).dests.length > 0 && instructions.get(x).dests[0].equals(candidateForRemoval.getSrc())){
                              mutated = true;
                           }
                        }
                        if(!mutated){
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


   public RegISARenderer render(RegISARenderer r){
      r.append("version 1:0:large;").nl();
      r.append("kernel &" + method.getName() + "(");
      int argOffset = method.isStatic() ? 0 : 1;
      if(!method.isStatic()){
         r.nl().pad(3).append("kernarg_u64 %_arg0");
      }

      for(TypeHelper.JavaMethodArg arg : method.argsAndReturnType.getArgs()){
         if((method.isStatic() && arg.getArgc() == 0)){
            r.nl();
         }else{
            r.separator().nl();
         }

         PrimitiveType type = arg.getJavaType().getPrimitiveType();
         r.pad(3).append("kernarg_");
         if(type == null){
            r.append("u64");
         }else{
            r.append(type.getHSAName());
         }
         r.append(" %_arg" + (arg.getArgc() + argOffset));
      }
      r.nl().pad(3).append("){").nl();

      java.util.Set<Instruction> s = new java.util.HashSet<Instruction>();
      boolean first = false;
      int  count = 0;
      for(RegInstruction i : instructions){
         if(!(i instanceof ld_kernarg) && !s.contains(i.from)){
            if (!first){
               r.pad(9).append("workitemaid $s" + (count - 1) + ", 0;").nl();
               first = true;
            }
            s.add(i.from);
            if(i.from.isBranchTarget()){

               r.label(i.from.getThisPC()).append(":");
               r.nl();
            }
           if (r.isShowingComments()){
               r.nl().pad(1).append("// ").mark().append(i.from.getThisPC()).relpad(2).space().i(i.from).nl();
           }

         }  else{
            count++;
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

      int idx = instructions.size();
      while(--idx >= 0){
         RegInstruction i = instructions.get(idx);
         if(i.dests != null){
            for(Reg d : i.dests){
               if(d.index == _index){
                  return (d);
               }
            }
         }
      }


      return (null);
   }

   public void addmov(Instruction _i, PrimitiveType _type, int _from, int _to){
      if(_type.equals(PrimitiveType.ref) || _type.getHsaBits() == 32){
         if(_type.equals(PrimitiveType.ref)){
            add(new mov<ref>(_i, new StackReg_ref(_i, _to), new StackReg_ref(_i, _from)));
         }else if(_type.equals(PrimitiveType.s32)){
            add(new mov<s32>(_i, new StackReg_s32(_i, _to), new StackReg_s32(_i, _from)));
         }else{
            throw new IllegalStateException(" unknown prefix 1 prefix for first of DUP2");
         }

      }else{
         throw new IllegalStateException(" unknown prefix 2 prefix for DUP2");
      }
   }

   public Reg addmov(Instruction _i, int _from, int _to){
      Reg r = getRegOfLastWriteToIndex(_i.getPreStackBase() + _i.getMethod().getCodeEntry().getMaxLocals() + _from);
      addmov(_i, r.type, _from, _to);
      return (r);
   }

   enum ParseState{NONE, COMPARE_F32, COMPARE_F64, COMPARE_S64 };


   public RegISA(ClassModel.ClassModelMethod _method){
      method = _method;
      ParseState parseState = ParseState.NONE;
      Instruction lastInstruction = null;
      for(Instruction i : method.getInstructions()){
         if(i.getThisPC() == 0){
            int argOffset = 0;
            if(!method.isStatic()){
               add(new ld_kernarg(i, new VarReg_ref(0)));
               argOffset++;
            }
            for(TypeHelper.JavaMethodArg arg : method.argsAndReturnType.getArgs()){
               if(arg.getJavaType().isArray()){
                  add(new ld_kernarg(i, new VarReg_ref(arg.getArgc() + argOffset)));
               }else if(arg.getJavaType().isInt()){
                  add(new ld_kernarg(i, new VarReg_s32(arg.getArgc() + argOffset)));

               }else if(arg.getJavaType().isFloat()){
                  add(new ld_kernarg(i, new VarReg_f32(arg.getArgc() + argOffset)));
               }
            }
         }

      switch(i.getByteCode()){

         case ACONST_NULL:
            add(new nyi(i));
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
            add(new mov_const<s32, Integer>(i, new StackReg_s32(i, 0), i.asIntegerConstant().getValue()));
            break;
         case LCONST_0:
         case LCONST_1:
            add(new mov_const<s64, Long>(i, new StackReg_s64(i, 0), i.asLongConstant().getValue()));
            break;
         case FCONST_0:
         case FCONST_1:
         case FCONST_2:
            add(new mov_const<f32, Float>(i, new StackReg_f32(i, 0), i.asFloatConstant().getValue()));
            break;
         case DCONST_0:
         case DCONST_1:
            add(new mov_const<f64, Double>(i, new StackReg_f64(i, 0), i.asDoubleConstant().getValue()));
            break;
         // case BIPUSH: moved up
         // case SIPUSH: moved up

         case LDC:
         case LDC_W:
         case LDC2_W:{
            InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant) i;

            ClassModel.ConstantPool.ConstantEntry e = (ClassModel.ConstantPool.ConstantEntry) cpe.getConstantPoolEntry();
            if(e instanceof ClassModel.ConstantPool.DoubleEntry){
               add(new mov_const<f64, Double>(i, new StackReg_f64(i, 0), ((ClassModel.ConstantPool.DoubleEntry) e).getValue()));
            }else if(e instanceof ClassModel.ConstantPool.FloatEntry){
               add(new mov_const<f32, Float>(i, new StackReg_f32(i, 0), ((ClassModel.ConstantPool.FloatEntry) e).getValue()));
            }else if(e instanceof ClassModel.ConstantPool.IntegerEntry){
               add(new mov_const<s32, Integer>(i, new StackReg_s32(i, 0), ((ClassModel.ConstantPool.IntegerEntry) e).getValue()));
            }else if(e instanceof ClassModel.ConstantPool.LongEntry){
               add(new mov_const<s64, Long>(i, new StackReg_s64(i, 0), ((ClassModel.ConstantPool.LongEntry) e).getValue()));

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
            add(new mov<s32>(i, new StackReg_s32(i, 0), new VarReg_s32(i)));

            break;
         case LLOAD:
         case LLOAD_0:
         case LLOAD_1:
         case LLOAD_2:
         case LLOAD_3:
            add(new mov<s64>(i, new StackReg_s64(i, 0), new VarReg_s64(i)));
            break;
         case FLOAD:
         case FLOAD_0:
         case FLOAD_1:
         case FLOAD_2:
         case FLOAD_3:
            add(new mov<f32>(i, new StackReg_f32(i, 0), new VarReg_f32(i)));
            break;
         case DLOAD:
         case DLOAD_0:
         case DLOAD_1:
         case DLOAD_2:
         case DLOAD_3:
            add(new mov<f64>(i, new StackReg_f64(i, 0), new VarReg_f64(i)));
            break;
         case ALOAD:
         case ALOAD_0:
         case ALOAD_1:
         case ALOAD_2:
         case ALOAD_3:
            add(new mov<ref>(i, new StackReg_ref(i, 0), new VarReg_ref(i)));
            break;
         case IALOAD:{
            add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
            add(new mul_const<ref, Long>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), (long) PrimitiveType.s32.getHsaBytes()));
            add(new add<ref>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
            add(new load<s32>(i, new StackReg_s32(i, 0), new StackReg_ref(i, 1)));
         }


         break;
         case LALOAD:
            add(new nyi(i));
            break;
         case FALOAD:
         {
            add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
            add(new mul_const<ref, Long>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), (long)  PrimitiveType.f32.getHsaBytes()));
            add(new add<ref>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
            add(new load<f32>(i, new StackReg_f32(i, 0), new StackReg_ref(i, 1)));
         }
            break;
         case DALOAD:
            add(new nyi(i));
            break;
         case AALOAD:
            add(new nyi(i));
            break;
         case BALOAD:
            add(new nyi(i));
            break;
         case CALOAD:
            add(new nyi(i));
            break;
         case SALOAD:
            add(new nyi(i));
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
            add(new mov<s32>(i, new VarReg_s32(i), new StackReg_s32(i, 0)));

            break;
         case LSTORE:
         case LSTORE_0:
         case LSTORE_1:
         case LSTORE_2:
         case LSTORE_3:
            add(new mov<s64>(i, new VarReg_s64(i), new StackReg_s64(i, 0)));

            break;
         case FSTORE:
         case FSTORE_0:
         case FSTORE_1:
         case FSTORE_2:
         case FSTORE_3:
            add(new mov<f32>(i, new VarReg_f32(i), new StackReg_f32(i, 0)));
            break;
         case DSTORE:
         case DSTORE_0:
         case DSTORE_1:
         case DSTORE_2:
         case DSTORE_3:
            add(new mov<f64>(i, new VarReg_f64(i), new StackReg_f64(i, 0)));
            break;
         case ASTORE:
         case ASTORE_0:
         case ASTORE_1:
         case ASTORE_2:
         case ASTORE_3:
            add(new mov<ref>(i, new VarReg_ref(i), new StackReg_ref(i, 0)));

            break;
         case IASTORE:

         {

                /*

                cvt_u64_s32 $d(index), $s(index)
                mul_u64 $d(index), $d(index), (sizeof array element);
                add_u64 $d(index), $d(index), $d{array};
                st_global_s32 $s3, [$d6 + 24];
                 */
            add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
            add(new mul_const<ref, Long>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), (long)  PrimitiveType.s32.getHsaBytes()));
            add(new add<ref>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
            add(new store<s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 2)));

         }
         break;
         case LASTORE:
            add(new nyi(i));
            break;
         case FASTORE:
            add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
            add(new mul_const<ref, Long>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), (long)  PrimitiveType.f32.getHsaBytes()));
            add(new add<ref>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
            add(new store<f32>(i, new StackReg_ref(i, 1), new StackReg_f32(i, 2)));
            break;
         case DASTORE:
            add(new nyi(i));
            break;
         case AASTORE:
            add(new nyi(i));
            break;
         case BASTORE:
            add(new nyi(i));
            break;
         case CASTORE:
            add(new nyi(i));
            break;
         case SASTORE:
            add(new nyi(i));
            break;
         case POP:
            add(new nyi(i));
            break;
         case POP2:
            add(new nyi(i));
            break;
         case DUP:
            add(new nyi(i));
            break;
         case DUP_X1:
            add(new nyi(i));
            break;
         case DUP_X2:{
            // Reg r = getRegOfLastWriteToIndex(_i.getPreStackBase()+_i.getMethod().getCodeEntry().getMaxLocals()+3);
            // addmov(_i, 2, 4);
            addmov(i, 2, 3);
            addmov(i, 1, 2);
            addmov(i, 0, 1);
            addmov(i, 3, 0);
            // add(new mov<s32>(_i, new StackReg_s32(_i, 3), new StackReg_s32(_i, 2)));
            // add(new mov<s32>(_i, new StackReg_s32(_i, 2),new StackReg_s32(_i, 1)));

            // add(new mov<s32>(_i, new StackReg_s32(_i, 1), new StackReg_s32(_i, 0)));

            // add(new mov<s32>(_i, new StackReg_s32(_i, 0), new StackReg_s32(_i, 3)));
         }
         break;
         case DUP2:{
            // DUP2 is problematic. DUP2 either dups top two items or one depending on the 'prefix' of the stack items.
            // To complicate this further HSA large model wants object/mem references to be 64 bits (prefix 2 in Java) whereas
            // in java object/array refs are 32 bits (prefix 1).
            addmov(i, 0, 2);
            addmov(i, 1, 3);
         }
         break;
         case DUP2_X1:
            add(new nyi(i));
            break;
         case DUP2_X2:
            add(new nyi(i));
            break;
         case SWAP:
            add(new nyi(i));
            break;
         case IADD:
            add(new add<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));


            break;
         case LADD:
            add(new add<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));


            break;
         case FADD:
            add(new add<f32>(i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
            break;
         case DADD:
            add(new add<f64>(i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));

            break;
         case ISUB:
            add(new sub<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));

            break;
         case LSUB:
            add(new sub<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));

            break;
         case FSUB:
            add(new sub<f32>(i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));

            break;
         case DSUB:
            add(new sub<f64>(i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));

            break;
         case IMUL:
            add(new mul<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));

            break;
         case LMUL:
            add(new mul<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));

            break;
         case FMUL:
            add(new mul<f32>(i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));

            break;
         case DMUL:
            add(new mul<f64>(i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
            break;
         case IDIV:
            add(new div<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));

            break;
         case LDIV:
            add(new div<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));

            break;
         case FDIV:
            add(new div<f32>(i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));

            break;
         case DDIV:
            add(new div<f64>(i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));

            break;
         case IREM:
            add(new rem<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));

            break;
         case LREM:
            add(new rem<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));

            break;
         case FREM:
            add(new rem<f32>(i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));

            break;
         case DREM:
            add(new rem<f64>(i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));

            break;
         case INEG:
            add(new nyi(i));
            break;
         case LNEG:
            add(new nyi(i));
            break;
         case FNEG:
            add(new nyi(i));
            break;
         case DNEG:
            add(new nyi(i));
            break;
         case ISHL:
            add(new nyi(i));
            break;
         case LSHL:
            add(new nyi(i));
            break;
         case ISHR:
            add(new nyi(i));
            break;
         case LSHR:
            add(new nyi(i));
            break;
         case IUSHR:
            add(new nyi(i));
            break;
         case LUSHR:
            add(new nyi(i));
            break;
         case IAND:
            add(new nyi(i));
            break;
         case LAND:
            add(new nyi(i));
            break;
         case IOR:
            add(new nyi(i));
            break;
         case LOR:
            add(new nyi(i));
            break;
         case IXOR:
            add(new nyi(i));
            break;
         case LXOR:
            add(new nyi(i));
            break;
         case IINC:
            add(new add_const<s32, Integer>(i, new VarReg_s32(i), new VarReg_s32(i), ((InstructionSet.I_IINC) i).getDelta()));

            break;
         case I2L:
            add(new cvt<s64, s32>(i, new StackReg_s64(i, 0), new StackReg_s32(i, 0)));
            break;
         case I2F:
            add(new cvt<f32, s32>(i, new StackReg_f32(i, 0), new StackReg_s32(i, 0)));
            break;
         case I2D:
            add(new cvt<f64, s32>(i, new StackReg_f64(i, 0), new StackReg_s32(i, 0)));
            break;
         case L2I:
            add(new nyi(i));
            break;
         case L2F:
            add(new nyi(i));
            break;
         case L2D:
            add(new nyi(i));
            break;
         case F2I:
            add(new nyi(i));
            break;
         case F2L:
            add(new nyi(i));
            break;
         case F2D:
            add(new nyi(i));
            break;
         case D2I:
            add(new nyi(i));
            break;
         case D2L:
            add(new nyi(i));
            break;
         case D2F:
            add(new nyi(i));
            break;
         case I2B:
            add(new nyi(i));
            break;
         case I2C:
            add(new nyi(i));
            break;
         case I2S:
            add(new nyi(i));
            break;
         case LCMP:
            add(new nyi(i));
            break;
         case FCMPL:
            parseState = ParseState.COMPARE_F32;
          //  add(new cmp<f32>(i,  "ge",new StackReg_s32(i, 0),  new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
            break;
         case FCMPG:
            parseState = ParseState.COMPARE_F32;
         //   add(new cmp<f32>(i,  "le", new StackReg_s32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));

            break;
         case DCMPL:
            parseState = ParseState.COMPARE_F64;
           // add(new cmp<f64>(i,  "ge", new StackReg_s32(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
            break;
         case DCMPG:
            parseState = ParseState.COMPARE_F64;
           // add(new cmp<f64>(i,  "le", new StackReg_s32(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
            break;
         case IFEQ:
            if (parseState.equals(ParseState.COMPARE_F32)){
               add(new cmp<f32>(lastInstruction,  "eq",  new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_F64)){
               add(new cmp<f64>(lastInstruction,  "eq",  new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_S64)){
               add(new cmp<s64>(lastInstruction,  "eq",  new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }  else{
            add(new cmp_s32_const_0(i, "eq", new StackReg_s32(i, 0))) ;

            }
            add(new cbr(i, i.asBranch().getAbsolute()));
            break;
         case IFNE:
            if (parseState.equals(ParseState.COMPARE_F32)){
               add(new cmp<f32>(lastInstruction,  "ne",  new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_F64)){
               add(new cmp<f64>(lastInstruction,  "ne",  new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_S64)){
               add(new cmp<s64>(lastInstruction,  "ne",  new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }  else{
            add(new cmp_s32_const_0(i, "ne", new StackReg_s32(i, 0))) ;

            }
            add(new cbr(i, i.asBranch().getAbsolute()));
            break;
         case IFLT:
            if (parseState.equals(ParseState.COMPARE_F32)){
               add(new cmp<f32>(lastInstruction,  "lt",  new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_F64)){
               add(new cmp<f64>(lastInstruction,  "lt",  new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_S64)){
               add(new cmp<s64>(lastInstruction,  "lt",  new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }  else{
            add(new cmp_s32_const_0(i, "lt", new StackReg_s32(i, 0))) ;

            }
            add(new cbr(i, i.asBranch().getAbsolute()));
            break;
         case IFGE:
            if (parseState.equals(ParseState.COMPARE_F32)){
               add(new cmp<f32>(lastInstruction,  "ge",  new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_F64)){
               add(new cmp<f64>(lastInstruction,  "ge",  new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_S64)){
               add(new cmp<s64>(lastInstruction,  "ge",  new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }  else{
               add(new cmp_s32_const_0(i, "ge", new StackReg_s32(i, 0))) ;

            }
            add(new cbr(i, i.asBranch().getAbsolute()));
            break;
         case IFGT:
            if (parseState.equals(ParseState.COMPARE_F32)){
               add(new cmp<f32>(lastInstruction,  "gt",  new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_F64)){
               add(new cmp<f64>(lastInstruction,  "gt",  new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_S64)){
               add(new cmp<s64>(lastInstruction,  "gt",  new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }  else{
            add(new cmp_s32_const_0(i, "gt", new StackReg_s32(i, 0))) ;

            }
            add(new cbr(i, i.asBranch().getAbsolute()));
            break;
         case IFLE:
            if (parseState.equals(ParseState.COMPARE_F32)){
               add(new cmp<f32>(lastInstruction,  "le",  new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_F64)){
               add(new cmp<f64>(lastInstruction,  "le",  new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }else if (parseState.equals(ParseState.COMPARE_S64)){
               add(new cmp<s64>(lastInstruction,  "le",  new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
               parseState = ParseState.NONE;
            }  else{
            add(new cmp_s32_const_0(i, "le", new StackReg_s32(i, 0))) ;

            }
            add(new cbr(i, i.asBranch().getAbsolute()));
            break;
         case IF_ICMPEQ:

            add(new cmp_s32(i, "eq", new StackReg_s32(i, 0), new StackReg_s32(i, 1))) ;
            add(new cbr(i, i.asBranch().getAbsolute()));

            break;
         case IF_ICMPNE:

            add(new cmp_s32(i, "ne", new StackReg_s32(i, 0), new StackReg_s32(i, 1))) ;
            add(new cbr(i, i.asBranch().getAbsolute()));

            break;
         case IF_ICMPLT:

            add(new cmp_s32(i, "lt", new StackReg_s32(i, 0), new StackReg_s32(i, 1))) ;
            add(new cbr(i, i.asBranch().getAbsolute()));

            break;
         case IF_ICMPGE:

            add(new cmp_s32(i, "ge", new StackReg_s32(i, 0), new StackReg_s32(i, 1))) ;
            add(new cbr(i, i.asBranch().getAbsolute()));

            break;
         case IF_ICMPGT:

            add(new cmp_s32(i, "gt", new StackReg_s32(i, 0), new StackReg_s32(i, 1))) ;
            add(new cbr(i, i.asBranch().getAbsolute()));

            break;
         case IF_ICMPLE:

            add(new cmp_s32(i, "le", new StackReg_s32(i, 0), new StackReg_s32(i, 1))) ;
            add(new cbr(i, i.asBranch().getAbsolute()));

            break;
         case IF_ACMPEQ:
         case IF_ACMPNE:
            add(new branch(i, new StackReg_s32(i, 0), i.getByteCode().getName(), i.asBranch().getAbsolute()));
            break;
         case GOTO:
            add(new brn(i, i.asBranch().getAbsolute()));
            break;
         case IFNULL:
         case IFNONNULL:
         case GOTO_W:
            add(new branch(i, new StackReg_s32(i, 0), i.getByteCode().getName(), i.asBranch().getAbsolute()));
            break;
         case JSR:
            add(new nyi(i));
            break;
         case RET:
            add(new nyi(i));
            break;
         case TABLESWITCH:
            add(new nyi(i));
            break;
         case LOOKUPSWITCH:
            add(new nyi(i));
            break;
         case IRETURN:
            add(new ret<s32>(i, new StackReg_s32(i, 0)));
            break;
         case LRETURN:
            add(new ret<s64>(i, new StackReg_s64(i, 0)));
            break;
         case FRETURN:
            add(new ret<f32>(i, new StackReg_f32(i, 0)));
            break;
         case DRETURN:
            add(new ret<f64>(i, new StackReg_f64(i, 0)));
            break;
         case ARETURN:
            add(new nyi(i));
            break;
         case RETURN:
            add(new retvoid(i));
            break;
         case GETSTATIC:
         case GETFIELD:{
            TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();
            if(type.isArray()){
               add(new get_field<ref>(i, new StackReg_ref(i, 0)));
            }else if(type.isInt()){
               add(new get_field<s32>(i, new StackReg_s32(i, 0)));
            }else if(type.isFloat()){
               add(new get_field<f32>(i, new StackReg_f32(i, 0)));
            }
         }
         break;
         case PUTSTATIC:
         case PUTFIELD:{
            TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();
            if(type.isArray()){
               add(new put_field<ref>(i, new StackReg_ref(i, 0)));
            }else if(type.isInt()){
               add(new put_field<s32>(i, new StackReg_s32(i, 0)));
            }else if(type.isFloat()){
               add(new put_field<f32>(i, new StackReg_f32(i, 0)));
            }

         }
         break;
         case INVOKEVIRTUAL:
         case INVOKESPECIAL:
         case INVOKESTATIC:
         case INVOKEINTERFACE:
         case INVOKEDYNAMIC:
            add(new call(i));
            break;
         case NEW:
            add(new nyi(i));
            break;
         case NEWARRAY:
            add(new nyi(i));
            break;
         case ANEWARRAY:
            add(new nyi(i));
            break;
         case ARRAYLENGTH:
            add(new nyi(i));
            break;
         case ATHROW:
            add(new nyi(i));
            break;
         case CHECKCAST:
            add(new nyi(i));
            break;
         case INSTANCEOF:
            add(new nyi(i));
            break;
         case MONITORENTER:
            add(new nyi(i));
            break;
         case MONITOREXIT:
            add(new nyi(i));
            break;
         case WIDE:
            add(new nyi(i));
            break;
         case MULTIANEWARRAY:
            add(new nyi(i));
            break;
         case JSR_W:
            add(new nyi(i));
            break;

      }
      lastInstruction = i;


      }
   }
}
