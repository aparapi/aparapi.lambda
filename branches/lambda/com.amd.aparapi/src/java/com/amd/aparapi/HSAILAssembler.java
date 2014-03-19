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
    Stack<HSAILStackFrame> frames;
    List<HSAILStackFrame> frameSet;

    HSAILAssembler( List<HSAILInstruction> _instructions,  Stack<HSAILStackFrame> _frames, List<HSAILStackFrame> _frameSet){
        instructions = _instructions;
        frames = _frames;
        frameSet = _frameSet;
    }
    public HSAILAssembler field_store_s64(Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_store<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(this);
    }
    public HSAILAssembler field_store_f64(Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_store<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(this);
    }
    public HSAILAssembler field_store_f32(Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_store<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(this);
    }
    
    public HSAILAssembler field_store_s32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }

     public HSAILAssembler field_store_s16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_s16, s16>(frames.peek(), _i, new StackReg_s16(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_store_u16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_u16, u16>(frames.peek(), _i, new StackReg_u16(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_store_s8( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_s8, s8>(frames.peek(), _i, new StackReg_s8(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_store_ref( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_ref, ref>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }

     public HSAILAssembler field_load_ref( Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_load<StackReg_ref, ref>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_f32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_f64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s16, s16>(frames.peek(), _i, new StackReg_s16(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_u16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_u16, u16>(frames.peek(), _i, new StackReg_u16(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s8( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s8, s8>(frames.peek(), _i, new StackReg_s8(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_f64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_f32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s16, s16>(frames.peek(), _i, new StackReg_s16(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_u16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_u16, u16>(frames.peek(), _i, new StackReg_u16(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s8( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s8, s8>(frames.peek(), _i, new StackReg_s8(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_ref( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_ref, ref>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler ret_void( Instruction _i){
        add( new HSAILInstructionSet.retvoid(frames.peek(), _i));
       return(this);
    }
     public HSAILAssembler ret_ref( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_ref, ref>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i))));
       return(this);
    }

     public HSAILAssembler ret_s32( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }

     public HSAILAssembler ret_f32( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i))));
       return(this);
    }

     public HSAILAssembler ret_s64( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i))));
       return(this);
    }

     public HSAILAssembler ret_f64( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i))));
       return(this);
    }
     public HSAILAssembler branch( Instruction _i){
       add( new HSAILInstructionSet.branch(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), _i.getByteCode().getName(), _i.asBranch().getAbsolute()));
       return(this);
    }
     public HSAILAssembler brn( Instruction _i){
       add( new HSAILInstructionSet.brn(frames.peek(), _i, _i.asBranch().getAbsolute()));
       return(this);
    }
     public HSAILAssembler cbr( Instruction _i){
       add( new HSAILInstructionSet.cbr(frames.peek(), _i, _i.asBranch().getAbsolute()));
       return(this);
    }
     public HSAILAssembler cmp_ref_ne( Instruction _i){
       add( new HSAILInstructionSet.cmp_ref(frames.peek(), _i, "ne", new StackReg_ref(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler cmp_ref_eq( Instruction _i){
        add( new HSAILInstructionSet.cmp_ref(frames.peek(), _i, "eq", new StackReg_ref(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s32_ne( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frames.peek(), _i, "ne", new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_eq( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frames.peek(), _i, "eq", new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_lt( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frames.peek(), _i, "lt", new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_gt( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frames.peek(), _i, "gt", new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_ge( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frames.peek(), _i, "ge", new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_le( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(frames.peek(), _i, "le", new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_le_const_0( Instruction _i){
       add( new HSAILInstructionSet.cmp_s32_const_0(frames.peek(), _i, "le", new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }

    public HSAILAssembler cmp_s32_gt_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(frames.peek(), _i, "gt", new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cmp_s32_ge_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(frames.peek(), _i, "ge", new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cmp_s32_lt_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(frames.peek(), _i, "lt", new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cmp_s32_eq_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(frames.peek(), _i, "eq", new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cmp_s32_ne_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(frames.peek(), _i, "ne", new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }

    public HSAILAssembler cmp_s64_le( Instruction _i){
       Instruction lastInstruction = _i.getPrevPC();
       add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frames.peek(), lastInstruction, "le", new StackReg_s64(frames.peek().stackIdx(lastInstruction)), new StackReg_s64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_ge( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frames.peek(), lastInstruction, "ge", new StackReg_s64(frames.peek().stackIdx(lastInstruction)), new StackReg_s64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_gt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frames.peek(), lastInstruction, "gt", new StackReg_s64(frames.peek().stackIdx(lastInstruction)), new StackReg_s64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_lt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frames.peek(), lastInstruction, "lt", new StackReg_s64(frames.peek().stackIdx(lastInstruction)), new StackReg_s64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_eq( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frames.peek(), lastInstruction, "eq", new StackReg_s64(frames.peek().stackIdx(lastInstruction)), new StackReg_s64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_ne( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(frames.peek(), lastInstruction, "ne", new StackReg_s64(frames.peek().stackIdx(lastInstruction)), new StackReg_s64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }

    public HSAILAssembler cmp_f64_le( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frames.peek(), lastInstruction, "le", new StackReg_f64(frames.peek().stackIdx(lastInstruction)), new StackReg_f64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_ge( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frames.peek(), lastInstruction, "ge", new StackReg_f64(frames.peek().stackIdx(lastInstruction)), new StackReg_f64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_lt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frames.peek(), lastInstruction, "lt", new StackReg_f64(frames.peek().stackIdx(lastInstruction)), new StackReg_f64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_gt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frames.peek(), lastInstruction, "gt", new StackReg_f64(frames.peek().stackIdx(lastInstruction)), new StackReg_f64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_eq( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frames.peek(), lastInstruction, "eq", new StackReg_f64(frames.peek().stackIdx(lastInstruction)), new StackReg_f64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_ne( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(frames.peek(), lastInstruction, "ne", new StackReg_f64(frames.peek().stackIdx(lastInstruction)), new StackReg_f64(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }

    public HSAILAssembler cmp_f32_le( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frames.peek(), lastInstruction, "le", new StackReg_f32(frames.peek().stackIdx(lastInstruction)), new StackReg_f32(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_ge( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frames.peek(), lastInstruction, "ge", new StackReg_f32(frames.peek().stackIdx(lastInstruction)), new StackReg_f32(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_lt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frames.peek(), lastInstruction, "lt", new StackReg_f32(frames.peek().stackIdx(lastInstruction)), new StackReg_f32(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_gt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frames.peek(), lastInstruction, "gt", new StackReg_f32(frames.peek().stackIdx(lastInstruction)), new StackReg_f32(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_eq( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frames.peek(), lastInstruction, "eq", new StackReg_f32(frames.peek().stackIdx(lastInstruction)), new StackReg_f32(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_ne( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(frames.peek(), lastInstruction, "ne", new StackReg_f32(frames.peek().stackIdx(lastInstruction)), new StackReg_f32(frames.peek().stackIdx(lastInstruction)+1)));
       return(this);
    }
    public HSAILAssembler cvt_s8_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s8, StackReg_s32, s8, s32>(frames.peek(), _i, new StackReg_s8(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s16_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s16, StackReg_s32, s16, s32>(frames.peek(), _i, new StackReg_s16(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_u16_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_u16, StackReg_s32, u16, s32>(frames.peek(), _i, new StackReg_u16(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f32_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f32, StackReg_s32, f32, s32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s64_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s64, StackReg_s32, s64, s32>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f64_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f64, StackReg_s32, f64, s32>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_ref_s32_1( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_ref, StackReg_s32, ref, s32>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler cvt_ref_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_ref, StackReg_s32, ref, s32>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s32_s64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s32, StackReg_s64, s32, s64>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f32_s64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f32, StackReg_s64, f32, s64>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f64_s64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f64, StackReg_s64, f64, s64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i))));
       return(this);
    }

    public HSAILAssembler cvt_s32_f32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s32, StackReg_f32, s32, f32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f64_f32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f64, StackReg_f32, f64, f32>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s64_f32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s64, StackReg_f32, s64, f32>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s32_f64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s32, StackReg_f64, s32, f64>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_f32_f64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f32, StackReg_f64, f32, f64>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler cvt_s64_f64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s64, StackReg_f64, s64, f64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler add_const_s32( Instruction _i){
        add( new HSAILInstructionSet.add_const<VarReg_s32, s32, Integer>(frames.peek(), _i, new VarReg_s32(_i, frames.peek().stackOffset), new VarReg_s32(_i, frames.peek().stackOffset), ((InstructionSet.I_IINC) _i).getDelta()));
       return(this);
    }
    public HSAILAssembler xor_s64( Instruction _i){
        add( new HSAILInstructionSet.xor<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler xor_s32( Instruction _i){
        add( new HSAILInstructionSet.xor<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler or_s64( Instruction _i){
        add( new HSAILInstructionSet.or<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler or_s32( Instruction _i){
        add( new HSAILInstructionSet.or<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler and_s64( Instruction _i){
        add( new HSAILInstructionSet.and<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler and_s32( Instruction _i){
        add( new HSAILInstructionSet.and<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler ushr_s64( Instruction _i){
        add( new HSAILInstructionSet.ushr<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler ushr_s32( Instruction _i){
        add( new HSAILInstructionSet.ushr<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler shr_s64( Instruction _i){
        add( new HSAILInstructionSet.shr<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler shr_s32( Instruction _i){
        add( new HSAILInstructionSet.shr<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler shl_s64( Instruction _i){
        add( new HSAILInstructionSet.shl<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler shl_s32( Instruction _i){
        add( new HSAILInstructionSet.shl<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler neg_f64( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler neg_s64( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler neg_f32( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler neg_s32( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler rem_s64( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler rem_s32( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler rem_f64( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler rem_f32( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler div_s64( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler div_s32( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler div_f64( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler div_f32( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler mul_s64( Instruction _i){
        add( new HSAILInstructionSet.mul<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler mul_s32( Instruction _i){
        add( new HSAILInstructionSet.mul<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler mul_f64( Instruction _i){
        add( new HSAILInstructionSet.mul<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler mul_f32( Instruction _i){
        add( new HSAILInstructionSet.mul<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler sub_s64( Instruction _i){
        add( new HSAILInstructionSet.sub<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler sub_s32( Instruction _i){
        add( new HSAILInstructionSet.sub<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler sub_f64( Instruction _i){
        add( new HSAILInstructionSet.sub<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler sub_f32( Instruction _i){
        add( new HSAILInstructionSet.sub<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler add_s64( Instruction _i){
        add( new HSAILInstructionSet.add<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_s64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler add_s32( Instruction _i){
        add( new HSAILInstructionSet.add<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_s32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler add_s32( Instruction _i, int _dest, int _lhs, int _rhs){
        add( new HSAILInstructionSet.add<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(_dest), new StackReg_s32(_lhs), new StackReg_s32(_rhs)));
       return(this);
    }
    public HSAILAssembler add_f64( Instruction _i){
        add( new HSAILInstructionSet.add<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_f64(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler add_f32( Instruction _i){
        add( new HSAILInstructionSet.add<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_f32(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_store_s16( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s16, s16>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_s16(frames.peek().stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_u16( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_u16, u16>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_u16(frames.peek().stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_s32( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s32, s32>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_s32(frames.peek().stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_f32( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_f32, f32>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_f32(frames.peek().stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_f64( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_f64, f64>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_f64(frames.peek().stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_ref( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_ref, ref>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_s8( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s8, s8>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_s8(frames.peek().stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler array_store_s64( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s64, s64>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_s64(frames.peek().stackIdx(_i)+2)));
       return(this);
    }
    public HSAILAssembler mad( Instruction _i, int _size){
       add( new HSAILInstructionSet.mad(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)+1), new StackReg_ref(frames.peek().stackIdx(_i)), (long) _size));
       return(this);
    }
    public HSAILAssembler mov_var_ref( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_ref, StackReg_ref, ref, ref>(frames.peek(), _i, new VarReg_ref(_i, frames.peek().stackOffset), new StackReg_ref(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler mov_var_s32( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_s32, StackReg_s32, s32, s32>(frames.peek(), _i, new VarReg_s32(_i, frames.peek().stackOffset), new StackReg_s32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler mov_var_f32( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_f32, StackReg_f32, f32, f32>(frames.peek(), _i, new VarReg_f32(_i, frames.peek().stackOffset), new StackReg_f32(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler mov_var_f64( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_f64, StackReg_f64, f64, f64>(frames.peek(), _i, new VarReg_f64(_i, frames.peek().stackOffset), new StackReg_f64(frames.peek().stackIdx(_i))));
       return(this);
    }
    public HSAILAssembler mov_var_s64( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_s64, StackReg_s64, s64, s64>(frames.peek(), _i, new VarReg_s64(_i, frames.peek().stackOffset), new StackReg_s64(frames.peek().stackIdx(_i))));
       return(this);
    }

    public HSAILAssembler array_load_s32( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_f32( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_u16( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_u16, u16>(frames.peek(), _i, new StackReg_u16(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_s16( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s16, s16>(frames.peek(), _i, new StackReg_s16(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_s64( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_f64( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)+1)));
       return(this);
    }

    public HSAILAssembler array_load_s8( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s8, s8>(frames.peek(), _i, new StackReg_s8(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler array_load_ref( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_ref, ref>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i)+1)));
       return(this);
    }
    public HSAILAssembler mov_f64_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_f64, VarReg_f64, f64, f64>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), new VarReg_f64(_i, frames.peek().stackOffset)));
       return(this);
    }
    public HSAILAssembler mov_f32_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_f32, VarReg_f32, f32, f32>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), new VarReg_f32(_i, frames.peek().stackOffset)));
       return(this);
    }

    public HSAILAssembler mov_s64_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_s64, VarReg_s64, s64, s64>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), new VarReg_s64(_i, frames.peek().stackOffset)));
       return(this);
    }
    public HSAILAssembler mov_s32_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_s32, VarReg_s32, s32, s32>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), new VarReg_s32(_i, frames.peek().stackOffset)));
       return(this);
    }


    public HSAILAssembler mov_ref_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_ref, VarReg_ref, ref, ref>(frames.peek(), _i, new StackReg_ref(frames.peek().stackIdx(_i)), new VarReg_ref(_i, frames.peek().stackOffset)));
       return(this);
    }
    public HSAILAssembler mov_s64_const( Instruction _i, long _value){
        add( new HSAILInstructionSet.mov_const<StackReg_s64, s64, Long>(frames.peek(), _i, new StackReg_s64(frames.peek().stackIdx(_i)), _value));
       return(this);
    }
    public HSAILAssembler mov_s32_const( Instruction _i, int _value){
        add( new HSAILInstructionSet.mov_const<StackReg_s32, s32, Integer>(frames.peek(), _i, new StackReg_s32(frames.peek().stackIdx(_i)), _value));
       return(this);
    }

    public HSAILAssembler mov_f64_const( Instruction _i, double _value){
        add( new HSAILInstructionSet.mov_const<StackReg_f64, f64, Double>(frames.peek(), _i, new StackReg_f64(frames.peek().stackIdx(_i)), _value));
       return(this);
    }
    public HSAILAssembler mov_f32_const( Instruction _i, float _value){
        add( new HSAILInstructionSet.mov_const<StackReg_f32, f32, Float>(frames.peek(), _i, new StackReg_f32(frames.peek().stackIdx(_i)), _value));
       return(this);
    }
    public HSAILAssembler ld_arg_ref( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(frames.peek(), _i, new VarReg_ref(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_ref( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(frames.peek(), _i, new VarReg_ref(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_s32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(frames.peek(), _i, new VarReg_s32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_s32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(frames.peek(), _i, new VarReg_s32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_f32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(frames.peek(), _i, new VarReg_f32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_f32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(frames.peek(), _i, new VarReg_f32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_f64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(frames.peek(), _i, new VarReg_f64(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_f64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(frames.peek(), _i, new VarReg_f64(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_s64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(frames.peek(), _i, new VarReg_s64(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_s64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(frames.peek(), _i, new VarReg_s64(_varOffset)));
       return(this);
    }
    public HSAILAssembler workitemabsid_u32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.workitemabsid(frames.peek(), _i, new VarReg_s32(_varOffset)));
       return(this);
    }

    public HSAILAssembler nsqrt( Instruction _i, int _stackOffset){
       add( new HSAILInstructionSet.nsqrt(frames.peek(), _i, new StackReg_f64(_stackOffset)));
        return(this);
    }
    public HSAILAssembler nsqrt( Instruction _i){
        nsqrt(_i, frames.peek().stackIdx(_i));
        return(this);
    }

    public HSAILAssembler nyi( Instruction _i){
      add( new HSAILInstructionSet.nyi(frames.peek(), _i));
        return(this);
    }

   // public HSAILAssembler mov_s32_const( Instruction _i){
    //   add( new HSAILInstructionSet.nyi(frames.peek(), _i));
  //      return(this);
   // }
   // mov_s32_const(instructions, hsailStackframes.peek(), i,  i.asIntegerConstant().getValue());

    public HSAILAssembler array_len(Instruction _i){
       add( new HSAILInstructionSet.array_len(frames.peek(),_i, new StackReg_s32(frames.peek().stackIdx(_i)), new StackReg_ref(frames.peek().stackIdx(_i))));
       return(this);
    }

    public HSAILAssembler nop( Instruction _i, String _label){
       add( new HSAILInstructionSet.nop(frames.peek(), _i, _label));
        return(this);
    }
    public HSAILAssembler nop( Instruction _i){
        return(nop(_i, null));
    }
    public HSAILAssembler nopUniqueLabel( Instruction _i){
        return(nop(_i, frames.peek().getUniqueName()));
    }
    public HSAILAssembler mov_ref( Instruction _i, int _destStackOffset, int _sourceStackOffset){

       add( new HSAILInstructionSet.mov(frames.peek(), _i, new StackReg_ref(_destStackOffset), new StackReg_ref(_sourceStackOffset)));
        return(this);
    }
    public HSAILAssembler mov_s32( Instruction _i, int _destStackOffset, int _sourceStackOffset){

       add( new HSAILInstructionSet.mov(frames.peek(), _i, new StackReg_s32(_destStackOffset), new StackReg_s32(_sourceStackOffset)));
        return(this);
    }
    public HSAILAssembler mov_f32( Instruction _i, int _destStackOffset, int _sourceStackOffset){

       add( new HSAILInstructionSet.mov(frames.peek(), _i, new StackReg_f32(_destStackOffset), new StackReg_f32(_sourceStackOffset)));
        return(this);
    }
    public HSAILAssembler mov_s64( Instruction _i, int _destStackOffset, int _sourceStackOffset){

        add(new HSAILInstructionSet.mov(frames.peek(), _i, new StackReg_s64(_destStackOffset), new StackReg_s64(_sourceStackOffset)));
        return(this);
    }
    public HSAILAssembler mov_f64( Instruction _i, int _destStackOffset, int _sourceStackOffset){

       add( new HSAILInstructionSet.mov(frames.peek(), _i, new StackReg_f64(_destStackOffset), new StackReg_f64(_sourceStackOffset)));
        return(this);
    }
    public HSAILAssembler returnBranchUniqueName( Instruction _i){
       add( new HSAILInstructionSet.returnBranch(frames.peek(), _i, frames.peek().getUniqueName()));
        return(this);
    }


    public void addmov( Instruction _i, PrimitiveType _type, int _from, int _to) {
        if (_type.equals(PrimitiveType.ref) || _type.getHsaBits() == 32) {
            if (_type.equals(PrimitiveType.ref)) {
                mov_ref(_i, frames.peek().stackIdx(_i) + _to, frames.peek().stackIdx(_i) + _from);
            } else if (_type.equals(PrimitiveType.s32)) {
                mov_s32(_i, frames.peek().stackIdx(_i) + _to, frames.peek().stackIdx(_i) + _from);
            } else if (_type.equals(PrimitiveType.f32)) {
                mov_f32(_i, frames.peek().stackIdx(_i) + _to, frames.peek().stackIdx(_i) + _from);
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
        HSAILRegister r = getRegOfLastWriteToIndex( frames.peek().stackIdx(_i) + _from);
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


    enum ParseState {NONE, COMPARE_F32, COMPARE_F64, COMPARE_S64}
    ;


    public void addInstructions( ClassModel.ClassModelMethod  method){
        HSAILAssembler assembler = this;
        ParseState parseState = ParseState.NONE;

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
                    if (frames.size()>1){
                        int maxLocals=i.getMethod().getCodeEntry().getMaxLocals(); // hsailStackFrame.stackOffset -maxLocals is the slot for the return value

                        switch(i.getByteCode()){
                            case IRETURN: assembler.mov_s32(i, frames.peek().stackIdx(i) - maxLocals, frames.peek().stackIdx(i));break;
                            case LRETURN: assembler.mov_s64(i, frames.peek().stackIdx(i) - maxLocals, frames.peek().stackIdx(i));break;
                            case FRETURN: assembler.mov_f32(i, frames.peek().stackIdx(i) - maxLocals, frames.peek().stackIdx(i));break;
                            case DRETURN: assembler.mov_f64(i, frames.peek().stackIdx(i) - maxLocals, frames.peek().stackIdx(i));break;
                            case ARETURN: assembler.mov_ref(i, frames.peek().stackIdx(i) - maxLocals, frames.peek().stackIdx(i));break;
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
                    if (frames.size()>1){
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
                        call.add(this, i);
                    }else{
                        try{
                            Class theClass = Class.forName(callInfo.dotClassName);
                            ClassModel classModel = ClassModel.getClassModel(theClass);
                            ClassModel.ClassModelMethod calledMethod = classModel.getMethod(callInfo.name, callInfo.sig);
                            frames.push(new HSAILStackFrame(frames.peek(),  calledMethod, i.getThisPC(), i.getPreStackBase()+i.getMethod().getCodeEntry().getMaxLocals()+frames.peek().stackOffset));
                            frameSet.add(frames.peek());
                            addInstructions( calledMethod);
                            frames.pop();
                        }catch (ClassParseException cpe){

                        }catch (ClassNotFoundException cnf){

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

