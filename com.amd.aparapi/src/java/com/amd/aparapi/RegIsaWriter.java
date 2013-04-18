package com.amd.aparapi;

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

   RegIsaWriter(ClassModel.ClassModelMethod _method, int _maxLocals, int _maxStack){
      method = _method;
      maxLocals = _maxLocals;
      maxStack = _maxStack;
   }

   void write(){

      System.out.println("MaxLocals=" + maxLocals);
      System.out.println("MaxStack=" + maxStack);
      Table table = new Table("|%2d ", "|%s", "|%d", "|%d", "|%-60s", "|%s");
      table.header("|PC ", "|Consumes + count", "|Produces", "|Base", "|Instruction", "|Branches");

      for(Instruction i : method.getInstructions()){
         String label = render(i);

         StringBuilder consumes = new StringBuilder();
         for(int pc : i.getConsumeIndices()){
            consumes.append(pc).append(" ");
         }
         StringBuilder sb = new StringBuilder();
         for(InstructionHelper.BranchVector branchInfo : InstructionHelper.getBranches(method)){
            sb.append(branchInfo.render(i.getThisPC(), i.getStartPC()));
         }
         table.data(i.getThisPC());
         table.data("" + i.getStackConsumeCount() + " {" + consumes + "}");
         table.data(i.getStackProduceCount());
         table.data(i.getStackBase());
         table.data(label);
         table.data(sb + (i.isEndOfTernary() ? "*" : ""));
      }
      System.out.println("{\n" + table.toString() + "}\n");
   }

   private String render(Instruction instruction){
      String returnString = null;
      switch(instruction.getByteCode()){

         case ACONST_NULL:
            break;
         case ICONST_M1:
            break;
         case ICONST_0:
            break;
         case ICONST_1:
            break;
         case ICONST_2:
            break;
         case ICONST_3:
            break;
         case ICONST_4:
            break;
         case ICONST_5:
            break;
         case LCONST_0:
            break;
         case LCONST_1:
            break;
         case FCONST_0:
            break;
         case FCONST_1:
            break;
         case FCONST_2:
            break;
         case DCONST_0:
            break;
         case DCONST_1:
            break;
         case BIPUSH:
            break;
         case SIPUSH:
            break;
         case LDC:
            break;
         case LDC_W:
            break;
         case LDC2_W:
            break;

         case LLOAD:
            break;
         case FLOAD:
            break;
         case DLOAD:
            break;
         case ALOAD:
            break;
         case ILOAD:
         case ILOAD_0:
         case ILOAD_1:
         case ILOAD_2:
         case ILOAD_3:
            returnString = mov_s32(
                  stack(instruction.getStackBase()),
                  reg(instruction.asLocalVariableAccessor().getLocalVariableTableIndex()));
            break;
         case LLOAD_0:
            break;
         case LLOAD_1:
            break;
         case LLOAD_2:
            break;
         case LLOAD_3:
            break;
         case FLOAD_0:
            break;
         case FLOAD_1:
            break;
         case FLOAD_2:
            break;
         case FLOAD_3:
            break;
         case DLOAD_0:
            break;
         case DLOAD_1:
            break;
         case DLOAD_2:
            break;
         case DLOAD_3:
            break;
         case ALOAD_0:
            break;
         case ALOAD_1:
            break;
         case ALOAD_2:
            break;
         case ALOAD_3:
            break;
         case IALOAD:
            returnString = load_s32(
                  stack(instruction.getStackBase() + 0),   //index & value
                  stack(instruction.getStackBase() + 1));  //array
            break;
         case LALOAD:
            break;
         case FALOAD:
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
         case ISTORE:
            break;
         case LSTORE:
            break;
         case FSTORE:
            break;
         case DSTORE:
            break;
         case ASTORE:
            break;
         case ISTORE_0:
            break;
         case ISTORE_1:
            break;
         case ISTORE_2:
            break;
         case ISTORE_3:
            break;
         case LSTORE_0:
            break;
         case LSTORE_1:
            break;
         case LSTORE_2:
            break;
         case LSTORE_3:
            break;
         case FSTORE_0:
            break;
         case FSTORE_1:
            break;
         case FSTORE_2:
            break;
         case FSTORE_3:
            break;
         case DSTORE_0:
            break;
         case DSTORE_1:
            break;
         case DSTORE_2:
            break;
         case DSTORE_3:
            break;
         case ASTORE_0:
            break;
         case ASTORE_1:
            break;
         case ASTORE_2:
            break;
         case ASTORE_3:
            break;
         case IASTORE:
            returnString = store_s32(
                  stack(instruction.getStackBase() + 0), //value
                  stack(instruction.getStackBase() + 1), //index
                  stack(instruction.getStackBase() + 2));//array
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
            break;
         case DUP2:
            break;
         case DUP2_X1:
            break;
         case DUP2_X2:
            break;
         case SWAP:
            break;
         case IADD:
            break;
         case LADD:
            break;
         case FADD:
            break;
         case DADD:
            break;
         case ISUB:
            break;
         case LSUB:
            break;
         case FSUB:
            break;
         case DSUB:
            break;
         case IMUL:
            break;
         case LMUL:
            break;
         case FMUL:
            break;
         case DMUL:
            break;
         case IDIV:
            break;
         case LDIV:
            break;
         case FDIV:
            break;
         case DDIV:
            break;
         case IREM:
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
            break;
         case I2L:
            break;
         case I2F:
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
            break;
         case IFNE:
            break;
         case IFLT:
            break;
         case IFGE:
            break;
         case IFGT:
            break;
         case IFLE:
            break;
         case IF_ICMPEQ:
            break;
         case IF_ICMPNE:
            break;
         case IF_ICMPLT:
            break;
         case IF_ICMPGE:
            break;
         case IF_ICMPGT:
            break;
         case IF_ICMPLE:
            break;
         case IF_ACMPEQ:
            break;
         case IF_ACMPNE:
            break;
         case GOTO:
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
            break;
         case IFNONNULL:
            break;
         case GOTO_W:
            break;
         case JSR_W:
            break;
         case ILLEGAL_202:
            break;
         case ILLEGAL_203:
            break;
         case ILLEGAL_204:
            break;
         case ILLEGAL_205:
            break;
         case ILLEGAL_206:
            break;
         case ILLEGAL_207:
            break;
         case ILLEGAL_208:
            break;
         case ILLEGAL_209:
            break;
         case ILLEGAL_210:
            break;
         case ILLEGAL_211:
            break;
         case ILLEGAL_212:
            break;
         case ILLEGAL_213:
            break;
         case ILLEGAL_214:
            break;
         case ILLEGAL_215:
            break;
         case ILLEGAL_216:
            break;
         case ILLEGAL_217:
            break;
         case ILLEGAL_218:
            break;
         case ILLEGAL_219:
            break;
         case ILLEGAL_220:
            break;
         case ILLEGAL_221:
            break;
         case ILLEGAL_222:
            break;
         case ILLEGAL_223:
            break;
         case ILLEGAL_224:
            break;
         case ILLEGAL_225:
            break;
         case ILLEGAL_226:
            break;
         case ILLEGAL_227:
            break;
         case ILLEGAL_228:
            break;
         case ILLEGAL_229:
            break;
         case ILLEGAL_230:
            break;
         case ILLEGAL_231:
            break;
         case ILLEGAL_232:
            break;
         case ILLEGAL_233:
            break;
         case ILLEGAL_234:
            break;
         case ILLEGAL_235:
            break;
         case ILLEGAL_236:
            break;
         case ILLEGAL_237:
            break;
         case ILLEGAL_238:
            break;
         case ILLEGAL_239:
            break;
         case ILLEGAL_240:
            break;
         case ILLEGAL_241:
            break;
         case ILLEGAL_242:
            break;
         case ILLEGAL_243:
            break;
         case ILLEGAL_244:
            break;
         case ILLEGAL_245:
            break;
         case ILLEGAL_246:
            break;
         case ILLEGAL_247:
            break;
         case ILLEGAL_248:
            break;
         case ILLEGAL_249:
            break;
         case ILLEGAL_250:
            break;
         case ILLEGAL_251:
            break;
         case ILLEGAL_252:
            break;
         case ILLEGAL_253:
            break;
         case ILLEGAL_254:
            break;
         case ILLEGAL_255:
            break;
         case NONE:
            break;
         case COMPOSITE_IF:
            break;
         case COMPOSITE_IF_ELSE:
            break;
         case COMPOSITE_FOR_SUN:
            break;
         case COMPOSITE_FOR_ECLIPSE:
            break;
         case COMPOSITE_ARBITRARY_SCOPE:
            break;
         case COMPOSITE_WHILE:
            break;
         case CLONE:
            break;
         case INCREMENT:
            break;
         case INLINE_ASSIGN:
            break;
         case MULTI_ASSIGN:
            break;
         case FAKEGOTO:
            break;
         case FIELD_ARRAY_ELEMENT_INCREMENT:
            break;
         case FIELD_ARRAY_ELEMENT_ASSIGN:
            break;
         case HEAD:
            break;
         case COMPOSITE_EMPTY_LOOP:
            break;
         case COMPOSITE_DO_WHILE:
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
         if(type.isInt()){
            sb.append("s32 " + dotClassName + "." + name + dest_separator() + s32Name(stack(_i.getStackBase())));
         }
      }else{
         if(type.isArray()){
            sb.append("arr_");
         }
         if(type.isInt()){
            sb.append("s32 " + ((type.isArray())?"arr_":"")+s32Name(stack(_i.getStackBase())));
         }
         sb.append(dest_separator() + dotClassName + "." + name);
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
         sb.append("call_void VOID" + dest_separator() + dotClassName + "." + name + " " + sb);
      }else if(returnType.isInt()){
         sb.append("call_s32 " + s32Name(stack(_i.getStackBase())) + dest_separator() + dotClassName + "." + name + " " + sb);
      }else if(returnType.isDouble()){
         sb.append("call_f64 " + f64Name(stack(_i.getStackBase())) + dest_separator() + dotClassName + "." + name + " " + sb);

      }
      for(TypeHelper.Arg arg : argsAndReturnType.getArgs()){
         if(arg.getArgc() > 0){
            sb.append(", ");
         }
         if(arg.isDouble()){
            sb.append(f64Name(stack(_i.getStackBase() + arg.getArgc())));
         }else if(arg.isFloat()){
            sb.append(f32Name(stack(_i.getStackBase() + arg.getArgc())));
         }else if(arg.isInt()){
            sb.append(s32Name(stack(_i.getStackBase() + arg.getArgc())));
         }else if(arg.isLong()){
            sb.append(s64Name(stack(_i.getStackBase() + arg.getArgc())));
         }
      }
      return (sb.toString());
   }

   private String mov(){
      return ("mov_");
   }

   private String load(){
      return ("load_");
   }

   private String store(){
      return ("store_");
   }

   private String mov_s32(int _dest, int _source){
      return (mov() + "s32 " + s32Name(_dest) + dest_separator() + s32Name(_source));
   }

   private String load_s32(int _indexAndValue, int _array){
      return (load() + "s32 " + s32Name(_indexAndValue) + dest_separator() + s32Array(_array, _indexAndValue));
   }

   private String store_s32(int _value, int _index, int _array){
      return (store() + "s32 " + s32Array(_array, _index) + dest_separator() + s32Name(_value));
   }

   private String load_s64(int _indexAndValue, int _array){
      return (load() + "s64 " + s64Name(_indexAndValue) + dest_separator() + s64Array(_array, _indexAndValue));
   }

   private String s32Array(int arr_reg, int index){
      return ("*(s32_" + regNum(arr_reg) + " + " + array_len_offset() + " + " + sizeof_s32() + " * " + s32Name(index) + ")");
   }

   private String s64Array(int arr_reg, int index){
      return ("*(s64_" + regNum(arr_reg) + " + " + array_len_offset() + " + " + sizeof_s64() + " * " + s64Name(index) + ")");
   }

   private String s32Name(int reg){
      return ("s32_" + regNum(reg));
   }

   private String s64Name(int reg){
      return ("s64_" + regNum(reg));
   }

   private String f64Name(int reg){
      return ("f64_" + regNum(reg));
   }

   private String f32Name(int reg){
      return ("f32_" + regNum(reg));
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

   private String regNum(int reg){
      return (String.format("%02d", reg));
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


   private String dest_separator(){
      return (separator());
   }

   private String separator(){
      return (", ");
   }
}
