package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/27/13
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class HSAILMethod{




   static abstract class HSAILInstruction{
      Instruction from;
      HSAILRegister[] dests = null;
      HSAILRegister[] sources = null;


      HSAILInstruction(Instruction _from, int _destCount, int _sourceCount){
         from = _from;
         dests = new HSAILRegister[_destCount];
         sources = new HSAILRegister[_sourceCount];
      }


      abstract void render(HSAILRenderer r);

   }

   static abstract class HSAILInstructionWithDest<T extends PrimitiveType> extends HSAILInstruction{


      HSAILInstructionWithDest(Instruction _from, HSAILRegister<T> _dest){
         super(_from, 1, 0);
         dests[0] = _dest;

      }

      HSAILRegister<T> getDest(){
         return ((HSAILRegister<T>) dests[0]);
      }
   }

   static abstract class HSAILInstructionWithSrc<T extends PrimitiveType> extends HSAILInstruction{

      HSAILInstructionWithSrc(Instruction _from, HSAILRegister<T> _src){
         super(_from, 0, 1);
         sources[0] = _src;
      }

      HSAILRegister<T> getSrc(){
         return ((HSAILRegister<T>) sources[0]);
      }
   }

   static abstract class HSAILInstructionWithSrcSrc<T extends PrimitiveType> extends HSAILInstruction{

      HSAILInstructionWithSrcSrc(Instruction _from, HSAILRegister<T> _src_lhs, HSAILRegister<T> _src_rhs){
         super(_from, 0, 2);
         sources[0] = _src_lhs;
         sources[1] = _src_rhs;
      }

      HSAILRegister<T> getSrcLhs(){
         return ((HSAILRegister<T>) sources[0]);
      }

      HSAILRegister<T> getSrcRhs(){
         return ((HSAILRegister<T>) sources[1]);
      }
   }

   static abstract class HSAILInstructionWithDestSrcSrc<D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstruction{

      HSAILInstructionWithDestSrcSrc(Instruction _from, HSAILRegister<D> _dest, HSAILRegister<T> _src_lhs, HSAILRegister<T> _src_rhs){
         super(_from, 1, 2);
         dests[0] = _dest;
         sources[0] = _src_lhs;
         sources[1] = _src_rhs;
      }

      HSAILRegister<D> getDest(){
         return ((HSAILRegister<D>) dests[0]);
      }

      HSAILRegister<T> getSrcLhs(){
         return ((HSAILRegister<T>) sources[0]);
      }

      HSAILRegister<T> getSrcRhs(){
         return ((HSAILRegister<T>) sources[1]);
      }
   }

   static abstract class HSAILInstructionWithDestSrc<T extends PrimitiveType> extends HSAILInstruction{

      HSAILInstructionWithDestSrc(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _src){
         super(_from, 1, 1);
         dests[0] = _dest;
         sources[0] = _src;
      }

      HSAILRegister<T> getDest(){
         return ((HSAILRegister<T>) dests[0]);
      }

      HSAILRegister<T> getSrc(){
         return ((HSAILRegister<T>) sources[0]);
      }
   }

   static class branch extends HSAILInstructionWithSrc<s32>{
      String name;
      int pc;

      branch(Instruction _from, HSAILRegister<s32> _src, String _name, int _pc){
         super(_from, _src);
         name = _name;
         pc = _pc;
      }


      @Override
      public void render(HSAILRenderer r){
         r.append(name + " ");
         r.label(pc);
      }
   }

   static class cmp_s32_const_0 extends HSAILInstructionWithSrc<s32>{
      String type;

      cmp_s32_const_0(Instruction _from, String _type, Reg_s32 _src){
         super(_from, _src);
         type = _type;
      }


      @Override
      public void render(HSAILRenderer r){
         r.append("cmp_").append(type).append("_b1_").typeName(getSrc()).space().append("$c1").separator().regName(getSrc()).separator().append("0");

      }
   }

   static class cmp_s32 extends HSAILInstructionWithSrcSrc<s32>{
      String type;

      cmp_s32(Instruction _from, String _type, Reg_s32 _srcLhs, Reg_s32 _srcRhs){
         super(_from, _srcLhs, _srcRhs);
         type = _type;
      }


      @Override
      public void render(HSAILRenderer r){
         r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs()).separator().regName(getSrcRhs());

      }
   }

   static class cmp<T extends PrimitiveType> extends HSAILInstructionWithSrcSrc<T>{
      String type;

      cmp(Instruction _from, String _type, HSAILRegister<T> _srcLhs, HSAILRegister<T> _srcRhs){
         super(_from, _srcLhs, _srcRhs);
         type = _type;
      }


      @Override
      public void render(HSAILRenderer r){
         r.append("cmp_").append(type).append("u").append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs()).separator().regName(getSrcRhs());

      }
   }

   static class cbr extends HSAILInstruction{
      int pc;

      cbr(Instruction _from, int _pc){
         super(_from, 0, 0);
         pc = _pc;
      }


      @Override
      public void render(HSAILRenderer r){
         r.append("cbr").space().append("$c1").separator().label(pc);

      }
   }

   static class brn extends HSAILInstruction{
      int pc;

      brn(Instruction _from, int _pc){
         super(_from, 0, 0);
         pc = _pc;
      }


      @Override
      public void render(HSAILRenderer r){
         r.append("brn").space().label(pc);

      }
   }


   static class call extends HSAILInstruction{

      call(Instruction _from){
         super(_from, 0, 0);
      }

      @Override void render(HSAILRenderer r){
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


   }


   static class nyi extends HSAILInstruction{

      nyi(Instruction _from){
         super(_from, 0, 0);
      }


      @Override void render(HSAILRenderer r){

         r.append("NYI ").i(from);

      }
   }

   static class ld_kernarg<T extends PrimitiveType> extends HSAILInstructionWithDest<T>{


      ld_kernarg(Instruction _from, HSAILRegister<T> _dest){
         super(_from, _dest);
      }

      @Override void render(HSAILRenderer r){
         r.append("ld_kernarg_").typeName(getDest()).space().regName(getDest()).separator().append("[%_arg").append(getDest().index).append("]");
      }


   }

   static abstract class binary_const<T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDestSrc<T>{
      C value;
      String op;

      binary_const(Instruction _from, String _op, HSAILRegister<T> _dest, HSAILRegister _src, C _value){
         super(_from, _dest, _src);
         value = _value;
         op = _op;
      }

      @Override void render(HSAILRenderer r){
         r.append(op).typeName(getDest()).space().regName(getDest()).separator().regName(getSrc()).separator().append(value);
      }


   }

   static class add_const<T extends PrimitiveType, C extends Number> extends binary_const<T, C>{

      add_const(Instruction _from, HSAILRegister<T> _dest, HSAILRegister _src, C _value){
         super(_from, "add_", _dest, _src, _value);

      }

   }

   static class and_const<T extends PrimitiveType, C extends Number> extends binary_const<T, C>{

      and_const(Instruction _from, HSAILRegister<T> _dest, HSAILRegister _src, C _value){
         super(_from, "and_", _dest, _src, _value);

      }

      @Override void render(HSAILRenderer r){
         r.append(op).append("b64").space().regName(getDest()).separator().regName(getSrc()).separator().append(value);
      }


   }

   static class mul_const<T extends PrimitiveType, C extends Number> extends binary_const<T, C>{

      mul_const(Instruction _from, HSAILRegister<T> _dest, HSAILRegister _src, C _value){
         super(_from, "mul_", _dest, _src, _value);

      }

   }

   static class mad extends HSAILInstructionWithDestSrcSrc<ref, ref>{
      long size;

      mad(Instruction _from, Reg_ref _dest, Reg_ref _src_lhs, Reg_ref _src_rhs, long _size){
         super(_from, _dest, _src_lhs, _src_rhs);
         size = _size;
      }


      @Override void render(HSAILRenderer r){
         r.append("mad_").typeName(getDest()).space().regName(getDest()).separator().regName(getSrcLhs()).separator().append(size).separator().regName(getSrcRhs());
      }
   }


   static class cvt<T1 extends PrimitiveType, T2 extends PrimitiveType> extends HSAILInstruction{


      cvt(Instruction _from, HSAILRegister<T1> _dest, HSAILRegister<T2> _src){
         super(_from, 1, 1);
         dests[0] = _dest;
         sources[0] = _src;
      }

      HSAILRegister<T1> getDest(){
         return ((HSAILRegister<T1>) dests[0]);
      }

      HSAILRegister<T2> getSrc(){
         return ((HSAILRegister<T2>) sources[0]);
      }

      @Override void render(HSAILRenderer r){
         r.append("cvt_").typeName(getDest()).append("_").typeName(getSrc()).space().regName(getDest()).separator().regName(getSrc());
      }


   }


   static class retvoid extends HSAILInstruction{

      retvoid(Instruction _from){
         super(_from, 0, 0);

      }

      @Override void render(HSAILRenderer r){
         r.append("ret");
      }


   }

   static class ret<T extends PrimitiveType> extends HSAILInstructionWithSrc<T>{

      ret(Instruction _from, HSAILRegister<T> _src){
         super(_from, _src);

      }

      @Override void render(HSAILRenderer r){
         r.append("ret_").typeName(getSrc()).space().regName(getSrc());
      }


   }

   static class array_store<T extends PrimitiveType> extends HSAILInstructionWithSrc<T>{

      Reg_ref mem;

      array_store(Instruction _from, Reg_ref _mem, HSAILRegister<T> _src){
         super(_from, _src);

         mem = _mem;
      }

      @Override void render(HSAILRenderer r){
         // r.append("st_global_").typeName(getSrc()).space().append("[").regName(mem).append("+").array_len_offset().append("]").separator().regName(getSrc());
         r.append("st_global_").typeName(getSrc()).space().regName(getSrc()).separator().append("[").regName(mem).append("+").array_base_offset().append("]");
      }


   }


   static class array_load<T extends PrimitiveType> extends HSAILInstructionWithDest<T>{
      Reg_ref mem;


      array_load(Instruction _from, HSAILRegister<T> _dest, Reg_ref _mem){
         super(_from, _dest);

         mem = _mem;
      }

      @Override void render(HSAILRenderer r){
         r.append("ld_global_").typeName(getDest()).space().regName(getDest()).separator().append("[").regName(mem).append("+").array_base_offset().append("]");
      }


   }

    static class array_len extends HSAILInstructionWithDest<s32>{
        Reg_ref mem;


        array_len(Instruction _from, Reg_s32 _dest, Reg_ref _mem){
            super(_from, _dest);

            mem = _mem;
        }

        @Override void render(HSAILRenderer r){
            r.append("ld_global_").typeName(getDest()).space().regName(getDest()).separator().append("[").regName(mem).append("+").array_len_offset().append("]");
        }


    }

   static class field_load<T extends PrimitiveType> extends HSAILInstructionWithDest<T>{
      Reg_ref mem;
      long offset;


      field_load(Instruction _from, HSAILRegister<T> _dest, Reg_ref _mem, long _offset){
         super(_from, _dest);
         offset = _offset;
         mem = _mem;
      }

      @Override void render(HSAILRenderer r){
         r.append("ld_global_").typeName(getDest()).space().regName(getDest()).separator().append("[").regName(mem).append("+").append(offset).append("]");
      }


   }

    static class static_field_load<T extends PrimitiveType> extends HSAILInstructionWithDest<T>{

        long offset;


        static_field_load(Instruction _from, HSAILRegister<T> _dest,  long _offset){
            super(_from, _dest);
            offset = _offset;

        }

        @Override void render(HSAILRenderer r){
            r.append("ld_global_").typeName(getDest()).space().regName(getDest()).separator().append("[").append(offset).append("]");
        }


    }


   static class field_store<T extends PrimitiveType> extends HSAILInstructionWithSrc<T>{
      Reg_ref mem;
      long offset;


      field_store(Instruction _from, HSAILRegister<T> _src, Reg_ref _mem, long _offset){
         super(_from, _src);
         offset = _offset;
         mem = _mem;
      }

      @Override void render(HSAILRenderer r){
         r.append("st_global_").typeName(getSrc()).space().regName(getSrc()).separator().append("[").regName(mem).append("+").append(offset).append("]");
      }


   }


   static final class mov<T extends PrimitiveType> extends HSAILInstructionWithDestSrc{

      public mov(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _src){
         super(_from, _dest, _src);
      }

      @Override void render(HSAILRenderer r){
         r.append("mov_").movTypeName(getDest()).space().regName(getDest()).separator().regName(getSrc());

      }


   }

   static abstract class unary<T extends PrimitiveType> extends HSAILInstructionWithDestSrc{

      String op;

      public unary(Instruction _from, String _op, HSAILRegister<T> _destSrc){
         super(_from, _destSrc, _destSrc);

         op = _op;
      }

      @Override void render(HSAILRenderer r){
         r.append(op).typeName(getDest()).space().regName(getDest()).separator().regName(getDest());
      }

      HSAILRegister<T> getDest(){
         return ((HSAILRegister<T>) dests[0]);
      }

      HSAILRegister<T> getSrc(){
         return ((HSAILRegister<T>) sources[0]);
      }




   }

   static abstract class binary<T extends PrimitiveType> extends HSAILInstruction{

      String op;

      public binary(Instruction _from, String _op, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, 1, 2);
         dests[0] = _dest;
         sources[0] = _lhs;
         sources[1] = _rhs;
         op = _op;
      }

      @Override void render(HSAILRenderer r){
         r.append(op).typeName(getDest()).space().regName(getDest()).separator().regName(getLhs()).separator().regName(getRhs());
      }

      HSAILRegister<T> getDest(){
         return ((HSAILRegister<T>) dests[0]);
      }

      HSAILRegister<T> getRhs(){
         return ((HSAILRegister<T>) sources[1]);
      }

      HSAILRegister<T> getLhs(){
         return ((HSAILRegister<T>) sources[0]);
      }


   }

  /* static abstract class binaryRegConst<T extends JavaType, C> extends HSAILInstruction{
      HSAILRegister<T> dest, lhs;
      C value;
      String op;

      public binaryRegConst(Instruction _from, String _op,  HSAILRegister<T> _dest, HSAILRegister<T> _lhs, C _value){
         super(_from);
         dest = _dest;
         lhs = _lhs;
         value = _value;
         op = _op;
      }
      @Override void render(HSAILRenderer r){
         r.append(op).typeName(dest).space().regName(dest).separator().regName(lhs).separator().append(value.toString());
      }
   }

   static  class addConst<T extends JavaType, C> extends binaryRegConst<T, C>{

      public addConst(Instruction _from,   HSAILRegister<T> _dest, HSAILRegister<T> _lhs, C _value_rhs){
         super(_from, "add_", _dest, _lhs, _value_rhs);
      }
   }
   */

   static class add<T extends PrimitiveType> extends binary<T>{
      public add(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "add_", _dest, _lhs, _rhs);
      }

   }

   static class sub<T extends PrimitiveType> extends binary<T>{
      public sub(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "sub_", _dest, _lhs, _rhs);
      }

   }

   static class div<T extends PrimitiveType> extends binary<T>{
      public div(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "div_", _dest, _lhs, _rhs);
      }

   }

   static class mul<T extends PrimitiveType> extends binary<T>{
      public mul(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "mul_", _dest, _lhs, _rhs);
      }

   }

   static class rem<T extends PrimitiveType> extends binary<T>{
      public rem(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "rem_", _dest, _lhs, _rhs);
      }

   }
   static class neg<T extends PrimitiveType> extends unary<T>{
      public neg(Instruction _from, HSAILRegister<T> _destSrc){
         super(_from, "neg_", _destSrc);
      }

   }
   static class shl<T extends PrimitiveType> extends binary<T>{
      public shl(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "shl_", _dest, _lhs, _rhs);
      }

   }
   static class shr<T extends PrimitiveType> extends binary<T>{
      public shr(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "shr_", _dest, _lhs, _rhs);
      }

   }
   static class ushr<T extends PrimitiveType> extends binary<T>{
      public ushr(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "ushr_", _dest, _lhs, _rhs);
      }

   }


   static class and<T extends PrimitiveType> extends binary<T>{
      public and(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "and_", _dest, _lhs, _rhs);
      }
      @Override void render(HSAILRenderer r){
         r.append(op).movTypeName(getDest()).space().regName(getDest()).separator().regName(getLhs()).separator().regName(getRhs());
      }

   }
   static class or<T extends PrimitiveType> extends binary<T>{
      public or(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "or_", _dest, _lhs, _rhs);
      }
      @Override void render(HSAILRenderer r){
         r.append(op).movTypeName(getDest()).space().regName(getDest()).separator().regName(getLhs()).separator().regName(getRhs());
      }

   }
   static class xor<T extends PrimitiveType> extends binary<T>{
      public xor(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs){
         super(_from, "xor_", _dest, _lhs, _rhs);
      }
      @Override void render(HSAILRenderer r){
         r.append(op).movTypeName(getDest()).space().regName(getDest()).separator().regName(getLhs()).separator().regName(getRhs());
      }

   }

   static class mov_const<T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDest<T>{

      C value;

      public mov_const(Instruction _from, HSAILRegister<T> _dest, C _value){
         super(_from, _dest);
         value = _value;
      }

      @Override void render(HSAILRenderer r){
         r.append("mov_").movTypeName(getDest()).space().regName(getDest()).separator().append(value);

      }


   }

   List<HSAILInstruction> instructions = new ArrayList<HSAILInstruction>();
   ClassModel.ClassModelMethod method;

   boolean optimizeMoves = false || Config.enableOptimizeRegMoves;

   void add(HSAILInstruction _regInstruction){
      // before we add lets see if this is a redundant mov
      if(optimizeMoves && _regInstruction.sources != null && _regInstruction.sources.length > 0){
         for(int regIndex = 0; regIndex < _regInstruction.sources.length; regIndex++){
            HSAILRegister r = _regInstruction.sources[regIndex];
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


   public HSAILRenderer render(HSAILRenderer r){
      // r.append("version 1:0:large;").nl();
      r.append("version 0:95: $full : $large;").nl();
      // r.append("kernel &" + method.getName() + "(");
      r.append("kernel &run(");
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
      int count = 0;
      for(HSAILInstruction i : instructions){
         if(!(i instanceof ld_kernarg) && !s.contains(i.from)){
            if(!first){
               r.pad(9).append("workitemabsid_u32 $s" + (count - 1) + ", 0;").nl();
               first = true;
            }
            s.add(i.from);
            if(i.from.isBranchTarget()){

               r.label(i.from.getThisPC()).append(":");
               r.nl();
            }
            if(r.isShowingComments()){
               r.nl().pad(1).append("// ").mark().append(i.from.getThisPC()).relpad(2).space().i(i.from).nl();
            }

         }else{
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

   public HSAILRegister getRegOfLastWriteToIndex(int _index){

      int idx = instructions.size();
      while(--idx >= 0){
         HSAILInstruction i = instructions.get(idx);
         if(i.dests != null){
            for(HSAILRegister d : i.dests){
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

   public HSAILRegister addmov(Instruction _i, int _from, int _to){
      HSAILRegister r = getRegOfLastWriteToIndex(_i.getPreStackBase() + _i.getMethod().getCodeEntry().getMaxLocals() + _from);
      addmov(_i, r.type, _from, _to);
      return (r);
   }

   enum ParseState{NONE, COMPARE_F32, COMPARE_F64, COMPARE_S64};




   public HSAILMethod(ClassModel.ClassModelMethod _method){
      if (UnsafeWrapper.getObjectPointerSizeInBytes()==4){
          throw new IllegalStateException("Object pointer size is 4, you need to use 64 bit JVM and set -XX:-UseCompressedOops!");
      }
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
               }else    if(arg.getJavaType().isObject()){
                  add(new ld_kernarg(i, new VarReg_ref(arg.getArgc() + argOffset)));
               }else if(arg.getJavaType().isInt()){
                  add(new ld_kernarg(i, new VarReg_s32(arg.getArgc() + argOffset)));
               }else if(arg.getJavaType().isFloat()){
                  add(new ld_kernarg(i, new VarReg_f32(arg.getArgc() + argOffset)));
               }else if(arg.getJavaType().isDouble()){
                   add(new ld_kernarg(i, new VarReg_f64(arg.getArgc() + argOffset)));
               }else if(arg.getJavaType().isLong()){
                   add(new ld_kernarg(i, new VarReg_s64(arg.getArgc() + argOffset)));
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
            case IALOAD:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s32.getHsaBytes()));
               add(new array_load<s32>(i, new StackReg_s32(i, 0), new StackReg_ref(i, 1)));
            break;
            case LALOAD:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s64.getHsaBytes()));
               add(new array_load<s64>(i, new StackReg_s64(i, 0), new StackReg_ref(i, 1)));
               break;
            case FALOAD:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f32.getHsaBytes()));
               add(new array_load<f32>(i, new StackReg_f32(i, 0), new StackReg_ref(i, 1)));

            break;
            case DALOAD:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f64.getHsaBytes()));
               add(new array_load<f64>(i, new StackReg_f64(i, 0), new StackReg_ref(i, 1)));

            break;
            case AALOAD:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.ref.getHsaBytes()));
               add(new array_load<ref>(i, new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
            break;
            case BALOAD:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s8.getHsaBytes()));
               add(new array_load<s8>(i, new StackReg_s8(i, 0), new StackReg_ref(i, 1)));
               break;
            case CALOAD:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.u16.getHsaBytes()));
               add(new array_load<u16>(i, new StackReg_u16(i, 0), new StackReg_ref(i, 1)));
               break;
            case SALOAD:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s16.getHsaBytes()));
               add(new array_load<s16>(i, new StackReg_s16(i, 0), new StackReg_ref(i, 1)));
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
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s32.getHsaBytes()));
               add(new array_store<s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 2)));
               break;
            case LASTORE:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s64.getHsaBytes()));
               add(new array_store<u64>(i, new StackReg_ref(i, 1), new StackReg_u64(i, 2)));
               break;
            case FASTORE:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f32.getHsaBytes()));
               add(new array_store<f32>(i, new StackReg_ref(i, 1), new StackReg_f32(i, 2)));
               break;
            case DASTORE:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f64.getHsaBytes()));
               add(new array_store<f64>(i, new StackReg_ref(i, 1), new StackReg_f64(i, 2)));
               break;
            case AASTORE:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.ref.getHsaBytes()));
               add(new array_store<ref>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 2)));

               break;
            case BASTORE:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s8.getHsaBytes()));
               add(new array_store<s8>(i, new StackReg_ref(i, 1), new StackReg_s8(i, 2)));

               break;
            case CASTORE:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s8.getHsaBytes()));
               add(new array_store<u16>(i, new StackReg_ref(i, 1), new StackReg_u16(i, 2)));
               break;
            case SASTORE:
               add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
               add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s8.getHsaBytes()));
               add(new array_store<s16>(i, new StackReg_ref(i, 1), new StackReg_s16(i, 2)));
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
            case DUP_X2:

               addmov(i, 2, 3);
               addmov(i, 1, 2);
               addmov(i, 0, 1);
               addmov(i, 3, 0);

               break;
            case DUP2:
               // DUP2 is problematic. DUP2 either dups top two items or one depending on the 'prefix' of the stack items.
               // To complicate this further HSA large model wants object/mem references to be 64 bits (prefix 2 in Java) whereas
               // in java object/array refs are 32 bits (prefix 1).
               addmov(i, 0, 2);
               addmov(i, 1, 3);
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
               add(new neg<s32>(i, new StackReg_s32(i, 0)));
               break;
            case LNEG:
               add(new neg<s64>(i, new StackReg_s64(i, 0)));
               break;
            case FNEG:
               add(new neg<f32>(i, new StackReg_f32(i, 0)));
               break;
            case DNEG:
               add(new neg<f64>(i, new StackReg_f64(i, 0)));
               break;
            case ISHL:
               add(new shl<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               break;
            case LSHL:
               add(new shl<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
               break;
            case ISHR:
               add(new shr<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               break;
            case LSHR:
               add(new shr<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
               break;
            case IUSHR:
               add(new ushr<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               break;
            case LUSHR:
               add(new ushr<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
               break;
            case IAND:
               add(new and<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               break;
            case LAND:
               add(new and<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
               break;
            case IOR:
               add(new or<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               break;
            case LOR:
               add(new or<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
               break;
            case IXOR:
               add(new xor<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               break;
            case LXOR:
               add(new xor<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
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
               add(new cvt<s32, s64>(i, new StackReg_s32(i, 0), new StackReg_s64(i, 0)));
               break;
            case L2F:
               add(new cvt<f32, s64>(i, new StackReg_f32(i, 0), new StackReg_s64(i, 0)));
               break;
            case L2D:
               add(new cvt<f64, s64>(i, new StackReg_f64(i, 0), new StackReg_s64(i, 0)));
               break;
            case F2I:
               add(new cvt<s32, f32>(i, new StackReg_s32(i, 0), new StackReg_f32(i, 0)));
               break;
            case F2L:
               add(new cvt<s64, f32>(i, new StackReg_s64(i, 0), new StackReg_f32(i, 0)));
               break;
            case F2D:
               add(new cvt<f64, f32>(i, new StackReg_f64(i, 0), new StackReg_f32(i, 0)));
               break;
            case D2I:
               add(new cvt<s32, f64>(i, new StackReg_s32(i, 0), new StackReg_f64(i, 0)));
               break;
            case D2L:
               add(new cvt<s64, f64>(i, new StackReg_s64(i, 0), new StackReg_f64(i, 0)));
               break;
            case D2F:
               add(new cvt<f32, f64>(i, new StackReg_f32(i, 0), new StackReg_f64(i, 0)));
               break;
            case I2B:
               add(new cvt<s8, s32>(i, new StackReg_s8(i, 0), new StackReg_s32(i, 0)));
               break;
            case I2C:
               add(new cvt<u16, s32>(i, new StackReg_u16(i, 0), new StackReg_s32(i, 0)));
               break;
            case I2S:
               add(new cvt<s16, s32>(i, new StackReg_s16(i, 0), new StackReg_s32(i, 0)));
               break;
            case LCMP:
               parseState = ParseState.COMPARE_S64;
               break;
            case FCMPL:
               parseState = ParseState.COMPARE_F32;
               break;
            case FCMPG:
               parseState = ParseState.COMPARE_F32;
               break;
            case DCMPL:
               parseState = ParseState.COMPARE_F64;
               break;
            case DCMPG:
               parseState = ParseState.COMPARE_F64;
               break;
            case IFEQ:
               if(parseState.equals(ParseState.COMPARE_F32)){
                  add(new cmp<f32>(lastInstruction, "eq", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_F64)){
                  add(new cmp<f64>(lastInstruction, "eq", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_S64)){
                  add(new cmp<s64>(lastInstruction, "eq", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else{
                  add(new cmp_s32_const_0(i, "eq", new StackReg_s32(i, 0)));

               }
               add(new cbr(i, i.asBranch().getAbsolute()));
               break;
            case IFNE:
               if(parseState.equals(ParseState.COMPARE_F32)){
                  add(new cmp<f32>(lastInstruction, "ne", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_F64)){
                  add(new cmp<f64>(lastInstruction, "ne", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_S64)){
                  add(new cmp<s64>(lastInstruction, "ne", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else{
                  add(new cmp_s32_const_0(i, "ne", new StackReg_s32(i, 0)));

               }
               add(new cbr(i, i.asBranch().getAbsolute()));
               break;
            case IFLT:
               if(parseState.equals(ParseState.COMPARE_F32)){
                  add(new cmp<f32>(lastInstruction, "lt", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_F64)){
                  add(new cmp<f64>(lastInstruction, "lt", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_S64)){
                  add(new cmp<s64>(lastInstruction, "lt", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else{
                  add(new cmp_s32_const_0(i, "lt", new StackReg_s32(i, 0)));

               }
               add(new cbr(i, i.asBranch().getAbsolute()));
               break;
            case IFGE:
               if(parseState.equals(ParseState.COMPARE_F32)){
                  add(new cmp<f32>(lastInstruction, "ge", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_F64)){
                  add(new cmp<f64>(lastInstruction, "ge", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_S64)){
                  add(new cmp<s64>(lastInstruction, "ge", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else{
                  add(new cmp_s32_const_0(i, "ge", new StackReg_s32(i, 0)));

               }
               add(new cbr(i, i.asBranch().getAbsolute()));
               break;
            case IFGT:
               if(parseState.equals(ParseState.COMPARE_F32)){
                  add(new cmp<f32>(lastInstruction, "gt", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_F64)){
                  add(new cmp<f64>(lastInstruction, "gt", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_S64)){
                  add(new cmp<s64>(lastInstruction, "gt", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else{
                  add(new cmp_s32_const_0(i, "gt", new StackReg_s32(i, 0)));

               }
               add(new cbr(i, i.asBranch().getAbsolute()));
               break;
            case IFLE:
               if(parseState.equals(ParseState.COMPARE_F32)){
                  add(new cmp<f32>(lastInstruction, "le", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_F64)){
                  add(new cmp<f64>(lastInstruction, "le", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else if(parseState.equals(ParseState.COMPARE_S64)){
                  add(new cmp<s64>(lastInstruction, "le", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                  parseState = ParseState.NONE;
               }else{
                  add(new cmp_s32_const_0(i, "le", new StackReg_s32(i, 0)));

               }
               add(new cbr(i, i.asBranch().getAbsolute()));
               break;
            case IF_ICMPEQ:

               add(new cmp_s32(i, "eq", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               add(new cbr(i, i.asBranch().getAbsolute()));

               break;
            case IF_ICMPNE:

               add(new cmp_s32(i, "ne", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               add(new cbr(i, i.asBranch().getAbsolute()));

               break;
            case IF_ICMPLT:

               add(new cmp_s32(i, "lt", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               add(new cbr(i, i.asBranch().getAbsolute()));

               break;
            case IF_ICMPGE:

               add(new cmp_s32(i, "ge", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               add(new cbr(i, i.asBranch().getAbsolute()));

               break;
            case IF_ICMPGT:

               add(new cmp_s32(i, "gt", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
               add(new cbr(i, i.asBranch().getAbsolute()));

               break;
            case IF_ICMPLE:

               add(new cmp_s32(i, "le", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
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
               add(new ret<ref>(i, new StackReg_ref(i, 0)));
               break;
            case RETURN:
               add(new retvoid(i));
               break;
            case GETSTATIC:{
                TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

                try{
                    Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                    Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());

                    if(type.isArray()){
                        add(new static_field_load<ref>(i, new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));

                        add(new and_const<u64, Long>(i, new StackReg_u64(i, 0), new StackReg_ref(i, 0), (long) 0xffffffffL));
                    }else if(type.isInt()){
                        add(new static_field_load<s32>(i, new StackReg_s32(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));

                    }else if(type.isFloat()){
                        add(new static_field_load<f32>(i, new StackReg_f32(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
                    }else if(type.isDouble()){
                        add(new static_field_load<f64>(i, new StackReg_f64(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
                    }else if(type.isLong()){
                        add(new static_field_load<s64>(i, new StackReg_s64(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
                    }
                }catch(ClassNotFoundException e){
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }catch(NoSuchFieldException e){
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }


            }
            break;
            case GETFIELD:{
               TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try{
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                  if(type.isArray()){
                     add(new field_load<ref>(i, new StackReg_ref(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));

                     add(new and_const<u64, Long>(i, new StackReg_u64(i, 0), new StackReg_ref(i, 0), (long) 0xffffffffL));
                  }else if(type.isInt()){
                     add(new field_load<s32>(i, new StackReg_s32(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));

                  }else if(type.isFloat()){
                     add(new field_load<f32>(i, new StackReg_f32(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                  }else if(type.isDouble()){
                     add(new field_load<f64>(i, new StackReg_f64(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                  }else if(type.isLong()){
                     add(new field_load<s64>(i, new StackReg_s64(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                  }
               }catch(ClassNotFoundException e){
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }catch(NoSuchFieldException e){
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }


            }
            break;
            case PUTSTATIC:
               add(new nyi(i));
               break;
            case PUTFIELD:{
               TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try{
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                  if(type.isArray()){
                     add(new field_store<ref>(i, new StackReg_ref(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));

                   //  add(new and_const<u64, Long>(i, new StackReg_u64(i, 1), new StackReg_ref(i, 0), (long) 0xffffffffL));
                  }else if(type.isInt()){
                     add(new field_store<s32>(i, new StackReg_s32(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));

                  }else if(type.isFloat()){
                     add(new field_store<f32>(i, new StackReg_f32(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                  }else if(type.isDouble()){
                     add(new field_store<f64>(i, new StackReg_f64(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                  }else if(type.isLong()){
                     add(new field_store<s64>(i, new StackReg_s64(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                  }
               }catch(ClassNotFoundException e){
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }catch(NoSuchFieldException e){
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
               add(new array_len(i, new StackReg_s32(i, 0), new StackReg_ref(i, 0)));

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
