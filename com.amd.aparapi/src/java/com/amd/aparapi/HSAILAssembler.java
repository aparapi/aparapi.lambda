package com.amd.aparapi;

import com.amd.aparapi.HSAILInstructionSet.HSAILInstruction;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by user1 on 1/14/14.
 */
public class HSAILAssembler{

   private List<HSAILInstruction> instructions;
   private Stack<HSAILStackFrame> frames;
   private List<HSAILStackFrame> frameSet;

   HSAILAssembler(List<HSAILInstruction> _instructions, Stack<HSAILStackFrame> _frames, List<HSAILStackFrame> _frameSet){
      instructions = _instructions;
      frames = _frames;
      frameSet = _frameSet;
   }

   HSAILAssembler(){
      this(new ArrayList<HSAILInstruction>(), new Stack<HSAILStackFrame>(), new ArrayList<HSAILStackFrame>());
   }

   HSAILAssembler(ClassModel.ClassModelMethod _method){
      this();
      HSAILStackFrame base = new HSAILStackFrame(null, _method, 0, 0);
      frames.push(base);
      frameSet.add(base);
   }

   public <T extends StackReg> HSAILAssembler st_global(Instruction _i, T _source, StackReg_ref _ref, long _offset){
      add(new HSAILInstructionSet.st_global(currentFrame(), _i, _source, _ref, _offset));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler st_global(Instruction _i, T _source, StackReg_ref _ref, Field _f){
      return (st_global(_i, _source, _ref, (long)UnsafeWrapper.objectFieldOffset(_f)));
   }

   public <T extends StackReg> HSAILAssembler ld_global(Instruction _i, T _dest, StackReg_ref _ref, long _offset){
      add(new HSAILInstructionSet.ld_global(currentFrame(), _i, _dest, _ref, _offset));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler ld_global(Instruction _i, T _dest, StackReg_ref _ref, Field _f){
      return (ld_global(_i, _dest, _ref, (long)UnsafeWrapper.objectFieldOffset(_f)));
   }

   public <T extends StackReg> HSAILAssembler static_field_load(Instruction _i, T _dest, StackReg_ref _ref, Field _f){
      add(new HSAILInstructionSet.static_field_load(currentFrame(), _i, _dest, _ref, (long)UnsafeWrapper.staticFieldOffset(_f)));
      return (this);
   }

   public HSAILAssembler ret_void(Instruction _i){
      add(new HSAILInstructionSet.retvoid(currentFrame(), _i));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler ret(Instruction _i, T _source){
      add(new HSAILInstructionSet.ret(currentFrame(), _i, _source));
      return (this);
   }

   //public HSAILAssembler branch( Instruction _i){
   // add( new HSAILInstructionSet.branch(currentFrame(), _i, stackReg_s32(_i), _i.getByteCode().getName(), _i.asBranch().getAbsolute()));
   //return(this);
   // }
   public HSAILAssembler brn(Instruction _i){
      add(new HSAILInstructionSet.brn(currentFrame(), _i, _i.asBranch().getAbsolute()));
      return (this);
   }

   public HSAILAssembler cbr(Instruction _i){
      add(new HSAILInstructionSet.cbr(currentFrame(), _i, _i.asBranch().getAbsolute()));
      return (this);
   }

   public HSAILAssembler lda_group_u64(Instruction _i, StackReg_ref _dest, String _uniqueName){
      add(new HSAILInstructionSet.lda_group_u64(currentFrame(), _i, _dest, _uniqueName));
      return (this);
   }

   public HSAILAssembler group_u32(Instruction _i, String _uniqueName, int _size){
      add(new HSAILInstructionSet.group_u32(currentFrame(), _i, _uniqueName, _size));
      return (this);
   }

   public HSAILAssembler group_f32(Instruction _i, String _uniqueName, int _size){
      add(new HSAILInstructionSet.group_f32(currentFrame(), _i, _uniqueName, _size));
      return (this);
   }

   public HSAILAssembler group_ref(Instruction _i, String _uniqueName, int _size){
      add(new HSAILInstructionSet.group_ref(currentFrame(), _i, _uniqueName, _size));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler cmp(Instruction _i, String _type, T _lhs, T _rhs){
      add(new HSAILInstructionSet.cmp(currentFrame(), _i, _type, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler cmov(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.cmov(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler cmp_const_0(Instruction _i, String _type, T _source){
      add(new HSAILInstructionSet.cmp_s32_const_0(currentFrame(), _i, _type, _source));
      return (this);
   }

   public <Td extends StackReg, Ts extends StackReg> HSAILAssembler cvt(Instruction _i, Td _dest, Ts _source){
      add(new HSAILInstructionSet.cvt(currentFrame(), _i, _dest, _source));
      return (this);
   }

   public HSAILAssembler incvar(Instruction _i, VarReg_s32 _dest, VarReg_s32 _source, int _delta){
      add(new HSAILInstructionSet.add_const(currentFrame(), _i, _dest, _source, _delta));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler xor(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.xor(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler or(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.or(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler and(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.and(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler ushr(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.ushr(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler shr(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.shr(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler shl(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.shl(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler neg(Instruction _i, T _dest, T _source){
      add(new HSAILInstructionSet.neg(currentFrame(), _i, _dest, _source));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler rem(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.rem(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler div(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.div(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler mul(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.mul(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler sub(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.sub(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler add(Instruction _i, T _dest, T _lhs, T _rhs){
      add(new HSAILInstructionSet.add(currentFrame(), _i, _dest, _lhs, _rhs));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler mad(Instruction _i, T _dest, T _lhs, T _rhs, int _constant){
      add(new HSAILInstructionSet.mad(currentFrame(), _i, _dest, _lhs, _rhs, _constant));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler array_store(Instruction _i, boolean _isLocal, StackReg_ref _arrayRef, T _source){
      add(new HSAILInstructionSet.array_store(currentFrame(), _i, _isLocal, _arrayRef, _source));
      return (this);
   }

   public <Td extends VarReg, Ts extends StackReg> HSAILAssembler mov(Instruction _i, Td _dest, Ts _source){
      add(new HSAILInstructionSet.mov(currentFrame(), _i, _dest, _source));
      return (this);
   }

   public <Td extends StackReg, Ts extends VarReg> HSAILAssembler mov(Instruction _i, Td _dest, Ts _source){
      add(new HSAILInstructionSet.mov(currentFrame(), _i, _dest, _source));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler array_load(Instruction _i, boolean _isLocal, T _dest, StackReg_ref _arrayRef){
      add(new HSAILInstructionSet.array_load(currentFrame(), _i, _isLocal, _dest, _arrayRef));
      return (this);
   }

   public HSAILAssembler mov_const(Instruction _i, StackReg_ref _dest, long _value){
      add(new HSAILInstructionSet.mov_const(currentFrame(), _i, _dest, _value));
      return (this);
   }

   public HSAILAssembler mov_const(Instruction _i, StackReg_s64 _dest, long _value){
      add(new HSAILInstructionSet.mov_const(currentFrame(), _i, _dest, _value));
      return (this);
   }

   public HSAILAssembler mov_const(Instruction _i, StackReg_s32 _dest, int _value){
      add(new HSAILInstructionSet.mov_const(currentFrame(), _i, _dest, _value));
      return (this);
   }

   public HSAILAssembler mov_const(Instruction _i, StackReg_f64 _dest, double _value){
      add(new HSAILInstructionSet.mov_const(currentFrame(), _i, _dest, _value));
      return (this);
   }

   public HSAILAssembler mov_const(Instruction _i, StackReg_f32 _dest, float _value){
      add(new HSAILInstructionSet.mov_const(currentFrame(), _i, _dest, _value));
      return (this);
   }

   public <T extends VarReg> HSAILAssembler ld_kernarg(Instruction _i, T _var){
      add(new HSAILInstructionSet.ld_kernarg(currentFrame(), _i, _var));
      return (this);
   }

   public <T extends VarReg> HSAILAssembler ld_arg(Instruction _i, T _var){
      add(new HSAILInstructionSet.ld_arg(currentFrame(), _i, _var));
      return (this);
   }

   public HSAILAssembler workitemabsid_u32(Instruction _i, VarReg_s32 _dest){
      add(new HSAILInstructionSet.workitemabsid(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler gridsize_s32(Instruction _i, StackReg_s32 _dest){
      add(new HSAILInstructionSet.gridsize(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler countuplane_s32(Instruction _i, StackReg_s32 _dest){
      add(new HSAILInstructionSet.countuplane(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler masklane_s32(Instruction _i, StackReg_s32 _dest){
      add(new HSAILInstructionSet.masklane(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler laneid_s32(Instruction _i, StackReg_s32 _dest){
      add(new HSAILInstructionSet.laneid(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler cuid_s32(Instruction _i, StackReg_s32 _dest){
      add(new HSAILInstructionSet.cuid(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler clock_s64(Instruction _i, StackReg_s64 _dest){
      add(new HSAILInstructionSet.clock(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler workgroupid_s32(Instruction _i, StackReg_s32 _dest){
      add(new HSAILInstructionSet.workgroupid(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler workgroupsize_s32(Instruction _i, StackReg_s32 _dest){
      add(new HSAILInstructionSet.workgroupsize(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler currentworkgroupsize_s32(Instruction _i, StackReg_s32 _dest){
      add(new HSAILInstructionSet.currentworkgroupsize(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler workitemid_s32(Instruction _i, StackReg_s32 _dest){
      add(new HSAILInstructionSet.workitemid(currentFrame(), _i, _dest));
      return (this);
   }

   public HSAILAssembler nsqrt(Instruction _i, StackReg_f64 _dest, StackReg_f64 _source){
      add(new HSAILInstructionSet.nsqrt(currentFrame(), _i, _dest, _source));
      return (this);
   }

   public HSAILAssembler ncos(Instruction _i, StackReg_f64 _dest, StackReg_f64 _source){
      add(new HSAILInstructionSet.ncos(currentFrame(), _i, _dest, _source));
      return (this);
   }

   public HSAILAssembler nsin(Instruction _i, StackReg_f64 _dest, StackReg_f64 _source){
      add(new HSAILInstructionSet.nsin(currentFrame(), _i, _dest, _source));
      return (this);
   }

   public HSAILAssembler nyi(Instruction _i){
      add(new HSAILInstructionSet.nyi(currentFrame(), _i));
      return (this);
   }

   public HSAILAssembler checkcast(Instruction _i){
      add(new HSAILInstructionSet.checkcast(currentFrame(), _i));
      return (this);
   }

   public HSAILAssembler array_len(Instruction _i, StackReg_s32 _dest, StackReg_ref _source){
      add(new HSAILInstructionSet.array_len(currentFrame(), _i, _dest, _source));
      return (this);
   }

   public HSAILAssembler nop(Instruction _i, String _label){
      add(new HSAILInstructionSet.nop(currentFrame(), _i, _label));
      return (this);
   }

   public HSAILAssembler barrier_fgroup(Instruction _i){
      add(new HSAILInstructionSet.barrier_fgroup(currentFrame(), _i));
      return (this);
   }

   public HSAILAssembler nop(Instruction _i){
      return (nop(_i, null));
   }

   public HSAILAssembler nopUniqueLabel(Instruction _i){
      return (nop(_i, currentFrame().getUniqueName()));
   }

   public <T extends StackReg> HSAILAssembler mov(Instruction _i, T _dest, T _source){

      add(new HSAILInstructionSet.mov(currentFrame(), _i, _dest, _source));
      return (this);
   }

   public HSAILAssembler returnBranchUniqueName(Instruction _i){
      add(new HSAILInstructionSet.returnBranch(currentFrame(), _i, currentFrame().getUniqueName()));
      return (this);
   }

   public HSAILAssembler cvt(Instruction _i, StackReg_u64 _dest, StackReg_s32 _source){
      add(new HSAILInstructionSet.cvt(currentFrame(), _i, _dest, _source));
      return (this);
   }

   public <T extends StackReg> HSAILAssembler ld_global(Instruction _i, T _dest, StackReg_ref _source, int _offset){
      add(new HSAILInstructionSet.ld_global(currentFrame(), _i, _dest, _source, _offset));
      return (this);
   }

   public void addmov(Instruction _i, PrimitiveType _type, int _from, int _to){
      if (_type.equals(PrimitiveType.ref) || _type.getHsaBits() == 32){
         if (_type.equals(PrimitiveType.ref)){
            mov(_i, stackReg_ref(_i, _to), stackReg_ref(_i, _from));
         }else if (_type.equals(PrimitiveType.s32)){
            mov(_i, stackReg_s32(_i, _to), stackReg_s32(_i, _from));
         }else if (_type.equals(PrimitiveType.f32)){

            mov(_i, stackReg_f32(_i, _to), stackReg_f32(_i, _from));
         }else{
            throw new IllegalStateException(" unknown prefix 1 prefix for first of DUP2");
         }

      }else{
         throw new IllegalStateException(" unknown prefix 2 prefix for DUP2");
      }
   }

   public HSAILRegister getRegOfLastWriteToIndex(int _index){

      int idx = instructions.size();
      while (--idx>=0){
         HSAILInstruction i = instructions.get(idx);
         if (i.dests != null){
            for (HSAILRegister d : i.dests){
               if (d.index == _index){
                  return (d);
               }
            }
         }
      }
      return (null);
   }

   public boolean isLocalRef(int _index){
      int idx = instructions.size();
      while (--idx>=0){
         HSAILInstruction i = instructions.get(idx);
         if (i instanceof HSAILInstructionSet.mov && i.dests != null && i.dests.length == 1 && i.dests[0].index == _index && i.dests[0].type == PrimitiveType.ref && i.sources != null && i.sources.length == 1){
            HSAILOperand sourceOperand = i.sources[0];
            if (sourceOperand instanceof StackReg_ref){
               StackReg_ref sourceStackRef = (StackReg_ref)i.sources[0];
               if (sourceStackRef.isLocal()){
                  return (true);
               }else{
                  _index = sourceStackRef.index; // keep looking
               }
            }else if (sourceOperand instanceof VarReg_ref){
               VarReg_ref sourceVarRef = (VarReg_ref)i.sources[0];
               //   if (sourceVarRef.isLocal()){
               //       return(true);
               //  }else{
               _index = sourceVarRef.index; // keep looking
               // }
            }else throw new IllegalStateException("how?");

         }else if (i instanceof HSAILInstructionSet.lda_group_u64 && i.dests != null && i.dests.length == 1 && i.dests[0].index == _index && i.dests[0].type == PrimitiveType.ref){
            if (i.dests[0] instanceof StackReg_ref){
               if (((StackReg_ref)i.dests[0]).isLocal()){
                  return (true);
               }else{
                  return (false);
               }
            }else{
               return (false);
            }

         }
      }
      return (false);
   }

   public HSAILRegister addmov(Instruction _i, int _from, int _to){
      HSAILRegister r = getRegOfLastWriteToIndex(stackIdx(_i)+_from);
      if (r == null){
         System.out.println("damn!");
      }
      addmov(_i, r.type, _from, _to);
      return (r);
   }

   static boolean compressMovs = false;

   public void add(HSAILInstruction _instruction){

      if (compressMovs){
         // before we add lets see if this is a redundant mov
         for (int srcIndex = 0; srcIndex<_instruction.sources.length; srcIndex++){
            HSAILOperand source = _instruction.sources[srcIndex];
            if (source instanceof StackReg){
               // look up the list of reg instructions for the instruction which assigns to this
               int i = instructions.size();
               while ((--i)>=0){
                  if (instructions.get(i) instanceof HSAILInstructionSet.mov){
                     // we have found a move
                     HSAILInstructionSet.mov candidateForRemoval = (HSAILInstructionSet.mov)instructions.get(i);
                     if (candidateForRemoval.from.getBlock() == _instruction.from.getBlock()
                           && (candidateForRemoval.getDest() instanceof StackReg) && candidateForRemoval.getDest().equals(source)){
                        // so i may be a candidate if between i and instruction.size() i.dest() is not mutated
                        boolean mutated = false;
                        for (int x = i+1; !mutated && x<instructions.size(); x++){
                           if (instructions.get(x).dests.length>0 && instructions.get(x).dests[0].equals(candidateForRemoval.getSrc())){
                              mutated = true;
                           }
                        }
                        if (!mutated){
                           instructions.remove(i);
                           _instruction.sources[srcIndex] = candidateForRemoval.getSrc();
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      instructions.add(_instruction);
   }

   enum ParseState{NONE, COMPARE_F32, COMPARE_F64, COMPARE_S64}

   ;

   public void addInstructions(ClassModel.ClassModelMethod method, Aparapi.Lambda... inline){

      ParseState parseState = ParseState.NONE;

      boolean needsReturnLabel = false;
      for (Instruction i : method.getInstructions()){

         switch (i.getByteCode()){

            case ACONST_NULL:
               mov_const(i, stackReg_ref(i), i.asIntegerConstant().getValue());
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
               mov_const(i, stackReg_s32(i), i.asIntegerConstant().getValue());
               break;
            case LCONST_0:
            case LCONST_1:
               mov_const(i, stackReg_s64(i), i.asLongConstant().getValue());
               break;
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
               mov_const(i, stackReg_f32(i), i.asFloatConstant().getValue());
               break;
            case DCONST_0:
            case DCONST_1:
               mov_const(i, stackReg_f64(i), i.asDoubleConstant().getValue());
               break;
            // case BIPUSH: moved up
            // case SIPUSH: moved up

            case LDC:
            case LDC_W:
            case LDC2_W:{
               InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant)i;

               ClassModel.ConstantPool.ConstantEntry e = (ClassModel.ConstantPool.ConstantEntry)cpe.getConstantPoolEntry();
               if (e instanceof ClassModel.ConstantPool.DoubleEntry){
                  mov_const(i, stackReg_f64(i), ((ClassModel.ConstantPool.DoubleEntry)e).getValue());
               }else if (e instanceof ClassModel.ConstantPool.FloatEntry){
                  mov_const(i, stackReg_f32(i), ((ClassModel.ConstantPool.FloatEntry)e).getValue());
               }else if (e instanceof ClassModel.ConstantPool.IntegerEntry){
                  mov_const(i, stackReg_s32(i), ((ClassModel.ConstantPool.IntegerEntry)e).getValue());
               }else if (e instanceof ClassModel.ConstantPool.LongEntry){
                  mov_const(i, stackReg_s64(i), ((ClassModel.ConstantPool.LongEntry)e).getValue());
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
               mov(i, stackReg_s32(i), varReg_s32(i));

               break;
            case LLOAD:
            case LLOAD_0:
            case LLOAD_1:
            case LLOAD_2:
            case LLOAD_3:
               mov(i, stackReg_s64(i), varReg_s64(i));
               break;
            case FLOAD:
            case FLOAD_0:
            case FLOAD_1:
            case FLOAD_2:
            case FLOAD_3:
               mov(i, stackReg_f32(i), varReg_f32(i));
               break;
            case DLOAD:
            case DLOAD_0:
            case DLOAD_1:
            case DLOAD_2:
            case DLOAD_3:
               mov(i, stackReg_f64(i), varReg_f64(i));
               break;
            case ALOAD:
            case ALOAD_0:
            case ALOAD_1:
            case ALOAD_2:
            case ALOAD_3:

               mov(i, stackReg_ref(i), varReg_ref(i));
               break;
            case IALOAD:{
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               StackReg_ref arrayBase = stackReg_ref(i);

               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), arrayBase, PrimitiveType.s32.getHsaBytes());

               array_load(i, isLocalRef(arrayBase.index), stackReg_s32(i), stackReg_ref(i, 1));
               break;
            }
            case LALOAD:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.s64.getHsaBytes());
               array_load(i, false, stackReg_s64(i), stackReg_ref(i, 1));
               break;
            case FALOAD:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.f32.getHsaBytes());
               array_load(i, false, stackReg_f32(i), stackReg_ref(i, 1));

               break;
            case DALOAD:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.f64.getHsaBytes());
               array_load(i, false, stackReg_f64(i), stackReg_ref(i, 1));

               break;
            case AALOAD:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.ref.getHsaBytes());
               array_load(i, false, stackReg_ref(i), stackReg_ref(i, 1));

               break;
            case BALOAD:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.s8.getHsaBytes());
               array_load(i, false, stackReg_s8(i), stackReg_ref(i, 1));

               break;
            case CALOAD:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.u16.getHsaBytes());
               array_load(i, false, stackReg_u16(i), stackReg_ref(i, 1));

               break;
            case SALOAD:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.s16.getHsaBytes());
               array_load(i, false, stackReg_s16(i), stackReg_ref(i, 1));
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
               mov(i, varReg_s32(i), stackReg_s32(i));
               break;
            case LSTORE:
            case LSTORE_0:
            case LSTORE_1:
            case LSTORE_2:
            case LSTORE_3:
               mov(i, varReg_s64(i), stackReg_s64(i));

               break;
            case FSTORE:
            case FSTORE_0:
            case FSTORE_1:
            case FSTORE_2:
            case FSTORE_3:
               mov(i, varReg_f32(i), stackReg_f32(i));
               break;
            case DSTORE:
            case DSTORE_0:
            case DSTORE_1:
            case DSTORE_2:
            case DSTORE_3:
               mov(i, varReg_f64(i), stackReg_f64(i));
               break;
            case ASTORE:
            case ASTORE_0:
            case ASTORE_1:
            case ASTORE_2:
            case ASTORE_3:
               mov(i, varReg_ref(i), stackReg_ref(i));
               break;
            case IASTORE:

            {
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               StackReg_ref arrayBase = stackReg_ref(i);

               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), arrayBase, PrimitiveType.s32.getHsaBytes());

               array_store(i, isLocalRef(arrayBase.index), stackReg_ref(i, 1), stackReg_s32(i, 2));
               break;
            }
            case LASTORE:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.s64.getHsaBytes());
               array_store(i, false, stackReg_ref(i, 1), stackReg_s64(i, 2));
               break;
            case FASTORE:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.f32.getHsaBytes());
               array_store(i, false, stackReg_ref(i, 1), stackReg_f32(i, 2));
               break;
            case DASTORE:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.f64.getHsaBytes());
               array_store(i, false, stackReg_ref(i, 1), stackReg_f64(i, 2));
               break;
            case AASTORE:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.ref.getHsaBytes());
               array_store(i, false, stackReg_ref(i, 1), stackReg_ref(i, 2));
               break;
            case BASTORE:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.s8.getHsaBytes());
               array_store(i, false, stackReg_ref(i, 1), stackReg_s8(i, 2));
               break;
            case CASTORE:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.u16.getHsaBytes());
               array_store(i, false, stackReg_ref(i, 1), stackReg_u16(i, 2));
               break;
            case SASTORE:
               cvt(i, stackReg_ref(i, 1), stackReg_s32(i, 1));
               mad(i, stackReg_ref(i, 1), stackReg_ref(i, 1), stackReg_ref(i), PrimitiveType.s16.getHsaBytes());
               array_store(i, false, stackReg_ref(i, 1), stackReg_s16(i, 2));
               break;
            case POP:
               nyi(i);
               break;
            case POP2:
               nyi(i);
               break;
            case DUP:
               addmov(i, 0, 1);
               break;
            case DUP_X1:
               nyi(i);
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
               nyi(i);
               break;
            case DUP2_X2:
               nyi(i);
               break;
            case SWAP:
               nyi(i);
               break;
            case IADD:
               add(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LADD:
               add(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case FADD:
               add(i, stackReg_f32(i), stackReg_f32(i), stackReg_f32(i, 1));
               break;
            case DADD:
               add(i, stackReg_f64(i), stackReg_f64(i), stackReg_f64(i, 1));
               break;
            case ISUB:
               sub(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LSUB:
               sub(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case FSUB:
               sub(i, stackReg_f32(i), stackReg_f32(i), stackReg_f32(i, 1));
               break;
            case DSUB:
               sub(i, stackReg_f64(i), stackReg_f64(i), stackReg_f64(i, 1));
               break;
            case IMUL:
               mul(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LMUL:
               mul(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case FMUL:
               mul(i, stackReg_f32(i), stackReg_f32(i), stackReg_f32(i, 1));
               break;
            case DMUL:
               mul(i, stackReg_f64(i), stackReg_f64(i), stackReg_f64(i, 1));
               break;
            case IDIV:
               div(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LDIV:
               div(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case FDIV:
               div(i, stackReg_f32(i), stackReg_f32(i), stackReg_f32(i, 1));
               break;
            case DDIV:
               div(i, stackReg_f64(i), stackReg_f64(i), stackReg_f64(i, 1));
               break;
            case IREM:
               rem(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LREM:
               rem(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case FREM:
               rem(i, stackReg_f32(i), stackReg_f32(i), stackReg_f32(i, 1));
               break;
            case DREM:
               rem(i, stackReg_f64(i), stackReg_f64(i), stackReg_f64(i, 1));
               break;
            case INEG:
               neg(i, stackReg_s32(i), stackReg_s32(i));
               break;
            case LNEG:
               neg(i, stackReg_s64(i), stackReg_s64(i));
               break;
            case FNEG:
               neg(i, stackReg_f32(i), stackReg_f32(i));
               break;
            case DNEG:
               neg(i, stackReg_f64(i), stackReg_f64(i));
               break;
            case ISHL:
               shl(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LSHL:
               shl(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case ISHR:
               shr(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LSHR:
               shr(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case IUSHR:
               ushr(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LUSHR:
               ushr(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case IAND:
               and(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LAND:
               and(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case IOR:
               or(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LOR:
               or(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case IXOR:
               xor(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
               break;
            case LXOR:
               xor(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
               break;
            case IINC:
               incvar(i, varReg_s32(i), varReg_s32(i), ((InstructionSet.I_IINC)i).getDelta());
               break;
            case I2L:
               cvt(i, stackReg_s64(i), stackReg_s32(i));
               break;
            case I2F:
               cvt(i, stackReg_f32(i), stackReg_s32(i));
               break;
            case I2D:
               cvt(i, stackReg_f64(i), stackReg_s32(i));
               break;
            case L2I:
               cvt(i, stackReg_s32(i), stackReg_s64(i));
               break;
            case L2F:
               cvt(i, stackReg_f32(i), stackReg_s64(i));
               break;
            case L2D:
               cvt(i, stackReg_f64(i), stackReg_s64(i));
               break;
            case F2I:
               cvt(i, stackReg_s32(i), stackReg_f32(i));
               break;
            case F2L:
               cvt(i, stackReg_s64(i), stackReg_f32(i));
               break;
            case F2D:
               cvt(i, stackReg_f64(i), stackReg_f32(i));
               break;
            case D2I:
               cvt(i, stackReg_s32(i), stackReg_f64(i));
               break;
            case D2L:
               cvt(i, stackReg_s64(i), stackReg_f64(i));
               break;
            case D2F:
               cvt(i, stackReg_f32(i), stackReg_f64(i));
               break;
            case I2B:
               cvt(i, stackReg_s8(i), stackReg_s32(i));
               break;
            case I2C:
               cvt(i, stackReg_u16(i), stackReg_s32(i));
               break;
            case I2S:
               cvt(i, stackReg_s16(i), stackReg_s32(i));
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
               if (parseState.equals(ParseState.COMPARE_F32)){
                  cmp(i, "eq", stackReg_f32(i.getPrevPC()), stackReg_f32(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_F64)){
                  cmp(i, "eq", stackReg_f64(i.getPrevPC()), stackReg_f64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_S64)){
                  cmp(i, "eq", stackReg_s64(i.getPrevPC()), stackReg_s64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else{
                  cmp_const_0(i, "eq", stackReg_s32(i));
               }
               cbr(i);
               break;
            case IFNE:
               if (parseState.equals(ParseState.COMPARE_F32)){
                  cmp(i, "ne", stackReg_f32(i.getPrevPC()), stackReg_f32(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_F64)){
                  cmp(i, "ne", stackReg_f64(i.getPrevPC()), stackReg_f64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_S64)){
                  cmp(i, "ne", stackReg_s64(i.getPrevPC()), stackReg_s64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else{
                  cmp_const_0(i, "ne", stackReg_s32(i));
               }
               cbr(i);
               break;
            case IFLT:
               if (parseState.equals(ParseState.COMPARE_F32)){
                  cmp(i, "lt", stackReg_f32(i.getPrevPC()), stackReg_f32(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_F64)){
                  cmp(i, "lt", stackReg_f64(i.getPrevPC()), stackReg_f64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_S64)){
                  cmp(i, "lt", stackReg_s64(i.getPrevPC()), stackReg_s64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else{
                  cmp_const_0(i, "lt", stackReg_s32(i));

               }
               cbr(i);
               break;
            case IFGE:
               if (parseState.equals(ParseState.COMPARE_F32)){
                  cmp(i, "ge", stackReg_f32(i.getPrevPC()), stackReg_f32(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_F64)){
                  cmp(i, "ge", stackReg_f64(i.getPrevPC()), stackReg_f64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_S64)){
                  cmp(i, "ge", stackReg_s64(i.getPrevPC()), stackReg_s64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else{
                  cmp_const_0(i, "ge", stackReg_s32(i));

               }
               cbr(i);
               break;
            case IFGT:
               if (parseState.equals(ParseState.COMPARE_F32)){
                  cmp(i, "gt", stackReg_f32(i.getPrevPC()), stackReg_f32(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_F64)){
                  cmp(i, "gt", stackReg_f64(i.getPrevPC()), stackReg_f64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_S64)){
                  cmp(i, "gt", stackReg_s64(i.getPrevPC()), stackReg_s64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else{
                  cmp_const_0(i, "gt", stackReg_s32(i));

               }
               cbr(i);
               break;
            case IFLE:
               if (parseState.equals(ParseState.COMPARE_F32)){
                  cmp(i, "le", stackReg_f32(i.getPrevPC()), stackReg_f32(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_F64)){
                  cmp(i, "le", stackReg_f64(i.getPrevPC()), stackReg_f64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else if (parseState.equals(ParseState.COMPARE_S64)){
                  cmp(i, "le", stackReg_s64(i.getPrevPC()), stackReg_s64(i.getPrevPC(), 1));
                  parseState = ParseState.NONE;
               }else{
                  cmp_const_0(i, "le", stackReg_s32(i));

               }
               cbr(i);
               break;
            case IF_ICMPEQ:
               cmp(i, "eq", stackReg_s32(i), stackReg_s32(i, 1)).cbr(i);
               break;
            case IF_ICMPNE:
               cmp(i, "ne", stackReg_s32(i), stackReg_s32(i, 1)).cbr(i);
               break;
            case IF_ICMPLT:
               cmp(i, "lt", stackReg_s32(i), stackReg_s32(i, 1)).cbr(i);
               break;
            case IF_ICMPGE:
               cmp(i, "ge", stackReg_s32(i), stackReg_s32(i, 1)).cbr(i);
               break;
            case IF_ICMPGT:
               cmp(i, "gt", stackReg_s32(i), stackReg_s32(i, 1)).cbr(i);
               break;
            case IF_ICMPLE:
               cmp(i, "le", stackReg_s32(i), stackReg_s32(i, 1)).cbr(i);
               break;
            case IF_ACMPEQ:
               cmp(i, "eq", stackReg_ref(i), stackReg_ref(i, 1)).cbr(i);
               break;
            case IF_ACMPNE:
               cmp(i, "ne", stackReg_ref(i), stackReg_ref(i, 1)).cbr(i);
               break;
            case GOTO:
               brn(i);
               break;
            case IFNULL:
               cmp_const_0(i, "eq", stackReg_ref(i)).cbr(i);// SWAG
               break;
            case IFNONNULL:
               cmp_const_0(i, "ne", stackReg_ref(i)).cbr(i);// SWAG
               break;
            case GOTO_W:
               nyi(i);
               break;
            case JSR:
               nyi(i);
               break;
            case RET:
               nyi(i);
               break;
            case TABLESWITCH:
               nyi(i);
               break;
            case LOOKUPSWITCH:
               nyi(i);
               break;
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
               if (frames.size()>1){
                  int maxLocals = i.getMethod().getCodeEntry().getMaxLocals(); // hsailStackFrame.stackOffset -maxLocals is the slot for the return value

                  switch (i.getByteCode()){
                     case IRETURN:
                        mov(i, stackReg_s32(i, -maxLocals), stackReg_s32(i));
                        break;
                     case LRETURN:
                        mov(i, stackReg_s64(i, -maxLocals), stackReg_s64(i));
                        break;
                     case FRETURN:
                        mov(i, stackReg_f32(i, -maxLocals), stackReg_f32(i));
                        break;
                     case DRETURN:
                        mov(i, stackReg_f64(i, -maxLocals), stackReg_f64(i));
                        break;
                     case ARETURN:
                        mov(i, stackReg_ref(i, -maxLocals), stackReg_ref(i));
                        break;
                  }
                  if (i.isLastInstruction()){
                     if (needsReturnLabel){
                        nopUniqueLabel(i);
                     }
                  }else{
                     returnBranchUniqueName(i);

                     needsReturnLabel = true;
                  }
               }else{
                  switch (i.getByteCode()){
                     case IRETURN:
                        ret(i, stackReg_s32(i));
                        break;
                     case LRETURN:
                        ret(i, stackReg_s64(i));
                        break;
                     case FRETURN:
                        ret(i, stackReg_f32(i));
                        break;
                     case DRETURN:
                        ret(i, stackReg_f64(i));
                        break;
                     case ARETURN:
                        ret(i, stackReg_ref(i));
                        break;

                  }

               }
               break;
            case RETURN:
               if (frames.size()>1){
                  if (i.getNextPC() != null){
                     returnBranchUniqueName(i);
                     needsReturnLabel = true;
                  }else{
                     if (i.isBranchTarget()){
                        nop(i);

                     }else if (needsReturnLabel){
                        nopUniqueLabel(i);
                     }
                  }
               }else{
                  ret_void(i);
               }
               break;
            case GETSTATIC:{
               TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try{
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());

                  if (!type.isPrimitive()){
                     static_field_load(i, stackReg_ref(i), stackReg_ref(i), f);
                  }else if (type.isInt()){
                     static_field_load(i, stackReg_s32(i), stackReg_ref(i), f);
                  }else if (type.isFloat()){
                     static_field_load(i, stackReg_f32(i), stackReg_ref(i), f);
                  }else if (type.isDouble()){
                     static_field_load(i, stackReg_f64(i), stackReg_ref(i), f);
                  }else if (type.isLong()){
                     static_field_load(i, stackReg_s64(i), stackReg_ref(i), f);
                  }else if (type.isChar()){
                     static_field_load(i, stackReg_u16(i), stackReg_ref(i), f);
                  }else if (type.isShort()){
                     static_field_load(i, stackReg_s16(i), stackReg_ref(i), f);
                  }else if (type.isChar()){
                     static_field_load(i, stackReg_s8(i), stackReg_ref(i), f);
                  }
               }catch (ClassNotFoundException e){
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }catch (NoSuchFieldException e){
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }

            }
            break;
            case GETFIELD:{
               // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try{
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                  if (!f.getType().isPrimitive()){
                     ld_global(i, stackReg_ref(i), stackReg_ref(i), f);
                  }else if (f.getType().equals(int.class)){
                     ld_global(i, stackReg_s32(i), stackReg_ref(i), f);
                  }else if (f.getType().equals(short.class)){
                     ld_global(i, stackReg_s16(i), stackReg_ref(i), f);
                  }else if (f.getType().equals(char.class)){
                     ld_global(i, stackReg_u16(i), stackReg_ref(i), f);
                  }else if (f.getType().equals(boolean.class)){
                     ld_global(i, stackReg_s8(i), stackReg_ref(i), f);
                  }else if (f.getType().equals(float.class)){
                     ld_global(i, stackReg_f32(i), stackReg_ref(i), f);
                  }else if (f.getType().equals(double.class)){
                     ld_global(i, stackReg_f64(i), stackReg_ref(i), f);
                  }else if (f.getType().equals(long.class)){
                     ld_global(i, stackReg_s64(i), stackReg_ref(i), f);
                  }else{
                     throw new IllegalStateException("unexpected get field type");
                  }
               }catch (ClassNotFoundException e){
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }catch (NoSuchFieldException e){
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }

            }
            break;
            case PUTSTATIC:
               nyi(i);
               break;
            case PUTFIELD:{
               // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try{
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                  if (!f.getType().isPrimitive()){
                     st_global(i, stackReg_ref(i, 1), stackReg_ref(i), f);
                  }else if (f.getType().equals(int.class)){
                     st_global(i, stackReg_s32(i, 1), stackReg_ref(i), f);
                  }else if (f.getType().equals(short.class)){
                     st_global(i, stackReg_s16(i, 1), stackReg_ref(i), f);
                  }else if (f.getType().equals(char.class)){
                     st_global(i, stackReg_u16(i, 1), stackReg_ref(i), f);
                  }else if (f.getType().equals(boolean.class)){
                     st_global(i, stackReg_s8(i, 1), stackReg_ref(i), f);
                  }else if (f.getType().equals(float.class)){
                     st_global(i, stackReg_f32(i, 1), stackReg_ref(i), f);
                  }else if (f.getType().equals(double.class)){
                     st_global(i, stackReg_f64(i, 1), stackReg_ref(i), f);
                  }else if (f.getType().equals(long.class)){
                     st_global(i, stackReg_s64(i, 1), stackReg_ref(i), f);
                  }else{
                     throw new IllegalStateException("unexpected put field type");
                  }
               }catch (ClassNotFoundException e){
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }catch (NoSuchFieldException e){
                  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
               }

            }
            break;
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case INVOKEINTERFACE:
            case INVOKEDYNAMIC:{
               CallInfo callInfo = new CallInfo(i);
               InlineIntrinsicCall call = HSAILIntrinsics.getInlineIntrinsic(callInfo);
               if (call != null){
                  call.add(this, i);
               }else{
                  if (i.getByteCode() == InstructionSet.ByteCode.INVOKEINTERFACE){
                     //System.out.println("an interface!");
                     try{
                        Class implementation = Class.forName(callInfo.dotClassName);
                        for (Aparapi.Lambda inlineable : inline){
                           Class interfaceClass = inlineable.getClass();
                           if (implementation.isAssignableFrom(interfaceClass)){

                              //System.out.println("we can bind to this!");

                              ClassModel classModel = ClassModel.getClassModel(interfaceClass);
                              ClassModel.ClassModelMethod calledMethod = classModel.getMethod(callInfo.name, callInfo.sig);
                              HSAILStackFrame newFrame = new HSAILStackFrame(currentFrame(), calledMethod, i.getThisPC(), i.getPreStackBase()+i.getMethod().getCodeEntry().getMaxLocals()+currentStackOffset());
                              frames.push(newFrame);
                              frameSet.add(newFrame);
                              addInstructions(calledMethod);
                              frames.pop();

                           }
                        }
                     }catch (Exception e){
                        e.printStackTrace();
                        ;
                     }

                  }else{
                     try{
                        Class theClass = Class.forName(callInfo.dotClassName);
                        ClassModel classModel = ClassModel.getClassModel(theClass);
                        ClassModel.ClassModelMethod calledMethod = classModel.getMethod(callInfo.name, callInfo.sig);
                        HSAILStackFrame newFrame = new HSAILStackFrame(currentFrame(), calledMethod, i.getThisPC(), i.getPreStackBase()+i.getMethod().getCodeEntry().getMaxLocals()+currentStackOffset());
                        frames.push(newFrame);
                        frameSet.add(newFrame);
                        addInstructions(calledMethod);
                        frames.pop();
                     }catch (ClassParseException cpe){

                     }catch (ClassNotFoundException cnf){

                     }
                  }

               }
            }
            break;
            case NEW:
               nyi(i);
               break;
            case NEWARRAY:
               nyi(i);
               break;
            case ANEWARRAY:
               nyi(i);
               break;
            case ARRAYLENGTH:
               array_len(i, stackReg_s32(i), stackReg_ref(i));

               break;
            case ATHROW:
               nyi(i);
               break;
            case CHECKCAST:
               checkcast(i);
               break;
            case INSTANCEOF:
               nyi(i);
               break;
            case MONITORENTER:
               nyi(i);
               break;
            case MONITOREXIT:
               nyi(i);
               break;
            case WIDE:
               nyi(i);
               break;
            case MULTIANEWARRAY:
               nyi(i);
               break;
            case JSR_W:
               nyi(i);
               break;

         }
      }

   }

   int stackIdx(Instruction _i){
      return (currentFrame().stackIdx(_i));
   }

   HSAILStackFrame currentFrame(){
      return (frames.peek());
   }

   List<HSAILInstruction> getInstructions(){
      return (instructions);
   }

   List<HSAILStackFrame> getFrameSet(){
      return (frameSet);
   }

   int currentStackOffset(){
      return (currentFrame().stackOffset);
   }

   //---
   VarReg_s32 varReg_s32(int _slot){
      return (new VarReg_s32(_slot));
   }

   VarReg_s32 varReg_s32(Instruction _i, int _offset){
      return (new VarReg_s32(_i, _offset));
   }

   VarReg_s32 varReg_s32(Instruction _i){
      return (varReg_s32(_i, currentStackOffset()));
   }

   //---
   VarReg_f32 varReg_f32(int _slot){
      return (new VarReg_f32(_slot));
   }

   VarReg_f32 varReg_f32(Instruction _i, int _offset){
      return (new VarReg_f32(_i, _offset));
   }

   VarReg_f32 varReg_f32(Instruction _i){
      return (varReg_f32(_i, currentStackOffset()));
   }

   //---
   VarReg_s64 varReg_s64(int _slot){
      return (new VarReg_s64(_slot));
   }

   VarReg_s64 varReg_s64(Instruction _i, int _offset){
      return (new VarReg_s64(_i, _offset));
   }

   VarReg_s64 varReg_s64(Instruction _i){
      return (varReg_s64(_i, currentStackOffset()));
   }

   //---
   VarReg_f64 varReg_f64(int _slot){
      return (new VarReg_f64(_slot));
   }

   VarReg_f64 varReg_f64(Instruction _i, int _offset){
      return (new VarReg_f64(_i, _offset));
   }

   VarReg_f64 varReg_f64(Instruction _i){
      return (varReg_f64(_i, currentStackOffset()));
   }

   //---
   VarReg_ref varReg_ref(int _slot){
      return (new VarReg_ref(_slot));
   }

   VarReg_ref varReg_ref(Instruction _i, int _offset){
      return (new VarReg_ref(_i, _offset));
   }

   VarReg_ref varReg_ref(Instruction _i){
      return (varReg_ref(_i, currentStackOffset()));
   }

   StackReg_u64 stackReg_u64(int _slot){
      return (new StackReg_u64(_slot));
   }

   StackReg_u64 stackReg_u64(Instruction _i, int _offset){
      return (stackReg_u64(stackIdx(_i)+_offset));
   }

   StackReg_u64 stackReg_u64(Instruction _i){
      return (stackReg_u64(_i, 0));
   }

   StackReg_s64 stackReg_s64(int _slot){
      return (new StackReg_s64(_slot));
   }

   StackReg_s64 stackReg_s64(Instruction _i, int _offset){
      return (stackReg_s64(stackIdx(_i)+_offset));
   }

   StackReg_s64 stackReg_s64(Instruction _i){
      return (stackReg_s64(_i, 0));
   }

   StackReg_f64 stackReg_f64(int _slot){
      return (new StackReg_f64(_slot));
   }

   StackReg_f64 stackReg_f64(Instruction _i, int _offset){
      return (stackReg_f64(stackIdx(_i)+_offset));
   }

   StackReg_f64 stackReg_f64(Instruction _i){
      return (stackReg_f64(_i, 0));
   }

   StackReg_s32 stackReg_s32(int _slot){
      return (new StackReg_s32(_slot));
   }

   StackReg_s32 stackReg_s32(Instruction _i, int _offset){
      return (stackReg_s32(stackIdx(_i)+_offset));
   }

   StackReg_s32 stackReg_s32(Instruction _i){
      return (stackReg_s32(_i, 0));
   }

   StackReg_f32 stackReg_f32(int _slot){
      return (new StackReg_f32(_slot));
   }

   StackReg_f32 stackReg_f32(Instruction _i, int _offset){
      return (stackReg_f32(stackIdx(_i)+_offset));
   }

   StackReg_f32 stackReg_f32(Instruction _i){
      return (stackReg_f32(_i, 0));
   }

   StackReg_s16 stackReg_s16(int _slot){
      return (new StackReg_s16(_slot));
   }

   StackReg_s16 stackReg_s16(Instruction _i, int _offset){
      return (stackReg_s16(stackIdx(_i)+_offset));
   }

   StackReg_s16 stackReg_s16(Instruction _i){
      return (stackReg_s16(_i, 0));
   }

   StackReg_u16 stackReg_u16(int _slot){
      return (new StackReg_u16(_slot));
   }

   StackReg_u16 stackReg_u16(Instruction _i, int _offset){
      return (stackReg_u16(stackIdx(_i)+_offset));
   }

   StackReg_u16 stackReg_u16(Instruction _i){
      return (stackReg_u16(_i, 0));
   }

   StackReg_s8 stackReg_s8(int _slot){
      return (new StackReg_s8(_slot));
   }

   StackReg_s8 stackReg_s8(Instruction _i, int _offset){
      return (stackReg_s8(stackIdx(_i)+_offset));
   }

   StackReg_s8 stackReg_s8(Instruction _i){
      return (stackReg_s8(_i, 0));
   }

   StackReg_ref stackReg_ref(int _slot){
      return (new StackReg_ref(_slot));
   }

   StackReg_ref stackReg_ref(Instruction _i, int _offset){
      return (stackReg_ref(stackIdx(_i)+_offset));
   }

   StackReg_ref stackReg_ref(Instruction _i){
      return (stackReg_ref(_i, 0));
   }
}

