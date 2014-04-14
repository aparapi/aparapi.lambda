package com.amd.aparapi;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/27/13
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */

class HSAILStackFrame{
   public int stackOffset;
   HSAILStackFrame parent;
   private String nameSpace;
   private String uniqueName;
   int id = 0;
   private int callPc;

   int stackIdx(Instruction _i){
      return (_i.getPreStackBase()+_i.getMethod().getCodeEntry().getMaxLocals()+stackOffset);
   }

   String getUniqueName(){
      return (uniqueName);
   }

   String getUniqueLocation(int _pc){
      return (String.format("%s_%04d", getUniqueName(), _pc));
   }

   int incId(){
      if (parent == null){
         return (id++);
      }
      return (parent.incId());
   }

   HSAILStackFrame(HSAILStackFrame _parent, ClassModel.ClassModelMethod _calledMethod, int _callPc, int _stackOffset){

      parent = _parent;
      stackOffset = _stackOffset;
      callPc = _callPc;
      nameSpace = _calledMethod.getClassModel().getDotClassName()+"."+_calledMethod.getName()+_calledMethod.getDescriptor();
      synchronized(HSAILStackFrame.class){
         uniqueName = String.format("%04d", incId());
      }
   }

   public HSAILRenderer renderStack(HSAILRenderer rc){
      if (parent != null){
         parent.renderStack(rc);
      }
      rc.pad(5).append(String.format("%04d -> %s", callPc, nameSpace)).nl();
      return (rc);
   }
}

interface CallSignature{
}

interface IItoI extends CallSignature{
   int op(int lhs, int rhs);
}

interface DDtoD extends CallSignature{
   double op(double lhs, double rhs);
}

class Call<T extends CallSignature>{
   T call;

   Call(T _call){
      call = _call;
   }

}

class InlineIntrinsicCall{

   interface IntrinsicAssembler{
      void assemble(HSAILAssembler _assembler, Instruction _from);
   }

   IntrinsicAssembler intrinsicAssembler;
   boolean isStatic;
   private String mappedMethod; // i.e  java.lang.Math.sqrt(D)D
   public Call call;

   String getMappedMethod(){
      return (mappedMethod);
   }

   public void add(HSAILAssembler _assembler, Instruction _from){
      intrinsicAssembler.assemble(_assembler, _from);
   }

   InlineIntrinsicCall(String _mappedMethod, boolean _isStatic, IntrinsicAssembler _intrinsicAssembler){
      mappedMethod = _mappedMethod;
      isStatic = _isStatic;
      intrinsicAssembler = _intrinsicAssembler;
   }

   InlineIntrinsicCall(Call _call, boolean _isStatic, IntrinsicAssembler _intrinsicAssembler){
      call = _call;

      isStatic = _isStatic;
      intrinsicAssembler = _intrinsicAssembler;
   }

}

class CallInfo{
   Instruction from;
   String name;
   String intrinsicLookupName;
   String dotClassName;
   String sig = null;

   CallInfo(Instruction _from){
      from = _from;
      if (from.isInterfaceMethodCall()){
         dotClassName = from.asInterfaceMethodCall().getConstantPoolInterfaceMethodEntry().getClassEntry().getDotClassName();
         name = from.asInterfaceMethodCall().getConstantPoolInterfaceMethodEntry().getName();
         sig = from.asInterfaceMethodCall().getConstantPoolInterfaceMethodEntry().getNameAndTypeEntry().getDescriptor();

         /** sig to specialize CharSequence to String  - big hack!**/
         if (dotClassName.equals("java.lang.CharSequence")){
            System.out.println("Specializing java.lang.CharSequence to java.lang.String!!!!! ");
            dotClassName = "java.lang.String";
         }

      }else{
         dotClassName = from.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName();
         name = from.asMethodCall().getConstantPoolMethodEntry().getName();

         sig = from.asMethodCall().getConstantPoolMethodEntry().getNameAndTypeEntry().getDescriptor();
      }
      intrinsicLookupName = dotClassName+"."+name+sig;
   }
}

class HSAILIntrinsics{
   public static Map<String, InlineIntrinsicCall> intrinsicMap = new HashMap<String, InlineIntrinsicCall>();

   public static void add(InlineIntrinsicCall _intrinsic){
      intrinsicMap.put(_intrinsic.getMappedMethod(), _intrinsic);
   }

   static InlineIntrinsicCall getInlineIntrinsic(CallInfo _callInfo){
      return (intrinsicMap.get(_callInfo.intrinsicLookupName));
   }

   static{
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.getGridSize()I", true, (_ass, _from) -> {
         //   gridsize_s32  $s${0}, 0;
         _ass.gridsize_s32(_from, _ass.stackReg_s32(_from));

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.getWorkGroupId()I", true, (_ass, _from) -> {
         //   workgroupid_s32  $s${0}, 0;
         _ass.workgroupid_s32(_from, _ass.stackReg_s32(_from));

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.getWorkGroupSize()I", true, (_ass, _from) -> {
         //   workgroupsize_s32  $s${0}, 0;
         _ass.workgroupsize_s32(_from, _ass.stackReg_s32(_from));

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.getCurrentWorkGroupSize()I", true, (_ass, _from) -> {
         //   currentworkgroupsize_s32  $s${0}, 0;
         _ass.currentworkgroupsize_s32(_from, _ass.stackReg_s32(_from));

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.getWorkItemId()I", true, (_ass, _from) -> {
         //   workitemid_s32  $s${0}, 0;
         _ass.workitemid_s32(_from, _ass.stackReg_s32(_from));

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.barrier()V", true, (_ass, _from) -> {
         //   barrier_fgroup;

         _ass.barrier_fgroup(_from);

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.localIntX1()[I", true, (_ass, _from) -> {
         //   align 4 group_u32 %uniquename[$s${0}]
         //   lda_group_u64 $d${0}, [%uniquename];" + "\n" +
         String unique = "local_buf_"+_ass.currentFrame().getUniqueName()+"_"+_from.getThisPC();
         _ass.group_u32(_from, unique, 256);
         StackReg_ref localRef = _ass.stackReg_ref(_from);
         localRef.setLocal(true);
         _ass.lda_group_u64(_from, localRef, unique);

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.localFloatX1()[F", true, (_ass, _from) -> {
         //   align 4 group_f32 %uniquename[$s${0}]
         //   lda_group_u64 $d${0}, [%uniquename];" + "\n" +
         String unique = "local_buf_"+_ass.currentFrame().getUniqueName()+"_"+_from.getThisPC();
         _ass.group_f32(_from, unique, 256);
         StackReg_ref localRef = _ass.stackReg_ref(_from);
         localRef.setLocal(true);
         _ass.lda_group_u64(_from, localRef, unique);

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.localObjectX1()[Ljava/lang/Object;", true, (_ass, _from) -> {
         //   align 8 group_u64 %uniquename[$s${0}]
         //   lda_group_u64 $d${0}, [%uniquename];" + "\n" +
         String unique = "local_buf_"+_ass.currentFrame().getUniqueName()+"_"+_from.getThisPC();
         _ass.group_ref(_from, unique, 256);
         StackReg_ref localRef = _ass.stackReg_ref(_from);
         localRef.setLocal(true);
         _ass.lda_group_u64(_from, localRef, unique);

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.localIntX2()[I", true, (_ass, _from) -> {
         //   align 4 group_u32 %uniquename[$s${0}]
         //   lda_group_u64 $d${0}, [%uniquename];" + "\n" +
         String unique = "local_buf_"+_ass.currentFrame().getUniqueName()+"_"+_from.getThisPC();
         _ass.group_u32(_from, unique, 512);
         StackReg_ref localRef = _ass.stackReg_ref(_from);
         localRef.setLocal(true);
         _ass.lda_group_u64(_from, localRef, unique);

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.getCountUpLane()I", true, (_ass, _from) -> {
         //   countuplane_s32 $s${0}

         _ass.countuplane_s32(_from, _ass.stackReg_s32(_from));

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.getMaskLane()I", true, (_ass, _from) -> {
         //   masklane_s32 $s${0}
         _ass.masklane_s32(_from, _ass.stackReg_s32(_from));

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.getLaneId()I", true, (_ass, _from) -> {
         //   laneid_s32 $s${0}
         _ass.laneid_s32(_from, _ass.stackReg_s32(_from));

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.getComputeUnitId()I", true, (_ass, _from) -> {
         //   cuid_s32 $s${0}
         _ass.cuid_s32(_from, _ass.stackReg_s32(_from));

      }));
      add(new InlineIntrinsicCall("com.amd.aparapi.HSA.getClock()J", true, (_ass, _from) -> {
         //   clock_u64 $s${0}
         _ass.clock_s64(_from, _ass.stackReg_s64(_from));

      }));
      add(new InlineIntrinsicCall("java.lang.Math.sqrt(D)D", true, (_ass, _from) -> {
         //   nsqrt_f64  $d${0}, $d${0};
         _ass.nsqrt(_from, _ass.stackReg_f64(_from), _ass.stackReg_f64(_from));

      }));
      add(new InlineIntrinsicCall("java.lang.String.charAt(I)C", false, (_ass, _from) -> {
         // ld_global_u64 $d${2}, [$d${0}+16];   // this string reference into $d${2}"
         // mov_b32 $s${3}, $s${1};              // copy index",
         // cvt_u64_s32 $d${3}, $s${3};          // convert array index to 64 bits",
         // mad_u64 $d${3}, $d${3}, 2, $d${2};   // get the char address",
         // ld_global_u16 $s${0}, [$d${3}+24];   // ld the char"

         // ld_global_u64 $d${2}, [$d${0}+16];   // this string reference into $d${2}"
         _ass.ld_global(_from, _ass.stackReg_ref(_from, 2), _ass.stackReg_ref(_from), 16); // 16 is known hardcoded offset of char[] in String

         // mov_b32 $s${3}, $s${1};              // copy index",
         _ass.mov(_from, _ass.stackReg_s32(_from, +3), _ass.stackReg_s32(_from, 1));

         // cvt_u64_s32 $d${3}, $s${3};          // convert array index to 64 bits",
         _ass.cvt(_from, _ass.stackReg_u64(_from, 3), _ass.stackReg_s32(_from, 3));

         // mad_u64 $d${3}, $d${3}, 2, $d${2};   // get the char address",
         _ass.mad(_from, _ass.stackReg_ref(_from, 3), _ass.stackReg_ref(_from, 3), _ass.stackReg_ref(_from, 2), 2);

         // ld_global_u16 $s${0}, [$d${3}+24];   // ld the char"
         _ass.ld_global(_from, _ass.stackReg_u16(_from), _ass.stackReg_ref(_from, 3), 24); // 24 is known offset from beginning of array to step over length

      }));
      add(new InlineIntrinsicCall("java.lang.Math.cos(D)D", true, (_ass, _from) -> {
         _ass.ncos(_from, _ass.stackReg_f64(_from), _ass.stackReg_f64(_from));

      }));
      add(new InlineIntrinsicCall("java.lang.Math.sin(D)D", true, (_ass, _from) -> {
         _ass.nsin(_from, _ass.stackReg_f64(_from), _ass.stackReg_f64(_from));

      }));
      add(new InlineIntrinsicCall("java.lang.Math.hypot(DD)D", true, (_ass, _from) -> {
         //mul_f64 $d0, $d0, $d0;",
         //mul_f64 $d1, $d1, $d1;",
         //add_f64 $d0, $d0, $d1;",
         //nsqrt_f64  $d0, $d0;",
         _ass.mul(_from, _ass.stackReg_f64(_from), _ass.stackReg_f64(_from), _ass.stackReg_f64(_from));
         _ass.mul(_from, _ass.stackReg_f64(_from, 1), _ass.stackReg_f64(_from, 1), _ass.stackReg_f64(_from, 1));
         _ass.add(_from, _ass.stackReg_f64(_from), _ass.stackReg_f64(_from), _ass.stackReg_f64(_from, 1));
         _ass.nsqrt(_from, _ass.stackReg_f64(_from), _ass.stackReg_f64(_from));

      }));
      add(new InlineIntrinsicCall("java.lang.Math.min(II)I", true, (_ass, _from) -> {
         // cmp_ge_b1_s32 $c1, $s0, $s1;
         // cmov_b32 $s0, $c1, $s1, $s0;
         _ass.cmp(_from, "ge", _ass.stackReg_s32(_from), _ass.stackReg_s32(_from, 1));
         _ass.cmov(_from, _ass.stackReg_s32(_from), _ass.stackReg_s32(_from, 1), _ass.stackReg_s32(_from));

      }));

      add(new InlineIntrinsicCall("java.lang.Math.max(II)I", true, (_ass, _from) -> {
         // cmp_le_b1_s32 $c1, $s0, $s1;
         // cmov_b32 $s0, $c1, $s1, $s0;
         _ass.cmp(_from, "le", _ass.stackReg_s32(_from), _ass.stackReg_s32(_from, 1));
         _ass.cmov(_from, _ass.stackReg_s32(_from), _ass.stackReg_s32(_from, 1), _ass.stackReg_s32(_from));

      }));
      add(new InlineIntrinsicCall("java.lang.Math.max(DD)D", true, (_ass, _from) -> {
         // cmp_le_b1_s32 $c1, $d0, $d1;
         // cmov_b32 $d0, $c1, $d1, $d0;
         _ass.cmp(_from, "le", _ass.stackReg_f64(_from), _ass.stackReg_f64(_from, 1));
         _ass.cmov(_from, _ass.stackReg_f64(_from), _ass.stackReg_f64(_from, 1), _ass.stackReg_f64(_from));

      }));

      add(new InlineIntrinsicCall("java.lang.Math.min(DD)D", true, (_ass, _from) -> {
         // cmp_ge_b1_s32 $c1, $d0, $d1;
         // cmov_b32 $d0, $c1, $d1, $d0;
         _ass.cmp(_from, "ge", _ass.stackReg_f64(_from), _ass.stackReg_f64(_from, 1));
         _ass.cmov(_from, _ass.stackReg_f64(_from), _ass.stackReg_f64(_from, 1), _ass.stackReg_f64(_from));

      }));

   }

}

public class HSAILMethod{
   HSAILAssembler assembler;
   ClassModel.ClassModelMethod method;

   public HSAILRenderer render(HSAILRenderer r, Object lambdaType){
      r.append("version 0:95: $full : $large").semicolon().nl();
      r.append("kernel &run").oparenth();
      boolean firstArg = true;
      int argc = 0;
      if (method.isNonStatic()){
         r.nl().pad(3).kernarg(ref.ref, 0);
         firstArg = false;
         argc++;
      }

      for (TypeHelper.JavaMethodArg arg : method.argsAndReturnType.getArgs()){
         if (!firstArg){
            r.separator();
         }
         r.nl().pad(3).kernarg(arg.getJavaType().getPrimitiveType(), argc++);
         firstArg = false;
      }
      r.nl().pad(3).cparenth().obrace().nl();

      Instruction last = null; // we track the last bytecode instruction (not HSAIL) here so that we con emit branch labels and comments only once for each mapped instruction
      for (HSAILInstructionSet.HSAILInstruction i : assembler.getInstructions()){
         if ((i instanceof HSAILInstructionSet.ld_kernarg) || (i instanceof HSAILInstructionSet.workitemabsid)){

         }else if ((last == null || last != i.from)){
            if (i.from.isBranchTarget()){
               r.label(i.location).colon();
            }
            if (r.isShowingComments()){
               r.nl().pad(1).lineCommentStart().mark().append(i.location).relpad(2).space().i(i.from).nl();
            }
            last = i.from;
         }else{
            last = i.from;
         }
         r.pad(9);
         i.render(r);
         r.nl();
      }
      r.cbrace().semicolon().nl();
      r.commentStart();
      for (HSAILStackFrame hsailStackFrame : assembler.getFrameSet()){
         r.nl().append(hsailStackFrame.getUniqueName()).append("=").obrace().nl();
         hsailStackFrame.renderStack(r).cbrace().nl();
      }
      r.nl().commentEnd();
      return (r);
   }

   static synchronized HSAILMethod getHSAILMethod(ClassModel.ClassModelMethod _method, Aparapi.Lambda _lambda){
      HSAILMethod instance = new HSAILMethod(_method, _lambda);
      return (instance);
   }

   private HSAILMethod(ClassModel.ClassModelMethod _method, Aparapi.Lambda _lambda){
      method = _method;

      assembler = new HSAILAssembler(method);

      if (UnsafeWrapper.addressSize() == 4){
         throw new IllegalStateException("Object pointer size is 4, you need to use 64 bit JVM and set -XX:-UseCompressedOops!");
      }

      Instruction initial = method.getInstructions().iterator().next();
      int argOffset = 0;
      if (!method.isStatic()){
         assembler.ld_kernarg(initial, assembler.varReg_ref(0));
         // HSAILInstructionSet.ld_arg( initial, assembler.varReg_ref(0)); // if we need to support real calls.
         argOffset++;
      }
      TypeHelper.JavaMethodArg[] args = method.argsAndReturnType.getArgs();
      int argc = args.length;
      for (TypeHelper.JavaMethodArg arg : args){
         if (arg.getJavaType().isArray() || arg.getJavaType().isObject()){
            assembler.ld_kernarg(initial, assembler.varReg_ref(arg.getArgc()+argOffset));
            // assembler.ld_arg( initial, assembler.varReg_ref(arg.getArgc() + argOffset)); // if we need to support real calls.
         }else if (arg.getJavaType().isInt()){
            assembler.ld_kernarg(initial, assembler.varReg_s32(arg.getArgc()+argOffset));
            // assembler.ld_arg( initial, assembler.varReg_s32(arg.getArgc() + argOffset)); // if we need to support real calls.
         }else if (arg.getJavaType().isFloat()){
            assembler.ld_kernarg(initial, assembler.varReg_f32(arg.getArgc()+argOffset));
            // assembler.ld_arg( initial, assembler.varReg_s32(arg.getArgc() + argOffset)); // if we need to support real calls.
         }else if (arg.getJavaType().isDouble()){
            assembler.ld_kernarg(initial, assembler.varReg_f64(arg.getArgc()+argOffset));
            // assembler.ld_arg( initial, assembler.varReg_s32(arg.getArgc() + argOffset)); // if we need to support real calls.
         }else if (arg.getJavaType().isLong()){
            assembler.ld_kernarg(initial, assembler.varReg_s64(arg.getArgc()+argOffset));
            // assembler.ld_arg( initial, assembler.varReg_s32(arg.getArgc() + argOffset)); // if we need to support real calls.
         }
      }

      if (_lambda instanceof Aparapi.IntTerminal){
         assembler.workitemabsid_u32(initial, assembler.varReg_s32(argc+argOffset)); // we overwrite the last arg +1 with the gid
         assembler.add(initial, assembler.stackReg_s32(argc+argOffset-1), assembler.stackReg_s32(argc+argOffset-1), assembler.stackReg_s32(argc+argOffset));
      }else if (_lambda instanceof Aparapi.ObjectTerminal){
         assembler.workitemabsid_u32(initial, assembler.varReg_s32(argc+argOffset)); // we overwrite the last arg +1 with the gid
         assembler.cvt(initial, assembler.stackReg_u64(argc+argOffset), assembler.stackReg_s32(argc+argOffset));
         assembler.mad(initial, assembler.stackReg_ref(argc+argOffset), assembler.stackReg_ref(argc+argOffset), assembler.stackReg_ref(argc+argOffset-1), 8);
         // ld_global_u64 $d${2}, [$d${0}+16];   // this string reference into $d${2}"
         assembler.ld_global(initial, assembler.stackReg_ref(argc+argOffset-1), assembler.stackReg_ref(argc+argOffset), 24); // 16 is known hardcoded offset of char[] in String

         // mov_b32 $s${3}, $s${1};              // copy index",
         // _ass.mov(_from, _ass.stackReg_s32(_from, +3), _ass.stackReg_s32(_from, 1));

         // cvt_u64_s32 $d${3}, $s${3};          // convert array index to 64 bits",
         //   _ass.cvt(_from, _ass.stackReg_u64(_from, 3), _ass.stackReg_s32(_from, 3));

         // mad_u64 $d${3}, $d${3}, 2, $d${2};   // get the char address",
         //  _ass.mad(_from, _ass.stackReg_ref(_from,3), _ass.stackReg_ref(_from,3), _ass.stackReg_ref(_from,2), 2);
         //      assembler.add(initial, assembler.stackReg_s32(argc + argOffset - 1), assembler.stackReg_s32(argc + argOffset - 1), assembler.stackReg_s32(argc + argOffset));

      }else{
         System.out.println("lambda type");
      }
      assembler.addInstructions(method);
   }
}
