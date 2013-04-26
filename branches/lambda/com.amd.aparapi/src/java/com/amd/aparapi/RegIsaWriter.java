package com.amd.aparapi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/16/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegIsaWriter{

   ClassModel.ClassModelMethod method;


   static class Renderer{





      StringBuilder sb = new StringBuilder();
      public Renderer label(int _pc){
         sb.append(String.format("@L%d", _pc));
         return(this);
      }

      public Renderer append(String s){
         sb.append(s);
         return(this);
      }
      public Renderer append(int i){
         sb.append(""+i);
         return(this);
      }
      public Renderer append(double d){
         sb.append(""+d);
         return(this);
      }
      public Renderer append(float f){
         sb.append(""+f);
         return(this);
      }
      public Renderer append(long l){
         sb.append(""+l);
         return(this);
      }


      public Renderer  array_len_offset(){
         append(4);
         return(this);
      }

      public Renderer sizeof_s32(){
         append(4);
         return(this);
      }
      public Renderer sizeof_s64(){
         append(8);
         return(this);
      }
      public Renderer  sizeof_f32(){
         append(4);
         return(this);
      }
      public Renderer  sizeof_f64(){
         append(8);
         return(this);
      }


      public Renderer regNum( int reg){
         append(reg);
         return(this);
      }

      public Renderer regPrefix(){
         append("$");
         return(this);
      }

      public Renderer s32Name( int reg){
         regPrefix().append("s").regNum(reg);
         return(this);
      }

      public Renderer s64Name( int reg){
         regPrefix();
         append("d");
         regNum(reg);
         return(this);
      }
      public Renderer u64Name( int reg){
         regPrefix();
         append("d");
         regNum(reg);
         return(this);
      }
      public Renderer f64Name( int reg){
         regPrefix();
         append("d");
         regNum(reg);
         return(this);
      }

      public Renderer f32Name( int reg){
         regPrefix();
         append("s");
         regNum(reg);
         return(this);
      }

      public Renderer separator(){
         append(", ");
         return(this);
      }

      public Renderer nl(){
         append("\n");
         return(this);
      }

      public Renderer indent(){
         append("      ");
         return(this);
      }

      public Renderer s32Array(int arr_reg, int index){
         append("[");
         u64Name(arr_reg);
         append("+");
         array_len_offset();
         append("+(");
         sizeof_s32();
         append("*");
         s32Name(index);
         append(")]");
         return(this);
      }

      public Renderer s64Array(int arr_reg, int index){
         append("[");
         u64Name(arr_reg);
         append("+");
         array_len_offset();
         append("+(");
         sizeof_s64();
         append("*");
         s64Name(index);
         append(")]");
         return(this);
      }

      public Renderer f64Array(int arr_reg, int index){
         append("[");
         u64Name(arr_reg);
         append("+");
         array_len_offset();
         append("+(");
         sizeof_f64();
         append("*");
         f64Name(index);
         append(")]");
         return(this);
      }

      public Renderer f32Array(int arr_reg, int index){
         append("[");
         u64Name(arr_reg);
         append("+");
         array_len_offset();
         append("+(");
         sizeof_f32();
         append("*");
         f32Name(index);
         append(")]");
         return(this);
      }

      public Renderer mov(){
         append("mov_");
         return(this);
      }

      public Renderer load(){
         return(append("ld_global_"));
      }

      public Renderer store(){
         append("st_global_");
         return(this);
      }

      public Renderer s(){
         return(append("s"));
      }

      public Renderer d(){
         return(append("d"));
      }

      public Renderer s32(){
         return(append("s32"));
      }

      public Renderer s64(){
         return(append("s64"));
      }
      public Renderer f32(){
         return(append("f32"));
      }
      public Renderer f64(){
         return(append("f64"));
      }
      public Renderer u64(){
         return(append("u64"));
      }
      public Renderer space(){
         return(append(" "));
      }
      public Renderer add(){
         return(append("add_"));
      }
      public Renderer sub(){
         return(append("sub_"));
      }

      public Renderer mul(){
         return(append("mul_"));
      }
      public Renderer div(){
         return(append("div_"));
      }

      public Renderer rem(){
         return(append("rem_"));
      }

      public Renderer cvt(){
         return(append("cvt_"));
      }


      String argType(TypeHelper.Arg _arg){
         if (_arg.isArray()){
            return("u64");
         }else if (_arg.isInt()){
            return("s32");
         }else if (_arg.isFloat()){
            return("f32");
         }else{
            return("?");
         }
      }
      String regType(TypeHelper.Arg _arg){
         if (_arg.isArray()){
            return("d");
         }else if (_arg.isInt()){
            return("s");
         }else if (_arg.isFloat()){
            return("s");
         }else{
            return("?");
         }
      }

      void writePrologue(ClassModel.ClassModelMethod method){
         append("version 1:0").nl();
         append("kernel &"+method.getName()+"(");
         int argOffset = method.isStatic()?0:1;
         if (!method.isStatic()){
            nl().indent().append("kernarg_u64 %_arg0");
         }

         for (TypeHelper.Arg arg:method.argsAndReturnType.getArgs()){
            if ((method.isStatic() && arg.getArgc()==0) ){
               nl();
            }           else{
               separator().nl();
            }
            indent().append("kernarg_"+argType(arg)+" %_arg"+(arg.getArgc()+argOffset));

         }
         nl().indent().append("){").nl();
         if (!method.isStatic()){
            indent().append("ld_kernarg_u64 $d"+0+", [%_arg0];").nl();
         }
         for (TypeHelper.Arg arg:method.argsAndReturnType.getArgs()){
            indent().append("ld_kernarg_"+argType(arg)+" "+"$"+regType(arg)+(arg.getArgc()+argOffset)+", [%_arg"+(arg.getArgc()+argOffset)+"];").nl();
         }

      }
      void writeEpilogue(ClassModel.ClassModelMethod method){
         append("};");
      }


      public Renderer semicolon(){
         return(append(";"));
      }
   }

   static abstract class RegInstruction{
       Instruction from;
       RegInstruction(Instruction _from){
          from = _from;
       }


       final void render(Renderer r){
          if (from.isBranchTarget()){
             r.nl();
             r.label(from.getThisPC());
          }
          r.indent();
          renderMe(r);
          r.semicolon();
       }
       abstract void renderMe(Renderer r);

   }

   static class branch extends RegInstruction{
      String name;
      int pc;
      branch(Instruction _from, String _name, int _pc){
         super(_from);
         name = _name;
         pc = _pc;
      }
      @Override public void renderMe(Renderer r){
         r.append(name+" ");
         r.label(pc);
      }
   }

   static class field extends RegInstruction{

      field(Instruction _from){
         super(_from);
      }

      @Override void renderMe(Renderer r){
            r.append("field_");
            TypeHelper.Type type = from.asFieldAccessor().getConstantPoolFieldEntry().getType();
            String dotClassName = from.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName();
            String name = from.asFieldAccessor().getConstantPoolFieldEntry().getName();
            if(from instanceof InstructionSet.I_PUTFIELD || from instanceof InstructionSet.I_PUTSTATIC){
               if(type.isArray()){
                  r.append("arr_");
               }
               if(type.isInt()){
                  r.append("s32 " + dotClassName + "." + name);
                  r.separator();
                  r.s32Name(from.getPreStackBaseOnLocals());
               }
               if(type.isFloat()){
                  r.append("f32 " + dotClassName + "." + name) ;
                  r.separator();
                  r.f32Name(from.getPreStackBaseOnLocals());
               }
            }else{
               if(type.isArray()){
                  r.append("arr_");
               }
               if(type.isInt()){
                  r.append("s32 " + ((type.isArray())?"arr_":""));
                  r.s32Name(from.getPreStackBaseOnLocals());
               }
               if(type.isFloat()){
                  r.append("f32 " + ((type.isArray())?"arr_":""));
                  r.f32Name(from.getPreStackBaseOnLocals());
               }
               r.separator();
               r.u64Name(from.getPreStackBaseOnLocals());
               r.separator();
               r.append(dotClassName + "." + name);
            }
      }

   }
   static class call extends RegInstruction{

      call(Instruction _from){
         super(_from);
      }

      @Override void renderMe(Renderer r){
            String dotClassName = from.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName();
            String name = from.asMethodCall().getConstantPoolMethodEntry().getName();
            TypeHelper.ArgsAndReturnType argsAndReturnType = from.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType();


            TypeHelper.Type returnType = argsAndReturnType.getReturnType();


            if(returnType.isVoid()){
               r.append("call_void VOID");
               r.separator();
               r.append(dotClassName + "." + name + " ");
            }else if(returnType.isInt()){
               r.append("call_s32 ");
                r.s32Name( from.getPreStackBaseOnLocals());
                r.separator();
                r.append(dotClassName + "." + name + " ");
            }else if(returnType.isDouble()){
               r.append("call_f64 ");
               r.f64Name(from.getPreStackBaseOnLocals());
              r.separator();
              r.append(dotClassName + "." + name + " ");

            }
            for(TypeHelper.Arg arg : argsAndReturnType.getArgs()){
               if(arg.getArgc() > 0){
                  r.append(", ");
               }
               if(arg.isDouble()){
                  r.f64Name(from.getPreStackBaseOnLocals() + arg.getArgc());
               }else if(arg.isFloat()){
                  r.f32Name( from.getPreStackBaseOnLocals() + arg.getArgc());
               }else if(arg.isInt()){
                  r.s32Name( from.getPreStackBaseOnLocals() + arg.getArgc());
               }else if(arg.isLong()){
                  r.s64Name( from.getPreStackBaseOnLocals() + arg.getArgc());
               }
            }
      }
   }

   static class nyi extends RegInstruction{

      nyi(Instruction _from){
         super(_from);
      }

      @Override void renderMe(Renderer r){

            r.append("NYI "+from.getByteCode().getName());//InstructionHelper.getLabel(i, false, false, false);
            if(from.isBranch()){
               r.append(" " + from.asBranch().getAbsolute());
            }else if(from.isFieldAccessor()){
               r.append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getType());
               r.append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());
               r.append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getName());
            }else if(from.isLocalVariableAccessor()){
               r.append(" #" + from.asLocalVariableAccessor().getLocalVariableInfo().getSlot());
               r.append(" " + from.asLocalVariableAccessor().getLocalVariableInfo().getVariableName());
               r.append(" " + from.asLocalVariableAccessor().getLocalVariableInfo().getVariableDescriptor());

            }else if(from.isMethodCall()){
               r.append(" " + from.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType().getReturnType());
               r.append(" " + from.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName());
               r.append("." + from.asMethodCall().getConstantPoolMethodEntry().getName());
            }else if(from.isConstant()){
               r.append("." + from.asConstant().getValue());
            }

      }
   }

   static abstract class ld_kernarg extends RegInstruction{
      int reg, argc;

      ld_kernarg(Instruction _from){
         super(_from);
      }
   }

   /*

   static class ld_kernarg_u64 extends ld_kernarg{

      ld_kernarg_u64(Instruction _from){
         super(_from);
      }

      @Override void renderMe(StringBuilder _sb){
      }
   }

   static class ld_kernarg_s32 extends ld_kernarg{

      ld_kernarg_s32(Instruction _from){
         super(_from);
      }
   }
   */
   static class add_const_s32 extends RegInstruction{
      int reg_dest, reg_src, value;
      add_const_s32(Instruction _from, int _reg_dest, int _reg_src, int _value){
         super(_from);
         reg_dest=_reg_dest;
         reg_src = _reg_src;
         value = _value;
      }
      @Override void renderMe(Renderer r){
         r.add().s32().space().s32Name(reg_dest).separator().s32Name(reg_src).separator().append(value);
      }
   }


   static abstract class cvt extends RegInstruction{
      int reg_dest, reg_src;
      cvt(Instruction _from, int _reg_dest, int _reg_src){
         super(_from);
         reg_dest=_reg_dest;
         reg_src = _reg_src;
      }
   }

   static abstract class store extends RegInstruction{
      int value, index, array;
      store(Instruction _from, int _value, int _index, int _array){
         super(_from);
         value=_value;
         index=_index;
         array = _index;
      }
   }

   static class store_s32 extends store{

      store_s32(Instruction _from, int _value, int _index, int _array){
         super(_from, _value, _index, _array);
      }

      @Override void renderMe(Renderer r){
         r.store().s32().space().s32Array(array, index).separator().s32Name(value);
      }
   }


   static abstract class load extends RegInstruction{
      int arrayAndValue, index;
      load(Instruction _from, int _arrayAndValue, int _index){
         super(_from);
         arrayAndValue = _arrayAndValue;
         index=_index;
      }
   }
   static class load_s32 extends load{

      load_s32(Instruction _from, int _arrayAndValue, int _index){
         super(_from, _arrayAndValue, _index);
      }

      @Override void renderMe(Renderer r){
         r.load().s32().space().s32Name(arrayAndValue).separator().s32Array(arrayAndValue, index);
      }
   }
   static class load_f32 extends load{

      load_f32(Instruction _from, int _arrayAndValue, int _index){
         super(_from, _arrayAndValue, _index);
      }
      @Override void renderMe(Renderer r){
         r.load().f32().space().f32Name(arrayAndValue).separator().f32Array(arrayAndValue, index);
      }
   }
   static class load_s64 extends load{

      load_s64(Instruction _from, int _arrayAndValue, int _index){
         super(_from, _arrayAndValue, _index);
      }
      @Override void renderMe(Renderer r){
         r.load().s64().space().s64Name(arrayAndValue).separator().s64Array(arrayAndValue, index);
      }
   }

   static class cvt_s32_f32 extends cvt{
      cvt_s32_f32(Instruction _from, int _reg_dest, int _reg_src){
         super(_from, _reg_dest, _reg_src);
      }
      @Override void renderMe(Renderer r){
         r.cvt().s32().append("_").f32().space().s32Name(reg_dest).separator().f32Name(reg_src);
      }
   }

   static abstract class mov extends RegInstruction{
      int reg_dest, reg_src;

      public mov(Instruction _from, int _reg_dest, int _reg_src){
         super(_from);
         reg_dest = _reg_dest;
         reg_src = _reg_src;
      }
   }

   static abstract class binary extends RegInstruction{
      int reg_dest, reg_lhs, reg_rhs;

      public binary(Instruction _from, int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from);
         reg_dest = _reg_dest;
         reg_lhs = _reg_lhs;
         reg_rhs = _reg_rhs;
      }
   }

   static abstract class add extends binary{

      public add(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from, _reg_dest, _reg_lhs, _reg_rhs);
      }
   }
   static class add_s32 extends add{

      public add_s32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }

      @Override void renderMe(Renderer r){
         r.add().s32().space().s32Name(reg_dest).separator().s32Name(reg_lhs).separator().s32Name(reg_rhs);
      }
   }
   static  class add_s64 extends add{

      public add_s64(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.add().s64().space().s64Name(reg_dest).separator().s64Name(reg_lhs).separator().s64Name(reg_rhs);
      }
   }
   static  class add_f32 extends add{

      public add_f32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.add().f32().space().f32Name(reg_dest).separator().f32Name(reg_lhs).separator().f32Name(reg_rhs);
      }
   }
   static  class add_f64 extends add{

      public add_f64(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.add().f64().space().f64Name(reg_dest).separator().f64Name(reg_lhs).separator().f64Name(reg_rhs);
      }
   }
   static abstract class sub extends binary{

      public sub(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }

   }
   static class sub_s32 extends sub{

      public sub_s32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.sub().s32().space().s32Name(reg_dest).separator().s32Name(reg_lhs).separator().s32Name(reg_rhs);
      }
   }

   static class sub_f64 extends sub{

      public sub_f64(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.sub().f64().space().f64Name(reg_dest).separator().f64Name(reg_lhs).separator().f64Name(reg_rhs);
      }
   }

   static class sub_f32 extends sub{

      public sub_f32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.sub().f32().space().f32Name(reg_dest).separator().f32Name(reg_lhs).separator().f32Name(reg_rhs);
      }
   }
   static abstract class div extends binary{

      public div(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
   }
   static class div_s32 extends div{

      public div_s32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.div().s32().space().s32Name(reg_dest).separator().s32Name(reg_lhs).separator().s32Name(reg_rhs);
      }
   }
   static class div_f32 extends div{

      public div_f32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.div().f32().space().f32Name(reg_dest).separator().f32Name(reg_lhs).separator().f32Name(reg_rhs);
      }
   }
   static abstract class mul extends binary{

      public mul(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
   }
   static class mul_s32 extends mul{

      public mul_s32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.mul().s32().space().s32Name(reg_dest).separator().s32Name(reg_lhs).separator().s32Name(reg_rhs);
      }
   }
   static class mul_f32 extends mul{

      public mul_f32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.mul().f32().space().f32Name(reg_dest).separator().f32Name(reg_lhs).separator().f32Name(reg_rhs);
      }
   }
   static abstract class rem extends binary{

      public rem(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
   }
   static class rem_s32 extends rem{

      public rem_s32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
      @Override void renderMe(Renderer r){
         r.rem().s32().space().s32Name(reg_dest).separator().s32Name(reg_lhs).separator().s32Name(reg_rhs);
      }
   }


   static abstract class mov_const<T> extends RegInstruction{
      int reg_dest;
      T value;

      public mov_const(Instruction _from,int _reg_dest, T _value){
         super(_from);
         reg_dest = _reg_dest;
         value = _value;
      }
   }

   static class mov_s32_const extends mov_const<Integer>{

      public mov_s32_const(Instruction _from,int _reg_dest, int _value){
         super(_from, _reg_dest, _value);
      }
      @Override void renderMe(Renderer r){
         r.mov().s32().space().s32Name(reg_dest).separator().append(value);

      }
   }
   static class mov_s64_const extends mov_const<Long>{

      public mov_s64_const(Instruction _from,int _reg_dest, long _value){
         super(_from,_reg_dest, _value);
      }
      @Override void renderMe(Renderer r){
         r.mov().s64().space().s64Name(reg_dest).separator().append(value);

      }
   }

   static class mov_f32_const extends mov_const<Float>{

      public mov_f32_const(Instruction _from,int _reg_dest, float _value){
         super(_from,_reg_dest, _value);
      }
      @Override void renderMe(Renderer r){
         r.mov().f32().space().f32Name(reg_dest).separator().append(value);

      }
   }

   static class mov_f64_const extends mov_const<Double>{

      public mov_f64_const(Instruction _from,int _reg_dest, double _value){
         super(_from,_reg_dest, _value);
      }
      @Override void renderMe(Renderer r){
         r.mov().f64().space().f64Name(reg_dest).separator().append(value);

      }
   }
   static class mov_s32 extends mov{

      public mov_s32(Instruction _from,int _reg_dest, int _reg_src){
         super(_from,_reg_dest, _reg_src);
      }

      @Override void renderMe(Renderer r){
         r.mov().s32().space().s32Name(reg_dest).separator().s32Name(reg_src);

      }
   }

   static class mov_s64 extends mov{

      public mov_s64(Instruction _from,int _reg_dest, int _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
      @Override void renderMe(Renderer r){
         r.mov().s64().space().s64Name(reg_dest).separator().s64Name(reg_src);

      }
   }
   static class mov_f64 extends mov{
      public mov_f64(Instruction _from,int _reg_dest, int _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
      @Override void renderMe(Renderer r){
         r.mov().f64().space().f64Name(reg_dest).separator().f64Name(reg_src);

      }
   }
   static class mov_f32 extends mov{
      public mov_f32(Instruction _from,int _reg_dest, int _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
      @Override void renderMe(Renderer r){
         r.mov().f32().space().f32Name(reg_dest).separator().f32Name(reg_src);

      }
   }
   static class mov_u64 extends mov{
      public mov_u64(Instruction _from,int _reg_dest, int _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
      @Override void renderMe(Renderer r){
         r.mov().u64().space().u64Name(reg_dest).separator().u64Name(reg_src);

      }
   }

   static class RegISA implements Iterable<RegInstruction>{
      List<RegInstruction> instructions = new ArrayList<RegInstruction>();
      void add(RegInstruction _regInstruction){
          instructions.add(_regInstruction);
      }

      @Override public Iterator<RegInstruction> iterator(){
         return(instructions.iterator());
      }
   }

   RegIsaWriter(ClassModel.ClassModelMethod _method){
      method = _method;
   }


   String indent = "    ";



   void write(){

      RegISA regISA = new RegISA();
      System.out.println(InstructionHelper.getJavapView(method));


      System.out.println("MaxLocals=" + method.getCodeEntry().getMaxLocals());
      System.out.println("MaxStack=" + method.getCodeEntry().getMaxStack());
      Table table = new Table("|%2d ", "|%2d", "|%2d", "|%s", "|%d", "|%d","|%d", "|%-60s", "|%s");
      table.header("|PC ", "|Depth", "|Block", "|Consumes + count", "|Produces", "|PreStackBase","|PostStackBase", "|Instruction", "|Branches");

      for(Instruction i : method.getInstructions()){
         add( regISA, i);
      }

      Renderer r = new Renderer();
      /*
      for(Instruction i : method.getInstructions()){

         StringBuilder consumes = new StringBuilder();
         for(Instruction.InstructionType instructionType : i.getConsumedInstructionTypes()){
            consumes.append(instructionType.getInstruction().getThisPC()).append(" ");
         }
         StringBuilder sb = new StringBuilder();
         for(InstructionHelper.BranchVector branchInfo : InstructionHelper.getBranches(method)){
            sb.append(branchInfo.render(i.getThisPC(), i.getStartPC()));
         }
         table.data(i.getThisPC());
          table.data(i.getDepth());
          table.data(i.getBlock());
         table.data("" + i.getStackConsumeCount() + " {" + consumes + "}");
         table.data(i.getStackProduceCount());
          table.data(i.getPreStackBaseN());
         table.data(i.getPostStackBase());
         table.data(label);
         table.data(sb + (i.isEndOfTernary() ? "*" : ""));
      }
       System.out.println("{\n" + table.toString() + "}\n");
       */
       r.writePrologue(method);
       for(RegInstruction i : regISA){
          i.render(r);
          r.nl();
       }
      r.writeEpilogue(method);
      System.out.println(r.sb.toString());
   }

   private void add(RegISA regISA, Instruction instruction){

      switch(instruction.getByteCode()){

         case ACONST_NULL:
            regISA.add(new nyi(instruction));
            break;
          case ICONST_M1:
          case ICONST_0:
          case ICONST_1:
          case ICONST_2:
          case ICONST_3:
          case ICONST_4:
          case ICONST_5:
          case BIPUSH:
          case SIPUSH:  {
          InstructionSet.Constant<Integer> c = ( InstructionSet.Constant) instruction;
          regISA.add(new mov_s32_const(instruction, instruction.getPreStackBaseOnLocals(), c.getValue()));
          }
          break;
         case LCONST_0:
         case LCONST_1:
         {
             InstructionSet.Constant<Long> c = ( InstructionSet.Constant) instruction;
            regISA.add(new mov_s64_const(instruction,instruction.getPreStackBaseOnLocals(), c.getValue()));
         }
            break;
         case FCONST_0:
         case FCONST_1:
          case FCONST_2:
          {
             InstructionSet.Constant<Float> c = ( InstructionSet.Constant) instruction;
             regISA.add(new mov_f32_const(instruction,instruction.getPreStackBaseOnLocals(), c.getValue()));
         }

            break;
         case DCONST_0:
         case DCONST_1:
         {
             InstructionSet.Constant<Double> c = ( InstructionSet.Constant) instruction;
            regISA.add(new mov_f64_const(instruction,instruction.getPreStackBaseOnLocals(), c.getValue()));

         }
            break;
         // case BIPUSH: moved up
         // case SIPUSH: moved up

         case LDC:
         case LDC_W:
         case LDC2_W: {
             InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant )instruction;

             ClassModel.ConstantPool.ConstantEntry e= (ClassModel.ConstantPool.ConstantEntry )cpe.getConstantPoolEntry() ;
             if (e instanceof ClassModel.ConstantPool.DoubleEntry){
                 regISA.add(new mov_f64_const(instruction,instruction.getPreStackBaseOnLocals(), ((ClassModel.ConstantPool.DoubleEntry)e).getValue()));
             } else if (e instanceof ClassModel.ConstantPool.FloatEntry){
                 regISA.add(new mov_f32_const(instruction,instruction.getPreStackBaseOnLocals(), ((ClassModel.ConstantPool.FloatEntry)e).getValue()));

             }  else if (e instanceof ClassModel.ConstantPool.IntegerEntry){
                 regISA.add(new mov_s32_const(instruction,instruction.getPreStackBaseOnLocals(), ((ClassModel.ConstantPool.IntegerEntry)e).getValue()));
             }  else if (e instanceof ClassModel.ConstantPool.LongEntry){
                regISA.add(new mov_s64_const(instruction,instruction.getPreStackBaseOnLocals(), ((ClassModel.ConstantPool.LongEntry)e).getValue()));
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
            regISA.add(new mov_s32(instruction,instruction.getPreStackBaseOnLocals(),
                  instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));

                 break;
         case LLOAD:
         case LLOAD_0:
         case LLOAD_1:
         case LLOAD_2:
         case LLOAD_3:
            regISA.add(new mov_s64(instruction,instruction.getPreStackBaseOnLocals(),
                  instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));
               break;
          case FLOAD:
         case FLOAD_0:
         case FLOAD_1:
         case FLOAD_2:
         case FLOAD_3:
            regISA.add(new mov_f32(instruction,instruction.getPreStackBaseOnLocals(),
                  instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));
               break;
          case DLOAD:
         case DLOAD_0:
         case DLOAD_1:
         case DLOAD_2:
         case DLOAD_3:
            regISA.add(new mov_f64(instruction,instruction.getPreStackBaseOnLocals(),
                  instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));
               break;
         case ALOAD:
         case ALOAD_0:
         case ALOAD_1:
         case ALOAD_2:
         case ALOAD_3:
            regISA.add(new mov_u64(instruction,instruction.getPreStackBaseOnLocals(),
                  instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));
               break;
         case IALOAD:   //arraref, index -> value
            regISA.add(new load_s32( instruction,
                  instruction.getPreStackBaseOnLocals(),   //array & value
                  instruction.getPreStackBaseOnLocals()+ 1)); //index
               break;
         case LALOAD:
            regISA.add(new load_s64( instruction,
                  instruction.getPreStackBaseOnLocals(),   //array & value
                  instruction.getPreStackBaseOnLocals()+ 1)); //index
              break;
         case FALOAD:
            regISA.add(new load_f32( instruction,
                  instruction.getPreStackBaseOnLocals(),   //array & value
                  instruction.getPreStackBaseOnLocals() + 1)); //index
              break;
         case DALOAD:
            regISA.add(new nyi(instruction));
            break;
         case AALOAD:
            regISA.add(new nyi(instruction));
            break;
         case BALOAD:
            regISA.add(new nyi(instruction));
            break;
         case CALOAD:
            regISA.add(new nyi(instruction));
            break;
         case SALOAD:
            regISA.add(new nyi(instruction));
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
            regISA.add(new mov_s32(instruction, instruction.asLocalVariableAccessor().getLocalVariableTableIndex(),
                  instruction.getPreStackBaseOnLocals()));

            break;
          case LSTORE:
         case LSTORE_0:
         case LSTORE_1:
         case LSTORE_2:
         case LSTORE_3:
            regISA.add(new mov_s64(instruction, instruction.asLocalVariableAccessor().getLocalVariableTableIndex(),
                  instruction.getPreStackBaseOnLocals()));

            break;
          case FSTORE:
          case FSTORE_0:
          case FSTORE_1:
          case FSTORE_2:
          case FSTORE_3:
             regISA.add(new mov_f32(instruction, instruction.asLocalVariableAccessor().getLocalVariableTableIndex(),
                   instruction.getPreStackBaseOnLocals()));
               break;
          case DSTORE:
         case DSTORE_0:
         case DSTORE_1:
         case DSTORE_2:
         case DSTORE_3:
            regISA.add(new mov_f64(instruction, instruction.asLocalVariableAccessor().getLocalVariableTableIndex(),
                  instruction.getPreStackBaseOnLocals()));
             break;
          case ASTORE:
         case ASTORE_0:
         case ASTORE_1:
         case ASTORE_2:
         case ASTORE_3:
            regISA.add(new mov_u64(instruction, instruction.asLocalVariableAccessor().getLocalVariableTableIndex(),
                  instruction.getPreStackBaseOnLocals()));
                break;
         case IASTORE:
            regISA.add(new store_s32(instruction,
                  instruction.getPreStackBaseOnLocals()+ 2, //value
                  instruction.getPreStackBaseOnLocals() + 1, //index
                  instruction.getPreStackBaseOnLocals() + 0));//array
              break;
         case LASTORE:
            regISA.add(new nyi(instruction));
            break;
         case FASTORE:
            regISA.add(new nyi(instruction));
            break;
         case DASTORE:
            regISA.add(new nyi(instruction));
            break;
         case AASTORE:
            regISA.add(new nyi(instruction));
            break;
         case BASTORE:
            regISA.add(new nyi(instruction));
            break;
         case CASTORE:
            regISA.add(new nyi(instruction));
            break;
         case SASTORE:
            regISA.add(new nyi(instruction));
            break;
         case POP:
            regISA.add(new nyi(instruction));
            break;
         case POP2:
            regISA.add(new nyi(instruction));
            break;
         case DUP:
            regISA.add(new nyi(instruction));
            break;
         case DUP_X1:
            regISA.add(new nyi(instruction));
            break;
         case DUP_X2:
             regISA.add(new mov_s32(instruction, instruction.getPreStackBaseOnLocals()+3, instruction.getPreStackBaseOnLocals()+2) ) ;
            regISA.add(new mov_s32(instruction, instruction.getPreStackBaseOnLocals()+2, instruction.getPreStackBaseOnLocals()+1) ) ;

            regISA.add(new mov_s32(instruction, instruction.getPreStackBaseOnLocals()+1, instruction.getPreStackBaseOnLocals()+0)) ;

            regISA.add(new mov_s32(instruction, instruction.getPreStackBaseOnLocals()+0, instruction.getPreStackBaseOnLocals()+3 )) ;
              break;
         case DUP2:

            regISA.add(new mov_s32(instruction, instruction.getPreStackBaseOnLocals()+2, instruction.getPreStackBaseOnLocals()+0) ) ;
            regISA.add(new mov_s32(instruction, instruction.getPreStackBaseOnLocals()+3, instruction.getPreStackBaseOnLocals()+1) ) ;
              break;
         case DUP2_X1:
            regISA.add(new nyi(instruction));
            break;
         case DUP2_X2:
            regISA.add(new nyi(instruction));
            break;
         case SWAP:
            regISA.add(new nyi(instruction));
            break;
         case IADD:
             regISA.add(new add_s32(instruction,instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals()+1));

             break;
         case LADD:
            regISA.add(new add_s64(instruction,instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals()+1));
    break;
         case FADD:
            regISA.add(new add_f32(instruction,instruction.getPreStackBaseOnLocals(),instruction.getPreStackBaseOnLocals(),instruction.getPreStackBaseOnLocals()+1));
    break;
         case DADD:
            regISA.add(new nyi(instruction));
            break;
         case ISUB:
            regISA.add(new sub_s32(instruction,instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals()+1));

             break;
         case LSUB:
            regISA.add(new nyi(instruction));
            break;
         case FSUB:
            regISA.add(new sub_f32(instruction,instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals()+1));
     break;
         case DSUB:
            regISA.add(new nyi(instruction));
            break;
         case IMUL:
            regISA.add(new mul_s32(instruction,instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals()+1));

              break;
         case LMUL:
            regISA.add(new nyi(instruction));
            break;
         case FMUL:
            regISA.add(new mul_f32(instruction,instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals()+1));

               break;
         case DMUL:
            regISA.add(new nyi(instruction));
            break;
         case IDIV:
            regISA.add(new div_s32(instruction,instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals()+1));

            break;
         case LDIV:
            regISA.add(new nyi(instruction));
            break;
         case FDIV:
            regISA.add(new div_f32(instruction,instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals()+1));

              break;
         case DDIV:
            regISA.add(new nyi(instruction));
            break;
         case IREM:
            regISA.add(new rem_s32(instruction,instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals(), instruction.getPreStackBaseOnLocals()+1));

              break;
         case LREM:
            regISA.add(new nyi(instruction));
            break;
         case FREM:
            regISA.add(new nyi(instruction));
            break;
         case DREM:
            regISA.add(new nyi(instruction));
            break;
         case INEG:
            regISA.add(new nyi(instruction));
            break;
         case LNEG:
            regISA.add(new nyi(instruction));
            break;
         case FNEG:
            regISA.add(new nyi(instruction));
            break;
         case DNEG:
            regISA.add(new nyi(instruction));
            break;
         case ISHL:
            regISA.add(new nyi(instruction));
            break;
         case LSHL:
            regISA.add(new nyi(instruction));
            break;
         case ISHR:
            regISA.add(new nyi(instruction));
            break;
         case LSHR:
            regISA.add(new nyi(instruction));
            break;
         case IUSHR:
            regISA.add(new nyi(instruction));
            break;
         case LUSHR:
            regISA.add(new nyi(instruction));
            break;
         case IAND:
            regISA.add(new nyi(instruction));
            break;
         case LAND:
            regISA.add(new nyi(instruction));
            break;
         case IOR:
            regISA.add(new nyi(instruction));
            break;
         case LOR:
            regISA.add(new nyi(instruction));
            break;
         case IXOR:
            regISA.add(new nyi(instruction));
            break;
         case LXOR:
            regISA.add(new nyi(instruction));
            break;
         case IINC:
            regISA.add(new add_const_s32(instruction, ((InstructionSet.I_IINC)instruction).getLocalVariableTableIndex(),((InstructionSet.I_IINC)instruction).getLocalVariableTableIndex(), ((InstructionSet.I_IINC)instruction).getDelta()));

             break;
         case I2L:
            regISA.add(new nyi(instruction));
            break;
         case I2F:
            regISA.add(new cvt_s32_f32(instruction, instruction.getPreStackBaseOnLocals(),instruction.getPreStackBaseOnLocals()));

            break;
         case I2D:
            regISA.add(new nyi(instruction));
            break;
         case L2I:
            regISA.add(new nyi(instruction));
            break;
         case L2F:
            regISA.add(new nyi(instruction));
            break;
         case L2D:
            regISA.add(new nyi(instruction));
            break;
         case F2I:
            regISA.add(new nyi(instruction));
            break;
         case F2L:
            regISA.add(new nyi(instruction));
            break;
         case F2D:
            regISA.add(new nyi(instruction));
            break;
         case D2I:
            regISA.add(new nyi(instruction));
            break;
         case D2L:
            regISA.add(new nyi(instruction));
            break;
         case D2F:
            regISA.add(new nyi(instruction));
            break;
         case I2B:
            regISA.add(new nyi(instruction));
            break;
         case I2C:
            regISA.add(new nyi(instruction));
            break;
         case I2S:
            regISA.add(new nyi(instruction));
            break;
         case LCMP:
            regISA.add(new nyi(instruction));
            break;
         case FCMPL:
            regISA.add(new nyi(instruction));
            break;
         case FCMPG:
            regISA.add(new nyi(instruction));
            break;
         case DCMPL:
            regISA.add(new nyi(instruction));
            break;
         case DCMPG:
            regISA.add(new nyi(instruction));
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
            regISA.add(new branch(instruction, instruction.getByteCode().getName(), instruction.asBranch().getAbsolute()));
            break;
         case JSR:
            regISA.add(new nyi(instruction));
            break;
         case RET:
            regISA.add(new nyi(instruction));
            break;
         case TABLESWITCH:
            regISA.add(new nyi(instruction));
            break;
         case LOOKUPSWITCH:
            regISA.add(new nyi(instruction));
            break;
         case IRETURN:
            regISA.add(new nyi(instruction));
            break;
         case LRETURN:
            regISA.add(new nyi(instruction));
            break;
         case FRETURN:
            regISA.add(new nyi(instruction));
            break;
         case DRETURN:
            regISA.add(new nyi(instruction));
            break;
         case ARETURN:
            regISA.add(new nyi(instruction));
            break;
         case RETURN:
            regISA.add(new nyi(instruction));
            break;
         case GETSTATIC:
         case PUTSTATIC:
         case GETFIELD:
         case PUTFIELD:
            regISA.add(new field(instruction));
            break;
         case INVOKEVIRTUAL:
         case INVOKESPECIAL:
         case INVOKESTATIC:
         case INVOKEINTERFACE:
         case INVOKEDYNAMIC:
            regISA.add(new call(instruction));
            break;
         case NEW:
            regISA.add(new nyi(instruction));
            break;
         case NEWARRAY:
            regISA.add(new nyi(instruction));
            break;
         case ANEWARRAY:
            regISA.add(new nyi(instruction));
            break;
         case ARRAYLENGTH:
            regISA.add(new nyi(instruction));
            break;
         case ATHROW:
            regISA.add(new nyi(instruction));
            break;
         case CHECKCAST:
            regISA.add(new nyi(instruction));
            break;
         case INSTANCEOF:
            regISA.add(new nyi(instruction));
            break;
         case MONITORENTER:
            regISA.add(new nyi(instruction));
            break;
         case MONITOREXIT:
            regISA.add(new nyi(instruction));
            break;
         case WIDE:
            regISA.add(new nyi(instruction));
            break;
         case MULTIANEWARRAY:
            regISA.add(new nyi(instruction));
            break;
         case JSR_W:
            regISA.add(new nyi(instruction));
            break;

      }

   }



















}
