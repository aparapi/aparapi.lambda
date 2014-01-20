package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/27/13
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */


    class HSAILStackFrame {
        Instruction caller;
        public int bottom;
        public int top;     // VARS + Stack
        private String nameSpace;

        // The following two fields only have value if this is the root frame.  ie parentHSAILStackFrame == null
        Map<HSAILStackFrame,Integer> locMap = new LinkedHashMap<HSAILStackFrame, Integer>();
        int loc=0;
        public String getLocation(HSAILStackFrame _parentHSAILStackFrame, int pc){
           if (parentHSAILStackFrame != null){
              return(parentHSAILStackFrame.getLocation(_parentHSAILStackFrame, pc));
           }
           Integer thisLoc = locMap.get(_parentHSAILStackFrame);
           if (thisLoc == null){
              thisLoc = loc++;
              locMap.put(_parentHSAILStackFrame, thisLoc);
           }
           return(String.format("%04d_%04d", thisLoc, pc));
        }

        public String getUniqueNameSpace(HSAILStackFrame _parentHSAILStackFrame){
            if (parentHSAILStackFrame != null){
                return(parentHSAILStackFrame.getUniqueNameSpace(_parentHSAILStackFrame));
            }
            Integer thisLoc = locMap.get(_parentHSAILStackFrame);
            if (thisLoc == null){
                thisLoc = loc++;
                locMap.put(_parentHSAILStackFrame, thisLoc);
            }
            return(String.format("%04d", thisLoc));

        }
        public String getLocation(int pc){
               return(getLocation(this, pc));
        }
        public String getUniqueNameSpace(){
            return(getUniqueNameSpace(this));
        }
        HSAILStackFrame parentHSAILStackFrame = null;
        HSAILStackFrame(HSAILStackFrame _parentHSAILStackFrame, String _nameSpace, Instruction _caller, int _bottom, int _top){
            caller = _caller;
            parentHSAILStackFrame = _parentHSAILStackFrame;
            if (parentHSAILStackFrame != null){
               top = parentHSAILStackFrame.top + _top;
               bottom = parentHSAILStackFrame.top + _bottom;
            }else{
               top = _top;
               bottom = _bottom;
            }
            nameSpace=_nameSpace;
        }

        public void renderStack(HSAILRenderer rc) {
            if (parentHSAILStackFrame != null){
                parentHSAILStackFrame.renderStack(rc);
            }
            rc.pad(5).append(nameSpace).nl();
        }
    }



   abstract class InlineIntrinsicCall {

      boolean isStatic;
      private String mappedMethod; // i.e  java.lang.Math.sqrt(D)D

      String getMappedMethod() {
         return (mappedMethod);
      }

      public abstract List<HSAILInstructionSet.HSAILInstruction> add(List<HSAILInstructionSet.HSAILInstruction> _instructions, HSAILStackFrame _hsailStackFrame, Instruction _from);


      InlineIntrinsicCall(String _mappedMethod, boolean _isStatic) {
         mappedMethod = _mappedMethod;
         isStatic = _isStatic;
        }

    }

class CallInfo{
    Instruction from;
    String name;
    String intrinsicLookupName;
    String dotClassName;
    String sig = null;
    CallInfo( Instruction _from) {
        from  = _from;
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
        intrinsicLookupName = dotClassName + "." + name + sig;
    }
}

class HSAILIntrinsics {
    public static Map<String, InlineIntrinsicCall> intrinsicMap = new HashMap<String, InlineIntrinsicCall>();

    public static void add(InlineIntrinsicCall _intrinsic) {
        intrinsicMap.put(_intrinsic.getMappedMethod(), _intrinsic);
    }

    static {
        add(new InlineIntrinsicCall("java.lang.Math.sqrt(D)D", true){
            public List<HSAILInstructionSet.HSAILInstruction> add(List<HSAILInstructionSet.HSAILInstruction> _instructions, HSAILStackFrame _hsailStackFrame, Instruction _from){
                //   nsqrt_f64  $d${0}, $d${0};
                _instructions.add(new HSAILInstructionSet.nsqrt<StackReg_f64, f64>(_hsailStackFrame, _from, new StackReg_f64(_from, _hsailStackFrame.top,0)));
                return(_instructions);
            }
        });
        add(new InlineIntrinsicCall( "java.lang.String.charAt(I)C", false){
            public List<HSAILInstructionSet.HSAILInstruction> add(List<HSAILInstructionSet.HSAILInstruction> _instructions, HSAILStackFrame _hsailStackFrame, Instruction _from){
                // ld_global_u64 $d${2}, [$d${0}+16];   // this string reference into $d${2}"
                // mov_b32 $s${3}, $s${1};              // copy index",
                // cvt_u64_s32 $d${3}, $s${3};          // convert array index to 64 bits",
                // mad_u64 $d${3}, $d${3}, 2, $d${2};   // get the char address",
                // ld_global_u16 $s${0}, [$d${3}+24];   // ld the char"

                // ld_global_u64 $d${2}, [$d${0}+16];   // this string reference into $d${2}"
                _instructions.add(new HSAILInstructionSet.field_load<StackReg_u64, u64>(_hsailStackFrame, _from, new StackReg_u64(_from,_hsailStackFrame.top, 2),  new StackReg_ref(_from,_hsailStackFrame.top, 0), 16));

                // mov_b32 $s${3}, $s${1};              // copy index",
                _instructions.add(new HSAILInstructionSet.mov<StackReg_s32, StackReg_s32,  s32,  s32>(_hsailStackFrame, _from, new StackReg_s32(_from,_hsailStackFrame.top, 3),  new StackReg_s32(_from, _hsailStackFrame.top,1)));

                // cvt_u64_s32 $d${3}, $s${3};          // convert array index to 64 bits",
                _instructions.add(new HSAILInstructionSet.cvt<StackReg_u64, StackReg_s32,  u64,  s32>(_hsailStackFrame, _from, new StackReg_u64(_from,_hsailStackFrame.top, 3),  new StackReg_s32(_from,_hsailStackFrame.top, 3)));

                // mad_u64 $d${3}, $d${3}, 2, $d${2};   // get the char address",
                _instructions.add(new HSAILInstructionSet.mad(_hsailStackFrame, _from, new StackReg_ref(_from,_hsailStackFrame.top, 3),new StackReg_ref(_from,_hsailStackFrame.top, 3),   new StackReg_ref(_from, _hsailStackFrame.top,2), 2));

                // ld_global_u16 $s${0}, [$d${3}+24];   // ld the char"
                _instructions.add(new HSAILInstructionSet.field_load<StackReg_u16, u16>(_hsailStackFrame, _from, new StackReg_u16(_from,_hsailStackFrame.top, 0),  new StackReg_ref(_from,_hsailStackFrame.top, 3), 24));
                return(_instructions);
            }
        });
        add(new InlineIntrinsicCall("java.lang.Math.cos(D)D", true){
            public List<HSAILInstructionSet.HSAILInstruction> add(List<HSAILInstructionSet.HSAILInstruction> _instructions, HSAILStackFrame _hsailStackFrame, Instruction _from){
                _instructions.add(new HSAILInstructionSet.ncos<StackReg_f64, f64>(_hsailStackFrame, _from,  new StackReg_f64(_from, _hsailStackFrame.top,0)));
                return(_instructions);
            }
        });
        add(new InlineIntrinsicCall("java.lang.Math.sin(D)D", true ){
            public List<HSAILInstructionSet.HSAILInstruction> add(List<HSAILInstructionSet.HSAILInstruction> _instructions, HSAILStackFrame _hsailStackFrame, Instruction _from){
                _instructions.add(new HSAILInstructionSet.nsin<StackReg_f64, f64>(_hsailStackFrame, _from,  new StackReg_f64(_from,_hsailStackFrame.top, 0)));
                return(_instructions);
            }
        });
        add(new InlineIntrinsicCall("java.lang.Math.hypot(DD)D", true ){
            public List<HSAILInstructionSet.HSAILInstruction> add(List<HSAILInstructionSet.HSAILInstruction> _instructions, HSAILStackFrame _hsailStackFrame, Instruction _from){
                //mul_f64 $d0, $d0, $d0;",
                //mul_f64 $d1, $d1, $d1;",
                //add_f64 $d0, $d0, $d1;",
                //nsqrt_f64  $d0, $d0;",
                _instructions.add(new HSAILInstructionSet.mul<StackReg_f64, f64>(_hsailStackFrame, _from,  new StackReg_f64(_from,_hsailStackFrame.top, 0),  new StackReg_f64(_from,_hsailStackFrame.top, 0),  new StackReg_f64(_from,_hsailStackFrame.top, 0)));
                _instructions.add(new HSAILInstructionSet.mul<StackReg_f64, f64>(_hsailStackFrame, _from,  new StackReg_f64(_from, _hsailStackFrame.top,1),  new StackReg_f64(_from, _hsailStackFrame.top,1),  new StackReg_f64(_from,_hsailStackFrame.top, 1)));
                _instructions.add(new HSAILInstructionSet.add<StackReg_f64, f64>(_hsailStackFrame, _from,  new StackReg_f64(_from,_hsailStackFrame.top, 0),  new StackReg_f64(_from,_hsailStackFrame.top, 0),  new StackReg_f64(_from,_hsailStackFrame.top, 1)));
                _instructions.add(new HSAILInstructionSet.nsqrt<StackReg_f64, f64>(_hsailStackFrame, _from,  new StackReg_f64(_from, _hsailStackFrame.top,0)));
                return(_instructions);
            }
        });

    }

    static InlineIntrinsicCall getInlineIntrinsic(CallInfo _callInfo){

    InlineIntrinsicCall call = null;
    for (InlineIntrinsicCall ic : HSAILIntrinsics.intrinsicMap.values()) {
        if (ic.getMappedMethod().equals(_callInfo.intrinsicLookupName)) {
            call = (InlineIntrinsicCall)ic;
            break;
        }
    }
        return(call);
    }
}

public class HSAILMethod {


    HSAILMethod entryPoint;
    HSAILStackFrame hsailStackFrame;
    List<HSAILInstructionSet.HSAILInstruction> instructions = new ArrayList<HSAILInstructionSet.HSAILInstruction>();
    ClassModel.ClassModelMethod method;

  //  public void add(SimpleMethodCall call) {
     //   getEntryPoint().calls.add(call);
  //  }

    HSAILMethod getEntryPoint() {
        if (entryPoint == null) {
            return (this);
        }
        return (entryPoint.getEntryPoint());
    }




    public HSAILRenderer renderInlinedFunctionBody(HSAILRenderer r,  int base) {
        Set<Instruction> s = new HashSet<Instruction>();

        String endLabel = null;
        for (HSAILInstructionSet.HSAILInstruction i : instructions) {
            if (!(i instanceof HSAILInstructionSet.ld_arg)){
               if (!s.contains(i.from)) {
                   s.add(i.from);
                   if (i.from.isBranchTarget()) {
                      r.label(i.location).colon().nl();
                   }
                   if (r.isShowingComments()) {
                      r.nl().pad(1).lineCommentStart().append(i.location).mark().relpad(2).space().i(i.from).nl();
                   }
               }
               if (i instanceof HSAILInstructionSet.retvoid){
                   r.pad(9).lineCommentStart().append(" ret removed as part of inlining");
               }else if (i instanceof HSAILInstructionSet.ret){

                   r.pad(9).lineComment("ret removed and replaced by branch to end of code").nl();
                   r.pad(9).append("mov_").movTypeName(((HSAILInstructionSet.ret) i).getSrc()).space().regPrefix(((HSAILInstructionSet.ret) i).getSrc().type).append(base).separator().operandName(((HSAILInstructionSet.ret) i).getSrc()).semicolon();
                   if (i != instructions.get(instructions.size()-1)){
                      endLabel = ((HSAILInstructionSet.ret)i).endLabel;
                      r.nl().pad(9).append("brn ").label(endLabel).semicolon();

                   }
               }else{
                   r.pad(9);
                   i.render(r);
               }
               r.nl();
            }
        }
        if (endLabel!=null){
            r.label(endLabel).colon().nl();
        }
        return (r);
    }

    public HSAILRenderer renderEntryPoint(HSAILRenderer r) {
        r.append("version 0:95: $full : $large").semicolon().nl();

        r.append("kernel &run").oparenth();
        boolean firstArg=true;
        int argc = 0;
        if (method.isNonStatic()) {
            r.nl().pad(3).kernarg(ref.ref, 0);
            firstArg = false;
            argc++;
        }

        for (TypeHelper.JavaMethodArg arg : method.argsAndReturnType.getArgs()) {
            if (!firstArg) {
                r.separator();
            }
            r.nl().pad(3).kernarg(arg.getJavaType().getPrimitiveType(), argc++);
            firstArg=false;
        }
        r.nl().pad(3).cparenth().obrace().nl();

        Instruction last = null; // we track the last bytecode instruction (not HSAIL) here so that we con emit branch labels and comments only once for each mapped instruction
        for (HSAILInstructionSet.HSAILInstruction i : instructions) {
            if ((i  instanceof HSAILInstructionSet.ld_kernarg) || (i instanceof HSAILInstructionSet.workitemabsid)){

            }else if ( (last == null || last != i.from)) {
                if (i.from.isBranchTarget()) {
                    r.label(i.location).colon();
                }
                if (r.isShowingComments()) {
                    r.nl().pad(1).lineCommentStart().mark().append(i.location).relpad(2).space().i(i.from).nl();
                }
            }else{
                last = i.from;
            }
            r.pad(9);
            i.render(r);
            r.nl();
        }
        r.cbrace().semicolon().nl().commentStart();
        /*
        for (Map.Entry<HSAILStackFrame, Integer> e:instructions.iterator().next().getHSAILStackFrame().locMap.entrySet()){
            r.nl().append(String.format("%04d",e.getValue())).append("=").obrace().nl();
            e.getKey().renderStack(r);
            r.cbrace().nl();
        }
        */
        r.nl().commentEnd();
        return (r);
    }

    static synchronized HSAILMethod getHSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint, HSAILStackFrame _HSAIL_stackFrame, Instruction _caller,  int _bottom, int _top) {
        HSAILMethod instance = new HSAILMethod(_method, _entryPoint, _HSAIL_stackFrame, _caller, _bottom, _top);
        return (instance);
    }


    static synchronized HSAILMethod getHSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint, HSAILStackFrame _HSAIL_stackFrame) {
       return(getHSAILMethod(_method, _entryPoint, _HSAIL_stackFrame, null, 0, 0));
    }

    static synchronized HSAILMethod getHSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint) {
       return getHSAILMethod(_method, _entryPoint, null);
    }

    private HSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint, HSAILStackFrame _HSAIL_stackFrame, Instruction _caller, int _bottom, int _top) {
        hsailStackFrame = new HSAILStackFrame(_HSAIL_stackFrame, _method.getClassModel().getDotClassName()+"."+_method.getName()+_method.getDescriptor(), _caller, _bottom, _top);

        entryPoint = _entryPoint;
        if (UnsafeWrapper.addressSize() == 4) {
            throw new IllegalStateException("Object pointer size is 4, you need to use 64 bit JVM and set -XX:-UseCompressedOops!");
        }
        method = _method;

        Instruction initial = method.getInstructions().iterator().next();


                int argOffset = 0;
                if (!method.isStatic()) {
                    if (entryPoint == null) {
                        HSAILInstructionSet.ld_kernarg_ref(instructions, hsailStackFrame, initial, 0);
                    } else {
                        HSAILInstructionSet.ld_arg_ref(instructions, hsailStackFrame, initial, 0);
                    }
                    argOffset++;
                }
                TypeHelper.JavaMethodArg[] args = method.argsAndReturnType.getArgs();
                int argc = args.length;
                for (TypeHelper.JavaMethodArg arg : args) {
                    if (arg.getJavaType().isArray() || arg.getJavaType().isObject()) {
                        if (_entryPoint == null) {
                            HSAILInstructionSet.ld_kernarg_ref(instructions, hsailStackFrame, initial, arg.getArgc() + argOffset);
                        } else {
                            HSAILInstructionSet.ld_arg_ref(instructions, hsailStackFrame, initial, arg.getArgc() + argOffset);
                        }
                    } else if (arg.getJavaType().isInt()) {
                        if (_entryPoint == null) {
                            HSAILInstructionSet.ld_kernarg_s32(instructions, hsailStackFrame, initial, arg.getArgc() + argOffset);
                        } else {
                            HSAILInstructionSet.ld_arg_s32(instructions, hsailStackFrame, initial, arg.getArgc() + argOffset);
                        }
                    } else if (arg.getJavaType().isFloat()) {
                        if (_entryPoint == null) {
                            HSAILInstructionSet.ld_kernarg_f32(instructions, hsailStackFrame, initial, arg.getArgc() + argOffset);
                        } else {
                            HSAILInstructionSet.ld_arg_f32(instructions, hsailStackFrame, initial, arg.getArgc() + argOffset);
                        }
                    } else if (arg.getJavaType().isDouble()) {
                        if (_entryPoint == null) {
                            HSAILInstructionSet.ld_kernarg_f64(instructions, hsailStackFrame, initial, arg.getArgc() + argOffset);
                        } else {
                            HSAILInstructionSet.ld_arg_f64(instructions, hsailStackFrame, initial, arg.getArgc() + argOffset);
                        }
                    } else if (arg.getJavaType().isLong()) {
                        if (_entryPoint == null) {
                            HSAILInstructionSet.ld_kernarg_s64(instructions, hsailStackFrame, initial, arg.getArgc() + argOffset);
                        } else {
                            HSAILInstructionSet.ld_arg_s64(instructions, hsailStackFrame, initial, arg.getArgc() + argOffset);
                        }
                    }
                }
                if (_entryPoint ==null){
                    HSAILInstructionSet.workitemabsid_u32(instructions, hsailStackFrame, initial, argc + argOffset -1); // we overwrite the last arg with the gid
                }

    HSAILInstructionSet.addInstructions(instructions, hsailStackFrame, method);
    }
}
