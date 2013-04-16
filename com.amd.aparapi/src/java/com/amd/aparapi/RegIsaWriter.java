package com.amd.aparapi;
import com.amd.aparapi.InstructionSet.LocalVariableTableIndexAccessor;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/16/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegIsaWriter{





   void write(ClassModel.ClassModelMethod _method, int _maxLocals){

      Table table = new Table("|%2d ", "|%s", "|%d", "|%d", "|%-60s", "|%s");
      table.header("|PC ", "|Consumes + count", "|Produces", "|Base", "|Instruction", "|Branches");

      for(Instruction i : _method.getInstructions()){
         String label = i.getByteCode().getName();//InstructionHelper.getLabel(i, false, false, false);
         if(i.isBranch()){
            label += " " + i.asBranch().getAbsolute();
         }else if(i.isFieldAccessor()){
            label += " " + i.asFieldAccessor().getConstantPoolFieldEntry().getType();
            label += " " + i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName();
            label += "." + i.asFieldAccessor().getConstantPoolFieldEntry().getName();
         }else if(i.isLocalVariableAccessor()){
            if (false){
               label += " #" + i.asLocalVariableAccessor().getLocalVariableInfo().getSlot();
               label += " " + i.asLocalVariableAccessor().getLocalVariableInfo().getVariableName();
               label += " " + i.asLocalVariableAccessor().getLocalVariableInfo().getVariableDescriptor();
            }else{
               int slot = i.asLocalVariableAccessor().getLocalVariableInfo().getSlot();
               String descriptor = i.asLocalVariableAccessor().getLocalVariableInfo().getVariableDescriptor();
               if (i instanceof InstructionSet.AccessLocalVariable){
                   label = renderLoad(i, slot, descriptor, _maxLocals);

               }
            }
         }else if(i.isMethodCall()){
            label += " " + i.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType().getReturnType();
            label += " " + i.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName();
            label += "." + i.asMethodCall().getConstantPoolMethodEntry().getName();
         }else if(i.isConstant() ){
            InstructionSet.Constant c = ((InstructionSet.Constant) i);
            label += " " + i.asConstant().getValue();
         }
         StringBuilder consumes = new StringBuilder();
         for(int pc : i.getConsumeIndices()){
            consumes.append(pc).append(" ");
         }
         StringBuilder sb = new StringBuilder();
         for(InstructionHelper.BranchVector branchInfo : InstructionHelper.getBranches(_method)){
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

   private String renderLoad(Instruction instruction, int slot, String descriptor, int maxLocals){
      String returnString = instruction.getByteCode().toString();
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
            break;
         case ILOAD_0:
         case ILOAD_1:
         case ILOAD_2:
         case ILOAD_3:
            returnString = "mov_32 "
                  +getRegName(instruction.getStackBase()+maxLocals,    instruction.asLocalVariableAccessor().getLocalVariableInfo().getVariableDescriptor())
                  +","
                  +getRegName(instruction.asLocalVariableAccessor().getLocalVariableInfo().getSlot(),
                  instruction.asLocalVariableAccessor().getLocalVariableInfo().getVariableDescriptor()) ;
                 // +", "
                //  +constantI32((Integer)instruction.asConstant().getValue());
                    //  ((InstructionSet.LocalVariableConstIndexLoad)instruction).get

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
            break;
         case PUTSTATIC:
            break;
         case GETFIELD:
            break;
         case PUTFIELD:
            break;
         case INVOKEVIRTUAL:
            break;
         case INVOKESPECIAL:
            break;
         case INVOKESTATIC:
            break;
         case INVOKEINTERFACE:
            break;
         case INVOKEDYNAMIC:
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
      return(returnString);
   }
   private String constantI32(int slot){
      return("(i32)"+slot);
   }
   private String getRegName(int slot, String variableDescriptor){
      return("i32_"+slot);
   }
}
