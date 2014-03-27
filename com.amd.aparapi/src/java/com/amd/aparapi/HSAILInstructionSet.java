package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;

/**
 * Created by user1 on 1/14/14.
 */
public class HSAILInstructionSet {

        abstract static class HSAILInstruction<H extends HSAILInstruction<H>>  {
            static  HSAILRegister[] NONE=new HSAILRegister[0];
            String location;
            Instruction from;
            HSAILRegister[] dests = NONE;
            HSAILOperand[] sources = NONE;


            HSAILInstruction(HSAILStackFrame _hsailStackFrame,Instruction _from, int _destCount, int _sourceCount) {
                from = _from;
                dests = _destCount>0?new HSAILRegister[_destCount]:NONE;
                sources = _sourceCount>0?new HSAILOperand[_sourceCount]:NONE;
                location = _hsailStackFrame.getUniqueLocation(from.getStartPC());
            }


            abstract void render(HSAILRenderer r);

        }

        static class nop extends HSAILInstruction<nop>{
            String endLabel;
            nop(HSAILStackFrame _hsailStackFrame,Instruction _from, String _endLabel) {
               super(_hsailStackFrame, _from, 0,0);
               endLabel = _endLabel;
            }
            @Override
            public void render(HSAILRenderer r) {
                if (endLabel!=null){
                    r.label(endLabel).colon().space();
                }
                r.append("// nop").semicolon();

            }
        }


    static class barrier_fgroup extends HSAILInstruction<barrier_fgroup>{

        barrier_fgroup(HSAILStackFrame _hsailStackFrame,Instruction _from) {
            super(_hsailStackFrame, _from, 0,0);
        }
        @Override
        public void render(HSAILRenderer r) {

            r.append("barrier_fgroup").semicolon();

        }
    }



    abstract static class HSAILInstructionWithDest<H extends HSAILInstructionWithDest<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {



            HSAILInstructionWithDest(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, 1, 0);
                dests[0] = _dest;
            }

            Rt getDest() {
                return ((Rt) dests[0]);
            }
        }

        abstract static class HSAILInstructionWithSrc<H extends HSAILInstructionWithSrc<H,Rt,T>, Rt extends HSAILOperand<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {



            HSAILInstructionWithSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src) {
                super(_hsailStackFrame,_from, 0, 1);
                sources[0] = _src;
            }

            Rt getSrc() {
                return ((Rt) sources[0]);
            }
        }

        abstract static class HSAILInstructionWithSrcSrc<H extends HSAILInstructionWithSrcSrc<H,Rt,T>, Rt extends HSAILOperand<Rt,T>, T extends PrimitiveType> extends HSAILInstruction<H> {


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

        abstract static class HSAILInstructionWithDestSrcSrc<H extends HSAILInstructionWithDestSrcSrc<H,Rd,Rlhs,Rrhs,D,Tlhs, Trhs>, Rd extends HSAILRegister<Rd,D>, Rlhs extends HSAILOperand<Rlhs,Tlhs>, Rrhs extends HSAILOperand<Rrhs,Trhs>,D extends PrimitiveType, Tlhs extends PrimitiveType, Trhs extends PrimitiveType> extends HSAILInstruction<H> {


            HSAILInstructionWithDestSrcSrc(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rlhs _src_lhs, Rrhs _src_rhs) {
                super(_hsailStackFrame,_from, 1, 2);
                dests[0] = _dest;
                sources[0] = _src_lhs;
                sources[1] = _src_rhs;
            }

            Rd getDest() {
                return ((Rd) dests[0]);
            }

            Rlhs getSrcLhs() {
                return ((Rlhs) sources[0]);
            }

            Rrhs getSrcRhs() {
                return ((Rrhs) sources[1]);
            }
        }





        abstract static class HSAILInstructionWithDestSrc<H extends HSAILInstructionWithDestSrc<H,Rd,Rt,D,T>, Rd extends HSAILRegister<Rd,D>, Rt extends HSAILOperand<Rt,T>, D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstruction<H> {

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
            String targetLabel;



            branch(HSAILStackFrame _hsailStackFrame,Instruction _from, R _src, String _branchName, int _pc) {
                super(_hsailStackFrame,_from, _src);
                branchName = _branchName;
                pc = _pc;
               targetLabel = _hsailStackFrame.getUniqueLocation(pc);
            }




            @Override
            public void render(HSAILRenderer r) {
                r.append(branchName).space().label(targetLabel).semicolon();
            }
        }

        static  class cmp_s32_const_0 <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrc<cmp_s32_const_0<R>,R, s32> {
            String type;



            cmp_s32_const_0(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _src) {
                super(_hsailStackFrame, _from, _src);
                type = _type;
            }



            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrc()).space().append("$c1").separator().operandName(getSrc()).separator().append("0").semicolon();

            }
        }

        static  class cmp_s32 <R extends HSAILRegister<R,s32>> extends HSAILInstructionWithSrcSrc<cmp_s32<R>,R, s32> {

            String type;



            cmp_s32(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _srcLhs, R _srcRhs) {
                super(_hsailStackFrame,_from, _srcLhs, _srcRhs);
                type = _type;
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();

            }
        }
        static  class cmp_ref <R extends HSAILRegister<R,ref>> extends HSAILInstructionWithSrcSrc<cmp_ref<R>,R, ref> {

            String type;



            cmp_ref(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, R _srcLhs, R _srcRhs) {
                super(_hsailStackFrame, _from, _srcLhs, _srcRhs);
                type = _type;
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("cmp_").append(type).append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();

            }
        }

        static   class cmp<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrcSrc<cmp<Rt,T>,Rt, T> {

            String type;


            cmp(HSAILStackFrame _hsailStackFrame,Instruction _from, String _type, Rt _srcLhs, Rt _srcRhs) {
                super(_hsailStackFrame,_from, _srcLhs, _srcRhs);
                type = _type;
            }


            @Override
            public void render(HSAILRenderer r) {

                r.append("cmp_").append(type);
                if (getSrcLhs().type == PrimitiveType.f32 || getSrcLhs().type == PrimitiveType.f64 ){
                  r.append("u");
                }
                r.append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();

            }
        }

        static  class cbr extends HSAILInstruction<cbr> {

            int pc;
            String targetLabel;



            cbr(HSAILStackFrame _hsailStackFrame,Instruction _from, int _pc) {
                super(_hsailStackFrame,_from, 0, 0);
                pc = _pc;
               targetLabel = _hsailStackFrame.getUniqueLocation(pc);
            }



            @Override
            public void render(HSAILRenderer r) {
                r.append("cbr").space().append("$c1").separator().label(targetLabel).semicolon();

            }
        }

        static  class brn extends HSAILInstruction<brn> {
            int pc;
            String targetLabel;



            brn(HSAILStackFrame _hsailStackFrame,Instruction _from, int _pc) {
                super(_hsailStackFrame, _from, 0, 0);
                pc = _pc;
               targetLabel = _hsailStackFrame.getUniqueLocation(pc);
            }


            @Override
            public void render(HSAILRenderer r) {
                r.append("brn").space().label(targetLabel).semicolon();

            }
        }

        static  class nyi extends HSAILInstruction<nyi> {


            nyi(HSAILStackFrame _hsailStackFrame,Instruction _from) {
                super(_hsailStackFrame, _from, 0, 0);
            }



            @Override
            void render(HSAILRenderer r) {

                r.append("NYI ").i(from);

            }
        }

        static  class ld_kernarg<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<ld_kernarg<Rt,T>,Rt, T> {



            ld_kernarg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, _dest);
            }


            @Override
            void render(HSAILRenderer r) {
                r.append("ld_kernarg_").typeName(getDest()).space().operandName(getDest()).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
            }
        }

    static  class workitemabsid<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<workitemabsid<Rt,T>,Rt, T> {


        workitemabsid(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("workitemabsid_").typeName(getDest()).space().operandName(getDest()).separator().append("0").semicolon();
        }
    }

    static  class gridsize<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<gridsize<Rt,T>,Rt, T> {


        gridsize(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("gridsize_").typeName(getDest()).space().operandName(getDest()).separator().append("0").semicolon();
        }
    }

    static  class countuplane<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<countuplane<Rt,T>,Rt, T> {


        countuplane(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("countuplane_").typeName(getDest()).space().operandName(getDest()).semicolon();
        }
    }

    static  class masklane<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<masklane<Rt,T>,Rt, T> {


        masklane(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("masklane_").typeName(getDest()).space().operandName(getDest()).semicolon();
        }
    }
    static  class laneid<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<laneid<Rt,T>,Rt, T> {


        laneid(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("laneid_").typeName(getDest()).space().operandName(getDest()).semicolon();
        }
    }
    static  class cuid<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<cuid<Rt,T>,Rt, T> {


        cuid(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("cuid_").typeName(getDest()).space().operandName(getDest()).semicolon();
        }
    }
    static  class clock<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<clock<Rt,T>,Rt, T> {


        clock(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("clock_").typeName(getDest()).space().operandName(getDest()).semicolon();
        }
    }
    static  class workgroupid<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<workgroupid<Rt,T>,Rt, T> {


        workgroupid(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("workgroupid_").typeName(getDest()).space().operandName(getDest()).separator().append("0").semicolon();
        }
    }

    static  class workgroupsize<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<workgroupsize<Rt,T>,Rt, T> {


        workgroupsize(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("workgroupsize_").typeName(getDest()).space().operandName(getDest()).separator().append("0").semicolon();
        }
    }
    static  class currentworkgroupsize<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<currentworkgroupsize<Rt,T>,Rt, T> {


        currentworkgroupsize(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("currentworkgroupsize_").typeName(getDest()).space().operandName(getDest()).separator().append("0").semicolon();
        }
    }
    static  class workitemid<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<workitemid<Rt,T>,Rt, T> {


        workitemid(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
            super(_hsailStackFrame, _from, _dest);
        }


        @Override
        void render(HSAILRenderer r) {
            r.append("workitemid_").typeName(getDest()).space().operandName(getDest()).separator().append("0").semicolon();
        }
    }

    static  class ld_arg<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<ld_arg<Rt,T>,Rt, T> {



            ld_arg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest) {
                super(_hsailStackFrame, _from, _dest);
            }


            @Override
            void render(HSAILRenderer r) {
                r.append("ld_arg_").typeName(getDest()).space().operandName(getDest()).separator().append("[%_arg").append(getDest().index).append("]").semicolon();
            }


        }

        static  abstract class binary_const<H extends binary_const<H, Rt, T, C>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDestSrc<H, Rt,Rt,T,T> {
            C value;
            String op;



            binary_const(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, _dest, _src);
                value = _value;
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().operandName(getDest()).separator().operandName(getSrc()).separator().append(value).semicolon();
            }


        }

        static  class add_const<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType, C extends Number> extends binary_const<add_const<Rt, T, C>, Rt,T, C> {


            add_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, "add_", _dest, _src, _value);

            }


        }


        static class mad<Rd extends HSAILRegister<Rd,ref>, Rt extends HSAILRegister<Rt,ref>> extends HSAILInstructionWithDestSrcSrc<mad<Rd,Rt>, Rd, Rt,Rt, ref, ref, ref> {
            long size;


            mad(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src_lhs, Rt _src_rhs, long _size) {
                super(_hsailStackFrame, _from, _dest, _src_lhs, _src_rhs);
                size = _size;
            }



            @Override void render(HSAILRenderer r) {
                r.append("mad_").typeName(getDest()).space().operandName(getDest()).separator().operandName(getSrcLhs()).separator().append(size).separator().operandName(getSrcRhs()).semicolon();
            }
        }

    static class cmov<Rd extends HSAILRegister<Rd,ref>, Rt extends HSAILRegister<Rt,ref>> extends HSAILInstructionWithDestSrcSrc<cmov<Rd,Rt>, Rd, Rt,Rt, ref, ref, ref> {



        cmov(HSAILStackFrame _hsailStackFrame, Instruction _from, Rd _dest, Rt _src_lhs, Rt _src_rhs) {
            super(_hsailStackFrame, _from, _dest, _src_lhs, _src_rhs);
        }



        @Override void render(HSAILRenderer r) {
            r.append("cmov_").movTypeName(getDest()).space().operandName(getDest()).separator().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();
        }
    }


        static   class cvt<Rt1 extends HSAILRegister<Rt1,T1>, Rt2 extends HSAILRegister<Rt2,T2>,T1 extends PrimitiveType, T2 extends PrimitiveType> extends HSAILInstruction<cvt<Rt1,Rt2,T1,T2>> {



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
                r.append("cvt_").typeName(getDest()).append("_").typeName(getSrc()).space().operandName(getDest()).separator().operandName(getSrc()).semicolon();
            }


        }


        static  class retvoid extends HSAILInstruction<retvoid> {


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

            ret(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src) {
                super(_hsailStackFrame,_from, _src);
                endLabel = _hsailStackFrame.getUniqueName()+"_END";

            }

            @Override
            void render(HSAILRenderer r) {
                r.append("st_arg_").typeName(getSrc()).space().operandName(getSrc()).separator().append("[%_result]").semicolon().nl();
                r.append("ret").semicolon();
            }


        }

        static  class array_store<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithSrc<array_store<Rt, T>,Rt, T> {
            StackReg_ref mem;



            array_store(HSAILStackFrame _hsailStackFrame,Instruction _from, StackReg_ref _mem, Rt _src) {
                super(_hsailStackFrame,_from, _src);
                mem = _mem;
            }



            @Override
            void render(HSAILRenderer r) {
                // r.append("st_global_").typeName(getSrc()).space().append("[").operandName(mem).append("+").array_len_offset().append("]").separator().operandName(getSrc());
                r.append("st_global_").typeName(getSrc()).space().operandName(getSrc()).separator().append("[").operandName(mem).append("+").array_base_offset().append("]").semicolon();
            }


        }


        static   class array_load<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<array_load<Rt,T>,Rt,T> {
            StackReg_ref mem;



            array_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, StackReg_ref _mem) {
                super(_hsailStackFrame,_from, _dest);
                mem = _mem;
            }


            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").array_base_offset().append("]").semicolon();

            }


        }

        static  class array_len<Rs32 extends HSAILRegister<Rs32,s32>> extends HSAILInstructionWithDest<array_len<Rs32>, Rs32, s32> {
            StackReg_ref mem;



            array_len(HSAILStackFrame _hsailStackFrame,Instruction _from, Rs32 _dest, StackReg_ref _mem) {
                super(_hsailStackFrame,_from, _dest);
                mem = _mem;
            }


            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").array_len_offset().append("]").semicolon();
            }


        }

        static  class field_load<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDest<field_load<Rt,T>, Rt,T> {

            StackReg_ref mem;
            long offset;


            field_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, StackReg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _dest);
                offset = _offset;
                mem = _mem;
            }



            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").append(offset).append("]").semicolon();
            }


        }
    static  class ld_global<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDest<field_load<Rt,T>, Rt,T> {

        StackReg_ref mem;
        long offset;


        ld_global(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, StackReg_ref _mem, long _offset) {
            super(_hsailStackFrame,_from, _dest);
            offset = _offset;
            mem = _mem;
        }



        @Override
        void render(HSAILRenderer r) {
            r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").append(offset).append("]").semicolon();
        }


    }


    static  class static_field_load<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithDest<static_field_load<Rt,T>,Rt, T> {
            long offset;
            StackReg_ref mem;


            static_field_load(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, StackReg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _dest);
                offset = _offset;
                mem = _mem;
            }



            @Override
            void render(HSAILRenderer r) {
                r.append("ld_global_").typeName(getDest()).space().operandName(getDest()).separator().append("[").operandName(mem).append("+").append(offset).append("]").semicolon();
            }


        }


        static  class field_store<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType> extends HSAILInstructionWithSrc<field_store<Rt,T>,Rt,T> {

            StackReg_ref mem;
            long offset;


            field_store(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _src, StackReg_ref _mem, long _offset) {
                super(_hsailStackFrame,_from, _src);
                offset = _offset;
                mem = _mem;
            }



            @Override
            void render(HSAILRenderer r) {
                r.append("st_global_").typeName(getSrc()).space().operandName(getSrc()).separator().append("[").operandName(mem).append("+").append(offset).append("]").semicolon();
            }


        }


        static final class mov<Rd extends HSAILRegister<Rd,D>,Rt extends HSAILRegister<Rt,T>,D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<mov<Rd,Rt,D,T>, Rd, Rt,D,T> {


            public mov(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src) {
                super(_hsailStackFrame,_from, _dest, _src);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("mov_").movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getSrc()).semicolon();

            }


        }



    static final class returnBranch extends  HSAILInstruction<returnBranch> {
        String endLabel;


        public returnBranch(HSAILStackFrame _hsailStackFrame,Instruction _from, String _endLabel) {
            super(_hsailStackFrame,_from,0,0);
            endLabel = _endLabel;
        }

        @Override
        void render(HSAILRenderer r) {
            r.append("brn").space().label(endLabel).semicolon();
        }


    }

        static  abstract class unary<H extends unary<H,Rt,T>, Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<H,Rt,Rt, T,T> {
            String op;



            public unary(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _dest, Rt _source) {
                super(_hsailStackFrame,_from, _dest, _source);
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().operandName(getDest()).separator().operandName(getDest()).semicolon();
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

            public binary(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, 1, 2);
                dests[0] = _dest;
                sources[0] = _lhs;
                sources[1] = _rhs;
                op = _op;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).typeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
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


        static   class add<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<add<Rt,T>, Rt, T> {


            public add(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "add_", _dest, _lhs, _rhs);
            }


        }

        static   class sub<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<sub<Rt,T>, Rt, T> {


            public sub(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "sub_", _dest, _lhs, _rhs);
            }

        }

        static  class div<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<div<Rt,T>, Rt, T> {


            public div(HSAILStackFrame _hsailStackFrame,Instruction _from,Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "div_", _dest, _lhs, _rhs);
            }

        }

        static  class mul<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<mul<Rt,T>, Rt, T> {


            public mul(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "mul_", _dest, _lhs, _rhs);
            }

        }

        static   class rem<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<rem<Rt,T>, Rt, T> {

            public rem(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "rem_", _dest, _lhs, _rhs);
            }

        }

        static  class neg<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<neg<Rt,T>, Rt, T> {


            public neg(HSAILStackFrame _hsailStackFrame,Instruction _from,  Rt _dest, Rt _source) {
                super(_hsailStackFrame,_from, "neg_", _dest, _source);
            }

        }

    static  class nsqrt<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<nsqrt<Rt,T>, Rt, T> {


        public nsqrt(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _source) {
            super(_hsailStackFrame,_from, "nsqrt_", _dest, _source);
        }

    }

    static  class ncos<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<ncos<Rt,T>, Rt, T> {


        public ncos(HSAILStackFrame _hsailStackFrame,Instruction _from,  Rt _dest, Rt _source) {
            super(_hsailStackFrame,_from, "ncos_", _dest, _source);
        }

    }

    static  class nsin<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<nsin<Rt,T>, Rt, T> {


        public nsin(HSAILStackFrame _hsailStackFrame,Instruction _from,  Rt _dest, Rt _source) {
            super(_hsailStackFrame,_from, "nsin_", _dest, _source);
        }

    }



        static  class shl<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<shl<Rt,T>, Rt, T> {

            public shl(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "shl_", _dest, _lhs, _rhs);
            }

        }

        static  class shr<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<shr<Rt,T>, Rt, T> {

            public shr(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "shr_", _dest, _lhs, _rhs);
            }

        }

        static  class ushr<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<ushr<Rt,T>, Rt, T> {

            public ushr(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "ushr_", _dest, _lhs, _rhs);
            }

        }


        static  class and<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<and<Rt,T>, Rt, T> {

            public and(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "and_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

        }

        static  class or<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<or<Rt,T>, Rt, T> {

            public or(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "or_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

        }

        static  class xor<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends binary<xor<Rt,T>, Rt, T> {

            public xor(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _lhs, Rt _rhs) {
                super(_hsailStackFrame,_from, "xor_", _dest, _lhs, _rhs);
            }

            @Override
            void render(HSAILRenderer r) {
                r.append(op).movTypeName(getDest()).space().operandName(getDest()).separator().operandName(getLhs()).separator().operandName(getRhs()).semicolon();
            }

        }

        static class mov_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends HSAILInstructionWithDest<mov_const<Rt,T,C>,Rt,T> {

            C value;

            public mov_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, C _value) {
                super(_hsailStackFrame,_from, _dest);
                value = _value;
            }

            @Override
            void render(HSAILRenderer r) {
                r.append("mov_").movTypeName(getDest()).space().operandName(getDest()).separator().append(value).semicolon();

            }
        }







}

