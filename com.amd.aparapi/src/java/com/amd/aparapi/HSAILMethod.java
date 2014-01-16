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
        public int baseOffset;
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
        HSAILStackFrame(HSAILStackFrame _parentHSAILStackFrame, String _nameSpace, int _baseOffset){
            parentHSAILStackFrame = _parentHSAILStackFrame;
            if (parentHSAILStackFrame != null){
               baseOffset = parentHSAILStackFrame.baseOffset + _baseOffset;
            }else{
               baseOffset = _baseOffset;
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



    abstract class CallType<T extends CallType> {
        private String mappedMethod; // i.e  java.lang.Math.sqrt(D)D

        List<HSAILInstructionSet.HSAILInstruction> instructions = new ArrayList<HSAILInstructionSet.HSAILInstruction>();

        String getMappedMethod() {
            return (mappedMethod);
        }

        CallType(String _mappedMethod) {

            mappedMethod = _mappedMethod;
        }

        abstract T renderDefinition(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame);
        abstract T renderDeclaration(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame);
        abstract T renderCallSite(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame, Instruction from,  String name, int base);

        abstract boolean isStatic();
        T add(HSAILInstructionSet.HSAILInstruction i){
            instructions.add(i);
            return((T)this);
        }

    }


   class IntrinsicCall extends CallType<IntrinsicCall> {
        String[] lines;
        boolean isStatic;


        IntrinsicCall(String _mappedMethod, boolean _isStatic, String... _lines) {
            super(_mappedMethod);
            lines = _lines;
            isStatic = _isStatic;
        }

        @Override
        IntrinsicCall renderDefinition(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame) {
            for (String line : lines) {
                if (!(line.trim().endsWith("{") || line.trim().startsWith("}"))) {
                    r.pad(9);
                }
                r.append(line).nl();
            }
            return this;
        }
        @Override
           IntrinsicCall renderCallSite(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame,   Instruction from,  String name, int base) {
            return(this);
        }
        @Override
        IntrinsicCall renderDeclaration(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame) {
            return(this);
        }
        @Override
        boolean isStatic() {
            return (isStatic);
        }
    }

   class InlineIntrinsicCall extends IntrinsicCall {
        InlineIntrinsicCall(String _mappedMethod, boolean _isStatic,  String... _lines) {
            super( _mappedMethod, _isStatic, _lines);
        }
        final Pattern regex= Pattern.compile("\\$\\{([0-9]+)\\}");
        String expand(String line, HSAILStackFrame _HSAIL_stackFrame, int base){
            StringBuffer sb= new StringBuffer();
            Matcher matcher = regex.matcher(line);

            while (matcher.find()) {
                matcher.appendReplacement(sb, String.format("%d",Integer.parseInt(matcher.group(1))+((_HSAIL_stackFrame == null)?0: _HSAIL_stackFrame.baseOffset+base)));
            }
            matcher.appendTail(sb);

            return(sb.toString());
        }

        @Override
        InlineIntrinsicCall renderCallSite(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame,  Instruction from, String name, int base) {
            boolean first = false;
            r.lineComment("inlining intrinsic "+getMappedMethod()+"{");
            if (isStatic){
                r.pad(9).lineComment(expand("This is a static method so $?${0} contains first arg (if any)", _HSAIL_stackFrame, base));
            }else{
                r.pad(9).lineComment(expand("This is a virtual method so $d${0} contains this. Other args (if any) from $?${1}", _HSAIL_stackFrame, base));
            }
            for (String line : lines) {
               // if (!first){
                    r.pad(9);
              //  }
                String expandedLine = expand(line, _HSAIL_stackFrame, base);

                r.append(expandedLine).nl();
                //first = false;
            }
            r.pad(9);
            r.lineComment("} inlining intrinsic "+getMappedMethod());
            return this;
        }
        @Override
        InlineIntrinsicCall renderDefinition(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame) {
            return(this);
        }
        @Override
        IntrinsicCall renderDeclaration(HSAILRenderer r,HSAILStackFrame _HSAIL_stackFrame) {
            return(this);
        }
        @Override
        boolean isStatic() {
            return (isStatic);
        }
    }

    class SimpleMethodCall extends CallType<SimpleMethodCall> {
        HSAILMethod method;


        SimpleMethodCall(String _mappedMethod, HSAILMethod _method) {
            super(_mappedMethod);
            method = _method;
        }

        @Override
        SimpleMethodCall renderDefinition(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame) {

            method.renderFunctionDefinition(r);
            r.nl().nl();
            return (this);
        }
        @Override
        SimpleMethodCall renderCallSite(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame,  Instruction from, String name, int _base) {

            TypeHelper.JavaMethodArgsAndReturnType argsAndReturnType = from.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType();
            TypeHelper.JavaType returnType = argsAndReturnType.getReturnType();
            r.obrace().nl();
            if (!isStatic()) {
                r.pad(12).append("arg_u64 %this").semicolon().nl();
                r.pad(12).append("st_arg_u64 $d" + _HSAIL_stackFrame.baseOffset + ", [%this]").semicolon().nl();
            }

            int offset = 0;
            if (!isStatic()) {
                offset++;
            }
            for (TypeHelper.JavaMethodArg arg : argsAndReturnType.getArgs()) {
                String argName = "%_arg_" + arg.getArgc();
                r.pad(12).append("arg_").typeName(arg.getJavaType()).space().append(argName).semicolon().nl();
                r.pad(12).append("st_arg_").typeName(arg.getJavaType()).space().regPrefix(arg.getJavaType()).append( + (_HSAIL_stackFrame.baseOffset + offset) + ", [" + argName + "]").semicolon().nl();
            }
            if (!returnType.isVoid()) {
                r.pad(12).append("arg_").typeName(returnType).append(" %_result").semicolon().nl();
            }
            r.pad(12).append("call &").append(name).space();
            r.oparenth();
            if (!returnType.isVoid()) {
                r.append("%_result");
            }
            r.cparenth().space();

            r.oparenth();
            if (!isStatic()) {
                r.append("%this ");
            }

            for (TypeHelper.JavaMethodArg arg : argsAndReturnType.getArgs()) {
                if (arg.getArgc() + offset > 0) {
                    r.separator();
                }
                r.append("%_arg_" + arg.getArgc());

            }
            r.cparenth().semicolon().nl();
            if (!returnType.isVoid()) {
                r.pad(12).append("ld_arg_").typeName(returnType).space().regPrefix(returnType).append( _HSAIL_stackFrame.baseOffset + ", [%_result]").semicolon().nl();
            }
            r.pad(9).cbrace();

            r.nl().nl();
            return(this);
        }
        @Override
        SimpleMethodCall renderDeclaration(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame) {
            method.renderFunctionDeclaration(r);
            r.semicolon().nl().nl();
            return (this);
        }
        @Override
        boolean isStatic() {
            return (method.method.isStatic());
        }
    }

    class InlineMethodCall extends CallType<InlineMethodCall> {
        HSAILMethod method;

        InlineMethodCall(String _mappedMethod, HSAILMethod _method) {
            super( _mappedMethod);
            method = _method;
        }
        @Override
        InlineMethodCall renderDefinition(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame) {
            return (this);
        }
        @Override
        InlineMethodCall renderCallSite(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame, Instruction from, String name, int base) {

            method.renderInlinedFunctionBody(r, base);

            //r.nl();
            return (this);
        }

        @Override
        InlineMethodCall renderDeclaration(HSAILRenderer r, HSAILStackFrame _HSAIL_stackFrame) {
            return (this);
        }

        @Override
        boolean isStatic() {
            return (method.method.isStatic());
        }
    }
class HSAILIntrinsics {
    public static Map<String, IntrinsicCall> intrinsicMap = new HashMap<String, IntrinsicCall>();

    public static void add(IntrinsicCall _intrinsic) {
        intrinsicMap.put(_intrinsic.getMappedMethod(), _intrinsic);
    }

    static {
       // add(new IntrinsicCall("java.lang.Math.sqrt(D)D", true,
              //  "function &sqrt (arg_f64 %_result) (arg_f64 %_val) {",
             //   "ld_arg_f64  $d0, [%_val];",
              //  "nsqrt_f64  $d0, $d0;",
              //  "st_arg_f64  $d0, [%_result];",
               // "ret;",
               // "};"));
        add(
                (new InlineIntrinsicCall("java.lang.Math.sqrt(D)D", true,
                "nsqrt_f64  $d${0}, $d${0};"
                )
    )/*.add(new nsqrt<StackReg_f64,f64>(null, null, new StackReg_f64(0), new StackReg_f64(0)))*/
        );
        add(new InlineIntrinsicCall( "java.lang.String.charAt(I)C", false,
                "ld_global_u64 $d${2}, [$d${0}+16];   // this string reference into $d${2}",
                "mov_b32 $s${3}, $s${1};              // copy index",
                "cvt_u64_s32 $d${3}, $s${3};          // convert array index to 64 bits",
                "mad_u64 $d${3}, $d${3}, 2, $d${2};      // get the char address",
                "ld_global_u16 $s${0}, [$d${3}+24];   // ld the char"
        ));
        add(new InlineIntrinsicCall("java.lang.Math.cos(D)D", true,
                "ncos_f64  $d${0}, $d${0};"
    ));
        add(new InlineIntrinsicCall("java.lang.Math.sin(D)D", true,
                "nsin_f64  $d${0}, $d${0};"
        ));
        add(new IntrinsicCall( "java.lang.Math.hypot(DD)D", true,
                "function &hypot (arg_f64 %_result) (arg_f64 %_val1, arg_f64 %_val2) {",
                "ld_arg_f64  $d0, [%_val1];",
                "ld_arg_f64  $d1, [%_val2];",
                "mul_f64 $d0, $d0, $d1;",
                "nsqrt_f64  $d0, $d0;",
                "st_arg_f64  $d0, [%_result];",
                "ret;",
                "};"));
    }
}

public class HSAILMethod {

    private Set<CallType> calls = null;

    public void add(CallType call) {
        getEntryPoint().calls.add(call);
    }


    HSAILMethod getEntryPoint() {
        if (entryPoint == null) {
            return (this);
        }
        return (entryPoint.getEntryPoint());
    }
    List<HSAILInstructionSet.HSAILInstruction> instructions = new ArrayList<HSAILInstructionSet.HSAILInstruction>();
    ClassModel.ClassModelMethod method;

    boolean optimizeMoves =  false || Config.enableOptimizeRegMoves;


    public HSAILRenderer renderFunctionDeclaration(HSAILRenderer r) {
        r.append("function &").append(method.getName()).append("(");
        if (method.getArgsAndReturnType().getReturnType().isInt()) {
            r.append("arg_s32 %_result");
        }
        r.append(") (");

        int argOffset = method.isStatic() ? 0 : 1;
        if (!method.isStatic()) {
            r.nl().pad(3).append("arg_u64 %_arg0");
        }

        for (TypeHelper.JavaMethodArg arg : method.argsAndReturnType.getArgs()) {
            if (!((method.isStatic() && arg.getArgc() == 0))) {
                r.separator();
            }
            r.nl().pad(3).append("arg_");

            PrimitiveType type = arg.getJavaType().getPrimitiveType();
            if (type == null) {
                r.append("u64");
            } else {
                r.append(type.getHSAName());
            }
            r.append(" %_arg" + (arg.getArgc() + argOffset));
        }
        r.nl().pad(3).append(")");
        return (r);
    }

    public HSAILRenderer renderFunctionDefinition(HSAILRenderer r) {
        renderFunctionDeclaration(r);

            r.obrace().nl();
            Set<Instruction> s = new HashSet<Instruction>();

            for (HSAILInstructionSet.HSAILInstruction i : instructions) {
                if (!(i instanceof HSAILInstructionSet.ld_kernarg || i instanceof HSAILInstructionSet.ld_arg) && !s.contains(i.from)) {
                    s.add(i.from);
                    if (i.from.isBranchTarget()) {
                        r.label("func_"+i.from.getThisPC()).colon().nl();
                    }
                    if (r.isShowingComments()) {
                        r.nl().pad(1).lineCommentStart().mark().append(i.getHSAILStackFrame().getLocation(i.from.getThisPC())).relpad(2).space().i(i.from).nl();
                    }
                }
                r.pad(9);
                i.render(r);
                r.nl();
            }
            r.cbrace().semicolon();



        return (r);
    }

    public HSAILRenderer renderInlinedFunctionBody(HSAILRenderer r,  int base) {
        Set<Instruction> s = new HashSet<Instruction>();
        boolean endBranchNeeded = false;

        for (HSAILInstructionSet.HSAILInstruction i : instructions) {
            if (!(i instanceof HSAILInstructionSet.ld_arg)){
               if (!s.contains(i.from)) {
                   s.add(i.from);
                     if (i.from.isBranchTarget()) {
                         r.label(i.getHSAILStackFrame().getLocation(i.from.getThisPC())).colon().nl();
                     }
                    if (r.isShowingComments()) {
                        r.nl().pad(1).lineCommentStart().append(i.getHSAILStackFrame().getLocation(i.from.getThisPC())).mark().relpad(2).space().i(i.from).nl();
                    }
                }
                if (i instanceof HSAILInstructionSet.retvoid){
                    r.pad(9).lineCommentStart().append("ret").semicolon();
                }else if (i instanceof HSAILInstructionSet.ret){
                  r.pad(9).append("mov_").movTypeName(((HSAILInstructionSet.ret)i).getSrc()).space().regPrefix(((HSAILInstructionSet.ret)i).getSrc().type).append(base).separator().regName(((HSAILInstructionSet.ret)i).getSrc(), i.getHSAILStackFrame()).semicolon();
                  if (i != instructions.get(instructions.size()-1)){
                  r.nl().pad(9).append("brn @L"+ i.getHSAILStackFrame().getUniqueNameSpace()+"_END").semicolon();
                  endBranchNeeded = true;
                  }
                  //r.nl().pad(9).lineCommentStart().append("st_arg_").typeName(((ret)i).getSrc()).space().regName(((ret)i).getSrc(), _renderContext).separator().append("[%_result]").semicolon().nl();
                  //r.pad(9).lineCommentStart().append("ret").semicolon();
              }   else{
                  r.pad(9);
                  i.render(r);
              }
                r.nl();


            }
        }
        if (endBranchNeeded){
        r.append("@L"+instructions.iterator().next().getHSAILStackFrame().getUniqueNameSpace()+"_END").colon().nl();
        }
        return (r);
    }

    public HSAILRenderer renderEntryPoint(HSAILRenderer r) {
        //r.append("version 1:0:large;").nl();
        r.append("version 0:95: $full : $large").semicolon().nl();

       // RenderContext rc = new RenderContext(null, this.method.getClassModel().getDotClassName()+"."+this.method.getName()+this.method.getDescriptor(), 0);
        for (CallType c : calls) {
            c.renderDeclaration(r, null);
        }

        for (CallType c : calls) {
            c.renderDefinition(r, null);
        }
        r.append("kernel &run").oparenth();
        int argOffset = method.isStatic() ? 0 : 1;
        if (!method.isStatic()) {
            r.nl().pad(3).append("kernarg_u64 %_arg0");
        }

        for (TypeHelper.JavaMethodArg arg : method.argsAndReturnType.getArgs()) {
            if ((method.isStatic() && arg.getArgc() == 0)) {
                r.nl();
            } else {
                r.separator().nl();
            }

            PrimitiveType type = arg.getJavaType().getPrimitiveType();
            r.pad(3).append("kernarg_");
            if (type == null) {
                r.append("u64");
            } else {
                r.append(type.getHSAName());
            }
            r.append(" %_arg" + (arg.getArgc() + argOffset));
        }
        r.nl().pad(3).cparenth().obrace().nl();

        java.util.Set<Instruction> s = new java.util.HashSet<Instruction>();
        boolean first = false;
        int count = 0;

        for (HSAILInstructionSet.HSAILInstruction i : instructions) {
            if (!(i instanceof HSAILInstructionSet.ld_kernarg) && !s.contains(i.from)) {
                if (!first) {
                    r.pad(9).append("workitemabsid_u32 $s" + (count - 1) + ", 0").semicolon().nl();
                    // r.pad(9).append("workitemaid $s" + (count - 1) + ", 0;").nl();
                    first = true;
                }
                s.add(i.from);
                if (i.from.isBranchTarget()) {

                    r.label(i.getHSAILStackFrame().getLocation(i.from.getThisPC())).colon().nl();
                }
                if (r.isShowingComments()) {
                    r.nl().pad(1).lineCommentStart().mark().append(i.getHSAILStackFrame().getLocation(i.from.getThisPC())).relpad(2).space().i(i.from).nl();
                }

            } else {
                count++;
            }
            r.pad(9);
            i.render(r);
            r.nl();
        }
        r.cbrace().semicolon();
        r.nl().commentStart();
        for (Map.Entry<HSAILStackFrame, Integer> e:instructions.iterator().next().getHSAILStackFrame().locMap.entrySet()){
            r.nl().append(String.format("%04d",e.getValue())).append("=").obrace().nl();
            e.getKey().renderStack(r);
            r.cbrace().nl();
        }
        r.nl().commentEnd();
        return (r);
    }





    enum ParseState {NONE, COMPARE_F32, COMPARE_F64, COMPARE_S64}

    ;

   static Map<ClassModel.ClassModelMethod, HSAILMethod> cache = new HashMap<ClassModel.ClassModelMethod, HSAILMethod>();
   static boolean useCache = false; // don't turn this on until we have inlining working


    static synchronized HSAILMethod getHSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint, HSAILStackFrame _HSAIL_stackFrame, int _base) {
        HSAILMethod instance = null;
        if (useCache){
            instance = cache.get(_method);
        }
        if (instance == null) {
            instance = new HSAILMethod(_method, _entryPoint, _HSAIL_stackFrame, _base);
            if (useCache){
                cache.put(_method, instance);
            }
        }
        return (instance);
    }


    static synchronized HSAILMethod getHSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint, HSAILStackFrame _HSAIL_stackFrame) {
       return(getHSAILMethod(_method, _entryPoint, _HSAIL_stackFrame, 0));
    }

    static synchronized HSAILMethod getHSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint) {
       return getHSAILMethod(_method, _entryPoint, null);
    }

    HSAILMethod entryPoint;
    HSAILStackFrame hsailStackFrame;

    private HSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint, HSAILStackFrame _HSAIL_stackFrame, int _base) {
        hsailStackFrame = new HSAILStackFrame(_HSAIL_stackFrame, _method.getClassModel().getDotClassName()+"."+_method.getName()+_method.getDescriptor(), _base);

        entryPoint = _entryPoint;
        if (entryPoint == null) {
            calls = new HashSet<CallType>();
        }
        if (UnsafeWrapper.addressSize() == 4) {
            throw new IllegalStateException("Object pointer size is 4, you need to use 64 bit JVM and set -XX:-UseCompressedOops!");
        }
        method = _method;
        ParseState parseState = ParseState.NONE;
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
                for (TypeHelper.JavaMethodArg arg : method.argsAndReturnType.getArgs()) {
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

    for (Instruction i : method.getInstructions()) {

            switch (i.getByteCode()) {

                case ACONST_NULL:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
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
                    HSAILInstructionSet.mov_s32_const(instructions, hsailStackFrame, i,  i.asIntegerConstant().getValue());
                    break;
                case LCONST_0:
                case LCONST_1:
                    HSAILInstructionSet.mov_s64_const(instructions, hsailStackFrame, i, i.asLongConstant().getValue());
                    break;
                case FCONST_0:
                case FCONST_1:
                case FCONST_2:
                    HSAILInstructionSet.mov_f32_const(instructions, hsailStackFrame, i, i.asFloatConstant().getValue());
                    break;
                case DCONST_0:
                case DCONST_1:
                    HSAILInstructionSet.mov_f64_const(instructions, hsailStackFrame, i, i.asDoubleConstant().getValue());

                    break;
                // case BIPUSH: moved up
                // case SIPUSH: moved up

                case LDC:
                case LDC_W:
                case LDC2_W: {
                    InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant) i;

                    ClassModel.ConstantPool.ConstantEntry e = (ClassModel.ConstantPool.ConstantEntry) cpe.getConstantPoolEntry();
                    if (e instanceof ClassModel.ConstantPool.DoubleEntry) {
                        HSAILInstructionSet.mov_f64_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.DoubleEntry) e).getValue());
                    } else if (e instanceof ClassModel.ConstantPool.FloatEntry) {
                        HSAILInstructionSet.mov_f32_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.FloatEntry) e).getValue());
                    } else if (e instanceof ClassModel.ConstantPool.IntegerEntry) {
                        HSAILInstructionSet.mov_s32_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.IntegerEntry) e).getValue());
                    } else if (e instanceof ClassModel.ConstantPool.LongEntry) {
                         HSAILInstructionSet.mov_s64_const(instructions, hsailStackFrame, i, ((ClassModel.ConstantPool.LongEntry) e).getValue());
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
                    HSAILInstructionSet.mov_s32_var(instructions, hsailStackFrame, i);

                    break;
                case LLOAD:
                case LLOAD_0:
                case LLOAD_1:
                case LLOAD_2:
                case LLOAD_3:
                    HSAILInstructionSet.mov_s64_var(instructions, hsailStackFrame, i);
                    break;
                case FLOAD:
                case FLOAD_0:
                case FLOAD_1:
                case FLOAD_2:
                case FLOAD_3:

                    HSAILInstructionSet.mov_f32_var(instructions, hsailStackFrame, i);
                    break;
                case DLOAD:
                case DLOAD_0:
                case DLOAD_1:
                case DLOAD_2:
                case DLOAD_3:

                    HSAILInstructionSet.mov_f64_var(instructions, hsailStackFrame, i);
                    break;
                case ALOAD:
                case ALOAD_0:
                case ALOAD_1:
                case ALOAD_2:
                case ALOAD_3:
                    HSAILInstructionSet.mov_ref_var(instructions, hsailStackFrame, i);

                    break;
                case IALOAD:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.s32.getHsaBytes());
                    HSAILInstructionSet.array_load_s32(instructions, hsailStackFrame, i);
                    break;
                case LALOAD:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.s64.getHsaBytes());
                    HSAILInstructionSet.array_load_s64(instructions, hsailStackFrame, i);
                    break;
                case FALOAD:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.f32.getHsaBytes());
                    HSAILInstructionSet.array_load_f32(instructions, hsailStackFrame, i);
                    break;
                case DALOAD:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.f64.getHsaBytes());
                    HSAILInstructionSet.array_load_f64(instructions, hsailStackFrame, i);
                    break;
                case AALOAD:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.ref.getHsaBytes());
                    HSAILInstructionSet.array_load_ref(instructions, hsailStackFrame, i);
                    break;
                case BALOAD:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.s8.getHsaBytes());
                    HSAILInstructionSet.array_load_s8(instructions, hsailStackFrame, i);
                    break;
                case CALOAD:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.u16.getHsaBytes());
                    HSAILInstructionSet.array_load_u16(instructions, hsailStackFrame, i);
                    break;
                case SALOAD:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.s32.getHsaBytes());
                    HSAILInstructionSet.array_load_s16(instructions, hsailStackFrame, i);
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
                    HSAILInstructionSet.mov_var_s32(instructions, hsailStackFrame, i);

                    break;
                case LSTORE:
                case LSTORE_0:
                case LSTORE_1:
                case LSTORE_2:
                case LSTORE_3:
                    HSAILInstructionSet.mov_var_s64(instructions, hsailStackFrame, i);

                    break;
                case FSTORE:
                case FSTORE_0:
                case FSTORE_1:
                case FSTORE_2:
                case FSTORE_3:
                    HSAILInstructionSet.mov_var_f32(instructions, hsailStackFrame, i);
                    break;
                case DSTORE:
                case DSTORE_0:
                case DSTORE_1:
                case DSTORE_2:
                case DSTORE_3:
                    HSAILInstructionSet.mov_var_f64(instructions, hsailStackFrame, i);
                    break;
                case ASTORE:
                case ASTORE_0:
                case ASTORE_1:
                case ASTORE_2:
                case ASTORE_3:
                    HSAILInstructionSet.mov_var_ref(instructions, hsailStackFrame, i);
                    break;
                case IASTORE:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.s32.getHsaBytes());
                    HSAILInstructionSet.array_store_s32(instructions, hsailStackFrame, i);
                    break;
                case LASTORE:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.s64.getHsaBytes());
                    HSAILInstructionSet.array_store_s64(instructions, hsailStackFrame, i);
                    break;
                case FASTORE:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.f32.getHsaBytes());
                    HSAILInstructionSet.array_store_f32(instructions, hsailStackFrame, i);
                    break;
                case DASTORE:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.f64.getHsaBytes());
                    HSAILInstructionSet.array_store_f64(instructions, hsailStackFrame, i);
                    break;
                case AASTORE:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.ref.getHsaBytes());
                    HSAILInstructionSet.array_store_ref(instructions, hsailStackFrame, i);
                    break;
                case BASTORE:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.s8.getHsaBytes());
                    HSAILInstructionSet.array_store_s8(instructions, hsailStackFrame, i);
                    break;
                case CASTORE:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.u16.getHsaBytes());
                    HSAILInstructionSet.array_store_u16(instructions, hsailStackFrame, i);
                    break;
                case SASTORE:
                    HSAILInstructionSet.cvt_ref_s32_1(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.mad(instructions, hsailStackFrame, i, PrimitiveType.s16.getHsaBytes());
                    HSAILInstructionSet.array_store_s16(instructions, hsailStackFrame, i);
                    break;
                case POP:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame,i);
                    break;
                case POP2:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame,i);
                    break;
                case DUP:
                   // add(new nyi(i));
                    HSAILInstructionSet.addmov(instructions, hsailStackFrame, i, 0, 1);
                    break;
                case DUP_X1:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame,i);
                    break;
                case DUP_X2:

                    HSAILInstructionSet.addmov(instructions, hsailStackFrame, i, 2, 3);
                    HSAILInstructionSet.addmov(instructions, hsailStackFrame, i, 1, 2);
                    HSAILInstructionSet.addmov(instructions, hsailStackFrame, i, 0, 1);
                    HSAILInstructionSet.addmov(instructions, hsailStackFrame, i, 3, 0);

                    break;
                case DUP2:
                    // DUP2 is problematic. DUP2 either dups top two items or one depending on the 'prefix' of the stack items.
                    // To complicate this further HSA large model wants object/mem references to be 64 bits (prefix 2 in Java) whereas
                    // in java object/array refs are 32 bits (prefix 1).
                    HSAILInstructionSet.addmov(instructions, hsailStackFrame, i, 0, 2);
                    HSAILInstructionSet.addmov(instructions, hsailStackFrame, i, 1, 3);
                    break;
                case DUP2_X1:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame,i);
                    break;
                case DUP2_X2:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame,i);
                    break;
                case SWAP:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame,i);
                    break;
                case IADD:
                    HSAILInstructionSet.add_s32(instructions, hsailStackFrame, i);
                    break;
                case LADD:
                    HSAILInstructionSet.add_s64(instructions, hsailStackFrame, i);
                    break;
                case FADD:
                    HSAILInstructionSet.add_f32(instructions, hsailStackFrame, i);
                    break;
                case DADD:
                    HSAILInstructionSet.add_f64(instructions, hsailStackFrame, i);
                    break;
                case ISUB:
                    HSAILInstructionSet.sub_s32(instructions, hsailStackFrame, i);
                    break;
                case LSUB:
                    HSAILInstructionSet.sub_s64(instructions, hsailStackFrame, i);
                    break;
                case FSUB:
                    HSAILInstructionSet.sub_f32(instructions, hsailStackFrame, i);
                    break;
                case DSUB:
                    HSAILInstructionSet.sub_f64(instructions, hsailStackFrame, i);
                    break;
                case IMUL:
                    HSAILInstructionSet.mul_s32(instructions, hsailStackFrame, i);
                    break;
                case LMUL:
                    HSAILInstructionSet.mul_s64(instructions, hsailStackFrame, i);
                    break;
                case FMUL:
                    HSAILInstructionSet.mul_f32(instructions, hsailStackFrame, i);
                    break;
                case DMUL:
                    HSAILInstructionSet.mul_f64(instructions, hsailStackFrame, i);
                    break;
                case IDIV:
                    HSAILInstructionSet.div_s32(instructions, hsailStackFrame, i);
                    break;
                case LDIV:
                    HSAILInstructionSet.div_s64(instructions, hsailStackFrame, i);
                    break;
                case FDIV:
                    HSAILInstructionSet.div_f32(instructions, hsailStackFrame, i);
                    break;
                case DDIV:
                    HSAILInstructionSet.div_f64(instructions, hsailStackFrame, i);
                    break;
                case IREM:
                    HSAILInstructionSet.rem_s32(instructions, hsailStackFrame, i);
                    break;
                case LREM:
                    HSAILInstructionSet.rem_s64(instructions, hsailStackFrame, i);
                    break;
                case FREM:
                    HSAILInstructionSet.rem_f32(instructions, hsailStackFrame, i);
                    break;
                case DREM:
                    HSAILInstructionSet.rem_f64(instructions, hsailStackFrame, i);
                    break;
                case INEG:
                    HSAILInstructionSet.neg_s32(instructions, hsailStackFrame, i);
                    break;
                case LNEG:
                    HSAILInstructionSet.neg_s64(instructions, hsailStackFrame, i);
                    break;
                case FNEG:
                    HSAILInstructionSet.neg_f32(instructions, hsailStackFrame, i);
                    break;
                case DNEG:
                    HSAILInstructionSet.neg_f64(instructions, hsailStackFrame, i);
                    break;
                case ISHL:
                    HSAILInstructionSet.shl_s32(instructions, hsailStackFrame, i);
                    break;
                case LSHL:
                    HSAILInstructionSet.shl_s64(instructions, hsailStackFrame, i);
                    break;
                case ISHR:
                    HSAILInstructionSet.shr_s32(instructions, hsailStackFrame, i);
                    break;
                case LSHR:
                    HSAILInstructionSet.shr_s64(instructions, hsailStackFrame, i);
                    break;
                case IUSHR:
                    HSAILInstructionSet.ushr_s32(instructions, hsailStackFrame, i);
                    break;
                case LUSHR:
                    HSAILInstructionSet.ushr_s64(instructions, hsailStackFrame, i);
                    break;
                case IAND:
                    HSAILInstructionSet.and_s32(instructions, hsailStackFrame, i);
                    break;
                case LAND:
                    HSAILInstructionSet.and_s64(instructions, hsailStackFrame, i);
                    break;
                case IOR:
                    HSAILInstructionSet.or_s32(instructions, hsailStackFrame, i);
                    break;
                case LOR:
                    HSAILInstructionSet.or_s64(instructions, hsailStackFrame, i);
                    break;
                case IXOR:
                    HSAILInstructionSet.xor_s32(instructions, hsailStackFrame, i);
                    break;
                case LXOR:
                    HSAILInstructionSet.xor_s64(instructions, hsailStackFrame, i);
                    break;
                case IINC:
                    HSAILInstructionSet.add_const_s32(instructions, hsailStackFrame, i);
                    break;
                case I2L:
                    HSAILInstructionSet.cvt_s64_s32(instructions, hsailStackFrame, i);
                    break;
                case I2F:
                    HSAILInstructionSet.cvt_f32_s32(instructions, hsailStackFrame, i);
                    break;
                case I2D:
                    HSAILInstructionSet.cvt_f64_s32(instructions, hsailStackFrame, i);
                    break;
                case L2I:
                    HSAILInstructionSet.cvt_s32_s64(instructions, hsailStackFrame, i);
                    break;
                case L2F:
                    HSAILInstructionSet.cvt_f32_s64(instructions, hsailStackFrame, i);
                    break;
                case L2D:
                    HSAILInstructionSet.cvt_f64_s64(instructions, hsailStackFrame, i);
                    break;
                case F2I:
                    HSAILInstructionSet.cvt_s32_f32(instructions, hsailStackFrame, i);
                    break;
                case F2L:
                    HSAILInstructionSet.cvt_s64_f32(instructions, hsailStackFrame, i);
                    break;
                case F2D:
                    HSAILInstructionSet.cvt_f64_f32(instructions, hsailStackFrame, i);
                    break;
                case D2I:
                    HSAILInstructionSet.cvt_s32_f64(instructions, hsailStackFrame, i);
                    break;
                case D2L:
                    HSAILInstructionSet.cvt_s64_f64(instructions, hsailStackFrame, i);
                    break;
                case D2F:
                    HSAILInstructionSet.cvt_f32_f64(instructions, hsailStackFrame, i);
                    break;
                case I2B:
                    HSAILInstructionSet.cvt_s8_s32(instructions, hsailStackFrame, i);
                    break;
                case I2C:
                    HSAILInstructionSet.cvt_u16_s32(instructions, hsailStackFrame, i);
                    break;
                case I2S:
                    HSAILInstructionSet.cvt_s16_s32(instructions, hsailStackFrame, i);
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
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        HSAILInstructionSet.cmp_f32_eq(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        HSAILInstructionSet.cmp_f64_eq(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        HSAILInstructionSet.cmp_s64_eq(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else {
                        HSAILInstructionSet.cmp_s32_eq_const_0(instructions, hsailStackFrame, i);
                    }
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IFNE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        HSAILInstructionSet.cmp_f32_ne(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        HSAILInstructionSet.cmp_f64_ne(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        HSAILInstructionSet.cmp_s64_ne(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else {
                        HSAILInstructionSet.cmp_s32_ne_const_0(instructions, hsailStackFrame, i);
                    }
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IFLT:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        HSAILInstructionSet.cmp_f32_lt(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        HSAILInstructionSet.cmp_f64_lt(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        HSAILInstructionSet.cmp_s64_lt(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else {
                        HSAILInstructionSet.cmp_s32_lt_const_0(instructions, hsailStackFrame, i);

                    }
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IFGE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        HSAILInstructionSet.cmp_f32_ge(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        HSAILInstructionSet.cmp_f64_ge(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        HSAILInstructionSet.cmp_s64_ge(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else {
                        HSAILInstructionSet.cmp_s32_ge_const_0(instructions, hsailStackFrame, i);

                    }
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IFGT:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        HSAILInstructionSet.cmp_f32_gt(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        HSAILInstructionSet.cmp_f64_gt(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        HSAILInstructionSet.cmp_s64_gt(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else {
                        HSAILInstructionSet.cmp_s32_gt_const_0(instructions, hsailStackFrame, i);

                    }
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IFLE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        HSAILInstructionSet.cmp_f32_le(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        HSAILInstructionSet.cmp_f64_le(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        HSAILInstructionSet.cmp_s64_le(instructions, hsailStackFrame, i);
                        parseState = ParseState.NONE;
                    } else {
                        HSAILInstructionSet.cmp_s32_le_const_0(instructions, hsailStackFrame, i);
                       

                    }
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IF_ICMPEQ:

                    HSAILInstructionSet.cmp_s32_eq(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);

                    break;
                case IF_ICMPNE:
                    HSAILInstructionSet.cmp_s32_ne(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IF_ICMPLT:
                    HSAILInstructionSet.cmp_s32_lt(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IF_ICMPGE:
                    HSAILInstructionSet.cmp_s32_ge(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IF_ICMPGT:
                    HSAILInstructionSet.cmp_s32_gt(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IF_ICMPLE:
                    HSAILInstructionSet.cmp_s32_le(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IF_ACMPEQ:
                    HSAILInstructionSet.cmp_ref_eq(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case IF_ACMPNE:
                    HSAILInstructionSet.cmp_ref_ne(instructions, hsailStackFrame, i);
                    HSAILInstructionSet.cbr(instructions, hsailStackFrame, i);
                    break;
                case GOTO:
                    HSAILInstructionSet.brn(instructions, hsailStackFrame, i);
                    break;
                case IFNULL:
                    HSAILInstructionSet.branch(instructions, hsailStackFrame, i);
                case IFNONNULL:
                    HSAILInstructionSet.branch(instructions, hsailStackFrame, i);
                case GOTO_W:
                    HSAILInstructionSet.branch(instructions, hsailStackFrame, i);
                    break;
                case JSR:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case RET:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case TABLESWITCH:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case LOOKUPSWITCH:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case IRETURN:
                    HSAILInstructionSet.ret_s32(instructions, hsailStackFrame, i);
                    break;
                case LRETURN:
                    HSAILInstructionSet.ret_s64(instructions, hsailStackFrame, i);
                    break;
                case FRETURN:
                    HSAILInstructionSet.ret_f32(instructions, hsailStackFrame, i);
                    break;
                case DRETURN:
                    HSAILInstructionSet.ret_f64(instructions, hsailStackFrame, i);
                    break;
                case ARETURN:

                    HSAILInstructionSet.ret_ref(instructions, hsailStackFrame, i);
                    break;
                case RETURN:
                    HSAILInstructionSet.ret_void(instructions, hsailStackFrame, i);
                    break;
                case GETSTATIC: {
                    TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

                    try {
                        Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                        Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());

                        if (!type.isPrimitive()) {
                            HSAILInstructionSet.static_field_load_ref(instructions, hsailStackFrame, i, f);
                        } else if (type.isInt()) {
                            HSAILInstructionSet.static_field_load_s32(instructions, hsailStackFrame, i, f);
                        } else if (type.isFloat()) {
                            HSAILInstructionSet.static_field_load_f32(instructions, hsailStackFrame, i, f);
                        } else if (type.isDouble()) {
                            HSAILInstructionSet.static_field_load_f64(instructions, hsailStackFrame, i, f);
                        } else if (type.isLong()) {
                            HSAILInstructionSet.static_field_load_s64(instructions, hsailStackFrame, i, f);
                        } else if (type.isChar()) {
                            HSAILInstructionSet.static_field_load_u16(instructions, hsailStackFrame, i, f);
                        } else if (type.isShort()) {
                            HSAILInstructionSet.static_field_load_s16(instructions, hsailStackFrame, i, f);
                        } else if (type.isChar()) {
                            HSAILInstructionSet.static_field_load_s8(instructions, hsailStackFrame, i, f);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }


                }
                break;
                case GETFIELD: {
                   // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

                    try {
                        Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                        Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                        if (!f.getType().isPrimitive()) {
                            HSAILInstructionSet.field_load_ref(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(int.class)) {
                            HSAILInstructionSet.field_load_s32(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(short.class)) {
                            HSAILInstructionSet.field_load_s16(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(char.class)) {
                            HSAILInstructionSet.field_load_u16(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(boolean.class)) {
                            HSAILInstructionSet.field_load_s8(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(float.class)) {
                            HSAILInstructionSet.field_load_f32(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(double.class)) {
                            HSAILInstructionSet.field_load_f64(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(long.class)) {
                            HSAILInstructionSet.field_load_s64(instructions, hsailStackFrame, i, f);
                        } else {
                            throw new IllegalStateException("unexpected get field type");
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }


                }
                break;
                case PUTSTATIC:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case PUTFIELD: {
                   // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

                    try {
                        Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                        Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                        if (!f.getType().isPrimitive()) {
                            HSAILInstructionSet.field_store_ref(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(int.class)) {
                            HSAILInstructionSet.field_store_s32(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(short.class)) {
                            HSAILInstructionSet.field_store_s16(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(char.class)) {
                            HSAILInstructionSet.field_store_u16(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(boolean.class)) {
                            HSAILInstructionSet.field_store_s8(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(float.class)) {
                            HSAILInstructionSet.field_store_f32(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(double.class)) {
                            HSAILInstructionSet.field_store_f64(instructions, hsailStackFrame, i, f);
                        } else if (f.getType().equals(long.class)) {
                            HSAILInstructionSet.field_store_s64(instructions, hsailStackFrame, i, f);
                        }   else {
                            throw new IllegalStateException("unexpected put field type");
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }


                }
                break;
                case INVOKEVIRTUAL:
                case INVOKESPECIAL:
                case INVOKESTATIC:
                case INVOKEINTERFACE:
                case INVOKEDYNAMIC:
                    HSAILInstructionSet.call(instructions, this, hsailStackFrame, i);
                    break;
                case NEW:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case NEWARRAY:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case ANEWARRAY:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case ARRAYLENGTH:
                    HSAILInstructionSet.array_len(instructions, hsailStackFrame, i);
                    break;
                case ATHROW:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case CHECKCAST:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case INSTANCEOF:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case MONITORENTER:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case MONITOREXIT:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case WIDE:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case MULTIANEWARRAY:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;
                case JSR_W:
                    HSAILInstructionSet.nyi(instructions, hsailStackFrame, i);
                    break;

            }
           // lastInstruction = i;


        }

    }
}
