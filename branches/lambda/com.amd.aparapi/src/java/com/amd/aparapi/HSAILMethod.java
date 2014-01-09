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

    public static class RenderContext{
        public int baseOffset;
        private String nameSpace;
        Map<RenderContext,Integer> locMap = new LinkedHashMap<RenderContext, Integer>();
        int loc=0;
        public String getLocation(RenderContext _renderContext, int pc){
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

        public String getUniqueNameSpace(RenderContext _renderContext){
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
        RenderContext last = null;
        RenderContext(RenderContext _last, String _nameSpace, int _baseOffset){
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

        String getMappedMethod() {
            return (mappedMethod);
        }

        CallType(String _mappedMethod) {
            mappedMethod = _mappedMethod;
        }

        abstract T renderDefinition(HSAILRenderer r, RenderContext _renderContext);
        abstract T renderDeclaration(HSAILRenderer r, RenderContext _renderContext);
        abstract T renderCallSite(HSAILRenderer r, RenderContext _renderContext, Instruction from,  String name);
        abstract boolean isStatic();
    }


    public static class IntrinsicCall extends CallType<IntrinsicCall> {
        String[] lines;
        boolean isStatic;

        IntrinsicCall(String _mappedMethod, boolean _isStatic, String... _lines) {
            super(_mappedMethod);
            lines = _lines;
            isStatic = _isStatic;
        }

        @Override
        IntrinsicCall renderDefinition(HSAILRenderer r, RenderContext _renderContext) {
            for (String line : lines) {
                if (!(line.trim().endsWith("{") || line.trim().startsWith("}"))) {
                    r.pad(9);
                }
                r.append(line).nl();
            }
            return this;
        }
        @Override
           IntrinsicCall renderCallSite(HSAILRenderer r,  RenderContext _renderContext, Instruction from,  String name) {
            return(this);
        }
        @Override
        IntrinsicCall renderDeclaration(HSAILRenderer r, RenderContext _renderContext) {
            return(this);
        }
        @Override
        boolean isStatic() {
            return (isStatic);
        }
    }

    public static class InlineIntrinsicCall extends IntrinsicCall {



        InlineIntrinsicCall(String _mappedMethod, boolean _isStatic,  String... _lines) {
            super(_mappedMethod, _isStatic, _lines);
        }



        final Pattern regex= Pattern.compile("\\$\\{([0-9]+)\\}");
        String expand(String line, RenderContext _renderContext){
            StringBuffer sb= new StringBuffer();
            Matcher matcher = regex.matcher(line);

            while (matcher.find()) {
                matcher.appendReplacement(sb, String.format("%d",Integer.parseInt(matcher.group(1))+_renderContext.baseOffset));
            }
            matcher.appendTail(sb);

            return(sb.toString());
        }

        @Override
        InlineIntrinsicCall renderCallSite(HSAILRenderer r, RenderContext _renderContext, Instruction from, String name) {
            boolean first = false;
            r.lineComment("inlining intrinsic "+getMappedMethod()+"{");
            if (isStatic){
                r.pad(9).lineComment(expand("This is a static method so $?${0} contains first arg (if any)", _renderContext));
            }else{
                r.pad(9).lineComment(expand("This is a virtual method so $d${0} contains this. Other args (if any) from $?${1}", _renderContext));
            }
            for (String line : lines) {
               // if (!first){
                    r.pad(9);
              //  }
                String expandedLine = expand(line, _renderContext);

                r.append(expandedLine).nl();
                //first = false;
            }
            r.pad(9);
            r.lineComment("} inlining intrinsic "+getMappedMethod());
            return this;
        }
        @Override
        InlineIntrinsicCall renderDefinition(HSAILRenderer r, RenderContext _renderContext) {
            return(this);
        }
        @Override
        IntrinsicCall renderDeclaration(HSAILRenderer r, RenderContext _renderContext) {
            return(this);
        }
        @Override
        boolean isStatic() {
            return (isStatic);
        }
    }

    public static class MethodCall extends CallType<MethodCall> {
        HSAILMethod method;


        MethodCall(String _mappedMethod, HSAILMethod _method) {
            super(_mappedMethod);
            method = _method;
        }

        @Override
        MethodCall renderDefinition(HSAILRenderer r, RenderContext _renderContext) {

            method.renderFunctionDefinition(r, _renderContext);
            r.nl().nl();
            return (this);
        }
        @Override
        MethodCall renderCallSite(HSAILRenderer r, RenderContext _renderContext, Instruction from, String name) {

            TypeHelper.JavaMethodArgsAndReturnType argsAndReturnType = from.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType();
            TypeHelper.JavaType returnType = argsAndReturnType.getReturnType();
            r.obrace().nl();
            if (!isStatic()) {
                r.pad(12).append("arg_u64 %this").semicolon().nl();
                r.pad(12).append("st_arg_u64 $d" + _renderContext.baseOffset + ", [%this]").semicolon().nl();
            }

            int offset = 0;
            if (!isStatic()) {
                offset++;
            }
            for (TypeHelper.JavaMethodArg arg : argsAndReturnType.getArgs()) {
                String argName = "%_arg_" + arg.getArgc();
                r.pad(12).append("arg_").typeName(arg.getJavaType()).space().append(argName).semicolon().nl();
                r.pad(12).append("st_arg_").typeName(arg.getJavaType()).space().regPrefix(arg.getJavaType()).append( + (_renderContext.baseOffset + offset) + ", [" + argName + "]").semicolon().nl();
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
                r.pad(12).append("ld_arg_").typeName(returnType).space().regPrefix(returnType).append( _renderContext.baseOffset + ", [%_result]").semicolon().nl();
            }
            r.pad(9).cbrace();

            r.nl().nl();
            return(this);
        }
        @Override
        MethodCall renderDeclaration(HSAILRenderer r, RenderContext _renderContext) {
            method.renderFunctionDeclaration(r, _renderContext);
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

        InlineMethodCall(String _mappedMethod, HSAILMethod _method) {
            super(_mappedMethod);
            method = _method;
        }
        @Override
        InlineMethodCall renderDefinition(HSAILRenderer r, RenderContext _renderContext) {
            return (this);
        }
        @Override
        InlineMethodCall renderCallSite(HSAILRenderer r, RenderContext _renderContext, Instruction from, String name) {

            method.renderInlinedFunctionBody(r, _renderContext, _renderContext.baseOffset);

            //r.nl();
            return (this);
        }

        @Override
        InlineMethodCall renderDeclaration(HSAILRenderer r, RenderContext _renderContext) {
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
        add(new InlineIntrinsicCall("java.lang.Math.sqrt(D)D", true,
                "nsqrt_f64  $d${0}, $d${0};"
    ));
        add(new InlineIntrinsicCall("java.lang.String.charAt(I)C", false,
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
        add(new IntrinsicCall("java.lang.Math.hypot(DD)D", true,
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

    abstract class HSAILInstruction<H extends HSAILInstruction>  {
        Instruction from;
        HSAILRegister[] dests = null;
        HSAILRegister[] sources = null;

        HSAILInstruction(HSAILInstruction original) {
                from = original.from;
                dests = new HSAILRegister[original.dests.length];
                for (int i=0; i<dests.length; i++){
                    //  dests[i] = original.dests[i].cloneMe();
                }
                sources = new HSAILRegister[original.sources.length];
                for (int i=0; i<dests.length; i++){
                    //  sources[i] = original.sources[i].cloneMe();
                }

        }

        HSAILInstruction(Instruction _from, int _destCount, int _sourceCount) {
            from = _from;
            dests = new HSAILRegister[_destCount];
            sources = new HSAILRegister[_sourceCount];
        }

        public abstract  H cloneMe(H original);




        abstract void render(HSAILRenderer r, RenderContext _renderContext);

    }

    abstract class HSAILInstructionWithDest<H extends HSAILInstruction<H>, T extends PrimitiveType> extends HSAILInstruction<H> {

        protected HSAILInstructionWithDest(H original){
            super(original);

        }

        HSAILInstructionWithDest(Instruction _from, HSAILRegister<T> _dest) {
            super(_from, 1, 0);
            dests[0] = _dest;

        }

        HSAILRegister<T> getDest() {
            return ((HSAILRegister<T>) dests[0]);
        }
    }

    abstract class HSAILInstructionWithSrc<H extends HSAILInstruction<H>, T extends PrimitiveType> extends HSAILInstruction<H> {

        protected HSAILInstructionWithSrc(H original){
            super(original);
        }


        HSAILInstructionWithSrc(Instruction _from, HSAILRegister<T> _src) {
            super(_from, 0, 1);
            sources[0] = _src;
        }

        HSAILRegister<T> getSrc() {
            return ((HSAILRegister<T>) sources[0]);
        }
    }

    abstract class HSAILInstructionWithSrcSrc<H extends HSAILInstruction<H>,T extends PrimitiveType> extends HSAILInstruction<H> {

        protected HSAILInstructionWithSrcSrc(H original){
            super(original);
        }
        HSAILInstructionWithSrcSrc(Instruction _from, HSAILRegister<T> _src_lhs, HSAILRegister<T> _src_rhs) {
            super(_from, 0, 2);
            sources[0] = _src_lhs;
            sources[1] = _src_rhs;
        }

        HSAILRegister<T> getSrcLhs() {
            return ((HSAILRegister<T>) sources[0]);
        }

        HSAILRegister<T> getSrcRhs() {
            return ((HSAILRegister<T>) sources[1]);
        }
    }

    abstract class HSAILInstructionWithDestSrcSrc<H extends HSAILInstructionWithDestSrcSrc<H,D,T>, D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstruction<H> {

        protected HSAILInstructionWithDestSrcSrc(H original){
            super(original);
        }
        HSAILInstructionWithDestSrcSrc(Instruction _from, HSAILRegister<D> _dest, HSAILRegister<T> _src_lhs, HSAILRegister<T> _src_rhs) {
            super(_from, 1, 2);
            dests[0] = _dest;
            sources[0] = _src_lhs;
            sources[1] = _src_rhs;
        }

        HSAILRegister<D> getDest() {
            return ((HSAILRegister<D>) dests[0]);
        }

        HSAILRegister<T> getSrcLhs() {
            return ((HSAILRegister<T>) sources[0]);
        }

        HSAILRegister<T> getSrcRhs() {
            return ((HSAILRegister<T>) sources[1]);
        }
    }

    abstract class HSAILInstructionWithDestSrc<H extends HSAILInstructionWithDestSrc<H,T>, T extends PrimitiveType> extends HSAILInstruction<H> {
        HSAILInstructionWithDestSrc(H original){
            super(original);
        }
        HSAILInstructionWithDestSrc(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _src) {
            super(_from, 1, 1);
            dests[0] = _dest;
            sources[0] = _src;
        }

        HSAILRegister<T> getDest() {
            return ((HSAILRegister<T>) dests[0]);
        }

        HSAILRegister<T> getSrc() {
            return ((HSAILRegister<T>) sources[0]);
        }
    }

    class branch extends HSAILInstructionWithSrc<branch, s32> {
        String branchName;
        int pc;

        protected branch(branch original){
            super(original);
            branchName = original.branchName;
            pc = original.pc;
        }

        @Override public branch cloneMe(branch original){
            return(new branch(original));
        }

        branch(Instruction _from, HSAILRegister<s32> _src, String _branchName, int _pc) {
            super(_from, _src);
            branchName = _branchName;
            pc = _pc;
        }




        @Override
        public void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append(branchName).space().label(_renderContext.getLocation(pc)).semicolon();
        }
    }

    class cmp_s32_const_0 extends HSAILInstructionWithSrc<cmp_s32_const_0, s32> {

        protected cmp_s32_const_0(cmp_s32_const_0 original){
            super(original);
        }
        @Override public cmp_s32_const_0 cloneMe(cmp_s32_const_0 original){
            return(new cmp_s32_const_0(original));
        }
        String type;

        cmp_s32_const_0(Instruction _from, String _type, Reg_s32 _src) {
            super(_from, _src);
            type = _type;
        }


        @Override
        public void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("cmp_").append(type).append("_b1_").typeName(getSrc()).space().append("$c1").separator().regName(getSrc(), _renderContext).separator().append("0").semicolon();

        }
    }

    class cmp_s32 extends HSAILInstructionWithSrcSrc<cmp_s32, s32> {

        protected cmp_s32(cmp_s32 original){
            super(original);
            type = original.type;

        }

        @Override public cmp_s32 cloneMe(cmp_s32 original){
            return(new cmp_s32(original));
        }

        String type;

        cmp_s32(Instruction _from, String _type, Reg_s32 _srcLhs, Reg_s32 _srcRhs) {
            super(_from, _srcLhs, _srcRhs);
            type = _type;
        }


        @Override
        public void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs(), _renderContext).separator().regName(getSrcRhs(), _renderContext).semicolon();

        }
    }
    class cmp_ref extends HSAILInstructionWithSrcSrc<cmp_ref, ref> {

        protected cmp_ref(cmp_ref original){
            super(original);
            type = original.type;
        }

        @Override public cmp_ref cloneMe(cmp_ref original){
            return(new cmp_ref(original));
        }

        String type;

        cmp_ref(Instruction _from, String _type, Reg_ref _srcLhs, Reg_ref _srcRhs) {
            super(_from, _srcLhs, _srcRhs);
            type = _type;
        }


        @Override
        public void render(HSAILRenderer r,RenderContext _renderContext) {
            r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs(), _renderContext).separator().regName(getSrcRhs(), _renderContext).semicolon();

        }
    }

    class cmp<T extends PrimitiveType> extends HSAILInstructionWithSrcSrc<cmp<T>, T> {

        protected cmp(cmp original){
            super(original);
            type = original.type;
        }

        @Override public cmp cloneMe(cmp original){
            return(new cmp(original));
        }

        String type;

        cmp(Instruction _from, String _type, HSAILRegister<T> _srcLhs, HSAILRegister<T> _srcRhs) {
            super(_from, _srcLhs, _srcRhs);
            type = _type;
        }


        @Override
        public void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("cmp_").append(type).append("u").append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs(), _renderContext).separator().regName(getSrcRhs(), _renderContext).semicolon();

        }
    }

    class cbr extends HSAILInstruction<cbr> {

        protected cbr(cbr original){
            super(original);
            pc = original.pc;
        }

        @Override public cbr cloneMe(cbr original){
            return(new cbr(original));
        }


        int pc;

        cbr(Instruction _from, int _pc) {
            super(_from, 0, 0);
            pc = _pc;
        }


        @Override
        public void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("cbr").space().append("$c1").separator().label(_renderContext.getLocation(pc)).semicolon();

        }
    }

    class brn extends HSAILInstruction<brn> {

        protected brn(brn original){
            super(original);
            pc = original.pc;
        }

        @Override public brn cloneMe(brn original){
            return(new brn(original));
        }

        int pc;

        brn(Instruction _from, int _pc) {
            super(_from, 0, 0);
            pc = _pc;
        }


        @Override
        public void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("brn").space().label(_renderContext.getLocation(pc)).semicolon();

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

        protected call(call original){
            super(original);
            base = original.base;
            name = original.name;
            mangledName = original.mangledName;
            call = original.call;
        }

        @Override public call cloneMe(call original){
            return(new call(original));
        }

        int base;
        String name;
        String mangledName;
        CallType call;
        call(Instruction _from) {
            super(_from, 0, 0);
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
                    HSAILMethod hsailMethod = HSAILMethod.getHSAILMethod(method, getEntryPoint());
                    call = new InlineMethodCall(intrinsicLookup, hsailMethod);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (ClassParseException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            add(call);
        }


        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            RenderContext rc = new RenderContext(_renderContext, String.format("@%04d : %s",from.getThisPC(), mangledName), base);

            call.renderCallSite(r,rc, from,  name);

        }


    }


    class nyi extends HSAILInstruction<nyi> {

        protected nyi(nyi original){
            super(original);

        }

        @Override public nyi cloneMe(nyi original){
            return(new nyi(original));
        }
        nyi(Instruction _from) {
            super(_from, 0, 0);
        }


        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {

            r.append("NYI ").i(from);

        }
    }

    class ld_kernarg<T extends PrimitiveType> extends HSAILInstructionWithDest<ld_kernarg<T>, T> {

        protected ld_kernarg(ld_kernarg original){
            super(original);

        }

        @Override public ld_kernarg cloneMe(ld_kernarg original){
            return(new ld_kernarg(original));
        }

        ld_kernarg(Instruction _from, HSAILRegister<T> _dest) {
            super(_from, _dest);
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("ld_kernarg_").typeName(getDest()).space().regName(getDest(), _renderContext).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
        }
    }

    class ld_arg<T extends PrimitiveType> extends HSAILInstructionWithDest<ld_arg<T>, T> {

        protected ld_arg(ld_arg original){
            super(original);

        }

        @Override public ld_arg cloneMe(ld_arg original){
            return(new ld_arg(original));
        }
        ld_arg(Instruction _from, HSAILRegister<T> _dest) {
            super(_from, _dest);
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("ld_arg_").typeName(getDest()).space().regName(getDest(), _renderContext).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
        }


    }

    abstract class binary_const<H extends binary_const<H, T, C>, T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDestSrc<H, T> {

        protected binary_const(H original){
            super(original);
            value = original.value;
            op = original.op;
        }



        C value;
        String op;

        binary_const(Instruction _from, String _op, HSAILRegister<T> _dest, HSAILRegister _src, C _value) {
            super(_from, _dest, _src);
            value = _value;
            op = _op;
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append(op).typeName(getDest()).space().regName(getDest(), _renderContext).separator().regName(getSrc(), _renderContext).separator().append(value).semicolon();
        }


    }

    class add_const<T extends PrimitiveType, C extends Number> extends binary_const<add_const<T, C>, T, C> {
        protected add_const(add_const<T,C> original){
            super(original);


        }
       @Override public add_const<T,C> cloneMe(add_const<T,C> original){
            return(new add_const<T,C>(original));
        }
        add_const(Instruction _from, HSAILRegister<T> _dest, HSAILRegister _src, C _value) {
            super(_from, "add_", _dest, _src, _value);

        }

    }

    class and_const<T extends PrimitiveType, C extends Number> extends binary_const<and_const<T,C>, T, C> {

        protected and_const(and_const< T,C> original){
            super(original);


        }
        @Override public and_const< T,C> cloneMe(and_const<T,C> original){
            return(new and_const(original));
        }
        and_const(Instruction _from, HSAILRegister<T> _dest, HSAILRegister _src, C _value) {
            super(_from, "and_", _dest, _src, _value);

        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append(op).append("b64").space().regName(getDest(), _renderContext).separator().regName(getSrc(), _renderContext).separator().append(value).semicolon();
        }


    }

    class mul_const<T extends PrimitiveType, C extends Number> extends binary_const< mul_const<T,C>, T, C> {
        protected mul_const(mul_const<T,C> original){
            super(original);
        }
        @Override public mul_const<T,C> cloneMe(mul_const<T,C> original){
            return(new mul_const(original));
        }
        mul_const(Instruction _from, HSAILRegister<T> _dest, HSAILRegister _src, C _value) {
            super(_from, "mul_", _dest, _src, _value);

        }

    }

    class mad< D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstructionWithDestSrcSrc<mad<D,T>, ref, ref> {

        protected mad(mad<D,T> original){
            super(original);


        }
        @Override public mad cloneMe(mad<D,T> original){
            return(new mad(original));
        }
        long size;

        mad(Instruction _from, Reg_ref _dest, Reg_ref _src_lhs, Reg_ref _src_rhs, long _size) {
            super(_from, _dest, _src_lhs, _src_rhs);
            size = _size;
        }


        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("mad_").typeName(getDest()).space().regName(getDest(), _renderContext).separator().regName(getSrcLhs(), _renderContext).separator().append(size).separator().regName(getSrcRhs(), _renderContext).semicolon();
        }
    }


    class cvt<T1 extends PrimitiveType, T2 extends PrimitiveType> extends HSAILInstruction<cvt<T1,T2>> {

        protected cvt(cvt<T1,T2> original){
            super(original);


        }
        @Override public cvt<T1,T2> cloneMe(cvt<T1,T2> original){
            return(new cvt(original));
        }
        cvt(Instruction _from, HSAILRegister<T1> _dest, HSAILRegister<T2> _src) {
            super(_from, 1, 1);
            dests[0] = _dest;
            sources[0] = _src;
        }

        HSAILRegister<T1> getDest() {
            return ((HSAILRegister<T1>) dests[0]);
        }

        HSAILRegister<T2> getSrc() {
            return ((HSAILRegister<T2>) sources[0]);
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("cvt_").typeName(getDest()).append("_").typeName(getSrc()).space().regName(getDest(), _renderContext).separator().regName(getSrc(), _renderContext).semicolon();
        }


    }


    class retvoid extends HSAILInstruction<retvoid> {
        protected retvoid(retvoid original){
            super(original);


        }
        @Override public retvoid cloneMe(retvoid original){
            return(new retvoid(original));
        }

        retvoid(Instruction _from) {
            super(_from, 0, 0);

        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("ret").semicolon();
        }


    }

    class ret<T extends PrimitiveType> extends HSAILInstructionWithSrc<ret<T>, T> {

        protected ret(ret<T> original){
            super(original);


        }
        @Override public ret<T> cloneMe(ret<T> original){
            return(new ret(original));
        }
        ret(Instruction _from, HSAILRegister<T> _src) {
            super(_from, _src);

        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("st_arg_").typeName(getSrc()).space().regName(getSrc(), _renderContext).separator().append("[%_result]").semicolon().nl();
            r.append("ret").semicolon();
        }


    }

    class array_store<T extends PrimitiveType> extends HSAILInstructionWithSrc<array_store<T>, T> {
        protected array_store(array_store<T> original){
            super(original);
            mem = original.mem;

        }
        @Override public array_store<T> cloneMe(array_store<T> original){
            return(new array_store(original));
        }
        Reg_ref mem;

        array_store(Instruction _from, Reg_ref _mem, HSAILRegister<T> _src) {
            super(_from, _src);

            mem = _mem;
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            // r.append("st_global_").typeName(getSrc()).space().append("[").regName(mem).append("+").array_len_offset().append("]").separator().regName(getSrc());
            r.append("st_global_").typeName(getSrc()).space().regName(getSrc(), _renderContext).separator().append("[").regName(mem, _renderContext).append("+").array_base_offset().append("]").semicolon();
        }


    }


    class array_load<T extends PrimitiveType> extends HSAILInstructionWithDest<array_load<T>,T> {

        protected array_load(array_load<T> original){
            super(original);
            mem = original.mem;

        }
        @Override public array_load<T> cloneMe(array_load<T> original){
            return(new array_load(original));
        }
        Reg_ref mem;


        array_load(Instruction _from, HSAILRegister<T> _dest, Reg_ref _mem) {
            super(_from, _dest);

            mem = _mem;
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("ld_global_").typeName(getDest()).space().regName(getDest(), _renderContext).separator().append("[").regName(mem, _renderContext).append("+").array_base_offset().append("]").semicolon();
            if (getDest().type.getHsaBits()==8){
                r.nl().pad(9).append("//cvt_s32_u8 $s").regNum(getDest(), _renderContext).separator().space().regName(getDest(), _renderContext).semicolon();
            }     else   if (getDest().type.getHsaBits()==16){
                r.nl().pad(9).append("//cvt_s32_u16 $s").regNum(getDest(), _renderContext).separator().space().regName(getDest(), _renderContext).semicolon();
            }
        }


    }

    class array_len extends HSAILInstructionWithDest<array_len, s32> {
        protected array_len(array_len original){
            super(original);
            mem = original.mem;

        }
        @Override public array_len cloneMe(array_len original){
            return(new array_len(original));
        }

        Reg_ref mem;


        array_len(Instruction _from, Reg_s32 _dest, Reg_ref _mem) {
            super(_from, _dest);

            mem = _mem;
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("ld_global_").typeName(getDest()).space().regName(getDest(), _renderContext).separator().append("[").regName(mem, _renderContext).append("+").array_len_offset().append("]").semicolon();
        }


    }

    class field_load<T extends PrimitiveType> extends HSAILInstructionWithDest<field_load<T>, T> {

        protected field_load(field_load<T> original){
            super(original);
            mem = original.mem;
            offset = original.offset;

        }
        @Override public field_load<T> cloneMe(field_load<T> original){
            return(new field_load<T>(original));
        }

        Reg_ref mem;
        long offset;


        field_load(Instruction _from, HSAILRegister<T> _dest, Reg_ref _mem, long _offset) {
            super(_from, _dest);
            offset = _offset;
            mem = _mem;
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("ld_global_").typeName(getDest()).space().regName(getDest(), _renderContext).separator().append("[").regName(mem, _renderContext).append("+").append(offset).append("]").semicolon();
        }


    }

    class static_field_load<T extends PrimitiveType> extends HSAILInstructionWithDest<static_field_load<T>, T> {
        protected static_field_load(static_field_load<T> original){
            super(original);
            mem = original.mem;
            offset = original.offset;

        }
        @Override public static_field_load<T> cloneMe(static_field_load<T> original){
            return(new static_field_load<T>(original));
        }
        long offset;
        Reg_ref mem;

        static_field_load(Instruction _from, HSAILRegister<T> _dest, Reg_ref _mem, long _offset) {
            super(_from, _dest);
            offset = _offset;
            mem = _mem;

        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("ld_global_").typeName(getDest()).space().regName(getDest(), _renderContext).separator().append("[").regName(mem, _renderContext).append("+").append(offset).append("]").semicolon();
        }


    }


    class field_store<T extends PrimitiveType> extends HSAILInstructionWithSrc<field_store<T>,T> {
        protected field_store(field_store<T> original){
            super(original);
            mem = original.mem;
            offset = original.offset;

        }
        @Override public field_store<T> cloneMe(field_store<T> original){
            return(new field_store<T>(original));
        }

        Reg_ref mem;
        long offset;


        field_store(Instruction _from, HSAILRegister<T> _src, Reg_ref _mem, long _offset) {
            super(_from, _src);
            offset = _offset;
            mem = _mem;
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("st_global_").typeName(getSrc()).space().regName(getSrc(), _renderContext).separator().append("[").regName(mem, _renderContext).append("+").append(offset).append("]").semicolon();
        }


    }


    final class mov<T extends PrimitiveType> extends HSAILInstructionWithDestSrc<mov<T>, T> {
        protected mov(mov<T> original){
            super(original);

        }
        @Override public mov<T> cloneMe(mov<T> original){
            return(new mov<T>(original));
        }
        public mov(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _src) {
            super(_from, _dest, _src);
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append("mov_").movTypeName(getDest()).space().regName(getDest(), _renderContext).separator().regName(getSrc(), _renderContext).semicolon();

        }


    }

    abstract class unary<H extends unary<H,T>, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<H, T> {
        protected unary(H original){
            super(original);
            op = original.op;
        }

        String op;

        public unary(Instruction _from, String _op, HSAILRegister<T> _destSrc) {
            super(_from, _destSrc, _destSrc);

            op = _op;
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append(op).typeName(getDest()).space().regName(getDest(), _renderContext).separator().regName(getDest(), _renderContext).semicolon();
        }

        HSAILRegister<T> getDest() {
            return ((HSAILRegister<T>) dests[0]);
        }

        HSAILRegister<T> getSrc() {
            return ((HSAILRegister<T>) sources[0]);
        }


    }

    abstract class binary<H extends binary<H,T>, T extends PrimitiveType> extends HSAILInstruction<H> {
        protected binary(binary<H,T> original){
            super(original);
            op = original.op;

        }
        String op;

        public binary(Instruction _from, String _op, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, 1, 2);
            dests[0] = _dest;
            sources[0] = _lhs;
            sources[1] = _rhs;
            op = _op;
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append(op).typeName(getDest()).space().regName(getDest(), _renderContext).separator().regName(getLhs(), _renderContext).separator().regName(getRhs(), _renderContext).semicolon();
        }

        HSAILRegister<T> getDest() {
            return ((HSAILRegister<T>) dests[0]);
        }

        HSAILRegister<T> getRhs() {
            return ((HSAILRegister<T>) sources[1]);
        }

        HSAILRegister<T> getLhs() {
            return ((HSAILRegister<T>) sources[0]);
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

    class add<T extends PrimitiveType> extends binary<add<T>, T> {
        protected add(add<T> original){
            super(original);
        }
        @Override public add<T> cloneMe(add<T> original){
            return (new add<T>(original));
        }
        public add(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "add_", _dest, _lhs, _rhs);
        }

    }

    class sub<T extends PrimitiveType> extends binary<sub<T>, T> {
        protected sub(sub<T> original){
            super(original);
        }
        @Override public sub<T> cloneMe(sub<T> original){
            return (new sub<T>(original));
        }
        public sub(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "sub_", _dest, _lhs, _rhs);
        }

    }

    class div<T extends PrimitiveType> extends binary<div<T>,T> {
        protected div(div<T> original){
            super(original);
        }
        @Override public div<T> cloneMe(div<T> original){
            return (new div<T>(original));
        }
        public div(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "div_", _dest, _lhs, _rhs);
        }

    }

    class mul<T extends PrimitiveType> extends binary<mul<T>, T> {
        protected mul(mul<T> original){
            super(original);
        }
        @Override public mul<T> cloneMe(mul<T> original){
            return (new mul<T>(original));
        }
        public mul(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "mul_", _dest, _lhs, _rhs);
        }

    }

    class rem<T extends PrimitiveType> extends binary<rem<T>, T> {
        protected rem(rem<T> original){
            super(original);
        }
        @Override public rem<T> cloneMe(rem<T> original){
            return (new rem<T>(original));
        }
        public rem(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "rem_", _dest, _lhs, _rhs);
        }

    }

    class neg<T extends PrimitiveType> extends unary<neg<T>,T> {

        protected neg(neg<T> original){
            super(original);
        }
        @Override public neg<T> cloneMe(neg<T> original){
            return (new neg<T>(original));
        }
        public neg(Instruction _from, HSAILRegister<T> _destSrc) {
            super(_from, "neg_", _destSrc);
        }

    }

    class shl<T extends PrimitiveType> extends binary<shl<T>, T> {
        protected shl(shl<T> original){
            super(original);
        }
        @Override public shl<T> cloneMe(shl<T> original){
            return (new shl<T>(original));
        }
        public shl(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "shl_", _dest, _lhs, _rhs);
        }

    }

    class shr<T extends PrimitiveType>  extends binary<shr<T>, T> {
        protected shr(shr<T> original){
            super(original);
        }
        @Override public shr<T> cloneMe(shr<T> original){
            return (new shr<T>(original));
        }
        public shr(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "shr_", _dest, _lhs, _rhs);
        }

    }

    class ushr<T extends PrimitiveType>  extends binary<ushr<T>, T> {
        protected ushr(ushr<T> original){
            super(original);
        }
        @Override public ushr<T> cloneMe(ushr<T> original){
            return (new ushr<T>(original));
        }
        public ushr(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "ushr_", _dest, _lhs, _rhs);
        }

    }


    class and<T extends PrimitiveType>  extends binary<and<T>, T> {
        protected and(and<T> original){
            super(original);
        }
        @Override public and<T> cloneMe(and<T> original){
            return (new and<T>(original));
        }
        public and(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "and_", _dest, _lhs, _rhs);
        }

        @Override
        void render(HSAILRenderer r,RenderContext _renderContext) {
            r.append(op).movTypeName(getDest()).space().regName(getDest(), _renderContext).separator().regName(getLhs(), _renderContext).separator().regName(getRhs(), _renderContext).semicolon();
        }

    }

    class or<T extends PrimitiveType>  extends binary<or<T>, T> {
        protected or(or<T> original){
            super(original);
        }
        @Override public or<T> cloneMe(or<T> original){
            return (new or<T>(original));
        }
        public or(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "or_", _dest, _lhs, _rhs);
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append(op).movTypeName(getDest()).space().regName(getDest(), _renderContext).separator().regName(getLhs(), _renderContext).separator().regName(getRhs(), _renderContext).semicolon();
        }

    }

    class xor<T extends PrimitiveType>  extends binary<xor<T>, T> {
        protected xor(xor<T> original){
            super(original);
        }
        @Override public xor<T> cloneMe(xor<T> original){
            return (new xor<T>(original));
        }
        public xor(Instruction _from, HSAILRegister<T> _dest, HSAILRegister<T> _lhs, HSAILRegister<T> _rhs) {
            super(_from, "xor_", _dest, _lhs, _rhs);
        }

        @Override
        void render(HSAILRenderer r, RenderContext _renderContext) {
            r.append(op).movTypeName(getDest()).space().regName(getDest(), _renderContext).separator().regName(getLhs(), _renderContext).separator().regName(getRhs(), _renderContext).semicolon();
        }

    }

    class mov_const<T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDest<mov_const<T,C>,T> {
        protected mov_const(mov_const<T,C> original){
            super(original);
            value = original.value;
        }
        @Override public mov_const<T,C> cloneMe(mov_const<T,C> original){
            return (new mov_const<T,C>(original));
        }
        C value;

        public mov_const(Instruction _from, HSAILRegister<T> _dest, C _value) {
            super(_from, _dest);
            value = _value;
        }

        @Override
        void render(HSAILRenderer r,RenderContext _renderContext) {
            r.append("mov_").movTypeName(getDest()).space().regName(getDest(), _renderContext).separator().append(value).semicolon();

        }


    }

    List<HSAILInstruction> instructions = new ArrayList<HSAILInstruction>();
    ClassModel.ClassModelMethod method;

    boolean optimizeMoves =  false || Config.enableOptimizeRegMoves;

    void add(HSAILInstruction _regInstruction) {
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

    public HSAILRenderer renderFunctionDeclaration(HSAILRenderer r, RenderContext _renderContext) {
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

    public HSAILRenderer renderFunctionDefinition(HSAILRenderer r, RenderContext _renderContext) {
        renderFunctionDeclaration(r, _renderContext);

            r.obrace().nl();
            Set<Instruction> s = new HashSet<Instruction>();

            for (HSAILInstruction i : instructions) {
                if (!(i instanceof ld_kernarg || i instanceof ld_arg) && !s.contains(i.from)) {
                    s.add(i.from);
                    if (i.from.isBranchTarget()) {
                        r.label("func_"+i.from.getThisPC()).colon().nl();
                    }
                    if (r.isShowingComments()) {
                        r.nl().pad(1).lineCommentStart().mark().append(_renderContext.getLocation(i.from.getThisPC())).relpad(2).space().i(i.from).nl();
                    }
                }
                r.pad(9);
                i.render(r,_renderContext);
                r.nl();
            }
            r.cbrace().semicolon();



        return (r);
    }

    public HSAILRenderer renderInlinedFunctionBody(HSAILRenderer r,  RenderContext _renderContext,  int base) {
        Set<Instruction> s = new HashSet<Instruction>();
        boolean endBranchNeeded = false;

        for (HSAILInstruction i : instructions) {
            if (!(i instanceof ld_arg)){
               if (!s.contains(i.from)) {
                   s.add(i.from);
                     if (i.from.isBranchTarget()) {
                         r.label(_renderContext.getLocation(i.from.getThisPC())).colon().nl();
                     }
                    if (r.isShowingComments()) {
                        r.nl().pad(1).lineCommentStart().append(_renderContext.getLocation(i.from.getThisPC())).mark().relpad(2).space().i(i.from).nl();
                    }
                }
                if (i instanceof retvoid){
                    r.pad(9).lineCommentStart().append("ret").semicolon();
                }else if (i instanceof ret){
                  r.pad(9).append("mov_").movTypeName(((ret)i).getSrc()).space().regPrefix(((ret)i).getSrc().type).append(base).separator().regName(((ret)i).getSrc(), _renderContext).semicolon();
                  if (i != instructions.get(instructions.size()-1)){
                  r.nl().pad(9).append("brn @L"+_renderContext.getUniqueNameSpace()+"_END").semicolon();
                  endBranchNeeded = true;
                  }
                  //r.nl().pad(9).lineCommentStart().append("st_arg_").typeName(((ret)i).getSrc()).space().regName(((ret)i).getSrc(), _renderContext).separator().append("[%_result]").semicolon().nl();
                  //r.pad(9).lineCommentStart().append("ret").semicolon();
              }   else{
                  r.pad(9);
                  i.render(r, _renderContext);
              }
                r.nl();


            }
        }
        if (endBranchNeeded){
        r.append("@L"+_renderContext.getUniqueNameSpace()+"_END").colon().nl();
        }
        return (r);
    }

    public HSAILRenderer renderEntryPoint(HSAILRenderer r) {
        //r.append("version 1:0:large;").nl();
        r.append("version 0:95: $full : $large").semicolon().nl();

        RenderContext rc = new RenderContext(null, this.method.getClassModel().getDotClassName()+"."+this.method.getName()+this.method.getDescriptor(), 0);
        for (CallType c : calls) {
            c.renderDeclaration(r, rc);
        }

        for (CallType c : calls) {
            c.renderDefinition(r, rc);
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

                    r.label(rc.getLocation(i.from.getThisPC())).colon().nl();
                }
                if (r.isShowingComments()) {
                    r.nl().pad(1).lineCommentStart().mark().append(rc.getLocation(i.from.getThisPC())).relpad(2).space().i(i.from).nl();
                }

            } else {
                count++;
            }
            r.pad(9);
            i.render(r, rc);
            r.nl();
        }
        r.cbrace().semicolon();
        r.nl().commentStart();
        for (Map.Entry<RenderContext, Integer> e:rc.locMap.entrySet()){
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

    public void addmov(Instruction _i, PrimitiveType _type, int _from, int _to) {
        if (_type.equals(PrimitiveType.ref) || _type.getHsaBits() == 32) {
            if (_type.equals(PrimitiveType.ref)) {
                add(new mov<ref>(_i, new StackReg_ref(_i, _to), new StackReg_ref(_i, _from)));
            } else if (_type.equals(PrimitiveType.s32)) {
                add(new mov<s32>(_i, new StackReg_s32(_i, _to), new StackReg_s32(_i, _from)));
            } else {
                throw new IllegalStateException(" unknown prefix 1 prefix for first of DUP2");
            }

        } else {
            throw new IllegalStateException(" unknown prefix 2 prefix for DUP2");
        }
    }

    public HSAILRegister addmov(Instruction _i, int _from, int _to) {
        HSAILRegister r = getRegOfLastWriteToIndex(_i.getPreStackBase() + _i.getMethod().getCodeEntry().getMaxLocals() + _from);
        if (r == null){
            System.out.println("damn!");
        }
        addmov(_i, r.type, _from, _to);
        return (r);
    }

    enum ParseState {NONE, COMPARE_F32, COMPARE_F64, COMPARE_S64}

    ;

    static Map<ClassModel.ClassModelMethod, HSAILMethod> cache = new HashMap<ClassModel.ClassModelMethod, HSAILMethod>();

    static synchronized HSAILMethod getHSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint) {
        HSAILMethod instance = cache.get(_method);
        if (instance == null) {
            instance = new HSAILMethod(_method, _entryPoint);
            cache.put(_method, instance);
        }
        return (instance);
    }

    private HSAILMethod(ClassModel.ClassModelMethod _method) {
        this(_method, null);
    }

    HSAILMethod entryPoint;

    private HSAILMethod(ClassModel.ClassModelMethod _method, HSAILMethod _entryPoint) {
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
        for (Instruction i : method.getInstructions()) {
            if (i.getThisPC() == 0) {

                int argOffset = 0;
                if (!method.isStatic()) {
                    if (entryPoint == null) {
                        add(new ld_kernarg(i, new VarReg_ref(0)));
                    } else {
                        add(new ld_arg(i, new VarReg_ref(0)));
                    }
                    argOffset++;
                }
                for (TypeHelper.JavaMethodArg arg : method.argsAndReturnType.getArgs()) {
                    if (arg.getJavaType().isArray()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(i, new VarReg_ref(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(i, new VarReg_ref(arg.getArgc() + argOffset)));
                        }
                    } else if (arg.getJavaType().isObject()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(i, new VarReg_ref(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(i, new VarReg_ref(arg.getArgc() + argOffset)));
                        }
                    } else if (arg.getJavaType().isInt()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(i, new VarReg_s32(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(i, new VarReg_s32(arg.getArgc() + argOffset)));
                        }
                    } else if (arg.getJavaType().isFloat()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(i, new VarReg_f32(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(i, new VarReg_f32(arg.getArgc() + argOffset)));
                        }
                    } else if (arg.getJavaType().isDouble()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(i, new VarReg_f64(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(i, new VarReg_f64(arg.getArgc() + argOffset)));
                        }
                    } else if (arg.getJavaType().isLong()) {
                        if (_entryPoint == null) {
                            add(new ld_kernarg(i, new VarReg_s64(arg.getArgc() + argOffset)));
                        } else {
                            add(new ld_arg(i, new VarReg_s64(arg.getArgc() + argOffset)));
                        }
                    }
                }
            }

            switch (i.getByteCode()) {

                case ACONST_NULL:
                    add(new nyi(i));
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
                    add(new mov_const<s32, Integer>(i, new StackReg_s32(i, 0), i.asIntegerConstant().getValue()));
                    break;
                case LCONST_0:
                case LCONST_1:
                    add(new mov_const<s64, Long>(i, new StackReg_s64(i, 0), i.asLongConstant().getValue()));
                    break;
                case FCONST_0:
                case FCONST_1:
                case FCONST_2:
                    add(new mov_const<f32, Float>(i, new StackReg_f32(i, 0), i.asFloatConstant().getValue()));
                    break;
                case DCONST_0:
                case DCONST_1:
                    add(new mov_const<f64, Double>(i, new StackReg_f64(i, 0), i.asDoubleConstant().getValue()));
                    break;
                // case BIPUSH: moved up
                // case SIPUSH: moved up

                case LDC:
                case LDC_W:
                case LDC2_W: {
                    InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant) i;

                    ClassModel.ConstantPool.ConstantEntry e = (ClassModel.ConstantPool.ConstantEntry) cpe.getConstantPoolEntry();
                    if (e instanceof ClassModel.ConstantPool.DoubleEntry) {
                        add(new mov_const<f64, Double>(i, new StackReg_f64(i, 0), ((ClassModel.ConstantPool.DoubleEntry) e).getValue()));
                    } else if (e instanceof ClassModel.ConstantPool.FloatEntry) {
                        add(new mov_const<f32, Float>(i, new StackReg_f32(i, 0), ((ClassModel.ConstantPool.FloatEntry) e).getValue()));
                    } else if (e instanceof ClassModel.ConstantPool.IntegerEntry) {
                        add(new mov_const<s32, Integer>(i, new StackReg_s32(i, 0), ((ClassModel.ConstantPool.IntegerEntry) e).getValue()));
                    } else if (e instanceof ClassModel.ConstantPool.LongEntry) {
                        add(new mov_const<s64, Long>(i, new StackReg_s64(i, 0), ((ClassModel.ConstantPool.LongEntry) e).getValue()));

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
                    add(new mov<s32>(i, new StackReg_s32(i, 0), new VarReg_s32(i)));

                    break;
                case LLOAD:
                case LLOAD_0:
                case LLOAD_1:
                case LLOAD_2:
                case LLOAD_3:
                    add(new mov<s64>(i, new StackReg_s64(i, 0), new VarReg_s64(i)));
                    break;
                case FLOAD:
                case FLOAD_0:
                case FLOAD_1:
                case FLOAD_2:
                case FLOAD_3:
                    add(new mov<f32>(i, new StackReg_f32(i, 0), new VarReg_f32(i)));
                    break;
                case DLOAD:
                case DLOAD_0:
                case DLOAD_1:
                case DLOAD_2:
                case DLOAD_3:
                    add(new mov<f64>(i, new StackReg_f64(i, 0), new VarReg_f64(i)));
                    break;
                case ALOAD:
                case ALOAD_0:
                case ALOAD_1:
                case ALOAD_2:
                case ALOAD_3:
                    add(new mov<ref>(i, new StackReg_ref(i, 0), new VarReg_ref(i)));
                    break;
                case IALOAD:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s32.getHsaBytes()));
                    add(new array_load<s32>(i, new StackReg_s32(i, 0), new StackReg_ref(i, 1)));
                    break;
                case LALOAD:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s64.getHsaBytes()));
                    add(new array_load<s64>(i, new StackReg_s64(i, 0), new StackReg_ref(i, 1)));
                    break;
                case FALOAD:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f32.getHsaBytes()));
                    add(new array_load<f32>(i, new StackReg_f32(i, 0), new StackReg_ref(i, 1)));

                    break;
                case DALOAD:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f64.getHsaBytes()));
                    add(new array_load<f64>(i, new StackReg_f64(i, 0), new StackReg_ref(i, 1)));

                    break;
                case AALOAD:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.ref.getHsaBytes()));
                    add(new array_load<ref>(i, new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
                    break;
                case BALOAD:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s8.getHsaBytes()));
                    add(new array_load<s8>(i, new StackReg_s8(i, 0), new StackReg_ref(i, 1)));
                    break;
                case CALOAD:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.u16.getHsaBytes()));
                    add(new array_load<u16>(i, new StackReg_u16(i, 0), new StackReg_ref(i, 1)));
                    break;
                case SALOAD:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));  // index converted to 64 bit
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s16.getHsaBytes()));
                    add(new array_load<s16>(i, new StackReg_s16(i, 0), new StackReg_ref(i, 1)));
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
                    add(new mov<s32>(i, new VarReg_s32(i), new StackReg_s32(i, 0)));

                    break;
                case LSTORE:
                case LSTORE_0:
                case LSTORE_1:
                case LSTORE_2:
                case LSTORE_3:
                    add(new mov<s64>(i, new VarReg_s64(i), new StackReg_s64(i, 0)));

                    break;
                case FSTORE:
                case FSTORE_0:
                case FSTORE_1:
                case FSTORE_2:
                case FSTORE_3:
                    add(new mov<f32>(i, new VarReg_f32(i), new StackReg_f32(i, 0)));
                    break;
                case DSTORE:
                case DSTORE_0:
                case DSTORE_1:
                case DSTORE_2:
                case DSTORE_3:
                    add(new mov<f64>(i, new VarReg_f64(i), new StackReg_f64(i, 0)));
                    break;
                case ASTORE:
                case ASTORE_0:
                case ASTORE_1:
                case ASTORE_2:
                case ASTORE_3:
                    add(new mov<ref>(i, new VarReg_ref(i), new StackReg_ref(i, 0)));

                    break;
                case IASTORE:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s32.getHsaBytes()));
                    add(new array_store<s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 2)));
                    break;
                case LASTORE:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s64.getHsaBytes()));
                    add(new array_store<u64>(i, new StackReg_ref(i, 1), new StackReg_u64(i, 2)));
                    break;
                case FASTORE:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f32.getHsaBytes()));
                    add(new array_store<f32>(i, new StackReg_ref(i, 1), new StackReg_f32(i, 2)));
                    break;
                case DASTORE:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.f64.getHsaBytes()));
                    add(new array_store<f64>(i, new StackReg_ref(i, 1), new StackReg_f64(i, 2)));
                    break;
                case AASTORE:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.ref.getHsaBytes()));
                    add(new array_store<ref>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 2)));

                    break;
                case BASTORE:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s8.getHsaBytes()));
                    add(new array_store<s8>(i, new StackReg_ref(i, 1), new StackReg_s8(i, 2)));

                    break;
                case CASTORE:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.u16.getHsaBytes()));
                    add(new array_store<u16>(i, new StackReg_ref(i, 1), new StackReg_u16(i, 2)));
                    break;
                case SASTORE:
                    add(new cvt<ref, s32>(i, new StackReg_ref(i, 1), new StackReg_s32(i, 1)));
                    add(new mad(i, new StackReg_ref(i, 1), new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) PrimitiveType.s16.getHsaBytes()));
                    add(new array_store<s16>(i, new StackReg_ref(i, 1), new StackReg_s16(i, 2)));
                    break;
                case POP:
                    add(new nyi(i));
                    break;
                case POP2:
                    add(new nyi(i));
                    break;
                case DUP:
                   // add(new nyi(i));
                    addmov(i, 0, 1);
                    break;
                case DUP_X1:
                    add(new nyi(i));
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
                    add(new nyi(i));
                    break;
                case DUP2_X2:
                    add(new nyi(i));
                    break;
                case SWAP:
                    add(new nyi(i));
                    break;
                case IADD:
                    add(new add<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LADD:
                    add(new add<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case FADD:
                    add(new add<f32>(i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
                    break;
                case DADD:
                    add(new add<f64>(i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
                    break;
                case ISUB:
                    add(new sub<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LSUB:
                    add(new sub<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case FSUB:
                    add(new sub<f32>(i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
                    break;
                case DSUB:
                    add(new sub<f64>(i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
                    break;
                case IMUL:
                    add(new mul<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LMUL:
                    add(new mul<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case FMUL:
                    add(new mul<f32>(i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
                    break;
                case DMUL:
                    add(new mul<f64>(i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
                    break;
                case IDIV:
                    add(new div<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LDIV:
                    add(new div<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case FDIV:
                    add(new div<f32>(i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
                    break;
                case DDIV:
                    add(new div<f64>(i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
                    break;
                case IREM:
                    add(new rem<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LREM:
                    add(new rem<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case FREM:
                    add(new rem<f32>(i, new StackReg_f32(i, 0), new StackReg_f32(i, 0), new StackReg_f32(i, 1)));
                    break;
                case DREM:
                    add(new rem<f64>(i, new StackReg_f64(i, 0), new StackReg_f64(i, 0), new StackReg_f64(i, 1)));
                    break;
                case INEG:
                    add(new neg<s32>(i, new StackReg_s32(i, 0)));
                    break;
                case LNEG:
                    add(new neg<s64>(i, new StackReg_s64(i, 0)));
                    break;
                case FNEG:
                    add(new neg<f32>(i, new StackReg_f32(i, 0)));
                    break;
                case DNEG:
                    add(new neg<f64>(i, new StackReg_f64(i, 0)));
                    break;
                case ISHL:
                    add(new shl<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LSHL:
                    add(new shl<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case ISHR:
                    add(new shr<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LSHR:
                    add(new shr<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case IUSHR:
                    add(new ushr<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LUSHR:
                    add(new ushr<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case IAND:
                    add(new and<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LAND:
                    add(new and<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case IOR:
                    add(new or<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LOR:
                    add(new or<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case IXOR:
                    add(new xor<s32>(i, new StackReg_s32(i, 0), new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    break;
                case LXOR:
                    add(new xor<s64>(i, new StackReg_s64(i, 0), new StackReg_s64(i, 0), new StackReg_s64(i, 1)));
                    break;
                case IINC:
                    add(new add_const<s32, Integer>(i, new VarReg_s32(i), new VarReg_s32(i), ((InstructionSet.I_IINC) i).getDelta()));
                    break;
                case I2L:
                    add(new cvt<s64, s32>(i, new StackReg_s64(i, 0), new StackReg_s32(i, 0)));
                    break;
                case I2F:
                    add(new cvt<f32, s32>(i, new StackReg_f32(i, 0), new StackReg_s32(i, 0)));
                    break;
                case I2D:
                    add(new cvt<f64, s32>(i, new StackReg_f64(i, 0), new StackReg_s32(i, 0)));
                    break;
                case L2I:
                    add(new cvt<s32, s64>(i, new StackReg_s32(i, 0), new StackReg_s64(i, 0)));
                    break;
                case L2F:
                    add(new cvt<f32, s64>(i, new StackReg_f32(i, 0), new StackReg_s64(i, 0)));
                    break;
                case L2D:
                    add(new cvt<f64, s64>(i, new StackReg_f64(i, 0), new StackReg_s64(i, 0)));
                    break;
                case F2I:
                    add(new cvt<s32, f32>(i, new StackReg_s32(i, 0), new StackReg_f32(i, 0)));
                    break;
                case F2L:
                    add(new cvt<s64, f32>(i, new StackReg_s64(i, 0), new StackReg_f32(i, 0)));
                    break;
                case F2D:
                    add(new cvt<f64, f32>(i, new StackReg_f64(i, 0), new StackReg_f32(i, 0)));
                    break;
                case D2I:
                    add(new cvt<s32, f64>(i, new StackReg_s32(i, 0), new StackReg_f64(i, 0)));
                    break;
                case D2L:
                    add(new cvt<s64, f64>(i, new StackReg_s64(i, 0), new StackReg_f64(i, 0)));
                    break;
                case D2F:
                    add(new cvt<f32, f64>(i, new StackReg_f32(i, 0), new StackReg_f64(i, 0)));
                    break;
                case I2B:
                    add(new cvt<s8, s32>(i, new StackReg_s8(i, 0), new StackReg_s32(i, 0)));
                    break;
                case I2C:
                    add(new cvt<u16, s32>(i, new StackReg_u16(i, 0), new StackReg_s32(i, 0)));
                    break;
                case I2S:
                    add(new cvt<s16, s32>(i, new StackReg_s16(i, 0), new StackReg_s32(i, 0)));
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
                        add(new cmp<f32>(lastInstruction, "eq", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<f64>(lastInstruction, "eq", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<s64>(lastInstruction, "eq", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(i, "eq", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(i, i.asBranch().getAbsolute()));
                    break;
                case IFNE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        add(new cmp<f32>(lastInstruction, "ne", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<f64>(lastInstruction, "ne", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<s64>(lastInstruction, "ne", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(i, "ne", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(i, i.asBranch().getAbsolute()));
                    break;
                case IFLT:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        add(new cmp<f32>(lastInstruction, "lt", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<f64>(lastInstruction, "lt", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<s64>(lastInstruction, "lt", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(i, "lt", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(i, i.asBranch().getAbsolute()));
                    break;
                case IFGE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        add(new cmp<f32>(lastInstruction, "ge", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<f64>(lastInstruction, "ge", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<s64>(lastInstruction, "ge", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(i, "ge", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(i, i.asBranch().getAbsolute()));
                    break;
                case IFGT:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        add(new cmp<f32>(lastInstruction, "gt", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<f64>(lastInstruction, "gt", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<s64>(lastInstruction, "gt", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(i, "gt", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(i, i.asBranch().getAbsolute()));
                    break;
                case IFLE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        add(new cmp<f32>(lastInstruction, "le", new StackReg_f32(lastInstruction, 0), new StackReg_f32(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        add(new cmp<f64>(lastInstruction, "le", new StackReg_f64(lastInstruction, 0), new StackReg_f64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        add(new cmp<s64>(lastInstruction, "le", new StackReg_s64(lastInstruction, 0), new StackReg_s64(lastInstruction, 1)));
                        parseState = ParseState.NONE;
                    } else {
                        add(new cmp_s32_const_0(i, "le", new StackReg_s32(i, 0)));

                    }
                    add(new cbr(i, i.asBranch().getAbsolute()));
                    break;
                case IF_ICMPEQ:

                    add(new cmp_s32(i, "eq", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(i, i.asBranch().getAbsolute()));

                    break;
                case IF_ICMPNE:

                    add(new cmp_s32(i, "ne", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(i, i.asBranch().getAbsolute()));

                    break;
                case IF_ICMPLT:

                    add(new cmp_s32(i, "lt", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(i, i.asBranch().getAbsolute()));

                    break;
                case IF_ICMPGE:

                    add(new cmp_s32(i, "ge", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(i, i.asBranch().getAbsolute()));

                    break;
                case IF_ICMPGT:

                    add(new cmp_s32(i, "gt", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(i, i.asBranch().getAbsolute()));

                    break;
                case IF_ICMPLE:

                    add(new cmp_s32(i, "le", new StackReg_s32(i, 0), new StackReg_s32(i, 1)));
                    add(new cbr(i, i.asBranch().getAbsolute()));

                    break;
                case IF_ACMPEQ:
                    add(new cmp_ref(i, "eq", new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
                    add(new cbr(i, i.asBranch().getAbsolute()));
                    break;
                case IF_ACMPNE:
                    add(new cmp_ref(i, "ne", new StackReg_ref(i, 0), new StackReg_ref(i, 1)));
                    add(new cbr(i, i.asBranch().getAbsolute()));
                    break;
                case GOTO:
                    add(new brn(i, i.asBranch().getAbsolute()));
                    break;
                case IFNULL:
                case IFNONNULL:
                case GOTO_W:
                    add(new branch(i, new StackReg_s32(i, 0), i.getByteCode().getName(), i.asBranch().getAbsolute()));
                    break;
                case JSR:
                    add(new nyi(i));
                    break;
                case RET:
                    add(new nyi(i));
                    break;
                case TABLESWITCH:
                    add(new nyi(i));
                    break;
                case LOOKUPSWITCH:
                    add(new nyi(i));
                    break;
                case IRETURN:
                    add(new ret<s32>(i, new StackReg_s32(i, 0)));
                    break;
                case LRETURN:
                    add(new ret<s64>(i, new StackReg_s64(i, 0)));
                    break;
                case FRETURN:
                    add(new ret<f32>(i, new StackReg_f32(i, 0)));
                    break;
                case DRETURN:
                    add(new ret<f64>(i, new StackReg_f64(i, 0)));
                    break;
                case ARETURN:
                    add(new ret<ref>(i, new StackReg_ref(i, 0)));
                    break;
                case RETURN:
                    add(new retvoid(i));
                    break;
                case GETSTATIC: {
                    TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

                    try {
                        Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                        Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());

                        if (type.isArray()) {
                            add(new static_field_load<ref>(i, new StackReg_ref(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));

                            //  add(new and_const<u64, Long>(i, new StackReg_u64(i, 0), new StackReg_ref(i, 0), (long) 0xffffffffL));
                        } else if (type.isInt()) {
                            add(new static_field_load<s32>(i, new StackReg_s32(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));

                        } else if (type.isFloat()) {
                            add(new static_field_load<f32>(i, new StackReg_f32(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
                        } else if (type.isDouble()) {
                            add(new static_field_load<f64>(i, new StackReg_f64(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
                        } else if (type.isLong()) {
                            add(new static_field_load<s64>(i, new StackReg_s64(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.staticFieldOffset(f)));
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
                            add(new field_load<ref>(i, new StackReg_ref(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(int.class)) {
                            add(new field_load<s32>(i, new StackReg_s32(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(short.class)) {
                            add(new field_load<s16>(i, new StackReg_s16(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(char.class)) {
                            add(new field_load<u16>(i, new StackReg_u16(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(boolean.class)) {
                            add(new field_load<s8>(i, new StackReg_s8(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(float.class)) {
                            add(new field_load<f32>(i, new StackReg_f32(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(double.class)) {
                            add(new field_load<f64>(i, new StackReg_f64(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(long.class)) {
                            add(new field_load<s64>(i, new StackReg_s64(i, 0), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));

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
                    add(new nyi(i));
                    break;
                case PUTFIELD: {
                   // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

                    try {
                        Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                        Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                        if (!f.getType().isPrimitive()) {
                            add(new field_store<ref>(i, new StackReg_ref(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(int.class)) {
                            add(new field_store<s32>(i, new StackReg_s32(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(short.class)) {
                            add(new field_store<s16>(i, new StackReg_s16(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(char.class)) {
                            add(new field_store<u16>(i, new StackReg_u16(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(boolean.class)) {
                            add(new field_store<s8>(i, new StackReg_s8(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(float.class)) {
                            add(new field_store<f32>(i, new StackReg_f32(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(double.class)) {
                            add(new field_store<f64>(i, new StackReg_f64(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));
                        } else if (f.getType().equals(long.class)) {
                            add(new field_store<s64>(i, new StackReg_s64(i, 1), new StackReg_ref(i, 0), (long) UnsafeWrapper.objectFieldOffset(f)));

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

                    add(new call(i));
                    break;
                case NEW:
                    add(new nyi(i));
                    break;
                case NEWARRAY:
                    add(new nyi(i));
                    break;
                case ANEWARRAY:
                    add(new nyi(i));
                    break;
                case ARRAYLENGTH:
                    add(new array_len(i, new StackReg_s32(i, 0), new StackReg_ref(i, 0)));

                    break;
                case ATHROW:
                    add(new nyi(i));
                    break;
                case CHECKCAST:
                    add(new nyi(i));
                    break;
                case INSTANCEOF:
                    add(new nyi(i));
                    break;
                case MONITORENTER:
                    add(new nyi(i));
                    break;
                case MONITOREXIT:
                    add(new nyi(i));
                    break;
                case WIDE:
                    add(new nyi(i));
                    break;
                case MULTIANEWARRAY:
                    add(new nyi(i));
                    break;
                case JSR_W:
                    add(new nyi(i));
                    break;

            }
            lastInstruction = i;


        }
    }
}
