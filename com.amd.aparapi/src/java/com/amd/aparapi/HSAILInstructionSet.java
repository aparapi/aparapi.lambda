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
                r.append("cmp_").append(type).append("u").append("_b1_").typeName(getSrcLhs()).space().append("$c1").separator().operandName(getSrcLhs()).separator().operandName(getSrcRhs()).semicolon();

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

        static   class and_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends binary_const<and_const<Rt, T,C>, Rt, T, C> {



            and_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest,Rt _src, C _value) {
                super(_hsailStackFrame,_from, "and_", _dest, _src, _value);

            }


            @Override
            void render(HSAILRenderer r) {
                r.append(op).append("b64").space().operandName(getDest()).separator().operandName(getSrc()).separator().append(value).semicolon();
            }


        }

        static  class mul_const<Rt extends HSAILRegister<Rt,T>,T extends PrimitiveType, C extends Number> extends binary_const< mul_const<Rt, T,C>, Rt, T, C> {


            mul_const(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _dest, Rt _src, C _value) {
                super(_hsailStackFrame,_from, "mul_", _dest, _src, _value);

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

   static final class returnMov<Rd extends HSAILRegister<Rd,D>,Rt extends HSAILRegister<Rt,T>,D extends PrimitiveType, T extends PrimitiveType> extends HSAILInstructionWithDestSrc<returnMov<Rd,Rt,D,T>, Rd, Rt,D,T> {
       String endLabel;


      public returnMov(HSAILStackFrame _hsailStackFrame,Instruction _from, Rd _dest, Rt _src, String _endLabel) {
         super(_hsailStackFrame,_from, _dest, _src);
         endLabel = _endLabel;
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



            public unary(HSAILStackFrame _hsailStackFrame,Instruction _from, String _op, Rt _destSrc) {
                super(_hsailStackFrame,_from, _destSrc, _destSrc);
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


            public neg(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
                super(_hsailStackFrame,_from, "neg_", _destSrc);
            }

        }

    static  class nsqrt<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<nsqrt<Rt,T>, Rt, T> {


        public nsqrt(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "nsqrt_", _destSrc);
        }

    }

    static  class ncos<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<ncos<Rt,T>, Rt, T> {


        public ncos(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "ncos_", _destSrc);
        }

    }

    static  class nsin<Rt extends HSAILRegister<Rt,T>, T extends PrimitiveType> extends unary<nsin<Rt,T>, Rt, T> {


        public nsin(HSAILStackFrame _hsailStackFrame,Instruction _from, Rt _destSrc) {
            super(_hsailStackFrame,_from, "nsin_", _destSrc);
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






   enum ParseState {NONE, COMPARE_F32, COMPARE_F64, COMPARE_S64}
   ;
    



   public static void addInstructions(List<HSAILInstruction> instructions, List<HSAILStackFrame> _frameSet, Stack<HSAILStackFrame> _frames, ClassModel.ClassModelMethod  method){
    //  HSAILStackFrame hsailStackFrame = _frames.peek();
      HSAILAssembler assembler = new HSAILAssembler(instructions, _frames.peek());
      ParseState parseState = ParseState.NONE;
      boolean inlining = true;
      boolean needsReturnLabel = false;
      for (Instruction i : method.getInstructions()) {

         switch (i.getByteCode()) {

            case ACONST_NULL:
                assembler.nyi(i);

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
                assembler.mov_s32_const(i, i.asIntegerConstant().getValue());

               break;
            case LCONST_0:
            case LCONST_1:
                assembler.mov_s64_const(i, i.asLongConstant().getValue());
               break;
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
                assembler.mov_f32_const(i, i.asFloatConstant().getValue());
               break;
            case DCONST_0:
            case DCONST_1:
                assembler.mov_f64_const(i, i.asDoubleConstant().getValue());

               break;
            // case BIPUSH: moved up
            // case SIPUSH: moved up

            case LDC:
            case LDC_W:
            case LDC2_W: {
               InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant) i;

               ClassModel.ConstantPool.ConstantEntry e = (ClassModel.ConstantPool.ConstantEntry) cpe.getConstantPoolEntry();
               if (e instanceof ClassModel.ConstantPool.DoubleEntry) {
                  assembler.mov_f64_const(i, ((ClassModel.ConstantPool.DoubleEntry) e).getValue());
               } else if (e instanceof ClassModel.ConstantPool.FloatEntry) {
                  assembler.mov_f32_const(i, ((ClassModel.ConstantPool.FloatEntry) e).getValue());
               } else if (e instanceof ClassModel.ConstantPool.IntegerEntry) {
                  assembler.mov_s32_const(i, ((ClassModel.ConstantPool.IntegerEntry) e).getValue());
               } else if (e instanceof ClassModel.ConstantPool.LongEntry) {
                  assembler.mov_s64_const(i, ((ClassModel.ConstantPool.LongEntry) e).getValue());
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
               assembler.mov_s32_var(i);

               break;
            case LLOAD:
            case LLOAD_0:
            case LLOAD_1:
            case LLOAD_2:
            case LLOAD_3:
                assembler.mov_s64_var(i);
               break;
            case FLOAD:
            case FLOAD_0:
            case FLOAD_1:
            case FLOAD_2:
            case FLOAD_3:

                assembler.mov_f32_var(i);
               break;
            case DLOAD:
            case DLOAD_0:
            case DLOAD_1:
            case DLOAD_2:
            case DLOAD_3:

               assembler.mov_f64_var(i);
               break;
            case ALOAD:
            case ALOAD_0:
            case ALOAD_1:
            case ALOAD_2:
            case ALOAD_3:
               assembler.mov_ref_var(i);

               break;
            case IALOAD:
               assembler.cvt_ref_s32_1(i).mad(i, PrimitiveType.s32.getHsaBytes()).array_load_s32(i);
               break;
            case LALOAD:
               assembler.cvt_ref_s32_1( i).mad(i, PrimitiveType.s64.getHsaBytes()).array_load_s64(i);
               break;
            case FALOAD:
                assembler.cvt_ref_s32_1( i).mad(i, PrimitiveType.f32.getHsaBytes()).array_load_f32(i);

               break;
            case DALOAD:
                assembler.cvt_ref_s32_1( i).mad(i, PrimitiveType.f64.getHsaBytes()).array_load_f64(i);

               break;
            case AALOAD:
                assembler.cvt_ref_s32_1( i).mad(i, PrimitiveType.ref.getHsaBytes()).array_load_ref(i);

               break;
            case BALOAD:
                assembler.cvt_ref_s32_1( i).mad(i, PrimitiveType.s8.getHsaBytes()).array_load_s8(i);

               break;
            case CALOAD:
                assembler.cvt_ref_s32_1( i).mad(i, PrimitiveType.u16.getHsaBytes()).array_load_u16(i);

               break;
            case SALOAD:
                assembler.cvt_ref_s32_1( i).mad(i, PrimitiveType.s16.getHsaBytes()).array_load_s16(i);
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
               assembler.mov_var_s32(i);

               break;
            case LSTORE:
            case LSTORE_0:
            case LSTORE_1:
            case LSTORE_2:
            case LSTORE_3:
                assembler.mov_var_s64(i);

               break;
            case FSTORE:
            case FSTORE_0:
            case FSTORE_1:
            case FSTORE_2:
            case FSTORE_3:
                assembler.mov_var_f32(i);
               break;
            case DSTORE:
            case DSTORE_0:
            case DSTORE_1:
            case DSTORE_2:
            case DSTORE_3:
                assembler.mov_var_f64(i);
               break;
            case ASTORE:
            case ASTORE_0:
            case ASTORE_1:
            case ASTORE_2:
            case ASTORE_3:
               assembler.mov_var_ref(i);
               break;
            case IASTORE:
               assembler.cvt_ref_s32_1( i).mad(i, PrimitiveType.s32.getHsaBytes()).array_store_s32(i);
               break;
            case LASTORE:
                assembler.cvt_ref_s32_1( i).mad( i, PrimitiveType.s64.getHsaBytes()).array_store_s64(i);
               break;
            case FASTORE:
                assembler.cvt_ref_s32_1( i).mad( i, PrimitiveType.f32.getHsaBytes()).array_store_f32(i);

               break;
            case DASTORE:
                assembler.cvt_ref_s32_1( i).mad( i, PrimitiveType.f64.getHsaBytes()).array_store_f64(i);
               break;
            case AASTORE:
                assembler.cvt_ref_s32_1( i).mad( i, PrimitiveType.ref.getHsaBytes()).array_store_ref(i);
               break;
            case BASTORE:
                assembler.cvt_ref_s32_1( i).mad( i, PrimitiveType.s8.getHsaBytes()).array_store_s8(i);
               break;
            case CASTORE:
                assembler.cvt_ref_s32_1( i).mad( i, PrimitiveType.u16.getHsaBytes()).array_store_u16(i);
               break;
            case SASTORE:
                assembler.cvt_ref_s32_1( i).mad( i, PrimitiveType.s16.getHsaBytes()).array_store_s16(i);
               break;
            case POP:
                assembler.nyi(i);
               break;
            case POP2:
                assembler.nyi(i);
               break;
            case DUP:
               assembler.addmov(i, 0, 1);
               break;
            case DUP_X1:
                assembler.nyi(i);
               break;
            case DUP_X2:

                assembler.addmov(i, 2, 3);
                assembler.addmov(i, 1, 2);
                assembler.addmov(i, 0, 1);
                assembler.addmov(i, 3, 0);

               break;
            case DUP2:
               // DUP2 is problematic. DUP2 either dups top two items or one depending on the 'prefix' of the stack items.
               // To complicate this further HSA large model wants object/mem references to be 64 bits (prefix 2 in Java) whereas
               // in java object/array refs are 32 bits (prefix 1).
                assembler.addmov(i, 0, 2);
                assembler.addmov(i, 1, 3);
               break;
            case DUP2_X1:
                assembler.nyi(i);
               break;
            case DUP2_X2:
                assembler.nyi(i);
               break;
            case SWAP:
                assembler.nyi(i);
               break;
            case IADD:
               assembler.add_s32(i);
               break;
            case LADD:
                assembler.add_s64(i);
               break;
            case FADD:
                assembler.add_f32(i);
               break;
            case DADD:
                assembler.add_f64(i);
               break;
            case ISUB:
                assembler.sub_s32(i);
               break;
            case LSUB:
                assembler.sub_s64(i);
               break;
            case FSUB:
                assembler.sub_f32(i);
               break;
            case DSUB:
                assembler.sub_f64(i);
               break;
            case IMUL:
                assembler.mul_s32(i);
               break;
            case LMUL:
                assembler.mul_s64(i);
               break;
            case FMUL:
                assembler.mul_f32(i);
               break;
            case DMUL:
                assembler.mul_f64(i);
               break;
            case IDIV:
                assembler.div_s32(i);
               break;
            case LDIV:
                assembler.div_s64(i);
               break;
            case FDIV:
                assembler.div_f32(i);
               break;
            case DDIV:
                assembler.div_f64(i);
               break;
            case IREM:
                assembler.rem_s32(i);
               break;
            case LREM:
                assembler.rem_s64(i);
               break;
            case FREM:
                assembler.rem_f32(i);
               break;
            case DREM:
                assembler.rem_f64(i);
               break;
            case INEG:
                assembler.neg_s32(i);
               break;
            case LNEG:
                assembler.neg_s64(i);
               break;
            case FNEG:
                assembler.neg_f32(i);
               break;
            case DNEG:
                assembler.neg_f64(i);
               break;
            case ISHL:
                assembler.shl_s32(i);
               break;
            case LSHL:
                assembler.shl_s64(i);
               break;
            case ISHR:
                assembler.shr_s32(i);
               break;
            case LSHR:
                assembler.shr_s64(i);
               break;
            case IUSHR:
                assembler.ushr_s32(i);
               break;
            case LUSHR:
                assembler.ushr_s64(i);
               break;
            case IAND:
                assembler.and_s32(i);
               break;
            case LAND:
                assembler.and_s64(i);
               break;
            case IOR:
                assembler.or_s32(i);
               break;
            case LOR:
                assembler.or_s64(i);
               break;
            case IXOR:
                assembler.xor_s32(i);
               break;
            case LXOR:
                assembler.xor_s64(i);
               break;
            case IINC:
                assembler.add_const_s32(i);
               break;
            case I2L:
                assembler.cvt_s64_s32(i);
               break;
            case I2F:
                assembler.cvt_f32_s32(i);
               break;
            case I2D:
                assembler.cvt_f64_s32(i);
               break;
            case L2I:
                assembler.cvt_s32_s64(i);
               break;
            case L2F:
                assembler.cvt_f32_s64(i);
               break;
            case L2D:
                assembler.cvt_f64_s64(i);
               break;
            case F2I:
                assembler.cvt_s32_f32(i);
               break;
            case F2L:
                assembler.cvt_s64_f32(i);
               break;
            case F2D:
                assembler.cvt_f64_f32(i);
               break;
            case D2I:
                assembler.cvt_s32_f64(i);
               break;
            case D2L:
                assembler.cvt_s64_f64(i);
               break;
            case D2F:
                assembler.cvt_f32_f64(i);
               break;
            case I2B:
                assembler.cvt_s8_s32(i);
               break;
            case I2C:
                assembler.cvt_u16_s32(i);
               break;
            case I2S:
                assembler.cvt_s16_s32(i);
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
                   assembler.cmp_f32_eq(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                   assembler.cmp_f64_eq(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                   assembler.cmp_s64_eq(i);
                  parseState = ParseState.NONE;
               } else {
                   assembler.cmp_s32_eq_const_0(i);
               }
               assembler.cbr(i);
               break;
            case IFNE:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                   assembler.cmp_f32_ne(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                   assembler.cmp_f64_ne(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                   assembler.cmp_s64_ne(i);
                  parseState = ParseState.NONE;
               } else {
                   assembler.cmp_s32_ne_const_0(i);
               }
               assembler.cbr(i);
               break;
            case IFLT:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                   assembler.cmp_f32_lt(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                   assembler.cmp_f64_lt(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                   assembler.cmp_s64_lt(i);
                  parseState = ParseState.NONE;
               } else {
                   assembler.cmp_s32_lt_const_0(i);

               }
               assembler.cbr(i);
               break;
            case IFGE:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                   assembler.cmp_f32_ge(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                   assembler.cmp_f64_ge(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                   assembler.cmp_s64_ge(i);
                  parseState = ParseState.NONE;
               } else {
                   assembler.cmp_s32_ge_const_0(i);

               }
               assembler.cbr(i);
               break;
            case IFGT:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                   assembler.cmp_f32_gt(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                   assembler.cmp_f64_gt(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                   assembler.cmp_s64_gt(i);
                  parseState = ParseState.NONE;
               } else {
                   assembler.cmp_s32_gt_const_0(i);

               }
               assembler.cbr(i);
               break;
            case IFLE:
               if (parseState.equals(ParseState.COMPARE_F32)) {
                   assembler.cmp_f32_le(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_F64)) {
                  assembler.cmp_f64_le(i);
                  parseState = ParseState.NONE;
               } else if (parseState.equals(ParseState.COMPARE_S64)) {
                   assembler.cmp_s64_le(i);
                  parseState = ParseState.NONE;
               } else {
                   assembler.cmp_s32_le_const_0(i);


               }
               assembler.cbr(i);
               break;
            case IF_ICMPEQ:

               assembler.cmp_s32_eq(i).cbr(i);

               break;
            case IF_ICMPNE:
                assembler.cmp_s32_ne(i).cbr(i);
               break;
            case IF_ICMPLT:
                assembler.cmp_s32_lt(i).cbr(i);
               break;
            case IF_ICMPGE:
                assembler.cmp_s32_ge(i).cbr(i);
               break;
            case IF_ICMPGT:
                assembler.cmp_s32_gt(i).cbr(i);
               break;
            case IF_ICMPLE:
                assembler.cmp_s32_le(i).cbr(i);
               break;
            case IF_ACMPEQ:
                assembler.cmp_ref_eq(i).cbr(i);
               break;
            case IF_ACMPNE:
                assembler.cmp_ref_ne(i).cbr(i);
               break;
            case GOTO:
                assembler.brn(i);
               break;
            case IFNULL:
                assembler.branch(i);
            case IFNONNULL:
                assembler.branch(i);
            case GOTO_W:
                assembler.branch(i);
               break;
            case JSR:
                assembler.nyi(i);
               break;
            case RET:
                assembler.nyi(i);
               break;
            case TABLESWITCH:
                assembler.nyi(i);
               break;
            case LOOKUPSWITCH:
                assembler.nyi(i);
               break;
            case IRETURN:
             case LRETURN:
             case FRETURN:
             case DRETURN:
             case ARETURN:
               if (inlining && _frames.size()>1){
                  int maxLocals=i.getMethod().getCodeEntry().getMaxLocals(); // hsailStackFrame.stackOffset -maxLocals is the slot for the return value

                  switch(i.getByteCode()){
                      case IRETURN: assembler.mov_s32(i, assembler.frame.stackIdx(i) - maxLocals, assembler.frame.stackIdx(i));break;
                      case LRETURN: assembler.mov_s64(i, assembler.frame.stackIdx(i) - maxLocals, assembler.frame.stackIdx(i));break;
                      case FRETURN: assembler.mov_f32(i, assembler.frame.stackIdx(i) - maxLocals, assembler.frame.stackIdx(i));break;
                      case DRETURN: assembler.mov_f64(i, assembler.frame.stackIdx(i) - maxLocals, assembler.frame.stackIdx(i));break;
                      case ARETURN: assembler.mov_ref(i, assembler.frame.stackIdx(i) - maxLocals, assembler.frame.stackIdx(i));break;
                   }
                   if (i.isLastInstruction()){
                      if (needsReturnLabel){
                         assembler.nopUniqueLabel(i);
                      }
                  }else{
                      assembler.returnBranchUniqueName(i);

                      needsReturnLabel=true;
                  }
               }else{
                   switch(i.getByteCode()){
                       case IRETURN:  assembler.ret_s32(i);break;
                       case LRETURN:  assembler.ret_s64(i);break;
                       case FRETURN:  assembler.ret_f32(i);break;
                       case DRETURN:  assembler.ret_s64(i);break;
                       case ARETURN:  assembler.ret_ref(i);break;

                   }

               }
               break;
            case RETURN:
                if (inlining && _frames.size()>1){
                    if (i.getNextPC()!=null){
                        assembler.returnBranchUniqueName(i);
                        needsReturnLabel=true;
                    }else{
                        if (i.isBranchTarget()){
                            assembler.nop(i);

                        }else if (needsReturnLabel){
                            assembler.nopUniqueLabel(i);
                        }
                    }
                }else{
                    assembler.ret_void(i);
                }
               break;
            case GETSTATIC: {
               TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try {
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());

                  if (!type.isPrimitive()) {
                      assembler.static_field_load_ref(i, f);
                  } else if (type.isInt()) {
                      assembler.static_field_load_s32(i, f);
                  } else if (type.isFloat()) {
                      assembler.static_field_load_f32(i, f);
                  } else if (type.isDouble()) {
                      assembler.static_field_load_f64(i, f);
                  } else if (type.isLong()) {
                      assembler.static_field_load_s64(i, f);
                  } else if (type.isChar()) {
                      assembler.static_field_load_u16(i, f);
                  } else if (type.isShort()) {
                      assembler.static_field_load_s16(i, f);
                  } else if (type.isChar()) {
                      assembler.static_field_load_s8(i, f);
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
                      assembler.field_load_ref(i, f);
                  } else if (f.getType().equals(int.class)) {
                      assembler.field_load_s32(i, f);
                  } else if (f.getType().equals(short.class)) {
                      assembler.field_load_s16(i, f);
                  } else if (f.getType().equals(char.class)) {
                      assembler.field_load_u16(i, f);
                  } else if (f.getType().equals(boolean.class)) {
                      assembler.field_load_s8(i, f);
                  } else if (f.getType().equals(float.class)) {
                      assembler.field_load_f32(i, f);
                  } else if (f.getType().equals(double.class)) {
                      assembler.field_load_f64(i, f);
                  } else if (f.getType().equals(long.class)) {
                      assembler.field_load_s64(i, f);
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
                assembler.nyi(i);
               break;
            case PUTFIELD: {
               // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

               try {
                  Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                  Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                  if (!f.getType().isPrimitive()) {
                      assembler.field_store_ref(i, f);
                  } else if (f.getType().equals(int.class)) {
                      assembler.field_store_s32(i, f);
                  } else if (f.getType().equals(short.class)) {
                      assembler.field_store_s16(i, f);
                  } else if (f.getType().equals(char.class)) {
                      assembler.field_store_u16(i, f);
                  } else if (f.getType().equals(boolean.class)) {
                      assembler.field_store_s8(i, f);
                  } else if (f.getType().equals(float.class)) {
                      assembler.field_store_f32(i, f);
                  } else if (f.getType().equals(double.class)) {
                      assembler.field_store_f64(i, f);
                   } else if (f.getType().equals(long.class)) {
                      assembler.field_store_s64(i, f);
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
            {
               CallInfo callInfo = new CallInfo(i);
               InlineIntrinsicCall call = HSAILIntrinsics.getInlineIntrinsic(callInfo);
               if (call != null){
                  call.add(instructions, assembler.frame, i);
               }else{
                  if (inlining){
                     try{

                        Class theClass = Class.forName(callInfo.dotClassName);
                        ClassModel classModel = ClassModel.getClassModel(theClass);
                        ClassModel.ClassModelMethod calledMethod = classModel.getMethod(callInfo.name, callInfo.sig);
                        _frames.push(new HSAILStackFrame(assembler.frame,  calledMethod, i.getThisPC(), i.getPreStackBase()+i.getMethod().getCodeEntry().getMaxLocals()+assembler.frame.stackOffset));
                        _frameSet.add(_frames.peek());
                        addInstructions(instructions, _frameSet, _frames, calledMethod);
                        _frames.pop();
                     }catch (ClassParseException cpe){

                     }catch (ClassNotFoundException cnf){

                     }

                  }  else {
                    // call(instructions, this, hsailStackFrame, i, callInfo);
                  }


               }
            }
            break;
            case NEW:
                assembler.nyi(i);
               break;
            case NEWARRAY:
                assembler.nyi(i);
                break;
            case ANEWARRAY:
                assembler.nyi(i);
                break;
            case ARRAYLENGTH:
                assembler.array_len(i);

               break;
            case ATHROW:
                assembler.nyi(i);
                break;
            case CHECKCAST:
                assembler.nyi(i);
                break;
            case INSTANCEOF:
                assembler.nyi(i);
                break;
            case MONITORENTER:
                assembler.nyi(i);
                break;
            case MONITOREXIT:
                assembler.nyi(i);
                break;
            case WIDE:
                assembler.nyi(i);
                break;
            case MULTIANEWARRAY:
                assembler.nyi(i);
                break;
            case JSR_W:
                assembler.nyi(i);
                break;

         }
      }

   }
}

