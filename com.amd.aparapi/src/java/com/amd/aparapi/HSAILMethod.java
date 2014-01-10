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
public class HSAILMethod {

    public static class StackFrame{
        public int baseOffset;
        private String nameSpace;
        Map<StackFrame,Integer> locMap = new LinkedHashMap<StackFrame, Integer>();
        int loc=0;
        public String getLocation(StackFrame _renderContext, int pc){
           if (last != null){
              return(last.getLocation(_renderContext, pc));
           }
           Integer thisLoc = locMap.get(_renderContext);
           if (thisLoc == null){
              thisLoc = loc++;
              locMap.put(_renderContext, thisLoc);
           }
           return(String.format("%04d_%04d", thisLoc, pc));

        }

        public String getUniqueNameSpace(StackFrame _renderContext){
            if (last != null){
                return(last.getUniqueNameSpace(_renderContext));
            }
            Integer thisLoc = locMap.get(_renderContext);
            if (thisLoc == null){
                thisLoc = loc++;
                locMap.put(_renderContext, thisLoc);
            }
            return(String.format("%04d", thisLoc));

        }
        public String getLocation(int pc){
               return(getLocation(this, pc));
        }
        public String getUniqueNameSpace(){
            return(getUniqueNameSpace(this));
        }
        StackFrame last = null;
        StackFrame(StackFrame _last, String _nameSpace, int _baseOffset){
            last = _last;
            if (last != null){
               baseOffset = last.baseOffset + _baseOffset;
            }else{
               baseOffset = _baseOffset;
            }
            nameSpace=_nameSpace;
        }

        public void renderStack(HSAILRenderer rc) {
            if (last != null){
                last.renderStack(rc);
            }
            rc.pad(5).append(nameSpace).nl();
        }
    }



    public static abstract class CallType<T extends CallType> {
        private String mappedMethod; // i.e  java.lang.Math.sqrt(D)D
        protected StackFrame stackFrame;

        String getMappedMethod() {
            return (mappedMethod);
        }

        CallType(StackFrame _stackFrame, String _mappedMethod) {
            stackFrame = _stackFrame;
            mappedMethod = _mappedMethod;
        }

        abstract T renderDefinition(HSAILRenderer r);
        abstract T renderDeclaration(HSAILRenderer r);
        abstract T renderCallSite(HSAILRenderer r, Instruction from,  String name);
        abstract boolean isStatic();
    }


    public static class IntrinsicCall extends CallType<IntrinsicCall> {
        String[] lines;
        boolean isStatic;


        IntrinsicCall(StackFrame _stackFrame,String _mappedMethod, boolean _isStatic, String... _lines) {
            super(_stackFrame, _mappedMethod);
            lines = _lines;
            isStatic = _isStatic;
        }

        @Override
        IntrinsicCall renderDefinition(HSAILRenderer r) {
            for (String line : lines) {
                if (!(line.trim().endsWith("{") || line.trim().startsWith("}"))) {
                    r.pad(9);
                }
                r.append(line).nl();
            }
            return this;
        }
        @Override
           IntrinsicCall renderCallSite(HSAILRenderer r,   Instruction from,  String name) {
            return(this);
        }
        @Override
        IntrinsicCall renderDeclaration(HSAILRenderer r) {
            return(this);
        }
        @Override
        boolean isStatic() {
            return (isStatic);
        }
    }

    public static class InlineIntrinsicCall extends IntrinsicCall {



        InlineIntrinsicCall(StackFrame _stackFrame,String _mappedMethod, boolean _isStatic,  String... _lines) {
            super(_stackFrame, _mappedMethod, _isStatic, _lines);
        }



        final Pattern regex= Pattern.compile("\\$\\{([0-9]+)\\}");
        String expand(String line){
            StringBuffer sb= new StringBuffer();
            Matcher matcher = regex.matcher(line);

            while (matcher.find()) {
                matcher.appendReplacement(sb, String.format("%d",Integer.parseInt(matcher.group(1))+((stackFrame == null)?0:stackFrame.baseOffset)));
            }
            matcher.appendTail(sb);

            return(sb.toString());
        }

        @Override
        InlineIntrinsicCall renderCallSite(HSAILRenderer r,  Instruction from, String name) {
            boolean first = false;
            r.lineComment("inlining intrinsic "+getMappedMethod()+"{");
            if (isStatic){
                r.pad(9).lineComment(expand("This is a static method so $?${0} contains first arg (if any)"));
            }else{
                r.pad(9).lineComment(expand("This is a virtual method so $d${0} contains this. Other args (if any) from $?${1}"));
            }
            for (String line : lines) {
               // if (!first){
                    r.pad(9);
              //  }
                String expandedLine = expand(line);

                r.append(expandedLine).nl();
                //first = false;
            }
            r.pad(9);
            r.lineComment("} inlining intrinsic "+getMappedMethod());
            return this;
        }
        @Override
        InlineIntrinsicCall renderDefinition(HSAILRenderer r) {
            return(this);
        }
        @Override
        IntrinsicCall renderDeclaration(HSAILRenderer r) {
            return(this);
        }
        @Override
        boolean isStatic() {
            return (isStatic);
        }
    }

    public static class MethodCall extends CallType<MethodCall> {
        HSAILMethod method;


        MethodCall(StackFrame _stackFrame, String _mappedMethod, HSAILMethod _method) {
            super(_stackFrame, _mappedMethod);
            method = _method;
        }

        @Override
        MethodCall renderDefinition(HSAILRenderer r) {

            method.renderFunctionDefinition(r);
            r.nl().nl();
            return (this);
        }
        @Override
        MethodCall renderCallSite(HSAILRenderer r,  Instruction from, String name) {

            TypeHelper.JavaMethodArgsAndReturnType argsAndReturnType = from.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType();
            TypeHelper.JavaType returnType = argsAndReturnType.getReturnType();
            r.obrace().nl();
            if (!isStatic()) {
                r.pad(12).append("arg_u64 %this").semicolon().nl();
                r.pad(12).append("st_arg_u64 $d" + stackFrame.baseOffset + ", [%this]").semicolon().nl();
            }

            int offset = 0;
            if (!isStatic()) {
                offset++;
            }
            for (TypeHelper.JavaMethodArg arg : argsAndReturnType.getArgs()) {
                String argName = "%_arg_" + arg.getArgc();
                r.pad(12).append("arg_").typeName(arg.getJavaType()).space().append(argName).semicolon().nl();
                r.pad(12).append("st_arg_").typeName(arg.getJavaType()).space().regPrefix(arg.getJavaType()).append( + (stackFrame.baseOffset + offset) + ", [" + argName + "]").semicolon().nl();
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
                r.pad(12).append("ld_arg_").typeName(returnType).space().regPrefix(returnType).append( stackFrame.baseOffset + ", [%_result]").semicolon().nl();
            }
            r.pad(9).cbrace();

            r.nl().nl();
            return(this);
        }
        @Override
        MethodCall renderDeclaration(HSAILRenderer r) {
            method.renderFunctionDeclaration(r);
            r.semicolon().nl().nl();
            return (this);
        }
        @Override
        boolean isStatic() {
            return (method.method.isStatic());
        }
    }

    public static class InlineMethodCall extends CallType<InlineMethodCall> {
        HSAILMethod method;

        InlineMethodCall(StackFrame _stackFrame, String _mappedMethod, HSAILMethod _method) {
            super(_stackFrame, _mappedMethod);
            method = _method;
        }
        @Override
        InlineMethodCall renderDefinition(HSAILRenderer r) {
            return (this);
        }
        @Override
        InlineMethodCall renderCallSite(HSAILRenderer r, Instruction from, String name) {

            method.renderInlinedFunctionBody(r, stackFrame.baseOffset);

            //r.nl();
            return (this);
        }

        @Override
        InlineMethodCall renderDeclaration(HSAILRenderer r) {
            return (this);
        }

        @Override
        boolean isStatic() {
            return (method.method.isStatic());
        }
    }

    public static Map<String, IntrinsicCall> intrinsicMap = new HashMap<String, IntrinsicCall>();

    public static void add(IntrinsicCall _intrinsic) {
        intrinsicMap.put(_intrinsic.getMappedMethod(), _intrinsic);
    }

    {
       // add(new IntrinsicCall("java.lang.Math.sqrt(D)D", true,
              //  "function &sqrt (arg_f64 %_result) (arg_f64 %_val) {",
             //   "ld_arg_f64  $d0, [%_val];",
              //  "nsqrt_f64  $d0, $d0;",
              //  "st_arg_f64  $d0, [%_result];",
               // "ret;",
               // "};"));
        add(new InlineIntrinsicCall(null, "java.lang.Math.sqrt(D)D", true,
                "nsqrt_f64  $d${0}, $d${0};"
    ));
        add(new InlineIntrinsicCall(null, "java.lang.String.charAt(I)C", false,
                "ld_global_u64 $d${2}, [$d${0}+16];   // this string reference into $d${2}",
                "mov_b32 $s${3}, $s${1};              // copy index",
                "cvt_u64_s32 $d${3}, $s${3};          // convert array index to 64 bits",
                "mad_u64 $d${3}, $d${3}, 2, $d${2};      // get the char address",
                "ld_global_u16 $s${0}, [$d${3}+24];   // ld the char"
        ));
        add(new InlineIntrinsicCall(null, "java.lang.Math.cos(D)D", true,
                "ncos_f64  $d${0}, $d${0};"
    ));
        add(new InlineIntrinsicCall(null, "java.lang.Math.sin(D)D", true,
                "nsin_f64  $d${0}, $d${0};"
        ));
        add(new IntrinsicCall(null, "java.lang.Math.hypot(DD)D", true,
                "function &hypot (arg_f64 %_result) (arg_f64 %_val1, arg_f64 %_val2) {",
                "ld_arg_f64  $d0, [%_val1];",
                "ld_arg_f64  $d1, [%_val2];",
                "mul_f64 $d0, $d0, $d1;",
                "nsqrt_f64  $d0, $d0;",
                "st_arg_f64  $d0, [%_result];",
                "ret;",
                "};"));
    }

    //  final static long ADDR_MASK = ((1L << 32)-1);

    abstract class HSAILInstruction<H extends HSAILInstruction<H>>  {
        Instruction from;
        HSAILRegister[] dests = null;
        HSAILRegister[] sources = null;
        StackFrame stackFrame = null;

        HSAILInstruction(HSAILInstruction original) {
                from = original.from;
            stackFrame = original.stackFrame;
                if (original.dests == null){
                    dests = null;
                }else{
                dests = new HSAILRegister[original.dests.length];
                for (int i=0; i<dests.length; i++){
                      dests[i] = original.dests[i].cloneMe();
                }
                }
                if (original.sources == null){
                    sources = null;
                }else{
                sources = new HSAILRegister[original.sources.length];
                for (int i=0; i<sources.length; i++){
                      sources[i] = original.sources[i].cloneMe();
                }
                }

        }

        HSAILInstruction(StackFrame _stackFrame,Instruction _from, int _destCount, int _sourceCount) {
stackFrame = _stackFrame;
            from = _from;
            dests = new HSAILRegister[_destCount];
            sources = new HSAILRegister[_sourceCount];
        }

        public abstract  H cloneMe();


        public StackFrame getStackFrame(){
            return(stackFrame);
        }
        abstract void render(HSAILRenderer r);

    }

    abstract class HSAILInstructionWithDest<H extends HSAILInstructionWithDest<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {

        protected HSAILInstructionWithDest(  H original){
            super(original);

        }

        HSAILInstructionWithDest(StackFrame _stackFrame,Instruction _from, Rt _dest) {
            super(_stackFrame, _from, 1, 0);
            dests[0] = _dest;
        }

        Rt getDest() {
            return ((Rt) dests[0]);
        }
    }

    abstract class HSAILInstructionWithSrc<H extends HSAILInstructionWithSrc<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {

        protected HSAILInstructionWithSrc( H original){
            super(original);
        }


        HSAILInstructionWithSrc(StackFrame _stackFrame,Instruction _from, Rt _src) {
            super(_stackFrame,_from, 0, 1);
            sources[0] = _src;
        }

        Rt getSrc() {
            return ((Rt) sources[0]);
        }
    }

    abstract class HSAILInstructionWithSrcSrc<H extends HSAILInstructionWithSrcSrc<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {

        protected HSAILInstructionWithSrcSrc(H original){
            super(original);
        }
         HSAILInstructionWithSrcSrc(StackFrame _stackFrame,Instruction _from, Rt _src_lhs, Rt _src_rhs) {
            super(_stackFrame,_from, 0, 2);
            sources[0] = _src_lhs;
            sources[1] = _src_rhs;
        }

        Rt getSrcLhs() {
            return ((Rt) sources[0]);
        }

         Rt getSrcRhs() {
            return ((Rt) sources[1]);
        }
    }

    abstract class HSAILInstructionWithDestSrcSrc<H extends HSAILInstructionWithDestSrcSrc<H,Rd,Rt,D,T>, Rd extends HSAILRegister<Rd,T>, Rt extends HSAILRegister<Rt,T>, D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstruction<H> {

        protected HSAILInstructionWithDestSrcSrc(H original){
            super(original);
        }
        HSAILInstructionWithDestSrcSrc(StackFrame _stackFrame,Instruction _from, Rd _dest, Rt _src_lhs, Rt _src_rhs) {
            super(_stackFrame,_from, 1, 2);
            dests[0] = _dest;
            sources[0] = _src_lhs;
            sources[1] = _src_rhs;
        }

        Rd getDest() {
            return ((Rd) dests[0]);
        }

       Rt getSrcLhs() {
            return ((Rt) sources[0]);
        }

       Rt getSrcRhs() {
            return ((Rt) sources[1]);
        }
    }



    abstract class HSAILInstructionWithDestSrc<H extends HSAILInstructionWithDestSrc<H,Rd,Rt,D,T>, Rd extends HSAILRegister<Rd,D>, Rt extends HSAILRegister<Rt,T>, D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstruction<H> {
        HSAILInstructionWithDestSrc(H original){
            super(original);
        }
        HSAILInstructionWithDestSrc(StackFrame _stackFrame,Instruction _from, Rd _dest, Rt _src) {
            super(_stackFrame,_from, 1, 1);
            dests[0] = _dest;
            sources[0] = _src;
        }

        Rd getDest() {
            return ((Rd) dests[0]);
        }

        Rt  getSrc() {
            return ((Rt) sources[0]);
        }
    }

    class branch <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrc<branch<R>,R, s32> {
        String branchName;
        int pc;

        protected branch(branch<R> original){
            super(original);
            branchName = original.branchName;
            pc = original.pc;
        }

         branch(StackFrame _stackFrame,Instruction _from, R _src, String _branchName, int _pc) {
            super(_stackFrame,_from, _src);
            branchName = _branchName;
            pc = _pc;
        }

        @Override public branch<R> cloneMe(){
            return(new branch<R>(this));
        }


        @Override
        public void render(HSAILRenderer r) {
            r.append(branchName).space().label(stackFrame.getLocation(pc)).semicolon();
        }
    }

    class cmp_s32_const_0 <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrc<cmp_s32_const_0<R>,R, s32> {
        String type;

        protected cmp_s32_const_0(cmp_s32_const_0<R> original){
            super(original);
            type = original.type;
        }

        cmp_s32_const_0(StackFrame _stackFrame,Instruction _from, String _type, R _src) {
            super(_stackFrame, _from, _src);
            type = _type;
        }

        @Override public cmp_s32_const_0<R> cloneMe(){
            return(new cmp_s32_const_0<R>(this));
        }

        @Override
        public void render(HSAILRenderer r) {
            r.append("cmp_").append(type).append("_b1_").typeName(getSrc()).space().append("$c1").separator().regName(getSrc(), stackFrame).separator().append("0").semicolon();

        }
    }

    class cmp_s32 <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrcSrc<cmp_s32<R>,R, s32> {

        String type;

        protected cmp_s32(cmp_s32<R> original){
            super(original);
            type = original.type;
        }

        cmp_s32(StackFrame _stackFrame,Instruction _from, String _type, R _srcLhs, R _srcRhs) {
            super(_stackFrame,_from, _srcLhs, _srcRhs);
            type = _type;
        }

        @Override public cmp_s32<R> cloneMe(){
            return(new cmp_s32<R>(this));
        }

        @Override
        public void render(HSAILRenderer r) {
            r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs(), stackFrame).separator().regName(getSrcRhs(), stackFrame).semicolon();

        }
    }
    class cmp_ref <R extends HSAILRegister<R,ref>> extends HSAILInstructionWithSrcSrc<cmp_ref<R>,R, ref> {

        String type;

        protected cmp_ref(cmp_ref<R> original){
            super(original);
            type = original.type;
        }

        cmp_ref(StackFrame _stackFrame,Instruction _from, String _type, R _srcLhs, R _srcRhs) {
            super(_stackFrame, _from, _srcLhs, _srcRhs);
            type = _type;
        }

        @Override public cmp_ref<R> cloneMe(){
            return(new cmp_ref<R>(this));
        }


        @Override
        public void render(HSAILRenderer r) {
            r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs(), stackFrame).separator().regName(getSrcRhs(), stackFrame).semicolon();

        }
    }

    class cmp<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrcSrc<cmp<Rt,T>,Rt, T> {

        String type;

        protected cmp(cmp<Rt,T> original){
            super(original);
            type = original.type;
        }

        cmp(StackFrame _stackFrame,Instruction _from, String _type, Rt _srcLhs, Rt _srcRhs) {
            super(_stackFrame,_from, _srcLhs, _srcRhs);
            type = _type;
        }

        @Override public cmp<Rt,T> cloneMe(){
            return(new cmp<Rt,T>(this));
        }

        @Override
        public void render(HSAILRenderer r) {
            r.append("cmp_").append(type).append("u").append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs(), stackFrame).separator().regName(getSrcRhs(), stackFrame).semicolon();

        }
    }

    class cbr extends HSAILInstruction<cbr> {

        int pc;

        protected cbr(cbr original){
            super(original);
            pc = original.pc;
        }

        cbr(StackFrame _stackFrame,Instruction _from, int _pc) {
            super(_stackFrame,_from, 0, 0);
            pc = _pc;
        }

        @Override public cbr cloneMe(){
            return(new cbr(this));
        }


        @Override
        public void render(HSAILRenderer r) {
            r.append("cbr").space().append("$c1").separator().label(stackFrame.getLocation(pc)).semicolon();

        }
    }

    class brn extends HSAILInstruction<brn> {
        int pc;

        protected brn(brn original){
            super(original);
            pc = original.pc;
        }

        brn(StackFrame _stackFrame,Instruction _from, int _pc) {
            super(_stackFrame, _from, 0, 0);
            pc = _pc;
        }

        @Override public brn cloneMe(){
            return(new brn(this));
        }

        @Override
        public void render(HSAILRenderer r) {
            r.append("brn").space().label(stackFrame.getLocation(pc)).semicolon();

        }
    }

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

    class call extends HSAILInstruction<call> {
        int base;
        String name;
        String mangledName;
        CallType call;

        protected call(call original){
            super(original);
            base = original.base;
            name = original.name;
            mangledName = original.mangledName;
            call = original.call;
        }



        call(StackFrame _stackFrame,Instruction _from) {
            super(_stackFrame, _from, 0, 0);
            base = from.getPreStackBase() + from.getMethod().getCodeEntry().getMaxLocals();
            String dotClassName = null;
            String sig = null;
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
            mangledName = (dotClassName+"_"+name+sig);//.replace(".","_").replace(";","_").replace("(","_").replace(")", "_").replace("/", "_").replace("$", "_").replace("[", "_");
            String intrinsicLookup = dotClassName + "." + name + sig;
            call = null;
            for (IntrinsicCall ic : intrinsicMap.values()) {
                if (ic.getMappedMethod().equals(intrinsicLookup)) {
                    call = ic;

                    break;
                }
            }
            if (call == null) { // not an intrinsic!
                try {
                    Class theClass = Class.forName(dotClassName);
                    ClassModel classModel = ClassModel.getClassModel(theClass);
                    ClassModel.ClassModelMethod method = classModel.getMethod(name, sig);
                    StackFrame newStackFrame = new StackFrame(stackFrame, String.format("@%04d : %s",from.getThisPC(), mangledName), base);
                    // Pass StackFrame down here!!!!
                    HSAILMethod hsailMethod = HSAILMethod.getHSAILMethod(method, getEntryPoint(), newStackFrame);
                    call = new InlineMethodCall(newStackFrame, intrinsicLookup, hsailMethod);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ClassParseException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            add(call);
        }

        @Override public call cloneMe(){
            return(new call(this));
        }

        @Override
        void render(HSAILRenderer r) {


            call.renderCallSite(r, from,  name);

        }


    }


    class nyi extends HSAILInstruction<nyi> {

        protected nyi(nyi original){
            super(original);
        }

        nyi(StackFrame _stackFrame,Instruction _from) {
            super(_stackFrame, _from, 0, 0);
        }

        @Override public nyi cloneMe(){
            return(new nyi(this));
        }

        @Override
        void render(HSAILRenderer r) {

            r.append("NYI ").i(from);

        }
    }

    class ld_kernarg<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<ld_kernarg<Rt,T>,Rt, T> {

        protected ld_kernarg(ld_kernarg<Rt,T> original){
            super(original);

        }

        ld_kernarg(StackFrame _stackFrame,Instruction _from, Rt _dest) {
            super(_stackFrame, _from, _dest);
        }

        @Override public ld_kernarg<Rt,T> cloneMe(){
            return(new ld_kernarg<Rt,T>(this));
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("ld_kernarg_").typeName(getDest()).space().regName(getDest(), stackFrame).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
        }
    }

    class ld_arg<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<ld_arg<Rt,T>,Rt, T> {

        protected ld_arg(ld_arg<Rt,T> original){
            super(original);

        }

        ld_arg(StackFrame _stackFrame,Instruction _from, Rt _dest) {
            super(_stackFrame, _from, _dest);
        }

        @Override public ld_arg cloneMe(){
            return(new ld_arg(this));
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("ld_arg_").typeName(getDest()).space().regName(getDest(), stackFrame).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
        }


    }

    abstract class binary_const<H extends binary_const<H, Rt, T, C>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDestSrc<H, Rt,Rt,T,T> {
        C value;
        String op;

        protected binary_const(H original){
            super(original);
            value = original.value;
            op = original.op;
        }

        binary_const(StackFrame _stackFrame,Instruction _from, String _op, Rt _dest, Rt _src, C _value) {
            super(_stackFrame,_from, _dest, _src);
            value = _value;
            op = _op;
        }

        @Override
        void render(HSAILRenderer r) {
            r.append(op).typeName(getDest()).space().regName(getDest(), stackFrame).separator().regName(getSrc(), stackFrame).separator().append(value).semicolon();
        }


    }

    class add_const<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType, C extends Number> extends binary_const<add_const<Rt, T, C>, Rt,T, C> {
        protected add_const(add_const<Rt,T,C> original){
            super(original);
        }

        add_const(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _src, C _value) {
            super(_stackFrame,_from, "add_", _dest, _src, _value);

        }
        @Override public add_const<Rt,T,C> cloneMe(){
            return(new add_const<Rt,T,C>(this));
        }

    }

    class and_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends binary_const<and_const<Rt, T,C>, Rt, T, C> {

        protected and_const(and_const<Rt, T,C> original){
            super(original);
        }

        and_const(StackFrame _stackFrame,Instruction _from, Rt _dest,Rt _src, C _value) {
            super(_stackFrame,_from, "and_", _dest, _src, _value);

        }

        @Override public and_const<Rt, T,C> cloneMe(){
            return(new and_const<Rt, T,C>(this));
        }

        @Override
        void render(HSAILRenderer r) {
            r.append(op).append("b64").space().regName(getDest(), stackFrame).separator().regName(getSrc(), stackFrame).separator().append(value).semicolon();
        }


    }

    class mul_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends binary_const< mul_const<Rt, T,C>, Rt, T, C> {
        protected mul_const(mul_const<Rt,T,C> original){
            super(original);
        }

        mul_const(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _src, C _value) {
            super(_stackFrame,_from, "mul_", _dest, _src, _value);

        }

        @Override public mul_const<Rt,T,C> cloneMe(){
            return(new mul_const<Rt,T,C>(this));
        }

    }

        class mad<Rd extends HSAILRegister<Rd,ref>, Rt extends HSAILRegister<Rt,ref>> extends HSAILInstructionWithDestSrcSrc<mad<Rd,Rt>, Rd, Rt, ref, ref> {
            long size;
        protected mad(mad<Rd,Rt> original){
            super(original);
            size = original.size;
        }

        mad(StackFrame _stackFrame,Instruction _from, Rd _dest, Rt _src_lhs, Rt _src_rhs, long _size) {
            super(_stackFrame, _from, _dest, _src_lhs, _src_rhs);
            size = _size;
        }

        @Override public mad<Rd,Rt> cloneMe(){
            return(new mad<Rd,Rt>(this));
        }

        @Override void render(HSAILRenderer r) {
            r.append("mad_").typeName(getDest()).space().regName(getDest(), stackFrame).separator().regName(getSrcLhs(), stackFrame).separator().append(size).separator().regName(getSrcRhs(), stackFrame).semicolon();
        }
    }


    class cvt<Rt1 extends HSAILRegister<Rt1,T1>, Rt2 extends HSAILRegister<Rt2,T2>,T1 extends PrimitiveType, T2 extends PrimitiveType> extends HSAILInstruction<cvt<Rt1,Rt2,T1,T2>> {

        protected cvt(cvt<Rt1,Rt2,T1,T2> original){
            super(original);


        }
        @Override public cvt<Rt1,Rt2,T1,T2> cloneMe(){
            return(new cvt(this));
        }
        cvt(StackFrame _stackFrame,Instruction _from, Rt1 _dest, Rt2 _src) {
            super(_stackFrame,_from, 1, 1);
            dests[0] = _dest;
            sources[0] = _src;
        }

        Rt1 getDest() {
            return ((Rt1) dests[0]);
        }

        Rt2 getSrc() {
            return ((Rt2) sources[0]);
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("cvt_").typeName(getDest()).append("_").typeName(getSrc()).space().regName(getDest(), stackFrame).separator().regName(getSrc(), stackFrame).semicolon();
        }


    }


    class retvoid extends HSAILInstruction<retvoid> {
        protected retvoid(retvoid original){
            super(original);


        }
        @Override public retvoid cloneMe(){
            return(new retvoid(this));
        }

        retvoid(StackFrame _stackFrame,Instruction _from) {
            super(_stackFrame,_from, 0, 0);

        }

        @Override
        void render(HSAILRenderer r) {
            r.append("ret").semicolon();
        }


    }

    class ret<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrc<ret<Rt,T>,Rt, T> {

        protected ret(ret<Rt,T> original){
            super(original);


        }
        @Override public ret<Rt,T> cloneMe(){
            return(new ret<Rt,T>(this));
        }
        ret(StackFrame _stackFrame,Instruction _from, Rt _src) {
            super(_stackFrame,_from, _src);

        }

        @Override
        void render(HSAILRenderer r) {
            r.append("st_arg_").typeName(getSrc()).space().regName(getSrc(), stackFrame).separator().append("[%_result]").semicolon().nl();
            r.append("ret").semicolon();
        }


    }

    class array_store<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrc<array_store<Rt, T>,Rt, T> {
        Reg_ref mem;

        protected array_store(array_store<Rt, T> original){
            super(original);
            mem = original.mem;
        }

        array_store(StackFrame _stackFrame,Instruction _from, Reg_ref _mem, Rt _src) {
            super(_stackFrame,_from, _src);
            mem = _mem;
        }

        @Override public array_store<Rt, T> cloneMe(){
            return(new array_store<Rt, T>(this));
        }

        @Override
        void render(HSAILRenderer r) {
            // r.append("st_global_").typeName(getSrc()).space().append("[").regName(mem).append("+").array_len_offset().append("]").separator().regName(getSrc());
            r.append("st_global_").typeName(getSrc()).space().regName(getSrc(), stackFrame).separator().append("[").regName(mem, stackFrame).append("+").array_base_offset().append("]").semicolon();
        }


    }


    class array_load<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<array_load<Rt,T>,Rt,T> {
        Reg_ref mem;

        protected array_load(array_load<Rt,T> original){
            super(original);
            mem = original.mem;
        }

        array_load(StackFrame _stackFrame,Instruction _from, Rt _dest, Reg_ref _mem) {
            super(_stackFrame,_from, _dest);
            mem = _mem;
        }

        @Override public array_load<Rt,T> cloneMe(){
            return(new array_load<Rt,T>(this));
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("ld_global_").typeName(getDest()).space().regName(getDest(), stackFrame).separator().append("[").regName(mem, stackFrame).append("+").array_base_offset().append("]").semicolon();
            if (getDest().type.getHsaBits()==8){
                r.nl().pad(9).append("//cvt_s32_u8 $s").regNum(getDest(), stackFrame).separator().space().regName(getDest(), stackFrame).semicolon();
            }     else   if (getDest().type.getHsaBits()==16){
                r.nl().pad(9).append("//cvt_s32_u16 $s").regNum(getDest(), stackFrame).separator().space().regName(getDest(), stackFrame).semicolon();
            }
        }


    }

    class array_len<Rs32 extends HSAILRegister<Rs32,s32>> extends HSAILInstructionWithDest<array_len<Rs32>, Rs32, s32> {
        Reg_ref mem;

        protected array_len(array_len<Rs32> original){
            super(original);
            mem = original.mem;
        }

        array_len(StackFrame _stackFrame,Instruction _from, Rs32 _dest, Reg_ref _mem) {
            super(_stackFrame,_from, _dest);
            mem = _mem;
        }

        @Override public array_len<Rs32> cloneMe(){
            return(new array_len<Rs32>(this));
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("ld_global_").typeName(getDest()).space().regName(getDest(), stackFrame).separator().append("[").regName(mem, stackFrame).append("+").array_len_offset().append("]").semicolon();
        }


    }

    class field_load<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDest<field_load<Rt,T>, Rt,T> {

        Reg_ref mem;
        long offset;
        protected field_load(field_load<Rt,T> original){
            super(original);
            mem = original.mem;
            offset = original.offset;
        }

        field_load(StackFrame _stackFrame,Instruction _from, Rt _dest, Reg_ref _mem, long _offset) {
            super(_stackFrame,_from, _dest);
            offset = _offset;
            mem = _mem;
        }

        @Override public field_load<Rt,T> cloneMe(){
            return(new field_load<Rt,T>(this));
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("ld_global_").typeName(getDest()).space().regName(getDest(), stackFrame).separator().append("[").regName(mem, stackFrame).append("+").append(offset).append("]").semicolon();
        }


    }

    class static_field_load<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<static_field_load<Rt,T>,Rt, T> {
        long offset;
        Reg_ref mem;
        protected static_field_load(static_field_load<Rt,T> original){
            super(original);
            mem = original.mem;
            offset = original.offset;
        }

        static_field_load(StackFrame _stackFrame,Instruction _from, Rt _dest, Reg_ref _mem, long _offset) {
            super(_stackFrame,_from, _dest);
            offset = _offset;
            mem = _mem;
        }

        @Override public static_field_load<Rt,T> cloneMe(){
            return(new static_field_load<Rt,T>(this));
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("ld_global_").typeName(getDest()).space().regName(getDest(), stackFrame).separator().append("[").regName(mem, stackFrame).append("+").append(offset).append("]").semicolon();
        }


    }


    class field_store<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithSrc<field_store<Rt,T>,Rt,T> {

        Reg_ref mem;
        long offset;

        protected field_store(field_store<Rt,T> original){
            super(original);
            mem = original.mem;
            offset = original.offset;
        }

        field_store(StackFrame _stackFrame,Instruction _from, Rt _src, Reg_ref _mem, long _offset) {
            super(_stackFrame,_from, _src);
            offset = _offset;
            mem = _mem;
        }

        @Override public field_store<Rt,T> cloneMe(){
            return(new field_store<Rt,T>(this));
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("st_global_").typeName(getSrc()).space().regName(getSrc(), stackFrame).separator().append("[").regName(mem, stackFrame).append("+").append(offset).append("]").semicolon();
        }


    }


    final class mov<Rd extends HSAILRegister<Rd,D>,Rt extends HSAILRegister<Rt,T>,D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<mov<Rd,Rt,D,T>, Rd, Rt,D,T> {
        protected mov(mov<Rd,Rt,D,T> original){
            super(original);

        }

        public mov(StackFrame _stackFrame,Instruction _from, Rd _dest, Rt _src) {
            super(_stackFrame,_from, _dest, _src);
        }
        @Override public mov<Rd,Rt,D,T> cloneMe(){
            return(new mov<Rd,Rt,D,T>(this));
        }
        @Override
        void render(HSAILRenderer r) {
            r.append("mov_").movTypeName(getDest()).space().regName(getDest(), stackFrame).separator().regName(getSrc(), stackFrame).semicolon();

        }


    }

    abstract class unary<H extends unary<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<H,Rt,Rt, T,T> {
        String op;

        protected unary(H original){
            super(original);
            op = original.op;
        }

        public unary(StackFrame _stackFrame,Instruction _from, String _op, Rt _destSrc) {
            super(_stackFrame,_from, _destSrc, _destSrc);
            op = _op;
        }

        @Override
        void render(HSAILRenderer r) {
            r.append(op).typeName(getDest()).space().regName(getDest(), stackFrame).separator().regName(getDest(), stackFrame).semicolon();
        }

        Rt getDest() {
            return ((Rt) dests[0]);
        }

        Rt getSrc() {
            return ((Rt) sources[0]);
        }


    }

    abstract class binary<H extends binary<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {
        String op;
        protected binary(H original){
            super(original);
            op = original.op;

        }
        public binary(StackFrame _stackFrame,Instruction _from, String _op, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, 1, 2);
            dests[0] = _dest;
            sources[0] = _lhs;
            sources[1] = _rhs;
            op = _op;
        }

        @Override
        void render(HSAILRenderer r) {
            r.append(op).typeName(getDest()).space().regName(getDest(), stackFrame).separator().regName(getLhs(), stackFrame).separator().regName(getRhs(), stackFrame).semicolon();
        }

        Rt getDest() {
            return ((Rt) dests[0]);
        }

        Rt getRhs() {
            return ((Rt) sources[1]);
        }

        Rt getLhs() {
            return ((Rt) sources[0]);
        }


    }

  /*  abstract class binaryRegConst<T extends JavaType, C> extends HSAILInstruction{
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
      @Override void renderDefinition(HSAILRenderer r){
         r.append(op).typeName(dest).space().regName(dest).separator().regName(lhs).separator().append(value.toString());
      }
   }

     class addConst<T extends JavaType, C> extends binaryRegConst<T, C>{

      public addConst(Instruction _from,   HSAILRegister<T> _dest, HSAILRegister<T> _lhs, C _value_rhs){
         super(_from, "add_", _dest, _lhs, _value_rhs);
      }
   }
   */

    class add<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<add<Rt,T>, Rt, T> {
        protected add(add<Rt,T> original){
            super(original);
        }

        public add(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "add_", _dest, _lhs, _rhs);
        }
        @Override public add<Rt,T> cloneMe(){
            return (new add<Rt,T>(this));
        }

    }

    class sub<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<sub<Rt,T>, Rt, T> {
        protected sub(sub<Rt,T> original){
            super(original);
        }

        public sub(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "sub_", _dest, _lhs, _rhs);
        }
        @Override public sub<Rt,T> cloneMe(){
            return (new sub<Rt,T>(this));
        }
    }

    class div<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<div<Rt,T>, Rt, T> {

        @Override public div<Rt,T> cloneMe(){
            return (new div<Rt,T>(this));
        }
        public div(StackFrame _stackFrame,Instruction _from,Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "div_", _dest, _lhs, _rhs);
        }
        protected div(div<Rt,T> original){
            super(original);
        }
    }

    class mul<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<mul<Rt,T>, Rt, T> {
        protected mul(mul<Rt,T> original){
            super(original);
        }
        @Override public mul<Rt,T> cloneMe(){
            return (new mul<Rt,T>(this));
        }
        public mul(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "mul_", _dest, _lhs, _rhs);
        }

    }

    class rem<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<rem<Rt,T>, Rt, T> {
        protected rem(rem<Rt,T> original){
            super(original);
        }
        @Override public rem<Rt,T> cloneMe(){
            return (new rem<Rt,T>(this));
        }
        public rem(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "rem_", _dest, _lhs, _rhs);
        }

    }

    class neg<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<neg<Rt,T>, Rt, T> {

        protected neg(neg<Rt,T> original){
            super(original);
        }
        @Override public neg<Rt,T> cloneMe(){
            return (new neg<Rt,T>(this));
        }
        public neg(StackFrame _stackFrame,Instruction _from, Rt _destSrc) {
            super(_stackFrame,_from, "neg_", _destSrc);
        }

    }

    class shl<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<shl<Rt,T>, Rt, T> {
        protected shl(shl<Rt,T> original){
            super(original);
        }
        @Override public shl<Rt,T> cloneMe(){
            return (new shl<Rt,T>(this));
        }
        public shl(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "shl_", _dest, _lhs, _rhs);
        }

    }

    class shr<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<shr<Rt,T>, Rt, T> {
        protected shr(shr<Rt,T> original){
            super(original);
        }
        @Override public shr<Rt,T> cloneMe(){
            return (new shr<Rt,T>(this));
        }
        public shr(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "shr_", _dest, _lhs, _rhs);
        }

    }

    class ushr<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<ushr<Rt,T>, Rt, T> {
        protected ushr(ushr<Rt,T> original){
            super(original);
        }
        @Override public ushr<Rt,T> cloneMe(){
            return (new ushr<Rt,T>(this));
        }
        public ushr(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "ushr_", _dest, _lhs, _rhs);
        }

    }


    class and<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<and<Rt,T>, Rt, T> {
        protected and(and<Rt,T> original){
            super(original);
        }
        @Override public and<Rt,T> cloneMe(){
            return (new and<Rt,T>(this));
        }
        public and(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "and_", _dest, _lhs, _rhs);
        }

        @Override
        void render(HSAILRenderer r) {
            r.append(op).movTypeName(getDest()).space().regName(getDest(), stackFrame).separator().regName(getLhs(), stackFrame).separator().regName(getRhs(), stackFrame).semicolon();
        }

    }

    class or<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<or<Rt,T>, Rt, T> {
        protected or(or<Rt,T> original){
            super(original);
        }
        @Override public or<Rt,T> cloneMe(){
            return (new or<Rt,T>(this));
        }
        public or(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "or_", _dest, _lhs, _rhs);
        }

        @Override
        void render(HSAILRenderer r) {
            r.append(op).movTypeName(getDest()).space().regName(getDest(), stackFrame).separator().regName(getLhs(), stackFrame).separator().regName(getRhs(), stackFrame).semicolon();
        }

    }

    class xor<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<xor<Rt,T>, Rt, T> {
        protected xor(xor<Rt,T> original){
            super(original);
        }
        @Override public xor<Rt,T> cloneMe(){
            return (new xor<Rt,T>(this));
        }
        public xor(StackFrame _stackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
            super(_stackFrame,_from, "xor_", _dest, _lhs, _rhs);
        }

        @Override
        void render(HSAILRenderer r) {
            r.append(op).movTypeName(getDest()).space().regName(getDest(), stackFrame).separator().regName(getLhs(), stackFrame).separator().regName(getRhs(), stackFrame).semicolon();
        }

    }

    class mov_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDest<mov_const<Rt,T,C>,Rt,T> {
        protected mov_const(mov_const<Rt,T,C> original){
            super(original);
            value = original.value;
        }
        @Override public mov_const<Rt,T,C> cloneMe(){
            return (new mov_const<Rt,T,C>(this));
        }
        C value;

        public mov_const(StackFrame _stackFrame,Instruction _from, Rt _dest, C _value) {
            super(_stackFrame,_from, _dest);
            value = _value;
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("mov_").movTypeName(getDest()).space().regName(getDest(), stackFrame).separator().append(value).semicolon();

        }


    }

    List<HSAILInstruction> instructions = new ArrayList<HSAILInstruction>();
    ClassModel.ClassModelMethod method;

    boolean optimizeMoves =  false || Config.enableOptimizeRegMoves;

    void add( HSAILInstruction _regInstruction) {
        // before we add lets see if this is a redundant mov
        if (optimizeMoves && _regInstruction.sources != null && _regInstruction.sources.length > 0) {
            for (int regIndex = 0; regIndex < _regInstruction.sources.length; regIndex++) {
                HSAILRegister r = _regInstruction.sources[regIndex];
                if (r.isStack()) {
                    // look up the list of reg instructions for the last mov which assigns to r
                    int i = instructions.size();
                    while ((--i) >= 0) {
                        if (instructions.get(i) instanceof mov) {
                            // we have found a move
                            mov candidateForRemoval = (mov) instructions.get(i);
                            if (candidateForRemoval.from.getBlock() == _regInstruction.from.getBlock()
                                    && candidateForRemoval.getDest().isStack() && candidateForRemoval.getDest().equals(r)) {
                                // so i may be a candidate if between i and instruction.size() i.dest() is not mutated
                                boolean mutated = false;
                                for (int x = i + 1; !mutated && x < instructions.size(); x++) {
                                    if (instructions.get(x).dests.length > 0 && instructions.get(x).dests[0].equals(candidateForRemoval.getSrc())) {
                                        mutated = true;
                                    }
                                }
                                if (!mutated) {
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

            for (HSAILInstruction i : instructions) {
                if (!(i instanceof ld_kernarg || i instanceof ld_arg) && !s.contains(i.from)) {
                    s.add(i.from);
                    if (i.from.isBranchTarget()) {
                        r.label("func_"+i.from.getThisPC()).colon().nl();
                    }
                    if (r.isShowingComments()) {
                        r.nl().pad(1).lineCommentStart().mark().append(i.getStackFrame().getLocation(i.from.getThisPC())).relpad(2).space().i(i.from).nl();
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

        for (HSAILInstruction i : instructions) {
            if (!(i instanceof ld_arg)){
               if (!s.contains(i.from)) {
                   s.add(i.from);
                     if (i.from.isBranchTarget()) {
                         r.label(i.getStackFrame().getLocation(i.from.getThisPC())).colon().nl();
                     }
                    if (r.isShowingComments()) {
                        r.nl().pad(1).lineCommentStart().append(i.getStackFrame().getLocation(i.from.getThisPC())).mark().relpad(2).space().i(i.from).nl();
                    }
                }
                if (i instanceof retvoid){
                    r.pad(9).lineCommentStart().append("ret").semicolon();
                }else if (i instanceof ret){
                  r.pad(9).append("mov_").movTypeName(((ret)i).getSrc()).space().regPrefix(((ret)i).getSrc().type).append(base).separator().regName(((ret)i).getSrc(), i.getStackFrame()).semicolon();
                  if (i != instructions.get(instructions.size()-1)){
                  r.nl().pad(9).append("brn @L"+ i.getStackFrame().getUniqueNameSpace()+"_END").semicolon();
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
        r.append("@L"+instructions.iterator().next().getStackFrame().getUniqueNameSpace()+"_END").colon().nl();
        }
        return (r);
    }

    public HSAILRenderer renderEntryPoint(HSAILRenderer r) {
        //r.append("version 1:0:large;").nl();
        r.append("version 0:95: $full : $large").semicolon().nl();

       // RenderContext rc = new RenderContext(null, this.method.getClassModel().getDotClassName()+"."+this.method.getName()+this.method.getDescriptor(), 0);
        for (CallType c : calls) {
            c.renderDeclaration(r);
        }

        for (CallType c : calls) {
            c.renderDefinition(r);
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

        for (HSAILInstruction i : instructions) {
            if (!(i instanceof ld_kernarg) && !s.contains(i.from)) {
                if (!first) {
                    r.pad(9).append("workitemabsid_u32 $s" + (count - 1) + ", 0").semicolon().nl();
                    // r.pad(9).append("workitemaid $s" + (count - 1) + ", 0;").nl();
                    first = true;
                }
                s.add(i.from);
                if (i.from.isBranchTarget()) {

                    r.label(i.getStackFrame().getLocation(i.from.getThisPC())).colon().nl();
                }
                if (r.isShowingComments()) {
                    r.nl().pad(1).lineCommentStart().mark().append(i.getStackFrame().getLocation(i.from.getThisPC())).relpad(2).space().i(i.from).nl();
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
        for (Map.Entry<StackFrame, Integer> e:instructions.iterator().next().getStackFrame().locMap.entrySet()){
            r.nl().append(String.format("%04d",e.getValue())).append("=").obrace().nl();
            e.getKey().renderStack(r);
            r.cbrace().nl();
        }
        r.nl().commentEnd();
        return (r);
    }

    public HSAILRegister getRegOfLastWriteToIndex(int _index) {

        int idx = instructions.size();
        while (--idx >= 0) {
            HSAILInstruction i = instructions.get(idx);
            if (i.dests != null) {
                for (HSAILRegister d : i.dests) {
                    if (d.index == _index) {
                        return (d);
                    }
                }
            }
        }


        return (null);
    }

    public void addmov(StackFrame _stackFrame,Instruction _i, PrimitiveType _type, int _from, int _to) {
        if (_type.equals(PrimitiveType.ref) || _type.getHsaBits() == 32) {
            if (_type.equals(PrimitiveType.ref)) {
                add(new mov<StackReg_ref,StackReg_ref,ref,ref>(_stackFrame,_i, new StackReg_ref( _i, _to), new StackReg_ref(_i, _from)));
            } else if (_type.equals(PrimitiveType.s32)) {
                add(new mov<StackReg_s32,StackReg_s32,s32,s32>(_stackFrame,_i, new StackReg_s32( _i, _to), new StackReg_s32(_i, _from)));
            } else {
                throw new IllegalStateException(" unknown prefix 1 prefix for first of DUP2");
            }

        } else {
            throw new IllegalStateException(" unknown prefix 2 prefix for DUP2");
        }
    }

    public HSAILRegister addmov(StackFrame _stackFrame, Instruction _i, int _from, int _to) {
        HSAILRegister r = getRegOfLastWriteToIndex(_i.getPreStackBase() + _i.getMethod().getCodeEntry().getMaxLocals() + _from);
        if (r == null){
            System.out.println("damn!");
        }
        addmov(_stackFrame, _i, r.type, _from, _to);
        return (r);
    }

    enum ParseState {NONE, COMPARE_F32, COMPARE_F64, COMPARE_S64}

    ;

    static Map<ClassModel.ClassModelMethod, HSAILMethod> cache = new HashMap<ClassModel.ClassModelMethod, HSAILMethod>();

    static synchronized HSAILMethod getHSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint, StackFrame _stackFrame) {
        HSAILMethod instance = cache.get(_method);
        if (instance == null) {
            instance = new HSAILMethod(_method, _entryPoint, _stackFrame);
            cache.put(_method, instance);
        }
        return (instance);
    }

    static synchronized HSAILMethod getHSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint) {
       return getHSAILMethod(_method, _entryPoint, null);
    }

    private HSAILMethod(ClassModel.ClassModelMethod _method) {
        this(_method, null, null);
    }

    HSAILMethod entryPoint;

    private HSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint, StackFrame _stackFrame) {



        StackFrame stackFrame = _stackFrame;
        if (stackFrame == null){
           stackFrame = new StackFrame(_stackFrame, _method.getClassModel().getDotClassName()+"."+_method.getName()+_method.getDescriptor(), 0);
        }
        entryPoint = _entryPoint;
        if (entryPoint == null) {
            calls = new HashSet<CallType>();
        }
        if (UnsafeWrapper.addressSize() == 4) {
            throw new IllegalStateException("Object pointer size is 4, you need to use 64 bit JVM and set -XX:-UseCompressedOops!");
        }
        method = _method;
        ParseState parseState = ParseState.NONE;
        Instruction lastInstruction = null;
      //  for (Instruction i : method.getInstructions()) {
     //      System.out.println(i.getThisPC()+" "+i.getPostStackBase());
      //  }
        Instruction initial = method.getInstructions().iterator().next();


                int argOffset = 0;
                if (!method.isStatic()) {
                    if (entryPoint == null) {
                        add( new ld_kernarg(stackFrame,initial, new VarReg_ref(0)));
                    } else {
                        add( new ld_arg(stackFrame,initial, new VarReg_ref(0)));
                    }
                    argOffset++;
                }
                for (TypeHelper.JavaMethodArg arg : method.argsAndReturnType.getArgs()) {
                    if (arg.getJavaType().isArray()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(stackFrame,initial, new VarReg_ref(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(stackFrame,initial, new VarReg_ref(arg.getArgc() + argOffset)));
                        }
                    } else if (arg.getJavaType().isObject()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(stackFrame,initial, new VarReg_ref(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(stackFrame,initial, new VarReg_ref(arg.getArgc() + argOffset)));
                        }
                    } else if (arg.getJavaType().isInt()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(stackFrame,initial, new VarReg_s32(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(stackFrame,initial, new VarReg_s32(arg.getArgc() + argOffset)));
                        }
                    } else if (arg.getJavaType().isFloat()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(stackFrame,initial, new VarReg_f32(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(stackFrame,initial, new VarReg_f32(arg.getArgc() + argOffset)));
                        }
                    } else if (arg.getJavaType().isDouble()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(stackFrame,initial, new VarReg_f64(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(stackFrame,initial, new VarReg_f64(arg.getArgc() + argOffset)));
                        }
                    } else if (arg.getJavaType().isLong()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(stackFrame,initial, new VarReg_s64(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(stackFrame,initial, new VarReg_s64(arg.getArgc() + argOffset)));
                        }
                    }
                }

    for (Instruction i : method.getInstructions()) {

            switch (i.getByteCode()) {

                case ACONST_NULL:
                    add(new nyi(stackFrame, i));
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
                    add(new mov_const<StackReg_s32,s32, Integer>(stackFrame,i, new StackReg_s32(i, 0), i.asIntegerConstant().getValue()));
                    break;
                case LCONST_0:
                case LCONST_1:
                    add(new mov_const<StackReg_s64,s64, Long>(stackFrame, i, new StackReg_s64(i, 0), i.asLongConstant().getValue()));
                    break;
                case FCONST_0:
                case FCONST_1:
                case FCONST_2:
                    add(new mov_const<StackReg_f32,f32, Float>(stackFrame, i, new StackReg_f32(i, 0), i.asFloatConstant().getValue()));
                    break;
                case DCONST_0:
                case DCONST_1:
                    add(new mov_const<StackReg_f64,f64, Double>(stackFrame, i, new StackReg_f64(i, 0), i.asDoubleConstant().getValue()));
                    break;
                // case BIPUSH: moved up
                // case SIPUSH: moved up

                case LDC:
                case LDC_W:
                case LDC2_W: {
                    InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant) i;

                    ClassModel.ConstantPool.ConstantEntry e = (ClassModel.ConstantPool.ConstantEntry) cpe.getConstantPoolEntry();
                    if (e instanceof ClassModel.ConstantPool.DoubleEntry) {
                        add(new mov_const<StackReg_f64,f64, Double>(stackFrame, i, new StackReg_f64(i, 0), ((ClassModel.ConstantPool.DoubleEntry) e).getValue()));
                    } else if (e instanceof ClassModel.ConstantPool.FloatEntry) {
                        add(new mov_const<StackReg_f32,f32, Float>(stackFrame, i, new StackReg_f32(i, 0), ((ClassModel.ConstantPool.FloatEntry) e).getValue()));
                    } else if (e instanceof ClassModel.ConstantPool.IntegerEntry) {
                        add(new mov_const<StackReg_s32,s32, Integer>(stackFrame, i, new StackReg_s32(i, 0), ((ClassModel.ConstantPool.IntegerEntry) e).getValue()));
                    } else if (e instanceof ClassModel.ConstantPool.LongEntry) {
                        add(new mov_const<StackReg_s64,s64, Long>(stackFrame, i, new StackReg_s64(i, 0), ((ClassModel.ConstantPool.LongEntry) e).getValue()));

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
                    add(new mov<StackReg_s32,VarReg_s32, s32, s32>(stackFrame, i, new StackReg_s32(i, 0), new VarReg_s32(i)));

                    break;
                case LLOAD:
                case LLOAD_0:
                case LLOAD_1:
                case LLOAD_2:
                case LLOAD_3:
                    add(new mov<StackReg_s64,VarReg_s64, s64, s64>(stackFrame, i, new StackReg_s64(i, 0), new VarReg_s64(i)));
                    break;
                case FLOAD:
                case FLOAD_0:
                case FLOAD_1:
                case FLOAD_2:
                case FLOAD_3:
                    add(new mov<StackReg_f32,VarReg_f32, f32, f32>(stackFrame, i, new StackReg_f32(i, 0), new VarReg_f32(i)));
                    break;
                case DLOAD:
                case DLOAD_0:
                case DLOAD_1:
                case DLOAD_2:
                case DLOAD_3:
                    add(new mov<StackReg_f64,VarReg_f64, f64, f64>(stackFrame, i, new StackReg_f64(i, 0), new VarReg_f64(i)));
                    break;
                case ALOAD:
                case ALOAD_0:
                case ALOAD_1:
                case ALOAD_2:
                case ALOAD_3:
                    add(new mov<StackReg_ref,VarReg_ref, ref ,ref>(stackFrame, i, new StackReg_ref(i, 0), new VarReg_ref(i)));
                    break;
                case IALOAD:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s32.getHsaBytes()));
                    add(new array_load<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_ref(i, 1)));
                    break;
                case LALOAD:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s64.getHsaBytes()));
                    add(new array_load<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_ref(i, 1)));
                    break;
                case FALOAD:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f32.getHsaBytes()));
                    add(new array_load<StackReg_f32,f32>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_ref(i, 1)));

                    break;
                case DALOAD:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f64.getHsaBytes()));
                    add(new array_load<StackReg_f64,f64>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_ref(i, 1)));

                    break;
                case AALOAD:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.ref.getHsaBytes()));
                    add(new array_load<StackReg_ref, ref>(stackFrame, i, new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
                    break;
                case BALOAD:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s8.getHsaBytes()));
                    add(new array_load<StackReg_s8, s8>(stackFrame, i, new StackReg_s8(i, 0), new StackReg_ref(i, 1)));
                    break;
                case CALOAD:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.u16.getHsaBytes()));
                    add(new array_load<StackReg_u16,u16>(stackFrame, i, new StackReg_u16(i, 0), new StackReg_ref(i, 1)));
                    break;
                case SALOAD:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s16.getHsaBytes()));
                    add(new array_load<StackReg_s16,s16>(stackFrame, i, new StackReg_s16(i, 0), new StackReg_ref(i, 1)));
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
                    add(new mov<VarReg_s32,StackReg_s32,s32,s32>(stackFrame, i, new VarReg_s32(i), new StackReg_s32(i, 0)));

                    break;
                case LSTORE:
                case LSTORE_0:
                case LSTORE_1:
                case LSTORE_2:
                case LSTORE_3:
                    add(new mov<VarReg_s64,StackReg_s64,s64,s64>(stackFrame, i, new VarReg_s64(i), new StackReg_s64(i, 0)));

                    break;
                case FSTORE:
                case FSTORE_0:
                case FSTORE_1:
                case FSTORE_2:
                case FSTORE_3:
                    add(new mov<VarReg_f32,StackReg_f32,f32,f32>(stackFrame, i, new VarReg_f32(i), new StackReg_f32(i, 0)));
                    break;
                case DSTORE:
                case DSTORE_0:
                case DSTORE_1:
                case DSTORE_2:
                case DSTORE_3:
                    add(new mov<VarReg_f64,StackReg_f64,f64,f64>(stackFrame, i, new VarReg_f64(i), new StackReg_f64(i, 0)));
                    break;
                case ASTORE:
                case ASTORE_0:
                case ASTORE_1:
                case ASTORE_2:
                case ASTORE_3:
                    add(new mov<VarReg_ref,StackReg_ref,ref,ref>(stackFrame, i, new VarReg_ref(i), new StackReg_ref(i, 0)));

                    break;
                case IASTORE:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s32.getHsaBytes()));
                    add(new array_store<StackReg_s32,s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 2)));
                    break;
                case LASTORE:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s64.getHsaBytes()));
                    add(new array_store<StackReg_u64,u64>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_u64(i, 2)));
                    break;
                case FASTORE:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f32.getHsaBytes()));
                    add(new array_store<StackReg_f32, f32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_f32(i, 2)));
                    break;
                case DASTORE:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f64.getHsaBytes()));
                    add(new array_store<StackReg_f64,f64>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_f64(i, 2)));
                    break;
                case AASTORE:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.ref.getHsaBytes()));
                    add(new array_store<StackReg_ref,ref>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_ref(i, 2)));

                    break;
                case BASTORE:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s8.getHsaBytes()));
                    add(new array_store<StackReg_s8,s8>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s8(i, 2)));

                    break;
                case CASTORE:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.u16.getHsaBytes()));
                    add(new array_store<StackReg_u16,u16>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_u16(i, 2)));
                    break;
                case SASTORE:
                    add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(stackFrame,i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s16.getHsaBytes()));
                    add(new array_store<StackReg_s16,s16>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_s16(i, 2)));
                    break;
                case POP:
                    add(new nyi(stackFrame,i));
                    break;
                case POP2:
                    add(new nyi(stackFrame,i));
                    break;
                case DUP:
                   // add(new nyi(i));
                    addmov(stackFrame,i, 0, 1);
                    break;
                case DUP_X1:
                    add(new nyi(stackFrame,i));
                    break;
                case DUP_X2:

                    addmov(stackFrame,i, 2, 3);
                    addmov(stackFrame,i, 1, 2);
                    addmov(stackFrame,i, 0, 1);
                    addmov(stackFrame,i, 3, 0);

                    break;
                case DUP2:
                    // DUP2 is problematic. DUP2 either dups top two items or one depending on the 'prefix' of the stack items.
                    // To complicate this further HSA large model wants object/mem references to be 64 bits (prefix 2 in Java) whereas
                    // in java object/array refs are 32 bits (prefix 1).
                    addmov(stackFrame, i, 0, 2);
                    addmov(stackFrame,i, 1, 3);
                    break;
                case DUP2_X1:
                    add(new nyi(stackFrame,i));
                    break;
                case DUP2_X2:
                    add(new nyi(stackFrame,i));
                    break;
                case SWAP:
                    add(new nyi(stackFrame,i));
                    break;
                case IADD:
                    add(new add<StackReg_s32, s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LADD:
                    add(new add<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case FADD:
                    add(new add<StackReg_f32,f32>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
                    break;
                case DADD:
                    add(new add<StackReg_f64,f64>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
                    break;
                case ISUB:
                    add(new sub<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LSUB:
                    add(new sub<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case FSUB:
                    add(new sub<StackReg_f32,f32>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
                    break;
                case DSUB:
                    add(new sub<StackReg_f64,f64>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
                    break;
                case IMUL:
                    add(new mul<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LMUL:
                    add(new mul<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case FMUL:
                    add(new mul<StackReg_f32,f32>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
                    break;
                case DMUL:
                    add(new mul<StackReg_f64,f64>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
                    break;
                case IDIV:
                    add(new div<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LDIV:
                    add(new div<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case FDIV:
                    add(new div<StackReg_f32,f32>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
                    break;
                case DDIV:
                    add(new div<StackReg_f64,f64>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
                    break;
                case IREM:
                    add(new rem<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LREM:
                    add(new rem<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case FREM:
                    add(new rem<StackReg_f32,f32>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
                    break;
                case DREM:
                    add(new rem<StackReg_f64,f64>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
                    break;
                case INEG:
                    add(new neg<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0)));
                    break;
                case LNEG:
                    add(new neg<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0)));
                    break;
                case FNEG:
                    add(new neg<StackReg_f32,f32>(stackFrame, i, new StackReg_f32(i, 0)));
                    break;
                case DNEG:
                    add(new neg<StackReg_f64,f64>(stackFrame, i, new StackReg_f64(i, 0)));
                    break;
                case ISHL:
                    add(new shl<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LSHL:
                    add(new shl<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case ISHR:
                    add(new shr<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LSHR:
                    add(new shr<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case IUSHR:
                    add(new ushr<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LUSHR:
                    add(new ushr<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case IAND:
                    add(new and<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LAND:
                    add(new and<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case IOR:
                    add(new or<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LOR:
                    add(new or<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case IXOR:
                    add(new xor<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LXOR:
                    add(new xor<StackReg_s64, s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case IINC:
                    add(new add_const<VarReg_s32, s32, Integer>(stackFrame, i, new VarReg_s32(i), new VarReg_s32(i), ((InstructionSet.I_IINC) i).getDelta()));
                    break;
                case I2L:
                    add(new cvt<StackReg_s64,StackReg_s32,s64, s32>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_s32(i, 0)));
                    break;
                case I2F:
                    add(new cvt<StackReg_f32,StackReg_s32,f32, s32>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_s32(i, 0)));
                    break;
                case I2D:
                    add(new cvt<StackReg_f64,StackReg_s32,f64, s32>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_s32(i, 0)));
                    break;
                case L2I:
                    add(new cvt<StackReg_s32,StackReg_s64,s32, s64>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_s64(i, 0)));
                    break;
                case L2F:
                    add(new cvt<StackReg_f32,StackReg_s64,f32, s64>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_s64(i, 0)));
                    break;
                case L2D:
                    add(new cvt<StackReg_f64,StackReg_s64,f64, s64>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_s64(i, 0)));
                    break;
                case F2I:
                    add(new cvt<StackReg_s32,StackReg_f32,s32, f32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_f32(i, 0)));
                    break;
                case F2L:
                    add(new cvt<StackReg_s64,StackReg_f32,s64, f32>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_f32(i, 0)));
                    break;
                case F2D:
                    add(new cvt<StackReg_f64,StackReg_f32,f64, f32>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_f32(i, 0)));
                    break;
                case D2I:
                    add(new cvt<StackReg_s32,StackReg_f64,s32, f64>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_f64(i, 0)));
                    break;
                case D2L:
                    add(new cvt<StackReg_s64,StackReg_f64,s64, f64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_f64(i, 0)));
                    break;
                case D2F:
                    add(new cvt<StackReg_f32,StackReg_f64,f32, f64>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_f64(i, 0)));
                    break;
                case I2B:
                    add(new cvt<StackReg_s8,StackReg_s32,s8, s32>(stackFrame, i, new StackReg_s8(i, 0), new StackReg_s32(i, 0)));
                    break;
                case I2C:
                    add(new cvt<StackReg_u16,StackReg_s32,u16, s32>(stackFrame, i, new StackReg_u16(i, 0), new StackReg_s32(i, 0)));
                    break;
                case I2S:
                    add(new cvt<StackReg_s16,StackReg_s32,s16, s32>(stackFrame, i, new StackReg_s16(i, 0), new StackReg_s32(i, 0)));
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
                        add(new cmp<StackReg_f32,f32>(stackFrame,lastInstruction, "eq", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<StackReg_f64,f64>(stackFrame,lastInstruction, "eq", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<StackReg_s64,s64>(stackFrame,lastInstruction, "eq", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(stackFrame,i, "eq", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));
                    break;
                case IFNE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        add(new cmp<StackReg_f32,f32>(stackFrame,lastInstruction, "ne", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<StackReg_f64,f64>(stackFrame,lastInstruction, "ne", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<StackReg_s64,s64>(stackFrame,lastInstruction, "ne", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(stackFrame,i, "ne", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));
                    break;
                case IFLT:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        add(new cmp<StackReg_f32,f32>(stackFrame,lastInstruction, "lt", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<StackReg_f64,f64>(stackFrame,lastInstruction, "lt", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<StackReg_s64,s64>(stackFrame,lastInstruction, "lt", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(stackFrame,i, "lt", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));
                    break;
                case IFGE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        add(new cmp<StackReg_f32,f32>(stackFrame,lastInstruction, "ge", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<StackReg_f64,f64>(stackFrame,lastInstruction, "ge", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<StackReg_s64,s64>(stackFrame,lastInstruction, "ge", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(stackFrame,i, "ge", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));
                    break;
                case IFGT:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        add(new cmp<StackReg_f32,f32>(stackFrame,lastInstruction, "gt", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<StackReg_f64,f64>(stackFrame,lastInstruction, "gt", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<StackReg_s64,s64>(stackFrame,lastInstruction, "gt", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(stackFrame,i, "gt", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));
                    break;
                case IFLE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        add(new cmp<StackReg_f32,f32>(stackFrame,lastInstruction, "le", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<StackReg_f64,f64>(stackFrame,lastInstruction, "le", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<StackReg_s64, s64>(stackFrame,lastInstruction, "le", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(stackFrame,i, "le", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));
                    break;
                case IF_ICMPEQ:

                    add(new cmp_s32(stackFrame,i, "eq", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));

                    break;
                case IF_ICMPNE:

                    add(new cmp_s32(stackFrame,i, "ne", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));

                    break;
                case IF_ICMPLT:

                    add(new cmp_s32(stackFrame,i, "lt", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));

                    break;
                case IF_ICMPGE:

                    add(new cmp_s32(stackFrame,i, "ge", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));

                    break;
                case IF_ICMPGT:

                    add(new cmp_s32(stackFrame,i, "gt", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));

                    break;
                case IF_ICMPLE:

                    add(new cmp_s32(stackFrame,i, "le", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));

                    break;
                case IF_ACMPEQ:
                    add(new cmp_ref(stackFrame,i, "eq", new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));
                    break;
                case IF_ACMPNE:
                    add(new cmp_ref(stackFrame,i, "ne", new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
                    add(new cbr(stackFrame,i, i.asBranch().getAbsolute()));
                    break;
                case GOTO:
                    add(new brn(stackFrame,i, i.asBranch().getAbsolute()));
                    break;
                case IFNULL:
                case IFNONNULL:
                case GOTO_W:
                    add(new branch(stackFrame,i, new StackReg_s32(i, 0), i.getByteCode().getName(), i.asBranch().getAbsolute()));
                    break;
                case JSR:
                    add(new nyi(stackFrame, i));
                    break;
                case RET:
                    add(new nyi(stackFrame, i));
                    break;
                case TABLESWITCH:
                    add(new nyi(stackFrame, i));
                    break;
                case LOOKUPSWITCH:
                    add(new nyi(stackFrame, i));
                    break;
                case IRETURN:
                    add(new ret<StackReg_s32, s32>(stackFrame, i, new StackReg_s32(i, 0)));
                    break;
                case LRETURN:
                    add(new ret<StackReg_s64, s64>(stackFrame, i, new StackReg_s64(i, 0)));
                    break;
                case FRETURN:
                    add(new ret<StackReg_f32, f32>(stackFrame, i, new StackReg_f32(i, 0)));
                    break;
                case DRETURN:
                    add(new ret<StackReg_f64, f64>(stackFrame, i, new StackReg_f64(i, 0)));
                    break;
                case ARETURN:
                    add(new ret<StackReg_ref,ref>(stackFrame, i, new StackReg_ref(i, 0)));
                    break;
                case RETURN:
                    add(new retvoid(stackFrame,i));
                    break;
                case GETSTATIC: {
                    TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

                    try {
                        Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                        Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());

                        if (type.isArray()) {
                            add(new static_field_load<StackReg_ref,ref>(stackFrame, i, new StackReg_ref(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
                        } else if (type.isInt()) {
                            add(new static_field_load<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
                        } else if (type.isFloat()) {
                            add(new static_field_load<StackReg_f32,f32>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
                        } else if (type.isDouble()) {
                            add(new static_field_load<StackReg_f64,f64>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
                        } else if (type.isLong()) {
                            add(new static_field_load<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
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
                            add(new field_load<StackReg_ref,ref>(stackFrame, i, new StackReg_ref(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(int.class)) {
                            add(new field_load<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(short.class)) {
                            add(new field_load<StackReg_s16,s16>(stackFrame, i, new StackReg_s16(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(char.class)) {
                            add(new field_load<StackReg_u16,u16>(stackFrame, i, new StackReg_u16(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(boolean.class)) {
                            add(new field_load<StackReg_s8,s8>(stackFrame, i, new StackReg_s8(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(float.class)) {
                            add(new field_load<StackReg_f32,f32>(stackFrame, i, new StackReg_f32(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(double.class)) {
                            add(new field_load<StackReg_f64,f64>(stackFrame, i, new StackReg_f64(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(long.class)) {
                            add(new field_load<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));

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
                    add(new nyi(stackFrame, i));
                    break;
                case PUTFIELD: {
                   // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

                    try {
                        Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                        Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                        if (!f.getType().isPrimitive()) {
                            add(new field_store<StackReg_ref, ref>(stackFrame, i, new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(int.class)) {
                            add(new field_store<StackReg_s32,s32>(stackFrame, i, new StackReg_s32(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(short.class)) {
                            add(new field_store<StackReg_s16,s16>(stackFrame, i, new StackReg_s16(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(char.class)) {
                            add(new field_store<StackReg_u16,u16>(stackFrame, i, new StackReg_u16(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(boolean.class)) {
                            add(new field_store<StackReg_s8,s8>(stackFrame, i, new StackReg_s8(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(float.class)) {
                            add(new field_store<StackReg_f32,f32>(stackFrame, i, new StackReg_f32(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(double.class)) {
                            add(new field_store<StackReg_f64,f64>(stackFrame, i, new StackReg_f64(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(long.class)) {
                            add(new field_store<StackReg_s64,s64>(stackFrame, i, new StackReg_s64(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));

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

                    add(new call(stackFrame,i));
                    break;
                case NEW:
                    add(new nyi(stackFrame, i));
                    break;
                case NEWARRAY:
                    add(new nyi(stackFrame, i));
                    break;
                case ANEWARRAY:
                    add(new nyi(stackFrame, i));
                    break;
                case ARRAYLENGTH:
                    add(new array_len(stackFrame,i, new StackReg_s32(i, 0), new StackReg_ref(i, 0)));

                    break;
                case ATHROW:
                    add(new nyi(stackFrame, i));
                    break;
                case CHECKCAST:
                    add(new nyi(stackFrame, i));
                    break;
                case INSTANCEOF:
                    add(new nyi(stackFrame, i));
                    break;
                case MONITORENTER:
                    add(new nyi(stackFrame, i));
                    break;
                case MONITOREXIT:
                    add(new nyi(stackFrame, i));
                    break;
                case WIDE:
                    add(new nyi(stackFrame, i));
                    break;
                case MULTIANEWARRAY:
                    add(new nyi(stackFrame, i));
                    break;
                case JSR_W:
                    add( new nyi(stackFrame, i));
                    break;

            }
            lastInstruction = i;


        }

    }
}
