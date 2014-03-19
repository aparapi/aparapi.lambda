package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Stack;
import com.amd.aparapi.HSAILInstructionSet.HSAILInstruction;

/**
 * Created by user1 on 1/14/14.
 */
public class HSAILAssembler {

   
    List<HSAILInstruction> instructions;
    HSAILStackFrame frame;

    HSAILAssembler( List<HSAILInstruction> _instructions,  HSAILStackFrame _frame){
        instructions = _instructions;
        frame = _frame;
    }
    public HSAILAssembler field_store_s64(Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_store<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(this);
    }
    public HSAILAssembler field_store_f64(Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_store<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(this);
    }
    public HSAILAssembler field_store_f32(Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_store<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(this);
    }
    
    public HSAILAssembler field_store_s32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }

     public HSAILAssembler field_store_s16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_s16, s16>(frame, _i, new StackReg_s16(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_store_u16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_u16, u16>(frame, _i, new StackReg_u16(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_store_s8( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_s8, s8>(frame, _i, new StackReg_s8(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_store_ref( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_ref, ref>(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }

     public HSAILAssembler field_load_ref( Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_load<StackReg_ref, ref>(frame, _i, new StackReg_ref(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_f32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_f64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s16, s16>(frame, _i, new StackReg_s16(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_u16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_u16, u16>(frame, _i, new StackReg_u16(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s8( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s8, s8>(frame, _i, new StackReg_s8(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_f64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_f32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s16, s16>(frame, _i, new StackReg_s16(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_u16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_u16, u16>(frame, _i, new StackReg_u16(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s8( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s8, s8>(frame, _i, new StackReg_s8(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_ref( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_ref, ref>(frame, _i, new StackReg_ref(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler ret_void( Instruction _i){
        add( new HSAILInstructionSet.retvoid(frame, _i));
       return(this);
    }
     public HSAILAssembler ret_ref( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_ref, ref>(frame, _i, new StackReg_ref(frame.stackIdx(_i))));
       return(this);
    }

     public HSAILAssembler ret_s32( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }

     public HSAILAssembler ret_f32( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i))));
       return(this);
    }

     public HSAILAssembler ret_s64( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i))));
       return(this);
    }

     public HSAILAssembler ret_f64( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i))));
       return(this);
    }
     public HSAILAssembler branch( Instruction _i){
       add( new HSAILInstructionSet.branch(frame, _i, new StackReg_s32(frame.stackIdx(_i)), _i.getByteCode().getName(), _i.asBranch().getAbsolute()));
       return(this);
    }
     public HSAILAssembler brn( Instruction _i){
       add( new HSAILInstructionSet.brn(frame, _i, _i.asBranch().getAbsolute()));
       return(this);
    }
     public HSAILAssembler cbr( Instruction _i){
       add( new HSAILInstructionSet.cbr(frame, _i, _i.asBranch().getAbsolute()));
       return(this);
    }
     public HSAILAssembler cmp_ref_ne( Instruction _i){
       add( new HSAILInstructionSet.cmp_ref(frame, _i, "ne", new StackReg_ref(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler cmp_ref_eq( Instruction _i){
        add( new HSAILInstructionSet.cmp_ref(frame, _i, "eq", new StackReg_ref(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s32_ne( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frame, _i, "ne", new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_eq( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frame, _i, "eq", new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_lt( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frame, _i, "lt", new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_gt( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frame, _i, "gt", new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_ge( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frame, _i, "ge", new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_le( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frame, _i, "le", new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_le_const_0( Instruction _i){
       add( new HSAILInstructionSet.cmp_s32_const_0(frame, _i, "le", new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }

    public HSAILAssembler cmp_s32_gt_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(frame, _i, "gt", new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cmp_s32_ge_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(frame, _i, "ge", new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cmp_s32_lt_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(frame, _i, "lt", new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cmp_s32_eq_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(frame, _i, "eq", new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cmp_s32_ne_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(frame, _i, "ne", new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }

    public HSAILAssembler cmp_s64_le( Instruction _i){
       Instruction lastInstruction = _i.getPrevPC();
       add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frame, lastInstruction, "le", new StackReg_s64(frame.stackIdx(lastInstruction)), new StackReg_s64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_ge( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frame, lastInstruction, "ge", new StackReg_s64(frame.stackIdx(lastInstruction)), new StackReg_s64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_gt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frame, lastInstruction, "gt", new StackReg_s64(frame.stackIdx(lastInstruction)), new StackReg_s64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_lt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frame, lastInstruction, "lt", new StackReg_s64(frame.stackIdx(lastInstruction)), new StackReg_s64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_eq( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frame, lastInstruction, "eq", new StackReg_s64(frame.stackIdx(lastInstruction)), new StackReg_s64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_ne( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frame, lastInstruction, "ne", new StackReg_s64(frame.stackIdx(lastInstruction)), new StackReg_s64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }

    public HSAILAssembler cmp_f64_le( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frame, lastInstruction, "le", new StackReg_f64(frame.stackIdx(lastInstruction)), new StackReg_f64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_ge( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frame, lastInstruction, "ge", new StackReg_f64(frame.stackIdx(lastInstruction)), new StackReg_f64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_lt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frame, lastInstruction, "lt", new StackReg_f64(frame.stackIdx(lastInstruction)), new StackReg_f64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_gt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frame, lastInstruction, "gt", new StackReg_f64(frame.stackIdx(lastInstruction)), new StackReg_f64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_eq( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frame, lastInstruction, "eq", new StackReg_f64(frame.stackIdx(lastInstruction)), new StackReg_f64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_ne( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frame, lastInstruction, "ne", new StackReg_f64(frame.stackIdx(lastInstruction)), new StackReg_f64(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }

    public HSAILAssembler cmp_f32_le( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frame, lastInstruction, "le", new StackReg_f32(frame.stackIdx(lastInstruction)), new StackReg_f32(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_ge( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frame, lastInstruction, "ge", new StackReg_f32(frame.stackIdx(lastInstruction)), new StackReg_f32(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_lt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frame, lastInstruction, "lt", new StackReg_f32(frame.stackIdx(lastInstruction)), new StackReg_f32(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_gt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frame, lastInstruction, "gt", new StackReg_f32(frame.stackIdx(lastInstruction)), new StackReg_f32(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_eq( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frame, lastInstruction, "eq", new StackReg_f32(frame.stackIdx(lastInstruction)), new StackReg_f32(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_ne( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frame, lastInstruction, "ne", new StackReg_f32(frame.stackIdx(lastInstruction)), new StackReg_f32(frame.stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cvt_s8_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s8, StackReg_s32, s8, s32>(frame, _i, new StackReg_s8(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s16_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s16, StackReg_s32, s16, s32>(frame, _i, new StackReg_s16(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_u16_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_u16, StackReg_s32, u16, s32>(frame, _i, new StackReg_u16(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f32_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f32, StackReg_s32, f32, s32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s64_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s64, StackReg_s32, s64, s32>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f64_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f64, StackReg_s32, f64, s32>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_ref_s32_1( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_ref, StackReg_s32, ref, s32>(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler cvt_ref_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_ref, StackReg_s32, ref, s32>(frame, _i, new StackReg_ref(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s32_s64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s32, StackReg_s64, s32, s64>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f32_s64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f32, StackReg_s64, f32, s64>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f64_s64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f64, StackReg_s64, f64, s64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i))));
       return(this);
    }

    public HSAILAssembler cvt_s32_f32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s32, StackReg_f32, s32, f32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f64_f32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f64, StackReg_f32, f64, f32>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s64_f32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s64, StackReg_f32, s64, f32>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s32_f64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s32, StackReg_f64, s32, f64>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f32_f64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f32, StackReg_f64, f32, f64>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s64_f64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s64, StackReg_f64, s64, f64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler add_const_s32( Instruction _i){
        add( new HSAILInstructionSet.add_const<VarReg_s32, s32, Integer>(frame, _i, new VarReg_s32(_i, frame.stackOffset), new VarReg_s32(_i, frame.stackOffset), ((InstructionSet.I_IINC) _i).getDelta()));
       return(this);
    }
    public HSAILAssembler xor_s64( Instruction _i){
        add( new HSAILInstructionSet.xor<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler xor_s32( Instruction _i){
        add( new HSAILInstructionSet.xor<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler or_s64( Instruction _i){
        add( new HSAILInstructionSet.or<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler or_s32( Instruction _i){
        add( new HSAILInstructionSet.or<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler and_s64( Instruction _i){
        add( new HSAILInstructionSet.and<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler and_s32( Instruction _i){
        add( new HSAILInstructionSet.and<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler ushr_s64( Instruction _i){
        add( new HSAILInstructionSet.ushr<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler ushr_s32( Instruction _i){
        add( new HSAILInstructionSet.ushr<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler shr_s64( Instruction _i){
        add( new HSAILInstructionSet.shr<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler shr_s32( Instruction _i){
        add( new HSAILInstructionSet.shr<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler shl_s64( Instruction _i){
        add( new HSAILInstructionSet.shl<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler shl_s32( Instruction _i){
        add( new HSAILInstructionSet.shl<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler neg_f64( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler neg_s64( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler neg_f32( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler neg_s32( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler rem_s64( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler rem_s32( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler rem_f64( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler rem_f32( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler div_s64( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler div_s32( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler div_f64( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler div_f32( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler mul_s64( Instruction _i){
        add( new HSAILInstructionSet.mul<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler mul_s32( Instruction _i){
        add( new HSAILInstructionSet.mul<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler mul_f64( Instruction _i){
        add( new HSAILInstructionSet.mul<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler mul_f32( Instruction _i){
        add( new HSAILInstructionSet.mul<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler sub_s64( Instruction _i){
        add( new HSAILInstructionSet.sub<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler sub_s32( Instruction _i){
        add( new HSAILInstructionSet.sub<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler sub_f64( Instruction _i){
        add( new HSAILInstructionSet.sub<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler sub_f32( Instruction _i){
        add( new HSAILInstructionSet.sub<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler add_s64( Instruction _i){
        add( new HSAILInstructionSet.add<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)), new StackReg_s64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler add_s32( Instruction _i){
        add( new HSAILInstructionSet.add<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)), new StackReg_s32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler add_s32( Instruction _i, int _dest, int _lhs, int _rhs){
        add( new HSAILInstructionSet.add<StackReg_s32, s32>(frame, _i, new StackReg_s32(_dest), new StackReg_s32(_lhs), new StackReg_s32(_rhs)));
       return(this);
    }
    public HSAILAssembler add_f64( Instruction _i){
        add( new HSAILInstructionSet.add<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i)), new StackReg_f64(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler add_f32( Instruction _i){
        add( new HSAILInstructionSet.add<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i)), new StackReg_f32(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_store_s16( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s16, s16>(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_s16(frame.stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_u16( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_u16, u16>(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_u16(frame.stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_s32( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s32, s32>(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_s32(frame.stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_f32( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_f32, f32>(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_f32(frame.stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_f64( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_f64, f64>(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_f64(frame.stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_ref( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_ref, ref>(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_s8( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s8, s8>(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_s8(frame.stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_s64( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s64, s64>(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_s64(frame.stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler mad( Instruction _i, int _size){
       add( new HSAILInstructionSet.mad(frame, _i, new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)+1), new StackReg_ref(frame.stackIdx(_i)), (long) _size));
       return(this);
    }
    public HSAILAssembler mov_var_ref( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_ref, StackReg_ref, ref, ref>(frame, _i, new VarReg_ref(_i, frame.stackOffset), new StackReg_ref(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler mov_var_s32( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_s32, StackReg_s32, s32, s32>(frame, _i, new VarReg_s32(_i, frame.stackOffset), new StackReg_s32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler mov_var_f32( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_f32, StackReg_f32, f32, f32>(frame, _i, new VarReg_f32(_i, frame.stackOffset), new StackReg_f32(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler mov_var_f64( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_f64, StackReg_f64, f64, f64>(frame, _i, new VarReg_f64(_i, frame.stackOffset), new StackReg_f64(frame.stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler mov_var_s64( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_s64, StackReg_s64, s64, s64>(frame, _i, new VarReg_s64(_i, frame.stackOffset), new StackReg_s64(frame.stackIdx(_i))));
       return(this);
    }

    public HSAILAssembler array_load_s32( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_f32( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_u16( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_u16, u16>(frame, _i, new StackReg_u16(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_s16( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s16, s16>(frame, _i, new StackReg_s16(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_s64( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_f64( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler array_load_s8( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s8, s8>(frame, _i, new StackReg_s8(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_ref( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_ref, ref>(frame, _i, new StackReg_ref(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler mov_f64_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_f64, VarReg_f64, f64, f64>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), new VarReg_f64(_i, frame.stackOffset)));
       return(this);
    }
    public HSAILAssembler mov_f32_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_f32, VarReg_f32, f32, f32>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), new VarReg_f32(_i, frame.stackOffset)));
       return(this);
    }

    public HSAILAssembler mov_s64_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_s64, VarReg_s64, s64, s64>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), new VarReg_s64(_i, frame.stackOffset)));
       return(this);
    }
    public HSAILAssembler mov_s32_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_s32, VarReg_s32, s32, s32>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), new VarReg_s32(_i, frame.stackOffset)));
       return(this);
    }


    public HSAILAssembler mov_ref_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_ref, VarReg_ref, ref, ref>(frame, _i, new StackReg_ref(frame.stackIdx(_i)), new VarReg_ref(_i, frame.stackOffset)));
       return(this);
    }
    public HSAILAssembler mov_s64_const( Instruction _i, long _value){
        add( new HSAILInstructionSet.mov_const<StackReg_s64, s64, Long>(frame, _i, new StackReg_s64(frame.stackIdx(_i)), _value));
       return(this);
    }
    public HSAILAssembler mov_s32_const( Instruction _i, int _value){
        add( new HSAILInstructionSet.mov_const<StackReg_s32, s32, Integer>(frame, _i, new StackReg_s32(frame.stackIdx(_i)), _value));
       return(this);
    }

    public HSAILAssembler mov_f64_const( Instruction _i, double _value){
        add( new HSAILInstructionSet.mov_const<StackReg_f64, f64, Double>(frame, _i, new StackReg_f64(frame.stackIdx(_i)), _value));
       return(this);
    }
    public HSAILAssembler mov_f32_const( Instruction _i, float _value){
        add( new HSAILInstructionSet.mov_const<StackReg_f32, f32, Float>(frame, _i, new StackReg_f32(frame.stackIdx(_i)), _value));
       return(this);
    }
    public HSAILAssembler ld_arg_ref( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(frame, _i, new VarReg_ref(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_ref( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(frame, _i, new VarReg_ref(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_s32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(frame, _i, new VarReg_s32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_s32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(frame, _i, new VarReg_s32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_f32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(frame, _i, new VarReg_f32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_f32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(frame, _i, new VarReg_f32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_f64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(frame, _i, new VarReg_f64(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_f64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(frame, _i, new VarReg_f64(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_s64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(frame, _i, new VarReg_s64(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_s64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(frame, _i, new VarReg_s64(_varOffset)));
       return(this);
    }
    public HSAILAssembler workitemabsid_u32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.workitemabsid(frame, _i, new VarReg_s32(_varOffset)));
       return(this);
    }

    public HSAILAssembler nsqrt( Instruction _i, int _stackOffset){
       add( new HSAILInstructionSet.nsqrt(frame, _i, new StackReg_f64(_stackOffset)));
        return(this);
    }
    public HSAILAssembler nsqrt( Instruction _i){
        nsqrt(_i, frame.stackIdx(_i));
        return(this);
    }

    public HSAILAssembler nyi( Instruction _i){
      add( new HSAILInstructionSet.nyi(frame, _i));
        return(this);
    }

   // public HSAILAssembler mov_s32_const( Instruction _i){
    //   add( new HSAILInstructionSet.nyi(frame, _i));
  //      return(this);
   // }
   // mov_s32_const(instructions, hsailStackFrame, i,  i.asIntegerConstant().getValue());

    public HSAILAssembler array_len(Instruction _i){
       add( new HSAILInstructionSet.array_len(frame,_i, new StackReg_s32(frame.stackIdx(_i)), new StackReg_ref(frame.stackIdx(_i))));
       return(this);
    }

    public HSAILAssembler nop( Instruction _i, String _label){
       add( new HSAILInstructionSet.nop(frame, _i, _label));
        return(this);
    }
    public HSAILAssembler nop( Instruction _i){
        return(nop(_i, null));
    }
    public HSAILAssembler nopUniqueLabel( Instruction _i){
        return(nop(_i, frame.getUniqueName()));
    }
    public HSAILAssembler mov_ref( Instruction _i, int _destStackOffset, int _sourceStackOffset){

       add( new HSAILInstructionSet.mov(frame, _i, new StackReg_ref(_destStackOffset), new StackReg_ref(_sourceStackOffset)));
        return(this);
    }
    public HSAILAssembler mov_s32( Instruction _i, int _destStackOffset, int _sourceStackOffset){

       add( new HSAILInstructionSet.mov(frame, _i, new StackReg_s32(_destStackOffset), new StackReg_s32(_sourceStackOffset)));
        return(this);
    }
    public HSAILAssembler mov_f32( Instruction _i, int _destStackOffset, int _sourceStackOffset){

       add( new HSAILInstructionSet.mov(frame, _i, new StackReg_f32(_destStackOffset), new StackReg_f32(_sourceStackOffset)));
        return(this);
    }
    public HSAILAssembler mov_s64( Instruction _i, int _destStackOffset, int _sourceStackOffset){

        add(new HSAILInstructionSet.mov(frame, _i, new StackReg_s64(_destStackOffset), new StackReg_s64(_sourceStackOffset)));
        return(this);
    }
    public HSAILAssembler mov_f64( Instruction _i, int _destStackOffset, int _sourceStackOffset){

       add( new HSAILInstructionSet.mov(frame, _i, new StackReg_f64(_destStackOffset), new StackReg_f64(_sourceStackOffset)));
        return(this);
    }
    public HSAILAssembler returnBranchUniqueName( Instruction _i){
       add( new HSAILInstructionSet.returnBranch(frame, _i, frame.getUniqueName()));
        return(this);
    }


    public void addmov( Instruction _i, PrimitiveType _type, int _from, int _to) {
        if (_type.equals(PrimitiveType.ref) || _type.getHsaBits() == 32) {
            if (_type.equals(PrimitiveType.ref)) {
                mov_ref(_i, frame.stackIdx(_i) + _to, frame.stackIdx(_i) + _from);
            } else if (_type.equals(PrimitiveType.s32)) {
                mov_s32(_i, frame.stackIdx(_i) + _to, frame.stackIdx(_i) + _from);
            } else if (_type.equals(PrimitiveType.f32)) {
                mov_f32(_i, frame.stackIdx(_i) + _to, frame.stackIdx(_i) + _from);
            } else {
                throw new IllegalStateException(" unknown prefix 1 prefix for first of DUP2");
            }

        } else {
            throw new IllegalStateException(" unknown prefix 2 prefix for DUP2");
        }
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
    
    public HSAILRegister addmov(Instruction _i, int _from, int _to) {
        HSAILRegister r = getRegOfLastWriteToIndex( frame.stackIdx(_i) + _from);
        if (r == null){
            System.out.println("damn!");
        }
        addmov( _i, r.type, _from, _to);
        return (r);
    }

    static boolean compressMovs = false;
    public void add( HSAILInstruction _instruction) {
        if (compressMovs){
            // before we add lets see if this is a redundant mov
            for (int srcIndex = 0; srcIndex < _instruction.sources.length; srcIndex++) {
                HSAILOperand source = _instruction.sources[srcIndex];
                if (source instanceof StackReg) {
                    // look up the list of reg instructions for the instruction which assigns to this
                    int i = instructions.size();
                    while ((--i) >= 0) {
                        if (instructions.get(i) instanceof HSAILInstructionSet.mov) {
                            // we have found a move
                            HSAILInstructionSet.mov candidateForRemoval = (HSAILInstructionSet.mov) instructions.get(i);
                            if (candidateForRemoval.from.getBlock() == _instruction.from.getBlock()
                                    && (candidateForRemoval.getDest() instanceof StackReg) && candidateForRemoval.getDest().equals(source)) {
                                // so i may be a candidate if between i and instruction.size() i.dest() is not mutated
                                boolean mutated = false;
                                for (int x = i + 1; !mutated && x < instructions.size(); x++) {
                                    if (instructions.get(x).dests.length > 0 && instructions.get(x).dests[0].equals(candidateForRemoval.getSrc())) {
                                        mutated = true;
                                    }
                                }
                                if (!mutated) {
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

}

