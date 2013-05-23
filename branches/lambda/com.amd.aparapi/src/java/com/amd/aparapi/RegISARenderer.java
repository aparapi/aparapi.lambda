package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/27/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegISARenderer extends TextRenderer<RegISARenderer>{



   public RegISARenderer label(int _pc){
      return (append(String.format("@L%d", _pc)));
   }

   public RegISARenderer array_base_offset(){

      return (append(UnsafeWrapper.arrayBaseOffset(int[].class)));
   }

   public RegISARenderer separator(){
      return (commaSpace());
   }

   public RegISARenderer typeName(RegISA.Reg _reg){
      return (this.append(_reg.type.getHSAName()));
   }
   public RegISARenderer movTypeName(RegISA.Reg _reg){
      return (this.append("b"+_reg.type.getHsaBits()));
   }

   public RegISARenderer regName(RegISA.Reg _reg){
      switch(_reg.type.getHsaBits()){
         case 32:
            append("$s");
            break;
         case 64:
            append("$d");
            break;
         default:
            append("$?");
            break;
      }
      return (this.append(_reg.index));
   }

   public RegISARenderer i(Instruction from){

      mark().append(from.getByteCode().getName()).relpad(8);//InstructionHelper.getLabel(i, false, false, false);

      if(from.isBranch()){
         append(" " + from.asBranch().getAbsolute());
      }else if(from.isFieldAccessor()){
         append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getType().getSignature());
         append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());
         append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getName());
      }else if(from.isLocalVariableAccessor()){
         append(" var#" + from.asLocalVariableAccessor().getLocalVariableInfo().getSlot());

         ClassModel.AttributePool.LocalVariableTableEntry.LocalVariableInfo lvi = from.asLocalVariableAccessor().getLocalVariableInfo();
         append("(" + lvi.getVariableName());
         if(lvi.isArg()){

            ClassModel.AttributePool.LocalVariableTableEntry.ArgLocalVariableInfo alvi = lvi.asArgLocalVariableInfo();
            append(" " + alvi.getRealType().getSignature());
         }else{
            InstructionSet.TypeSpec typeSpec = from.asLocalVariableAccessor().getLocalVariableInfo().getTypeSpec();
            append(" ");

            if(typeSpec.getPrimitiveType().equals(PrimitiveType.ref)){
               append(typeSpec.getPrimitiveType().getJavaTypeName());
            }else{
               append("call to type.toString()");
            }

         }
         append(")");

      }else if(from.isMethodCall()){
         append(" " + from.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType().getReturnType().getSignature());
         append(" " + from.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName());
         append("." + from.asMethodCall().getConstantPoolMethodEntry().getName());
      }else if(from.isConstant()){
         append("." + from.asConstant().getValue());
      }
      return (this);
   }
}