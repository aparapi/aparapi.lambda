package com.amd.aparapi;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/16/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegIsaWriter{
   private static final boolean regtop = true;
   ClassModel.ClassModelMethod method;
   int maxLocals;
   int maxStack;

   static abstract class RegInstruction{
       Instruction from;
       RegInstruction(Instruction _from){
          from = _from;
       }
   }

   static abstract class ld_kernarg extends RegInstruction{
      int reg, argc;

      ld_kernarg(Instruction _from){
         super(_from);
      }
   }

   static class ld_kernarg_u64 extends ld_kernarg{

      ld_kernarg_u64(Instruction _from){
         super(_from);
      }
   }

   static class ld_kernarg_s32 extends ld_kernarg{

      ld_kernarg_s32(Instruction _from){
         super(_from);
      }
   }

   static class add_const_s32 extends RegInstruction{
      int reg_dest, reg_src, value;
      add_const_s32(Instruction _from, int _reg_dest, int _reg_src, int _value){
         super(_from);
         reg_dest=_reg_dest;
         reg_src = _reg_src;
         value = _value;
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
   }
   static class load_f32 extends load{

      load_f32(Instruction _from, int _arrayAndValue, int _index){
         super(_from, _arrayAndValue, _index);
      }
   }
   static class load_s64 extends load{

      load_s64(Instruction _from, int _arrayAndValue, int _index){
         super(_from, _arrayAndValue, _index);
      }
   }

   static class cvt_s32_f32 extends cvt{
      cvt_s32_f32(Instruction _from, int _reg_dest, int _reg_src){
         super(_from, _reg_dest, _reg_src);
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
   static  class add_s32 extends add{

      public add_s32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
   }
   static  class add_s64 extends add{

      public add_s64(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
   }
   static  class add_f32 extends add{

      public add_f32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
      }
   }
   static  class add_f64 extends add{

      public add_f64(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
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
   }
   static class sub_f32 extends sub{

      public sub_f32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
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
   }
   static class div_f32 extends div{

      public div_f32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
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
   }
   static class mul_f32 extends mul{

      public mul_f32(Instruction _from,int _reg_dest, int _reg_lhs, int _reg_rhs){
         super(_from,_reg_dest, _reg_lhs, _reg_rhs);
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

      public mov_s32_const(Instruction _from,int _reg_dest, int _reg_src){
         super(_from, _reg_dest, _reg_src);
      }
   }
   static class mov_s64_const extends mov_const<Long>{

      public mov_s64_const(Instruction _from,int _reg_dest, long _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
   }

   static class mov_f32_const extends mov_const<Float>{

      public mov_f32_const(Instruction _from,int _reg_dest, float _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
   }

   static class mov_f64_const extends mov_const<Double>{

      public mov_f64_const(Instruction _from,int _reg_dest, double _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
   }
   static class mov_s32 extends mov{

      public mov_s32(Instruction _from,int _reg_dest, int _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
   }

   static class mov_s64 extends mov{

      public mov_s64(Instruction _from,int _reg_dest, int _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
   }
   static class mov_f64 extends mov{
      public mov_f64(Instruction _from,int _reg_dest, int _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
   }
   static class mov_f32 extends mov{
      public mov_f32(Instruction _from,int _reg_dest, int _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
   }
   static class mov_u64 extends mov{
      public mov_u64(Instruction _from,int _reg_dest, int _reg_src){
         super(_from,_reg_dest, _reg_src);
      }
   }

   RegIsaWriter(ClassModel.ClassModelMethod _method, int _maxLocals, int _maxStack){
      method = _method;
      maxLocals = _maxLocals;
      maxStack = _maxStack;
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

   String indent = "    ";

   void writeInstruction(List<RegInstruction> _regInstructions,Instruction i ){

           if (i.isBranchTarget()){
               System.out.println(label(i.getThisPC()));
           }
           System.out.println(indent+render( _regInstructions, i));

   }
   void writePrologue(List<RegInstruction> _regInstructions){
       System.out.println("version 1:0");
       System.out.print("kernel &"+method.getName()+"(");
       int argOffset = method.isStatic()?0:1;
       if (!method.isStatic()){
           System.out.print("\n"+indent+"kernarg_u64 %_arg0");
       }

       for (TypeHelper.Arg arg:method.argsAndReturnType.getArgs()){
           if ((method.isStatic() && arg.getArgc()==0) ){
               System.out.println();
           }           else{
               System.out.println(",");
           }
           System.out.print(indent+"kernarg_"+argType(arg)+" %_arg"+(arg.getArgc()+argOffset));

       }
       System.out.println("\n"+indent+"){");
       if (!method.isStatic()){
           System.out.println(indent+"ld_kernarg_u64 $d"+reg(0)+", [%_arg0];");
       }
       for (TypeHelper.Arg arg:method.argsAndReturnType.getArgs()){
           System.out.println(indent+"ld_kernarg_"+argType(arg)+" "+"$"+regType(arg)+reg(arg.getArgc()+argOffset)+", [%_arg"+(arg.getArgc()+argOffset)+"];");
       }

   }
   void writeEpilogue(List<RegInstruction> _regInstructions){
       System.out.println("};");
   }
   void write(){

      List<RegInstruction> regInstructions = new ArrayList<RegInstruction>();
      System.out.println(InstructionHelper.getJavapView(method));


      System.out.println("MaxLocals=" + maxLocals);
      System.out.println("MaxStack=" + maxStack);
      Table table = new Table("|%2d ", "|%2d", "|%2d", "|%s", "|%d", "|%d","|%d", "|%-60s", "|%s");
      table.header("|PC ", "|Depth", "|Block", "|Consumes + count", "|Produces", "|PreStackBase","|PostStackBase", "|Instruction", "|Branches");

      for(Instruction i : method.getInstructions()){
         String label = render( regInstructions, i);

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
          table.data(i.getPreStackBase());
         table.data(i.getPostStackBase());
         table.data(label);
         table.data(sb + (i.isEndOfTernary() ? "*" : ""));
      }
       System.out.println("{\n" + table.toString() + "}\n");
      regInstructions.clear();
      writePrologue(regInstructions);
       for(Instruction i : method.getInstructions()){
      writeInstruction(regInstructions, i);
       }
      writeEpilogue(regInstructions);
   }

   private String render( List<RegInstruction> regInstructions, Instruction instruction){
      String returnString = null;
      switch(instruction.getByteCode()){

         case ACONST_NULL:
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
          regInstructions.add(new mov_s32_const(instruction, stack(instruction.getPreStackBase()), c.getValue()));
          returnString = mov_s32_const(stack(instruction.getPreStackBase()), c.getValue());
          }
          break;
         case LCONST_0:
         case LCONST_1:
         {
             InstructionSet.Constant<Long> c = ( InstructionSet.Constant) instruction;
            regInstructions.add(new mov_s64_const(instruction,stack(instruction.getPreStackBase()), c.getValue()));
             returnString = mov_s64_const(stack(instruction.getPreStackBase()), c.getValue());
         }
            break;
         case FCONST_0:
         case FCONST_1:
          case FCONST_2:
          {
             InstructionSet.Constant<Float> c = ( InstructionSet.Constant) instruction;
             regInstructions.add(new mov_f32_const(instruction,stack(instruction.getPreStackBase()), c.getValue()));
             returnString = mov_f32_const(stack(instruction.getPreStackBase()), c.getValue());
         }

            break;
         case DCONST_0:
         case DCONST_1:
         {
             InstructionSet.Constant<Double> c = ( InstructionSet.Constant) instruction;
            regInstructions.add(new mov_f64_const(instruction,stack(instruction.getPreStackBase()), c.getValue()));
             returnString = mov_f64_const(stack(instruction.getPreStackBase()), c.getValue());

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
                 returnString = mov_f64_const(stack(instruction.getPreStackBase()),  ((ClassModel.ConstantPool.DoubleEntry)e).getValue());
                regInstructions.add(new mov_f64_const(instruction,stack(instruction.getPreStackBase()), ((ClassModel.ConstantPool.DoubleEntry)e).getValue()));
             } else if (e instanceof ClassModel.ConstantPool.FloatEntry){
                 returnString = mov_f32_const(stack(instruction.getPreStackBase()),  ((ClassModel.ConstantPool.FloatEntry)e).getValue());
                regInstructions.add(new mov_f32_const(instruction,stack(instruction.getPreStackBase()), ((ClassModel.ConstantPool.FloatEntry)e).getValue()));

             }  else if (e instanceof ClassModel.ConstantPool.IntegerEntry){
                 returnString = mov_s32_const(stack(instruction.getPreStackBase()),  ((ClassModel.ConstantPool.IntegerEntry)e).getValue());
                regInstructions.add(new mov_s32_const(instruction,stack(instruction.getPreStackBase()), ((ClassModel.ConstantPool.IntegerEntry)e).getValue()));
             }  else if (e instanceof ClassModel.ConstantPool.LongEntry){
                 returnString = mov_s64_const(stack(instruction.getPreStackBase()),  ((ClassModel.ConstantPool.LongEntry)e).getValue());
                regInstructions.add(new mov_s64_const(instruction,stack(instruction.getPreStackBase()), ((ClassModel.ConstantPool.LongEntry)e).getValue()));
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
            regInstructions.add(new mov_s32(instruction,stack(instruction.getPreStackBase()),
                  reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex())));

            returnString = mov_s32(
                  stack(instruction.getPreStackBase()),
                  reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));
            break;
         case LLOAD:
         case LLOAD_0:
         case LLOAD_1:
         case LLOAD_2:
         case LLOAD_3:
            regInstructions.add(new mov_s64(instruction,stack(instruction.getPreStackBase()),
                  reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex())));
             returnString = mov_s64(
                     stack(instruction.getPreStackBase()),
                     reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));
             break;
          case FLOAD:
         case FLOAD_0:
         case FLOAD_1:
         case FLOAD_2:
         case FLOAD_3:
            regInstructions.add(new mov_f32(instruction,stack(instruction.getPreStackBase()),
                  reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex())));
             returnString = mov_f32(
                     stack(instruction.getPreStackBase()),
                     reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));
             break;
          case DLOAD:
         case DLOAD_0:
         case DLOAD_1:
         case DLOAD_2:
         case DLOAD_3:
            regInstructions.add(new mov_f64(instruction,stack(instruction.getPreStackBase()),
                  reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex())));
             returnString = mov_f64(
                     stack(instruction.getPreStackBase()),
                     reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));
            break;
         case ALOAD:
         case ALOAD_0:
         case ALOAD_1:
         case ALOAD_2:
         case ALOAD_3:
            regInstructions.add(new mov_u64(instruction,stack(instruction.getPreStackBase()),
                  reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex())));
             returnString = mov_u64(
                     stack(instruction.getPreStackBase()),
                     reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));
            break;
         case IALOAD:   //arraref, index -> value
            regInstructions.add(new load_s32( instruction,
                  stack(instruction.getPreStackBase() + 0),   //array & value
                  stack(instruction.getPreStackBase() + 1))); //index
            returnString = load_s32(
                  stack(instruction.getPreStackBase() + 0),   //array & value
                  stack(instruction.getPreStackBase() + 1));  //index
            break;
         case LALOAD:
            regInstructions.add(new load_s64( instruction,
                  stack(instruction.getPreStackBase() + 0),   //array & value
                  stack(instruction.getPreStackBase() + 1))); //index
             returnString = load_s64(
                     stack(instruction.getPreStackBase() + 0),   //array & value
                     stack(instruction.getPreStackBase() + 1));  //index
             break;
         case FALOAD:
            regInstructions.add(new load_f32( instruction,
                  stack(instruction.getPreStackBase() + 0),   //array & value
                  stack(instruction.getPreStackBase() + 1))); //index
             returnString = load_f32(
                     stack(instruction.getPreStackBase() + 0),   //array & value
                     stack(instruction.getPreStackBase() + 1));  //index
            break;
         case DALOAD:
            break;
         case AALOAD:
            break;
         case BALOAD:
            break;
         case CALOAD:
            break;
         case SALOAD:
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
            regInstructions.add(new mov_s32(instruction, reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()),
                  stack(instruction.getPreStackBase())));
             returnString = mov_s32(
                     reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()),
                     stack(instruction.getPreStackBase())
             );
            break;
          case LSTORE:
         case LSTORE_0:
         case LSTORE_1:
         case LSTORE_2:
         case LSTORE_3:
            regInstructions.add(new mov_s64(instruction, reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()),
                  stack(instruction.getPreStackBase())));
             returnString = mov_s64(
                     reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()),
                     stack(instruction.getPreStackBase())
             );
            break;
          case FSTORE:
          case FSTORE_0:
          case FSTORE_1:
          case FSTORE_2:
          case FSTORE_3:
             regInstructions.add(new mov_f32(instruction, reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()),
                   stack(instruction.getPreStackBase())));
              returnString = mov_f32(
                      reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()),
                      stack(instruction.getPreStackBase())
              );
              break;
          case DSTORE:
         case DSTORE_0:
         case DSTORE_1:
         case DSTORE_2:
         case DSTORE_3:
            regInstructions.add(new mov_f64(instruction, reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()),
                  stack(instruction.getPreStackBase())));
             returnString = mov_f64(
                     reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()),
                     stack(instruction.getPreStackBase())
             );
            break;
          case ASTORE:
         case ASTORE_0:
         case ASTORE_1:
         case ASTORE_2:
         case ASTORE_3:
            regInstructions.add(new mov_u64(instruction, reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()),
                  stack(instruction.getPreStackBase())));
            returnString = mov_u64(
                  reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()),
                  stack(instruction.getPreStackBase())
            );
             break;
         case IASTORE:
            regInstructions.add(new store_s32(instruction,
                  stack(instruction.getPreStackBase() + 2), //value
                  stack(instruction.getPreStackBase() + 1), //index
                  stack(instruction.getPreStackBase() + 0)));//array
            returnString = store_s32(
                  stack(instruction.getPreStackBase() + 2), //value
                  stack(instruction.getPreStackBase() + 1), //index
                  stack(instruction.getPreStackBase() + 0));//array
            break;
         case LASTORE:
            break;
         case FASTORE:
            break;
         case DASTORE:
            break;
         case AASTORE:
            break;
         case BASTORE:
            break;
         case CASTORE:
            break;
         case SASTORE:
            break;
         case POP:
            break;
         case POP2:
            break;
         case DUP:
            break;
         case DUP_X1:
            break;
         case DUP_X2:
             returnString = mov_s32(stack(instruction.getPreStackBase()+3), stack(instruction.getPreStackBase()+2) )
                    + "\n"+indent+mov_s32(stack(instruction.getPreStackBase()+2), stack(instruction.getPreStackBase()+1))
                     + "\n"+indent+mov_s32(stack(instruction.getPreStackBase()+1), stack(instruction.getPreStackBase()+0))
                     + "\n"+indent+mov_s32(stack(instruction.getPreStackBase()+0), stack(instruction.getPreStackBase()+3)) ;


             break;
         case DUP2:
             returnString = mov_s32(stack(instruction.getPreStackBase()+2), stack(instruction.getPreStackBase())) +
                     "\n"+indent+mov_s32(stack(instruction.getPreStackBase()+3), stack(instruction.getPreStackBase()+1));
            break;
         case DUP2_X1:
            break;
         case DUP2_X2:
            break;
         case SWAP:
            break;
         case IADD:
             regInstructions.add(new add_s32(instruction,stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()+1)));
             returnString = "add_s32 "+f32Name(stack(instruction.getPreStackBase()))+separator()+ s32Name(stack(instruction.getPreStackBase()))+separator()+s32Name(stack(instruction.getPreStackBase()+1));

             break;
         case LADD:
            regInstructions.add(new add_s64(instruction,stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()+1)));

            returnString = "add_s64 "+s64Name(stack(instruction.getPreStackBase()))+separator()+ s64Name(stack(instruction.getPreStackBase()))+separator()+s64Name(stack(instruction.getPreStackBase() + 1));

            break;
         case FADD:
            regInstructions.add(new add_f32(instruction,stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()+1)));

            returnString = "add_f32 "+f32Name(stack(instruction.getPreStackBase()))+separator()+ f32Name(stack(instruction.getPreStackBase()))+separator()+f32Name(stack(instruction.getPreStackBase()+1));

             break;
         case DADD:
            break;
         case ISUB:
            regInstructions.add(new sub_s32(instruction,stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()+1)));

            returnString = "sub_s32 "+s32Name(stack(instruction.getPreStackBase()))+separator()+ s32Name(stack(instruction.getPreStackBase()))+separator()+s32Name(stack(instruction.getPreStackBase()+1));

            break;
         case LSUB:
            break;
         case FSUB:
            regInstructions.add(new sub_f32(instruction,stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()+1)));

            returnString = "sub_f32 "+f32Name(stack(instruction.getPreStackBase()))+separator()+ f32Name(stack(instruction.getPreStackBase()))+separator()+f32Name(stack(instruction.getPreStackBase()+1));

             break;
         case DSUB:
            break;
         case IMUL:
            regInstructions.add(new mul_s32(instruction,stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()+1)));

            returnString = "mul_s32 "+s32Name(stack(instruction.getPreStackBase()))+separator()+ s32Name(stack(instruction.getPreStackBase()))+separator()+s32Name(stack(instruction.getPreStackBase()+1));
            break;
         case LMUL:
            break;
         case FMUL:
            regInstructions.add(new mul_f32(instruction,stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()+1)));

            returnString = "mul_f32 "+f32Name(stack(instruction.getPreStackBase()))+separator()+ f32Name(stack(instruction.getPreStackBase()))+separator()+f32Name(stack(instruction.getPreStackBase()+1));

             break;
         case DMUL:
            break;
         case IDIV:
            regInstructions.add(new div_s32(instruction,stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()+1)));

            returnString = "div_s32 "+s32Name(stack(instruction.getPreStackBase()))+separator()+ s32Name(stack(instruction.getPreStackBase()))+separator()+s32Name(stack(instruction.getPreStackBase()+1));

             break;
         case LDIV:
            break;
         case FDIV:
            regInstructions.add(new div_f32(instruction,stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()+1)));

            returnString = "div_f32 "+f32Name(stack(instruction.getPreStackBase()))+separator()+ f32Name(stack(instruction.getPreStackBase()))+separator()+f32Name(stack(instruction.getPreStackBase()+1));

             break;
         case DDIV:
            break;
         case IREM:
            regInstructions.add(new rem_s32(instruction,stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()), stack(instruction.getPreStackBase()+1)));

            returnString = "rem_s32 "+s32Name(stack(instruction.getPreStackBase()))+separator()+ s32Name(stack(instruction.getPreStackBase()))+separator()+s32Name(stack(instruction.getPreStackBase()+1));

             break;
         case LREM:
            break;
         case FREM:
            break;
         case DREM:
            break;
         case INEG:
            break;
         case LNEG:
            break;
         case FNEG:
            break;
         case DNEG:
            break;
         case ISHL:
            break;
         case LSHL:
            break;
         case ISHR:
            break;
         case LSHR:
            break;
         case IUSHR:
            break;
         case LUSHR:
            break;
         case IAND:
            break;
         case LAND:
            break;
         case IOR:
            break;
         case LOR:
            break;
         case IXOR:
            break;
         case LXOR:
            break;
         case IINC:
            regInstructions.add(new add_const_s32(instruction, reg(((InstructionSet.I_IINC)instruction).getLocalVariableTableIndex()),reg(((InstructionSet.I_IINC)instruction).getLocalVariableTableIndex()), ((InstructionSet.I_IINC)instruction).getDelta()));

            returnString = "add_s32 "+s32Name(reg(((InstructionSet.I_IINC)instruction).getLocalVariableTableIndex()))+separator()+ s32Name(reg(((InstructionSet.I_IINC)instruction).getLocalVariableTableIndex()))+separator()+ ((InstructionSet.I_IINC)instruction).getDelta();
            break;
         case I2L:
            break;
         case I2F:
            regInstructions.add(new cvt_s32_f32(instruction, stack(instruction.getPreStackBase()),stack(instruction.getPreStackBase())));

            returnString="cvt_s32_f32 "+s32Name(stack(instruction.getPreStackBase()))+separator()+f32Name(stack(instruction.getPreStackBase()));
            break;
         case I2D:
            break;
         case L2I:
            break;
         case L2F:
            break;
         case L2D:
            break;
         case F2I:
            break;
         case F2L:
            break;
         case F2D:
            break;
         case D2I:
            break;
         case D2L:
            break;
         case D2F:
            break;
         case I2B:
            break;
         case I2C:
            break;
         case I2S:
            break;
         case LCMP:
            break;
         case FCMPL:
            break;
         case FCMPG:
            break;
         case DCMPL:
            break;
         case DCMPG:
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
            returnString = branch(instruction.getByteCode().getName(), instruction.asBranch().getAbsolute());
            break;
         case JSR:
            break;
         case RET:
            break;
         case TABLESWITCH:
            break;
         case LOOKUPSWITCH:
            break;
         case IRETURN:
            break;
         case LRETURN:
            break;
         case FRETURN:
            break;
         case DRETURN:
            break;
         case ARETURN:
            break;
         case RETURN:
            break;
         case GETSTATIC:
         case PUTSTATIC:
         case GETFIELD:
         case PUTFIELD:
            returnString = field(instruction);
            break;
         case INVOKEVIRTUAL:
         case INVOKESPECIAL:
         case INVOKESTATIC:
         case INVOKEINTERFACE:
         case INVOKEDYNAMIC:
            returnString = call(instruction);
            break;
         case NEW:
            break;
         case NEWARRAY:
            break;
         case ANEWARRAY:
            break;
         case ARRAYLENGTH:
            break;
         case ATHROW:
            break;
         case CHECKCAST:
            break;
         case INSTANCEOF:
            break;
         case MONITORENTER:
            break;
         case MONITOREXIT:
            break;
         case WIDE:
            break;
         case MULTIANEWARRAY:
            break;
         case IFNULL:
             returnString = branch(instruction.getByteCode().getName(), instruction.asBranch().getAbsolute());
            break;
         case IFNONNULL:
             returnString = branch(instruction.getByteCode().getName(), instruction.asBranch().getAbsolute());
            break;
         case GOTO_W:
             returnString = branch(instruction.getByteCode().getName(), instruction.asBranch().getAbsolute());
            break;
         case JSR_W:
            break;

      }
      if(returnString == null){
         String label = instruction.getByteCode().getName();//InstructionHelper.getLabel(i, false, false, false);
         if(instruction.isBranch()){
            label += " " + instruction.asBranch().getAbsolute();
         }else if(instruction.isFieldAccessor()){
            label += " " + instruction.asFieldAccessor().getConstantPoolFieldEntry().getType();
            label += " " + instruction.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName();
            label += "." + instruction.asFieldAccessor().getConstantPoolFieldEntry().getName();
         }else if(instruction.isLocalVariableAccessor()){
            label += " #" + instruction.asLocalVariableAccessor().getLocalVariableInfo().getSlot();
            label += " " + instruction.asLocalVariableAccessor().getLocalVariableInfo().getVariableName();
            label += " " + instruction.asLocalVariableAccessor().getLocalVariableInfo().getVariableDescriptor();

         }else if(instruction.isMethodCall()){
            label += " " + instruction.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType().getReturnType();
            label += " " + instruction.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName();
            label += "." + instruction.asMethodCall().getConstantPoolMethodEntry().getName();
         }else if(instruction.isConstant()){
            InstructionSet.Constant c = ((InstructionSet.Constant) instruction);
            label += " " + instruction.asConstant().getValue();
         }
         returnString = label;
      }
      return (returnString);
   }

   private String field(Instruction _i){
      StringBuilder sb = new StringBuilder("field_");
      TypeHelper.Type type = _i.asFieldAccessor().getConstantPoolFieldEntry().getType();
      String dotClassName = _i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName();
      String name = _i.asFieldAccessor().getConstantPoolFieldEntry().getName();
      if(_i instanceof InstructionSet.I_PUTFIELD || _i instanceof InstructionSet.I_PUTSTATIC){
          if(type.isArray()){
              sb.append("arr_");
          }
         if(type.isInt()){
            sb.append("s32 " + dotClassName + "." + name + separator() + s32Name(stack(_i.getPreStackBase())));
         }
          if(type.isFloat()){
              sb.append("f32 " + dotClassName + "." + name + separator() + f32Name(stack(_i.getPreStackBase())));
          }
      }else{
         if(type.isArray()){
            sb.append("arr_");
         }
         if(type.isInt()){
            sb.append("s32 " + ((type.isArray())?"arr_":"")+s32Name(stack(_i.getPreStackBase())));
         }
          if(type.isFloat()){
              sb.append("f32 " + ((type.isArray())?"arr_":"")+f32Name(stack(_i.getPreStackBase())));
          }
         sb.append(separator() + u64Name(stack(_i.getPreStackBase())));
         sb.append(separator() + dotClassName + "." + name);
      }
      return (sb.toString());
   }

   private String call(Instruction _i){
      String dotClassName = _i.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName();
      String name = _i.asMethodCall().getConstantPoolMethodEntry().getName();
      TypeHelper.ArgsAndReturnType argsAndReturnType = _i.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType();


      TypeHelper.Type returnType = argsAndReturnType.getReturnType();
      StringBuilder sb = new StringBuilder();

      if(returnType.isVoid()){
         sb.append("call_void VOID" + separator() + dotClassName + "." + name + " " + sb);
      }else if(returnType.isInt()){
         sb.append("call_s32 " + s32Name(stack(_i.getPreStackBase())) + separator() + dotClassName + "." + name + " " + sb);
      }else if(returnType.isDouble()){
         sb.append("call_f64 " + f64Name(stack(_i.getPreStackBase())) + separator() + dotClassName + "." + name + " " + sb);

      }
      for(TypeHelper.Arg arg : argsAndReturnType.getArgs()){
         if(arg.getArgc() > 0){
            sb.append(", ");
         }
         if(arg.isDouble()){
            sb.append(f64Name(stack(_i.getPreStackBase() + arg.getArgc())));
         }else if(arg.isFloat()){
            sb.append(f32Name(stack(_i.getPreStackBase() + arg.getArgc())));
         }else if(arg.isInt()){
            sb.append(s32Name(stack(_i.getPreStackBase() + arg.getArgc())));
         }else if(arg.isLong()){
            sb.append(s64Name(stack(_i.getPreStackBase() + arg.getArgc())));
         }
      }
      return (sb.toString());
   }

   private String mov(){
      return ("mov_");
   }

   private String load(){
      return ("ld_global_");
   }

   private String store(){
      return ("st_global_");
   }

   private String mov_s32(int _dest, int _source){
      return (mov() + "s32 " + s32Name(_dest) + separator() + s32Name(_source));
   }
    private String mov_f32(int _dest, int _source){
        return (mov() + "f32 " + f32Name(_dest) + separator() + f32Name(_source));
    }
    private String mov_s32_const(int _dest, int _const){
        return (mov() + "s32 " + s32Name(_dest) + separator() + _const);
    }
    private String mov_f32_const(int _dest, float _const){
        return (mov() + "f32 " + f32Name(_dest) + separator() + _const);
    }
    private String mov_f64_const(int _dest, double _const){
        return (mov() + "f64 " + f64Name(_dest) + separator() + _const);
    }
    private String mov_s64_const(int _dest, long _const){
        return (mov() + "s64 " + s64Name(_dest) + separator() + _const);
    }
    private String mov_s64(int _dest, int _source){
        return (mov() + "s64 " + s64Name(_dest) + separator() + s64Name(_source));
    }

    private String mov_f64(int _dest, int _source){
        return (mov() + "f64 " + f64Name(_dest) + separator() + f64Name(_source));
    }

    private String mov_u64(int _dest, int _source){
        return (mov() + "u64 " + u64Name(_dest) + separator() + u64Name(_source));
    }

   private String load_s32(int _arrayAndValue, int _index){
      return (load() + "s32 " + s32Name(_arrayAndValue) + separator() + s32Array(_arrayAndValue, _index));
   }

   private String store_s32(int _value, int _index, int _array){
      return (store() + "s32 " + s32Array(_array, _index) + separator() + s32Name(_value));
   }

   private String load_s64(int _arrayAndValue, int _index){
      return (load() + "s64 " + s64Name(_arrayAndValue) + separator() + s64Array(_arrayAndValue, _index));
   }

    private String load_f64(int _arrayAndValue, int _index){
        return (load() + "f64 " + f64Name(_arrayAndValue) + separator() + f64Array(_arrayAndValue, _index));
    }

    private String load_f32(int _arrayAndValue, int _index){
        return (load() + "f32 " + f32Name(_arrayAndValue) + separator() + f32Array(_arrayAndValue, _index));
    }

   private String s32Array(int arr_reg, int index){
      return ("["+u64Name(arr_reg) + " + " + array_len_offset() + " + " + sizeof_s32() + " * " + s32Name(index) + "]");
   }

   private String s64Array(int arr_reg, int index){
      return ("["+u64Name(arr_reg) + " + " + array_len_offset() + " + " + sizeof_s64() + " * " + s64Name(index) + "]");
   }

    private String f64Array(int arr_reg, int index){
        return ("["+u64Name(arr_reg) + " + " + array_len_offset() + " + " + sizeof_f64() + " * " + f64Name(index) + "]");
    }

    private String f32Array(int arr_reg, int index){
        return ("["+u64Name(arr_reg) + " + " + array_len_offset() + " + " + sizeof_f32() + " * " + f32Name(index) + "]");
    }

   private String regPrefix(){
      return("$");
   }

   private String s32Name(int reg){
      return (regPrefix()+"s" + regNum(reg));
   }

   private String s64Name(int reg){
      return (regPrefix()+"d" + regNum(reg));
   }
    private String u64Name(int reg){
        return (regPrefix()+"d" + regNum(reg));
    }

   private String f64Name(int reg){
      return (regPrefix()+"d" + regNum(reg));
   }

   private String f32Name(int reg){
      return (regPrefix()+"s" + regNum(reg));
   }


   private String array_len_offset(){
      return ("4");
   }

   private String sizeof_s32(){
      return ("4");
   }

   private String sizeof_s64(){
      return ("8");
   }

    private String sizeof_f32(){
        return ("4");
    }

    private String sizeof_f64(){
        return ("8");
    }

   private String regNum(int reg){
      return (String.format("%d", reg));
   }

   private int stack(int _reg){
      if(regtop){
         return (maxLocals + _reg);
      }else{
         return (_reg);
      }
   }

   private int reg(int _reg){
      if(regtop){
         return (_reg);
      }else{
         return (maxStack + _reg);
      }
   }


   private String branch(String type, int _pc){
       return(type+" "+label(_pc));
   }

   private String label(int _pc){
       return(String.format("@L%d", _pc));
   }
   private String separator(){
      return (", ");
   }
}
