package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by user1 on 1/14/14.
 */
public class HSAILInstructionSet {
        abstract static class HSAILInstruction<H extends HSAILInstruction<H>>  {
            String location;
            Instruction from;
            HSAILRegister[] dests = null;
            HSAILRegister[] sources = null;
           // HSAILStackFrame hsailStackFrame = null;

            HSAILInstruction(HSAILInstruction original) {
                from = original.from;
               // hsailStackFrame = original.hsailStackFrame;
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

            HSAILInstruction(HSAILStackFrame _hsailStackFrame,Instruction _from, int _destCount, int _sourceCount) {
              //  hsailStackFrame = _hsailStackFrame;
                from = _from;
                dests = new HSAILRegister[_destCount];
                sources = new HSAILRegister[_sourceCount];
                location = _hsailStackFrame.getLocation(from.getStartPC());
            }

            public abstract  H cloneMe();


           // public HSAILStackFrame getHSAILStackFrame(){
             //   return(hsailStackFrame);
           // }
            abstract void render(HSAILRenderer r);

        }

        abstract static class HSAILInstructionWithDest<H extends HSAILInstructionWithDest<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {

            protected HSAILInstructionWithDest(  H original){
                super(original);

            }

            HSAILInstructionWithDest(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, 1, 0);
                dests[0] = _dest;
            }

            Rt getDest() {
                return ((Rt) dests[0]);
            }
        }

        abstract static class HSAILInstructionWithSrc<H extends HSAILInstructionWithSrc<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {

            protected HSAILInstructionWithSrc( H original){
                super(original);
            }


            HSAILInstructionWithSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src) {
                super(_hsailStackFrame,_from, 0, 1);
                sources[0] = _src;
            }

            Rt getSrc() {
                return ((Rt) sources[0]);
            }
        }

        abstract static class HSAILInstructionWithSrcSrc<H extends HSAILInstructionWithSrcSrc<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {

            protected HSAILInstructionWithSrcSrc(H original){
                super(original);
            }
            HSAILInstructionWithSrcSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src_lhs, Rt _src_rhs) {
                super(_hsailStackFrame,_from, 0, 2);
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

        abstract static class HSAILInstructionWithDestSrcSrc<H extends HSAILInstructionWithDestSrcSrc<H,Rd,Rt,D,T>, Rd extends HSAILRegister<Rd,T>, Rt extends HSAILRegister<Rt,T>, D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstruction<H> {

            protected HSAILInstructionWithDestSrcSrc(H original){
                super(original);
            }
            HSAILInstructionWithDestSrcSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src_lhs, Rt _src_rhs) {
                super(_hsailStackFrame,_from, 1, 2);
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



        abstract static class HSAILInstructionWithDestSrc<H extends HSAILInstructionWithDestSrc<H,Rd,Rt,D,T>, Rd extends HSAILRegister<Rd,D>, Rt extends HSAILRegister<Rt,T>, D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstruction<H> {
            HSAILInstructionWithDestSrc(H original){
                super(original);
            }
            HSAILInstructionWithDestSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src) {
                super(_hsailStackFrame,_from, 1, 1);
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

        static  class branch <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrc<branch<R>,R, s32> {
            String branchName;
            int pc;
            String label;

            protected branch(branch<R> original){
                super(original);
                branchName = original.branchName;
                pc = original.pc;
                label = original.label;
            }

            branch(HSAILStackFrame _hsailStackFrame,Instruction _from, R _src, String _branchName, int _pc) {
                super(_hsailStackFrame,_from, _src);
                branchName = _branchName;
                pc = _pc;
                label = _hsailStackFrame.getLocation(pc);
            }

            @Override public branch<R> cloneMe(){
                return(new branch<R>(this));
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append(branchName).space().label(label).semicolon();
            }
        }

        static  class cmp_s32_const_0 <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrc<cmp_s32_const_0<R>,R, s32> {
            String type;

            protected cmp_s32_const_0(cmp_s32_const_0<R> original){
                super(original);
                type = original.type;
            }

            cmp_s32_const_0(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _src) {
                super(_hsailStackFrame, _from, _src);
                type = _type;
            }

            @Override public cmp_s32_const_0<R> cloneMe(){
                return(new cmp_s32_const_0<R>(this));
            }

            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrc()).space().append("$c1").separator().regName(getSrc()).separator().append("0").semicolon();

            }
        }

        static  class cmp_s32 <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrcSrc<cmp_s32<R>,R, s32> {

            String type;

            protected cmp_s32(cmp_s32<R> original){
                super(original);
                type = original.type;
            }

            cmp_s32(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _srcLhs, R _srcRhs) {
                super(_hsailStackFrame,_from, _srcLhs, _srcRhs);
                type = _type;
            }

            @Override public cmp_s32<R> cloneMe(){
                return(new cmp_s32<R>(this));
            }

            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs()).separator().regName(getSrcRhs()).semicolon();

            }
        }
        static  class cmp_ref <R extends HSAILRegister<R,ref>> extends HSAILInstructionWithSrcSrc<cmp_ref<R>,R, ref> {

            String type;

            protected cmp_ref(cmp_ref<R> original){
                super(original);
                type = original.type;
            }

            cmp_ref(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _srcLhs, R _srcRhs) {
                super(_hsailStackFrame, _from, _srcLhs, _srcRhs);
                type = _type;
            }

            @Override public cmp_ref<R> cloneMe(){
                return(new cmp_ref<R>(this));
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs()).separator().regName(getSrcRhs()).semicolon();

            }
        }

        static   class cmp<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrcSrc<cmp<Rt,T>,Rt, T> {

            String type;

            protected cmp(cmp<Rt,T> original){
                super(original);
                type = original.type;
            }

            cmp(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, Rt _srcLhs, Rt _srcRhs) {
                super(_hsailStackFrame,_from, _srcLhs, _srcRhs);
                type = _type;
            }

            @Override public cmp<Rt,T> cloneMe(){
                return(new cmp<Rt,T>(this));
            }

            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("u").append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().regName(getSrcLhs()).separator().regName(getSrcRhs()).semicolon();

            }
        }

        static  class cbr extends HSAILInstruction<cbr> {

            int pc;
            String label;

            protected cbr(cbr original){
                super(original);
                pc = original.pc;
                label = original.label;
            }

            cbr(HSAILStackFrame _hsailStackFrame,Instruction _from, int _pc) {
                super(_hsailStackFrame,_from, 0, 0);
                pc = _pc;
                label = _hsailStackFrame.getLocation(pc);
            }

            @Override public cbr cloneMe(){
                return(new cbr(this));
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("cbr").space().append("$c1").separator().label(label).semicolon();

            }
        }

        static  class brn extends HSAILInstruction<brn> {
            int pc;
            String label;
            protected brn(brn original){
                super(original);
                pc = original.pc;
                label = original.label;
            }

            brn(HSAILStackFrame _hsailStackFrame,Instruction _from, int _pc) {
                super(_hsailStackFrame, _from, 0, 0);
                pc = _pc;
                label = _hsailStackFrame.getLocation(pc);
            }

            @Override public brn cloneMe(){
                return(new brn(this));
            }

            @Override
            public void render(HSAILRenderer r) {
                r.append("brn").space().label(label).semicolon();

            }
        }




        static  class call extends HSAILInstruction<call> {
            CallInfo callInfo;
            CallableCallType callableMethodCall;
            int base;
            HSAILMethod hsailMethod;

            protected call(call original){
                super(original);
                callInfo = original.callInfo;
                hsailMethod = original.hsailMethod;
            }

            call(HSAILMethod hsailMethod, HSAILStackFrame _hsailStackFrame,Instruction _from, CallInfo _callInfo) {
                super(_hsailStackFrame, _from, 0, 0);
                callInfo = _callInfo;

                base = from.getPreStackBase() + from.getMethod().getCodeEntry().getMaxLocals();

                    try {
                        Class theClass = Class.forName(callInfo.dotClassName);
                        ClassModel classModel = ClassModel.getClassModel(theClass);
                        ClassModel.ClassModelMethod method = classModel.getMethod(callInfo.name, callInfo.sig);

                        // HSAILStackFrame newStackFrame = new HSAILStackFrame(HSAILStackFrame, String.format("@%04d : %s",from.getThisPC(), mangledName), base);
                        // Pass HSAILStackFrame down here!!!!
                        hsailMethod = HSAILMethod.getHSAILMethod(method, hsailMethod.getEntryPoint(), _hsailStackFrame, base);

                        callableMethodCall = new InlineMethodCall( callInfo.intrinsicLookupName, hsailMethod);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (ClassParseException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

               // if (callableMethodCall instanceof SimpleMethodCall){
                   //hsailMethod.add((SimpleMethodCall)callableMethodCall);
               // }
            }

            @Override public call cloneMe(){
                return(new call(this));
            }

            @Override
            void render(HSAILRenderer r) {
              //  callableMethodCall.renderCallSite(r, hsailStackFrame, from,  callInfo.name, base);

            }


        }


        static  class nyi extends HSAILInstruction<nyi> {

            protected nyi(nyi original){
                super(original);
            }

            nyi(HSAILStackFrame _hsailStackFrame,Instruction _from) {
                super(_hsailStackFrame, _from, 0, 0);
            }

            @Override public nyi cloneMe(){
                return(new nyi(this));
            }

            @Override
            void render(HSAILRenderer r) {

                r.append("NYI ").i(from);

            }
        }

        static  class ld_kernarg<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<ld_kernarg<Rt,T>,Rt, T> {

            protected ld_kernarg(ld_kernarg<Rt,T> original){
                super(original);

            }

            ld_kernarg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, _dest);
            }

            @Override public ld_kernarg<Rt,T> cloneMe(){
                return(new ld_kernarg<Rt,T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_kernarg_").typeName(getDest()).space().regName(getDest()).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
            }
        }

    static  class workitemabsid<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<workitemabsid<Rt,T>,Rt, T> {

        protected workitemabsid(workitemabsid<Rt,T> original){
            super(original);

        }

        workitemabsid(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }

        @Override public workitemabsid<Rt,T> cloneMe(){
            return(new workitemabsid<Rt,T>(this));
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("workitemabsid_").typeName(getDest()).space().regName(getDest()).separator().append("0").semicolon();
        }
    }

        static  class ld_arg<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<ld_arg<Rt,T>,Rt, T> {

            protected ld_arg(ld_arg<Rt,T> original){
                super(original);

            }

            ld_arg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, _dest);
            }

            @Override public ld_arg cloneMe(){
                return(new ld_arg(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_arg_").typeName(getDest()).space().regName(getDest()).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
            }


        }

        static  abstract class binary_const<H extends binary_const<H, Rt, T, C>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDestSrc<H, Rt,Rt,T,T> {
            C value;
            String op;

            protected binary_const(H original){
                super(original);
                value = original.value;
                op = original.op;
            }

            binary_const(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, _dest, _src);
                value = _value;
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().regName(getDest()).separator().regName(getSrc()).separator().append(value).semicolon();
            }


        }

        static  class add_const<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType, C extends Number> extends binary_const<add_const<Rt, T, C>, Rt,T, C> {
            protected add_const(add_const<Rt,T,C> original){
                super(original);
            }

            add_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, "add_", _dest, _src, _value);

            }
            @Override public add_const<Rt,T,C> cloneMe(){
                return(new add_const<Rt,T,C>(this));
            }

        }

        static   class and_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends binary_const<and_const<Rt, T,C>, Rt, T, C> {

            protected and_const(and_const<Rt, T,C> original){
                super(original);
            }

            and_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest,Rt _src, C _value) {
                super(_hsailStackFrame,_from, "and_", _dest, _src, _value);

            }

            @Override public and_const<Rt, T,C> cloneMe(){
                return(new and_const<Rt, T,C>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).append("b64").space().regName(getDest()).separator().regName(getSrc()).separator().append(value).semicolon();
            }


        }

        static  class mul_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends binary_const< mul_const<Rt, T,C>, Rt, T, C> {
            protected mul_const(mul_const<Rt,T,C> original){
                super(original);
            }

            mul_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, "mul_", _dest, _src, _value);

            }

            @Override public mul_const<Rt,T,C> cloneMe(){
                return(new mul_const<Rt,T,C>(this));
            }

        }

        static class mad<Rd extends HSAILRegister<Rd,ref>, Rt extends HSAILRegister<Rt,ref>> extends HSAILInstructionWithDestSrcSrc<mad<Rd,Rt>, Rd, Rt, ref, ref> {
            long size;
            protected mad(mad<Rd,Rt> original){
                super(original);
                size = original.size;
            }

            mad(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src_lhs, Rt _src_rhs, long _size) {
                super(_hsailStackFrame, _from, _dest, _src_lhs, _src_rhs);
                size = _size;
            }

            @Override public mad<Rd,Rt> cloneMe(){
                return(new mad<Rd,Rt>(this));
            }

            @Override void render(HSAILRenderer r) {
                r.append("mad_").typeName(getDest()).space().regName(getDest()).separator().regName(getSrcLhs()).separator().append(size).separator().regName(getSrcRhs()).semicolon();
            }
        }


        static   class cvt<Rt1 extends HSAILRegister<Rt1,T1>, Rt2 extends HSAILRegister<Rt2,T2>,T1 extends PrimitiveType, T2 extends PrimitiveType> extends HSAILInstruction<cvt<Rt1,Rt2,T1,T2>> {

            protected cvt(cvt<Rt1,Rt2,T1,T2> original){
                super(original);


            }
            @Override public cvt<Rt1,Rt2,T1,T2> cloneMe(){
                return(new cvt(this));
            }
            cvt(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt1 _dest, Rt2 _src) {
                super(_hsailStackFrame,_from, 1, 1);
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
                r.append("cvt_").typeName(getDest()).append("_").typeName(getSrc()).space().regName(getDest()).separator().regName(getSrc()).semicolon();
            }


        }


        static  class retvoid extends HSAILInstruction<retvoid> {
            protected retvoid(retvoid original){
                super(original);


            }
            @Override public retvoid cloneMe(){
                return(new retvoid(this));
            }

            retvoid(HSAILStackFrame _hsailStackFrame,Instruction _from) {
                super(_hsailStackFrame,_from, 0, 0);

            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ret").semicolon();
            }


        }

        static  class ret<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrc<ret<Rt,T>,Rt, T> {

            String endLabel;
            protected ret(ret<Rt,T> original){
                super(original);


            }
            @Override public ret<Rt,T> cloneMe(){
                return(new ret<Rt,T>(this));
            }
            ret(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src) {
                super(_hsailStackFrame,_from, _src);
                endLabel = _hsailStackFrame.getUniqueNameSpace()+"_END";

            }

            @Override
            void render(HSAILRenderer r) {
                r.append("st_arg_").typeName(getSrc()).space().regName(getSrc()).separator().append("[%_result]").semicolon().nl();
                r.append("ret").semicolon();
            }


        }

        static  class array_store<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrc<array_store<Rt, T>,Rt, T> {
            Reg_ref mem;

            protected array_store(array_store<Rt, T> original){
                super(original);
                mem = original.mem;
            }

            array_store(HSAILStackFrame _hsailStackFrame,Instruction _from, Reg_ref _mem, Rt _src) {
                super(_hsailStackFrame,_from, _src);
                mem = _mem;
            }

            @Override public array_store<Rt, T> cloneMe(){
                return(new array_store<Rt, T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                // r.append("st_global_").typeName(getSrc()).space().append("[").regName(mem).append("+").array_len_offset().append("]").separator().regName(getSrc());
                r.append("st_global_").typeName(getSrc()).space().regName(getSrc()).separator().append("[").regName(mem).append("+").array_base_offset().append("]").semicolon();
            }


        }


        static   class array_load<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<array_load<Rt,T>,Rt,T> {
            Reg_ref mem;

            protected array_load(array_load<Rt,T> original){
                super(original);
                mem = original.mem;
            }

            array_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Reg_ref _mem) {
                super(_hsailStackFrame,_from, _dest);
                mem = _mem;
            }

            @Override public array_load<Rt,T> cloneMe(){
                return(new array_load<Rt,T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().regName(getDest()).separator().append("[").regName(mem).append("+").array_base_offset().append("]").semicolon();
                if (getDest().type.getHsaBits()==8){
                    r.nl().pad(9).append("//cvt_s32_u8 $s").regNum(getDest()).separator().space().regName(getDest()).semicolon();
                }     else   if (getDest().type.getHsaBits()==16){
                    r.nl().pad(9).append("//cvt_s32_u16 $s").regNum(getDest()).separator().space().regName(getDest()).semicolon();
                }
            }


        }

        static  class array_len<Rs32 extends HSAILRegister<Rs32,s32>> extends HSAILInstructionWithDest<array_len<Rs32>, Rs32, s32> {
            Reg_ref mem;

            protected array_len(array_len<Rs32> original){
                super(original);
                mem = original.mem;
            }

            array_len(HSAILStackFrame _hsailStackFrame,Instruction _from, Rs32 _dest, Reg_ref _mem) {
                super(_hsailStackFrame,_from, _dest);
                mem = _mem;
            }

            @Override public array_len<Rs32> cloneMe(){
                return(new array_len<Rs32>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().regName(getDest()).separator().append("[").regName(mem).append("+").array_len_offset().append("]").semicolon();
            }


        }

        static  class field_load<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDest<field_load<Rt,T>, Rt,T> {

            Reg_ref mem;
            long offset;
            protected field_load(field_load<Rt,T> original){
                super(original);
                mem = original.mem;
                offset = original.offset;
            }

            field_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Reg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _dest);
                offset = _offset;
                mem = _mem;
            }

            @Override public field_load<Rt,T> cloneMe(){
                return(new field_load<Rt,T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().regName(getDest()).separator().append("[").regName(mem).append("+").append(offset).append("]").semicolon();
            }


        }

        static  class static_field_load<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<static_field_load<Rt,T>,Rt, T> {
            long offset;
            Reg_ref mem;
            protected static_field_load(static_field_load<Rt,T> original){
                super(original);
                mem = original.mem;
                offset = original.offset;
            }

            static_field_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Reg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _dest);
                offset = _offset;
                mem = _mem;
            }

            @Override public static_field_load<Rt,T> cloneMe(){
                return(new static_field_load<Rt,T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().regName(getDest()).separator().append("[").regName(mem).append("+").append(offset).append("]").semicolon();
            }


        }


        static  class field_store<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithSrc<field_store<Rt,T>,Rt,T> {

            Reg_ref mem;
            long offset;

            protected field_store(field_store<Rt,T> original){
                super(original);
                mem = original.mem;
                offset = original.offset;
            }

            field_store(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src, Reg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _src);
                offset = _offset;
                mem = _mem;
            }

            @Override public field_store<Rt,T> cloneMe(){
                return(new field_store<Rt,T>(this));
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("st_global_").typeName(getSrc()).space().regName(getSrc()).separator().append("[").regName(mem).append("+").append(offset).append("]").semicolon();
            }


        }


        static final class mov<Rd extends HSAILRegister<Rd,D>,Rt extends HSAILRegister<Rt,T>,D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<mov<Rd,Rt,D,T>, Rd, Rt,D,T> {
            protected mov(mov<Rd,Rt,D,T> original){
                super(original);

            }

            public mov(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src) {
                super(_hsailStackFrame,_from, _dest, _src);
            }
            @Override public mov<Rd,Rt,D,T> cloneMe(){
                return(new mov<Rd,Rt,D,T>(this));
            }
            @Override
            void render(HSAILRenderer r) {
                r.append("mov_").movTypeName(getDest()).space().regName(getDest()).separator().regName(getSrc()).semicolon();

            }


        }

        static  abstract class unary<H extends unary<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<H,Rt,Rt, T,T> {
            String op;

            protected unary(H original){
                super(original);
                op = original.op;
            }

            public unary(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _destSrc) {
                super(_hsailStackFrame,_from, _destSrc, _destSrc);
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().regName(getDest()).separator().regName(getDest()).semicolon();
            }

            Rt getDest() {
                return ((Rt) dests[0]);
            }

            Rt getSrc() {
                return ((Rt) sources[0]);
            }


        }

        static  abstract class binary<H extends binary<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {
            String op;
            protected binary(H original){
                super(original);
                op = original.op;

            }
            public binary(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, 1, 2);
                dests[0] = _dest;
                sources[0] = _lhs;
                sources[1] = _rhs;
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().regName(getDest()).separator().regName(getLhs()).separator().regName(getRhs()).semicolon();
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

        static   class add<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<add<Rt,T>, Rt, T> {
            protected add(add<Rt,T> original){
                super(original);
            }

            public add(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "add_", _dest, _lhs, _rhs);
            }
            @Override public add<Rt,T> cloneMe(){
                return (new add<Rt,T>(this));
            }

        }

        static   class sub<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<sub<Rt,T>, Rt, T> {
            protected sub(sub<Rt,T> original){
                super(original);
            }

            public sub(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "sub_", _dest, _lhs, _rhs);
            }
            @Override public sub<Rt,T> cloneMe(){
                return (new sub<Rt,T>(this));
            }
        }

        static  class div<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<div<Rt,T>, Rt, T> {

            @Override public div<Rt,T> cloneMe(){
                return (new div<Rt,T>(this));
            }
            public div(HSAILStackFrame _hsailStackFrame,Instruction _from,Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "div_", _dest, _lhs, _rhs);
            }
            protected div(div<Rt,T> original){
                super(original);
            }
        }

        static  class mul<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<mul<Rt,T>, Rt, T> {
            protected mul(mul<Rt,T> original){
                super(original);
            }
            @Override public mul<Rt,T> cloneMe(){
                return (new mul<Rt,T>(this));
            }
            public mul(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "mul_", _dest, _lhs, _rhs);
            }

        }

        static   class rem<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<rem<Rt,T>, Rt, T> {
            protected rem(rem<Rt,T> original){
                super(original);
            }
            @Override public rem<Rt,T> cloneMe(){
                return (new rem<Rt,T>(this));
            }
            public rem(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "rem_", _dest, _lhs, _rhs);
            }

        }

        static  class neg<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<neg<Rt,T>, Rt, T> {

            protected neg(neg<Rt,T> original){
                super(original);
            }
            @Override public neg<Rt,T> cloneMe(){
                return (new neg<Rt,T>(this));
            }
            public neg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
                super(_hsailStackFrame,_from, "neg_", _destSrc);
            }

        }

    static  class nsqrt<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<nsqrt<Rt,T>, Rt, T> {

        protected nsqrt(nsqrt<Rt,T> original){
            super(original);
        }
        @Override public nsqrt<Rt,T> cloneMe(){
            return (new nsqrt<Rt,T>(this));
        }
        public nsqrt(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "nsqrt_", _destSrc);
        }

    }

    static  class ncos<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<ncos<Rt,T>, Rt, T> {

        protected ncos(ncos<Rt,T> original){
            super(original);
        }
        @Override public ncos<Rt,T> cloneMe(){
            return (new ncos<Rt,T>(this));
        }
        public ncos(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "ncos_", _destSrc);
        }

    }

    static  class nsin<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<nsin<Rt,T>, Rt, T> {

        protected nsin(nsin<Rt,T> original){
            super(original);
        }
        @Override public nsin<Rt,T> cloneMe(){
            return (new nsin<Rt,T>(this));
        }
        public nsin(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "nsin_", _destSrc);
        }

    }



        static  class shl<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<shl<Rt,T>, Rt, T> {
            protected shl(shl<Rt,T> original){
                super(original);
            }
            @Override public shl<Rt,T> cloneMe(){
                return (new shl<Rt,T>(this));
            }
            public shl(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "shl_", _dest, _lhs, _rhs);
            }

        }

        static  class shr<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<shr<Rt,T>, Rt, T> {
            protected shr(shr<Rt,T> original){
                super(original);
            }
            @Override public shr<Rt,T> cloneMe(){
                return (new shr<Rt,T>(this));
            }
            public shr(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "shr_", _dest, _lhs, _rhs);
            }

        }

        static  class ushr<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<ushr<Rt,T>, Rt, T> {
            protected ushr(ushr<Rt,T> original){
                super(original);
            }
            @Override public ushr<Rt,T> cloneMe(){
                return (new ushr<Rt,T>(this));
            }
            public ushr(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "ushr_", _dest, _lhs, _rhs);
            }

        }


        static  class and<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<and<Rt,T>, Rt, T> {
            protected and(and<Rt,T> original){
                super(original);
            }
            @Override public and<Rt,T> cloneMe(){
                return (new and<Rt,T>(this));
            }
            public and(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "and_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().regName(getDest()).separator().regName(getLhs()).separator().regName(getRhs()).semicolon();
            }

        }

        static  class or<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<or<Rt,T>, Rt, T> {
            protected or(or<Rt,T> original){
                super(original);
            }
            @Override public or<Rt,T> cloneMe(){
                return (new or<Rt,T>(this));
            }
            public or(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "or_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().regName(getDest()).separator().regName(getLhs()).separator().regName(getRhs()).semicolon();
            }

        }

        static  class xor<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<xor<Rt,T>, Rt, T> {
            protected xor(xor<Rt,T> original){
                super(original);
            }
            @Override public xor<Rt,T> cloneMe(){
                return (new xor<Rt,T>(this));
            }
            public xor(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "xor_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().regName(getDest()).separator().regName(getLhs()).separator().regName(getRhs()).semicolon();
            }

        }

        static class mov_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDest<mov_const<Rt,T,C>,Rt,T> {
            protected mov_const(mov_const<Rt,T,C> original){
                super(original);
                value = original.value;
            }
            @Override public mov_const<Rt,T,C> cloneMe(){
                return (new mov_const<Rt,T,C>(this));
            }
            C value;

            public mov_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, C _value) {
                super(_hsailStackFrame,_from, _dest);
                value = _value;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("mov_").movTypeName(getDest()).space().regName(getDest()).separator().append(value).semicolon();

            }
        }

    static public  List<HSAILInstruction>  array_len(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add (new array_len(_hsailStackFrame,_i, new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> nyi(List<HSAILInstruction> _instructions, HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new nyi(_hsailStackFrame, _i));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_store_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
       _instructions.add(new field_store<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.baseOffset,1), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_store_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i, _hsailStackFrame.baseOffset, 1), new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_store_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 1), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1), new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }

    static public List<HSAILInstruction> field_store_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_s16,s16>(_hsailStackFrame, _i, new StackReg_s16(_i, _hsailStackFrame.baseOffset,1), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_u16,u16>(_hsailStackFrame, _i, new StackReg_u16(_i,_hsailStackFrame.baseOffset, 1), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_s8,s8>(_hsailStackFrame, _i, new StackReg_s8(_i,_hsailStackFrame.baseOffset, 1), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_store_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_store<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1), new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> call(List<HSAILInstruction> _instructions,HSAILMethod _hsailMethod, HSAILStackFrame _hsailStackFrame, Instruction _i, CallInfo _callInfo){
       _instructions.add(new call(_hsailMethod, _hsailStackFrame,_i,  _callInfo));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
       _instructions.add(new field_load<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_s16,s16>(_hsailStackFrame, _i, new StackReg_s16(_i, _hsailStackFrame.baseOffset,0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_u16,u16>(_hsailStackFrame, _i, new StackReg_u16(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> field_load_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new field_load<StackReg_s8,s8>(_hsailStackFrame, _i, new StackReg_s8(_i, _hsailStackFrame.baseOffset,0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_s16,s16>(_hsailStackFrame, _i, new StackReg_s16(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_u16,u16>(_hsailStackFrame, _i, new StackReg_u16(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_s8,s8>(_hsailStackFrame, _i, new StackReg_s8(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> static_field_load_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, Field _f){
        _instructions.add(new static_field_load<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), (long) UnsafeWrapper.staticFieldOffset(_f)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ret_void(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new retvoid(_hsailStackFrame,_i));
        return(_instructions);
    }
    static public List<HSAILInstruction> ret_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ret<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ret<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ret<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ret<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.baseOffset,0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> ret_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ret<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> branch(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       _instructions.add(new branch(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), _i.getByteCode().getName(), _i.asBranch().getAbsolute()));
        return(_instructions);
    }
    static public List<HSAILInstruction> brn(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       _instructions.add(new brn(_hsailStackFrame, _i, _i.asBranch().getAbsolute()));
        return(_instructions);
    }
    static public List<HSAILInstruction> cbr(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       _instructions.add(new cbr(_hsailStackFrame, _i, _i.asBranch().getAbsolute()));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_ref_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       _instructions.add(new HSAILInstructionSet.cmp_ref(_hsailStackFrame,_i, "ne", new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_ref_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new HSAILInstructionSet.cmp_ref(_hsailStackFrame,_i, "eq", new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new HSAILInstructionSet.cmp_s32(_hsailStackFrame,_i, "ne", new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new HSAILInstructionSet.cmp_s32(_hsailStackFrame,_i, "eq", new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new HSAILInstructionSet.cmp_s32(_hsailStackFrame,_i, "lt", new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new HSAILInstructionSet.cmp_s32(_hsailStackFrame,_i, "gt", new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new HSAILInstructionSet.cmp_s32(_hsailStackFrame,_i, "ge", new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new HSAILInstructionSet.cmp_s32(_hsailStackFrame,_i, "le", new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_le_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "le", new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s32_gt_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "gt", new StackReg_s32(_i, _hsailStackFrame.baseOffset,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_ge_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "ge", new StackReg_s32(_i, _hsailStackFrame.baseOffset,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_lt_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "lt", new StackReg_s32(_i, _hsailStackFrame.baseOffset,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_eq_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "eq", new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s32_ne_const_0(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cmp_s32_const_0(_hsailStackFrame,_i, "ne", new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_s64_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
       Instruction lastInstruction = _i.getPrevPC();
       _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "le", new StackReg_s64(lastInstruction, _hsailStackFrame.baseOffset,0), new StackReg_s64(lastInstruction, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "ge", new StackReg_s64(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_s64(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "gt", new StackReg_s64(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_s64(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "lt", new StackReg_s64(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_s64(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "eq", new StackReg_s64(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_s64(lastInstruction, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_s64_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_s64, s64>(_hsailStackFrame,lastInstruction, "ne", new StackReg_s64(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_s64(lastInstruction, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_f64_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "le", new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "ge", new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "lt", new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "gt", new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "eq", new StackReg_f64(lastInstruction, _hsailStackFrame.baseOffset,0), new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f64_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f64, f64>(_hsailStackFrame,lastInstruction, "ne", new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_f64(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cmp_f32_le(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "le", new StackReg_f32(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_f32(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_ge(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "ge", new StackReg_f32(lastInstruction, _hsailStackFrame.baseOffset,0), new StackReg_f32(lastInstruction, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_lt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "lt", new StackReg_f32(lastInstruction, _hsailStackFrame.baseOffset,0), new StackReg_f32(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_gt(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "gt", new StackReg_f32(lastInstruction, _hsailStackFrame.baseOffset,0), new StackReg_f32(lastInstruction, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_eq(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "eq", new StackReg_f32(lastInstruction, _hsailStackFrame.baseOffset,0), new StackReg_f32(lastInstruction,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cmp_f32_ne(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        _instructions.add(new cmp<StackReg_f32, f32>(_hsailStackFrame,lastInstruction, "ne", new StackReg_f32(lastInstruction,_hsailStackFrame.baseOffset, 0), new StackReg_f32(lastInstruction, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s8_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s8,StackReg_s32,s8, s32>(_hsailStackFrame, _i, new StackReg_s8(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s16_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s16,StackReg_s32,s16, s32>(_hsailStackFrame, _i, new StackReg_s16(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_u16_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_u16,StackReg_s32,u16, s32>(_hsailStackFrame, _i, new StackReg_u16(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f32_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f32,StackReg_s32,f32, s32>(_hsailStackFrame, _i, new StackReg_f32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s64_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s64,StackReg_s32,s64, s32>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f64_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f64,StackReg_s32,f64, s32>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_ref_s32_1(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_ref_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_ref,StackReg_s32,ref, s32>(_hsailStackFrame, _i, new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s32_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s32,StackReg_s64,s32, s64>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f32_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f32,StackReg_s64,f32, s64>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f64_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f64,StackReg_s64,f64, s64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> cvt_s32_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s32,StackReg_f32,s32, f32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f32(_i, _hsailStackFrame.baseOffset,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f64_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f64,StackReg_f32,f64, f32>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s64_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s64,StackReg_f32,s64, f32>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s32_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s32,StackReg_f64,s32, f64>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_f32_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_f32,StackReg_f64,f32, f64>(_hsailStackFrame, _i, new StackReg_f32(_i, _hsailStackFrame.baseOffset,0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> cvt_s64_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new cvt<StackReg_s64,StackReg_f64,s64, f64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_const_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new add_const<VarReg_s32, s32, Integer>(_hsailStackFrame, _i, new VarReg_s32(_i, _hsailStackFrame.baseOffset), new VarReg_s32(_i, _hsailStackFrame.baseOffset), ((InstructionSet.I_IINC) _i).getDelta()));
        return(_instructions);
    }
    static public List<HSAILInstruction> xor_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new xor<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> xor_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new xor<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> or_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new or<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> or_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new or<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i,_hsailStackFrame.baseOffset,0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> and_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new and<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> and_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new and<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ushr_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ushr<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ushr_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new ushr<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shr_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new shr<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shr_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new shr<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shl_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new shl<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> shl_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new shl<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new neg<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new neg<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.baseOffset,0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new neg<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> neg_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new neg<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new rem<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new rem<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new rem<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f64(_i, _hsailStackFrame.baseOffset,0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> rem_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new rem<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i, _hsailStackFrame.baseOffset,0), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new div<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new div<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new div<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> div_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new div<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f32(_i, _hsailStackFrame.baseOffset,0), new StackReg_f32(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mul<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mul<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mul<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i, _hsailStackFrame.baseOffset,0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f64(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mul_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mul<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f32(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new sub<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new sub<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new sub<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> sub_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new sub<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i, _hsailStackFrame.baseOffset,0), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new add<StackReg_s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s64(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new add<StackReg_s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new add<StackReg_f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> add_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new add<StackReg_f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_s16,s16>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1), new StackReg_s16(_i,_hsailStackFrame.baseOffset, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_u16,u16>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1), new StackReg_u16(_i,_hsailStackFrame.baseOffset, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i, _hsailStackFrame.baseOffset,1), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_s8,s8>(_hsailStackFrame, _i, new StackReg_ref(_i, _hsailStackFrame.baseOffset,1), new StackReg_s8(_i,_hsailStackFrame.baseOffset, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_store_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_store<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_ref(_i, _hsailStackFrame.baseOffset,1), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 2)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mad(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _size){
       _instructions.add(new HSAILInstructionSet.mad(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1), new StackReg_ref(_i, _hsailStackFrame.baseOffset,1), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), (long) _size));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<VarReg_ref,StackReg_ref,ref,ref>(_hsailStackFrame, _i, new VarReg_ref(_i, _hsailStackFrame.baseOffset), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<VarReg_s32,StackReg_s32,s32,s32>(_hsailStackFrame, _i, new VarReg_s32(_i, _hsailStackFrame.baseOffset), new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<VarReg_f32,StackReg_f32,f32,f32>(_hsailStackFrame, _i, new VarReg_f32(_i, _hsailStackFrame.baseOffset), new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<VarReg_f64,StackReg_f64,f64,f64>(_hsailStackFrame, _i, new VarReg_f64(_i, _hsailStackFrame.baseOffset), new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_var_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<VarReg_s64,StackReg_s64,s64,s64>(_hsailStackFrame, _i, new VarReg_s64(_i, _hsailStackFrame.baseOffset), new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0)));
        return(_instructions);
    }

    static public List<HSAILInstruction> array_load_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_s32,s32>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_f32,f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_u16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_u16,u16>(_hsailStackFrame, _i, new StackReg_u16(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_s16(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_s16,s16>(_hsailStackFrame, _i, new StackReg_s16(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_s64,s64>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i, _hsailStackFrame.baseOffset,1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_f64,f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }

    static public List<HSAILInstruction> array_load_s8(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_s8,s8>(_hsailStackFrame, _i, new StackReg_s8(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> array_load_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new array_load<StackReg_ref,ref>(_hsailStackFrame, _i, new StackReg_ref(_i,_hsailStackFrame.baseOffset, 0), new StackReg_ref(_i,_hsailStackFrame.baseOffset, 1)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_f64_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<StackReg_f64,VarReg_f64, f64, f64>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), new VarReg_f64(_i, _hsailStackFrame.baseOffset)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_f32_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<StackReg_f32,VarReg_f32, f32, f32>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), new VarReg_f32(_i, _hsailStackFrame.baseOffset)));
        return(_instructions);
    }

    static public List<HSAILInstruction> mov_s64_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<StackReg_s64,VarReg_s64, s64, s64>(_hsailStackFrame, _i, new StackReg_s64(_i, _hsailStackFrame.baseOffset,0), new VarReg_s64(_i, _hsailStackFrame.baseOffset)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_s32_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<StackReg_s32,VarReg_s32, s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i, _hsailStackFrame.baseOffset,0), new VarReg_s32(_i, _hsailStackFrame.baseOffset)));
        return(_instructions);
    }


    static public List<HSAILInstruction> mov_ref_var(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i){
        _instructions.add(new mov<StackReg_ref,VarReg_ref, ref, ref>(_hsailStackFrame, _i, new StackReg_ref(_i, _hsailStackFrame.baseOffset,0), new VarReg_ref(_i, _hsailStackFrame.baseOffset)));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_s64_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, long _value){
        _instructions.add(new  mov_const<StackReg_s64,s64, Long>(_hsailStackFrame, _i, new StackReg_s64(_i,_hsailStackFrame.baseOffset, 0), _value));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_s32_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _value){
        _instructions.add(new  mov_const<StackReg_s32,s32, Integer>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, 0), _value));
        return(_instructions);
    }

    static public List<HSAILInstruction> mov_f64_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, double _value){
        _instructions.add(new  mov_const<StackReg_f64,f64, Double>(_hsailStackFrame, _i, new StackReg_f64(_i,_hsailStackFrame.baseOffset, 0), _value));
        return(_instructions);
    }
    static public List<HSAILInstruction> mov_f32_const(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, float _value){
        _instructions.add(new  mov_const<StackReg_f32,f32, Float>(_hsailStackFrame, _i, new StackReg_f32(_i,_hsailStackFrame.baseOffset, 0), _value));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_arg(_hsailStackFrame,_i, new VarReg_ref(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_ref(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_kernarg(_hsailStackFrame,_i, new VarReg_ref(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_arg(_hsailStackFrame,_i, new VarReg_s32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_s32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_kernarg(_hsailStackFrame,_i, new VarReg_s32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_arg(_hsailStackFrame,_i, new VarReg_f32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_f32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_kernarg(_hsailStackFrame,_i, new VarReg_f32(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_arg(_hsailStackFrame,_i, new VarReg_f64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_f64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_kernarg(_hsailStackFrame,_i, new VarReg_f64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_arg_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_arg(_hsailStackFrame,_i, new VarReg_s64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> ld_kernarg_s64(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new  ld_kernarg(_hsailStackFrame,_i, new VarReg_s64(_argNum)));
        return(_instructions);
    }
    static public List<HSAILInstruction> workitemabsid_u32(List<HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, int _argNum){
        _instructions.add(new workitemabsid<VarReg_s32, s32>(_hsailStackFrame, _i, new VarReg_s32(_argNum)));
        return(_instructions);
    }
    static public void addmov(List<HSAILInstructionSet.HSAILInstruction> _instructions,HSAILStackFrame _hsailStackFrame, Instruction _i, PrimitiveType _type, int _from, int _to) {
        if (_type.equals(PrimitiveType.ref) || _type.getHsaBits() == 32) {
            if (_type.equals(PrimitiveType.ref)) {
                _instructions.add(new HSAILInstructionSet.mov<StackReg_ref,StackReg_ref,ref,ref>(_hsailStackFrame,_i, new StackReg_ref( _i,_hsailStackFrame.baseOffset, _to), new StackReg_ref(_i, _hsailStackFrame.baseOffset,_from)));
            } else if (_type.equals(PrimitiveType.s32)) {
                _instructions.add(new HSAILInstructionSet.mov<StackReg_s32, StackReg_s32, s32, s32>(_hsailStackFrame, _i, new StackReg_s32(_i,_hsailStackFrame.baseOffset, _to), new StackReg_s32(_i,_hsailStackFrame.baseOffset, _from)));
            } else {
                throw new IllegalStateException(" unknown prefix 1 prefix for first of DUP2");
            }

        } else {
            throw new IllegalStateException(" unknown prefix 2 prefix for DUP2");
        }
    }
    static public HSAILRegister getRegOfLastWriteToIndex(List<HSAILInstructionSet.HSAILInstruction>_instructions,int _index) {

        int idx = _instructions.size();
        while (--idx >= 0) {
            HSAILInstructionSet.HSAILInstruction i = _instructions.get(idx);
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
    static public HSAILRegister addmov(List<HSAILInstructionSet.HSAILInstruction>_instructions, HSAILStackFrame _hsailStackFrame, Instruction _i, int _from, int _to) {
        HSAILRegister r = getRegOfLastWriteToIndex(_instructions, _i.getPreStackBase() + _i.getMethod().getCodeEntry().getMaxLocals() + _from);
        if (r == null){
            System.out.println("damn!");
        }
        addmov(_instructions, _hsailStackFrame, _i, r.type, _from, _to);
        return (r);
    }

    // for reference
    static public void add(List<HSAILInstructionSet.HSAILInstruction>_instructions, HSAILInstructionSet.HSAILInstruction _regInstruction) {
        // before we add lets see if this is a redundant mov
        if ( _regInstruction.sources != null && _regInstruction.sources.length > 0) {
            for (int regIndex = 0; regIndex < _regInstruction.sources.length; regIndex++) {
                HSAILRegister r = _regInstruction.sources[regIndex];
                if (r.isStack()) {
                    // look up the list of reg instructions for the parentHSAILStackFrame mov which assigns to r
                    int i = _instructions.size();
                    while ((--i) >= 0) {
                        if (_instructions.get(i) instanceof HSAILInstructionSet.mov) {
                            // we have found a move
                            HSAILInstructionSet.mov candidateForRemoval = (HSAILInstructionSet.mov) _instructions.get(i);
                            if (candidateForRemoval.from.getBlock() == _regInstruction.from.getBlock()
                                    && candidateForRemoval.getDest().isStack() && candidateForRemoval.getDest().equals(r)) {
                                // so i may be a candidate if between i and instruction.size() i.dest() is not mutated
                                boolean mutated = false;
                                for (int x = i + 1; !mutated && x < _instructions.size(); x++) {
                                    if (_instructions.get(x).dests.length > 0 && _instructions.get(x).dests[0].equals(candidateForRemoval.getSrc())) {
                                        mutated = true;
                                    }
                                }
                                if (!mutated) {
                                    _instructions.remove(i);
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

        _instructions.add(_regInstruction);
    }

}
