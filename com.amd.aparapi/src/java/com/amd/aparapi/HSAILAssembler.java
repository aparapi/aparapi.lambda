package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import com.amd.aparapi.HSAILInstructionSet.HSAILInstruction;

/**
 * Created by user1 on 1/14/14.
 */
public class HSAILAssembler {

   
    private List<HSAILInstruction> instructions;
    private Stack<HSAILStackFrame> frames;
    private List<HSAILStackFrame> frameSet;

    HSAILAssembler( List<HSAILInstruction> _instructions,  Stack<HSAILStackFrame> _frames, List<HSAILStackFrame> _frameSet){
        instructions = _instructions;
        frames = _frames;
        frameSet = _frameSet;
    }

    HSAILAssembler(){
        this(new ArrayList<HSAILInstruction>(), new Stack<HSAILStackFrame>(), new ArrayList<HSAILStackFrame>());
    }

    HSAILAssembler(ClassModel.ClassModelMethod _method){
        this();
        HSAILStackFrame base = new HSAILStackFrame(null, _method, 0, 0);
        frames.push(base);
        frameSet.add(base);
    }
    public HSAILAssembler field_store_s64(Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_store<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i, 1), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(this);
    }
    public HSAILAssembler field_store_f64(Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_store<StackReg_f64, f64>(currentFrame(), _i, stackReg_f64(_i, 1), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(this);
    }
    public HSAILAssembler field_store_f32(Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_store<StackReg_f32, f32>(currentFrame(), _i, stackReg_f32(_i, 1), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
        return(this);
    }
    
    public HSAILAssembler field_store_s32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i, 1), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }

     public HSAILAssembler field_store_s16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_s16, s16>(currentFrame(), _i,stackReg_s16(_i, 1), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_store_u16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_u16, u16>(currentFrame(), _i,stackReg_u16(_i, 1), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_store_s8( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_s8, s8>(currentFrame(), _i, stackReg_s8(_i, 1), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_store_ref( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_store<StackReg_ref, ref>(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }

     public HSAILAssembler field_load_ref( Instruction _i, Field _f){
       add( new HSAILInstructionSet.field_load<StackReg_ref, ref>(currentFrame(), _i, stackReg_ref(_i), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_f32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_f32, f32>(currentFrame(), _i, stackReg_f32(_i), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_f64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_f64, f64>(currentFrame(), _i, stackReg_f64(_i), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s16, s16>(currentFrame(), _i, stackReg_s16(_i), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_u16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_u16, u16>(currentFrame(), _i, stackReg_u16(_i), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler field_load_s8( Instruction _i, Field _f){
        add( new HSAILInstructionSet.field_load<StackReg_s8, s8>(currentFrame(), _i, stackReg_s8(_i), stackReg_ref(_i), (long) UnsafeWrapper.objectFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_ref(_i), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_f64( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_f64, f64>(currentFrame(), _i, stackReg_f64(_i), stackReg_ref(_i), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_ref(_i), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_f32( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_f32, f32>(currentFrame(), _i, stackReg_f32(_i), stackReg_ref(_i), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s16, s16>(currentFrame(), _i, stackReg_s16(_i), stackReg_ref(_i), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_u16( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_u16, u16>(currentFrame(), _i, stackReg_u16(_i), stackReg_ref(_i), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_s8( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_s8, s8>(currentFrame(), _i, stackReg_s8(_i), stackReg_ref(_i), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler static_field_load_ref( Instruction _i, Field _f){
        add( new HSAILInstructionSet.static_field_load<StackReg_ref, ref>(currentFrame(), _i, stackReg_ref(_i), stackReg_ref(_i), (long) UnsafeWrapper.staticFieldOffset(_f)));
       return(this);
    }
     public HSAILAssembler ret_void( Instruction _i){
        add( new HSAILInstructionSet.retvoid(currentFrame(), _i));
       return(this);
    }
     public HSAILAssembler ret_ref( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_ref, ref>(currentFrame(), _i, stackReg_ref(_i)));
       return(this);
    }

     public HSAILAssembler ret_s32( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i)));
       return(this);
    }

     public HSAILAssembler ret_f32( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_f32, f32>(currentFrame(), _i, stackReg_f32(_i)));
       return(this);
    }

     public HSAILAssembler ret_s64( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i)));
       return(this);
    }

     public HSAILAssembler ret_f64( Instruction _i){
        add( new HSAILInstructionSet.ret<StackReg_f64, f64>(currentFrame(), _i, stackReg_f64(_i)));
       return(this);
    }
     public HSAILAssembler branch( Instruction _i){
       add( new HSAILInstructionSet.branch(currentFrame(), _i, stackReg_s32(_i), _i.getByteCode().getName(), _i.asBranch().getAbsolute()));
       return(this);
    }
     public HSAILAssembler brn( Instruction _i){
       add( new HSAILInstructionSet.brn(currentFrame(), _i, _i.asBranch().getAbsolute()));
       return(this);
    }
     public HSAILAssembler cbr( Instruction _i){
       add( new HSAILInstructionSet.cbr(currentFrame(), _i, _i.asBranch().getAbsolute()));
       return(this);
    }
     public HSAILAssembler cmp_ref_ne( Instruction _i){
       add( new HSAILInstructionSet.cmp_ref(currentFrame(), _i, "ne", stackReg_ref(_i), stackReg_ref(_i, 1)));
       return(this);
    }
    public HSAILAssembler cmp_ref_eq( Instruction _i){
        add( new HSAILInstructionSet.cmp_ref(currentFrame(), _i, "eq", stackReg_ref(_i), stackReg_ref(_i, 1)));
       return(this);
    }
    public HSAILAssembler cmp_s32_ne( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(currentFrame(), _i, "ne", stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_eq( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(currentFrame(), _i, "eq", stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_lt( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(currentFrame(), _i, "lt", stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_gt( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(currentFrame(), _i, "gt", stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_ge( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(currentFrame(), _i, "ge", stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_le( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32(currentFrame(), _i, "le", stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }

    public HSAILAssembler cmp_s32_le_const_0( Instruction _i){
       add( new HSAILInstructionSet.cmp_s32_const_0(currentFrame(), _i, "le", stackReg_s32(_i)));
       return(this);
    }

    public HSAILAssembler cmp_s32_gt_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(currentFrame(), _i, "gt", stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cmp_s32_ge_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(currentFrame(), _i, "ge", stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cmp_s32_lt_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(currentFrame(), _i, "lt", stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cmp_s32_eq_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(currentFrame(), _i, "eq", stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cmp_s32_ne_const_0( Instruction _i){
        add( new HSAILInstructionSet.cmp_s32_const_0(currentFrame(), _i, "ne", stackReg_s32(_i)));
       return(this);
    }

    public HSAILAssembler cmp_s64_le( Instruction _i){
       Instruction lastInstruction = _i.getPrevPC();
       add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(currentFrame(), lastInstruction, "le", stackReg_s64(lastInstruction), stackReg_s64(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_ge( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(currentFrame(), lastInstruction, "ge", stackReg_s64(lastInstruction), stackReg_s64(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_gt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(currentFrame(), lastInstruction, "gt", stackReg_s64(lastInstruction), stackReg_s64(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_lt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(currentFrame(), lastInstruction, "lt", stackReg_s64(lastInstruction), stackReg_s64(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_eq( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(currentFrame(), lastInstruction, "eq", stackReg_s64(lastInstruction), stackReg_s64(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_s64_ne( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_s64, s64>(currentFrame(), lastInstruction, "ne", stackReg_s64(lastInstruction), stackReg_s64(lastInstruction, 1)));
       return(this);
    }

    public HSAILAssembler cmp_f64_le( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(currentFrame(), lastInstruction, "le", stackReg_f64(lastInstruction), stackReg_f64(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_ge( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(currentFrame(), lastInstruction, "ge", stackReg_f64(lastInstruction), stackReg_f64(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_lt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(currentFrame(), lastInstruction, "lt", stackReg_f64(lastInstruction), stackReg_f64(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_gt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(currentFrame(), lastInstruction, "gt", stackReg_f64(lastInstruction), stackReg_f64(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_eq( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(currentFrame(), lastInstruction, "eq", stackReg_f64(lastInstruction), stackReg_f64(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_f64_ne( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f64, f64>(currentFrame(), lastInstruction, "ne", stackReg_f64(lastInstruction), stackReg_f64(lastInstruction, 1)));
       return(this);
    }

    public HSAILAssembler cmp_f32_le( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(currentFrame(), lastInstruction, "le", stackReg_f32(lastInstruction), stackReg_f32(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_ge( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(currentFrame(), lastInstruction, "ge", stackReg_f32(lastInstruction), stackReg_f32(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_lt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(currentFrame(), lastInstruction, "lt", stackReg_f32(lastInstruction), stackReg_f32(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_gt( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(currentFrame(), lastInstruction, "gt", stackReg_f32(lastInstruction), stackReg_f32(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_eq( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(currentFrame(), lastInstruction, "eq", stackReg_f32(lastInstruction), stackReg_f32(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cmp_f32_ne( Instruction _i){
        Instruction lastInstruction = _i.getPrevPC();
        add( new HSAILInstructionSet.cmp<StackReg_f32, f32>(currentFrame(), lastInstruction, "ne", stackReg_f32(lastInstruction), stackReg_f32(lastInstruction, 1)));
       return(this);
    }
    public HSAILAssembler cvt_s8_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s8, StackReg_s32, s8, s32>(currentFrame(), _i, stackReg_s8(_i), stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cvt_s16_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s16, StackReg_s32, s16, s32>(currentFrame(), _i, stackReg_s16(_i), stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cvt_u16_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_u16, StackReg_s32, u16, s32>(currentFrame(), _i, stackReg_u16(_i), stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cvt_f32_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f32, StackReg_s32, f32, s32>(currentFrame(), _i, stackReg_f32(_i), stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cvt_s64_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s64, StackReg_s32, s64, s32>(currentFrame(), _i, stackReg_s64(_i), stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cvt_f64_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f64, StackReg_s32, f64, s32>(currentFrame(), _i, stackReg_f64(_i), stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cvt_ref_s32_1( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_ref, StackReg_s32, ref, s32>(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_s32(_i, 1)));
       return(this);
    }
    public HSAILAssembler cvt_ref_s32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_ref, StackReg_s32, ref, s32>(currentFrame(), _i, stackReg_ref(_i), stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler cvt_s32_s64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s32, StackReg_s64, s32, s64>(currentFrame(), _i, stackReg_s32(_i), stackReg_s64(_i)));
       return(this);
    }
    public HSAILAssembler cvt_f32_s64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f32, StackReg_s64, f32, s64>(currentFrame(), _i, stackReg_f32(_i), stackReg_s64(_i)));
       return(this);
    }
    public HSAILAssembler cvt_f64_s64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f64, StackReg_s64, f64, s64>(currentFrame(), _i, stackReg_f64(_i), stackReg_s64(_i)));
       return(this);
    }

    public HSAILAssembler cvt_s32_f32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s32, StackReg_f32, s32, f32>(currentFrame(), _i, stackReg_s32(_i), stackReg_f32(_i)));
       return(this);
    }
    public HSAILAssembler cvt_f64_f32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f64, StackReg_f32, f64, f32>(currentFrame(), _i, stackReg_f64(_i), stackReg_f32(_i)));
       return(this);
    }
    public HSAILAssembler cvt_s64_f32( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s64, StackReg_f32, s64, f32>(currentFrame(), _i, stackReg_s64(_i), stackReg_f32(_i)));
       return(this);
    }
    public HSAILAssembler cvt_s32_f64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s32, StackReg_f64, s32, f64>(currentFrame(), _i, stackReg_s32(_i), stackReg_f64(_i)));
       return(this);
    }
    public HSAILAssembler cvt_f32_f64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_f32, StackReg_f64, f32, f64>(currentFrame(), _i, stackReg_f32(_i), stackReg_f64(_i)));
       return(this);
    }
    public HSAILAssembler cvt_s64_f64( Instruction _i){
        add( new HSAILInstructionSet.cvt<StackReg_s64, StackReg_f64, s64, f64>(currentFrame(), _i, stackReg_s64(_i), stackReg_f64(_i)));
       return(this);
    }
    public HSAILAssembler add_const_s32( Instruction _i){
        add( new HSAILInstructionSet.add_const<VarReg_s32, s32, Integer>(currentFrame(), _i, new VarReg_s32(_i, currentStackOffset()), new VarReg_s32(_i, currentStackOffset()), ((InstructionSet.I_IINC) _i).getDelta()));
       return(this);
    }
    public HSAILAssembler xor_s64( Instruction _i){
        add( new HSAILInstructionSet.xor<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_s64(_i), stackReg_s64(_i,1)));
       return(this);
    }
    public HSAILAssembler xor_s32( Instruction _i){
        add( new HSAILInstructionSet.xor<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }
    public HSAILAssembler or_s64( Instruction _i){
        add( new HSAILInstructionSet.or<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_s64(_i), stackReg_s64(_i,1)));
       return(this);
    }
    public HSAILAssembler or_s32( Instruction _i){
        add( new HSAILInstructionSet.or<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }
    public HSAILAssembler and_s64( Instruction _i){
        add( new HSAILInstructionSet.and<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_s64(_i), stackReg_s64(_i,1)));
       return(this);
    }
    public HSAILAssembler and_s32( Instruction _i){
        add( new HSAILInstructionSet.and<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }
    public HSAILAssembler ushr_s64( Instruction _i){
        add( new HSAILInstructionSet.ushr<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_s64(_i), stackReg_s64(_i,1)));
       return(this);
    }
    public HSAILAssembler ushr_s32( Instruction _i){
        add( new HSAILInstructionSet.ushr<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }
    public HSAILAssembler shr_s64( Instruction _i){
        add( new HSAILInstructionSet.shr<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_s64(_i), stackReg_s64(_i,1)));
       return(this);
    }
    public HSAILAssembler shr_s32( Instruction _i){
        add( new HSAILInstructionSet.shr<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }
    public HSAILAssembler shl_s64( Instruction _i){
        add( new HSAILInstructionSet.shl<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_s64(_i), stackReg_s64(_i,1)));
       return(this);
    }
    public HSAILAssembler shl_s32( Instruction _i){
        add( new HSAILInstructionSet.shl<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }
    public HSAILAssembler neg_f64( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_f64, f64>(currentFrame(), _i, stackReg_f64(_i)));
       return(this);
    }
    public HSAILAssembler neg_s64( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i)));
       return(this);
    }
    public HSAILAssembler neg_f32( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_f32, f32>(currentFrame(), _i, stackReg_f32(_i)));
       return(this);
    }
    public HSAILAssembler neg_s32( Instruction _i){
        add( new HSAILInstructionSet.neg<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler rem_s64( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_s64(_i), stackReg_s64(_i,1)));
       return(this);
    }
    public HSAILAssembler rem_s32( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }
    public HSAILAssembler rem_f64( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_f64, f64>(currentFrame(), _i, stackReg_f64(_i), stackReg_f64(_i), stackReg_f64(_i, 1)));
       return(this);
    }
    public HSAILAssembler rem_f32( Instruction _i){
        add( new HSAILInstructionSet.rem<StackReg_f32, f32>(currentFrame(), _i, stackReg_f32(_i), stackReg_f32(_i), stackReg_f32(_i, 1)));
       return(this);
    }
    public HSAILAssembler div_s64( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_s64(_i), stackReg_s64(_i,1)));
       return(this);
    }
    public HSAILAssembler div_s32( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_s32(_i), stackReg_s32(_i, 1)));
       return(this);
    }
    public HSAILAssembler div_f64( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_f64, f64>(currentFrame(), _i, stackReg_f64(_i), stackReg_f64(_i), stackReg_f64(_i, 1)));
       return(this);
    }
    public HSAILAssembler div_f32( Instruction _i){
        add( new HSAILInstructionSet.div<StackReg_f32, f32>(currentFrame(), _i, stackReg_f32(_i), stackReg_f32(_i), stackReg_f32(_i, 1)));
       return(this);
    }

    public <T extends StackReg> HSAILAssembler mul( Instruction _i, T _dest, T _lhs, T _rhs){
        add( new HSAILInstructionSet.mul(currentFrame(), _i, _dest, _lhs, _rhs));
       return(this);
    }

    public <T extends StackReg> HSAILAssembler sub( Instruction _i, T _dest, T _lhs, T _rhs){
        add(new HSAILInstructionSet.sub(currentFrame(), _i, _dest, _lhs, _rhs));
        return(this);
    }
    public <T extends StackReg> HSAILAssembler add( Instruction _i, T _dest, T _lhs, T _rhs){
        add( new HSAILInstructionSet.add(currentFrame(), _i, _dest, _lhs, _rhs));
       return(this);
    }

    public <T extends StackReg> HSAILAssembler mad( Instruction _i, T _dest, T _lhs, T _rhs, int _constant){
        add( new HSAILInstructionSet.mad(currentFrame(), _i, _dest, _lhs, _rhs, _constant));
        return(this);
    }
    public HSAILAssembler array_store_s16( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s16, s16>(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_s16(_i, 2)));
       return(this);
    }
    public HSAILAssembler array_store_u16( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_u16, u16>(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_u16(_i, 2)));
       return(this);
    }
    public HSAILAssembler array_store_s32( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s32, s32>(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_s32(_i, 2)));
       return(this);
    }
    public HSAILAssembler array_store_f32( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_f32, f32>(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_f32(_i, 2)));
       return(this);
    }
    public HSAILAssembler array_store_f64( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_f64, f64>(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_f64(_i, 2)));
       return(this);
    }
    public HSAILAssembler array_store_ref( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_ref, ref>(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_ref(_i, 2)));
       return(this);
    }
    public HSAILAssembler array_store_s8( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s8, s8>(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_s8(_i, 2)));
       return(this);
    }
    public HSAILAssembler array_store_s64( Instruction _i){
        add( new HSAILInstructionSet.array_store<StackReg_s64, s64>(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_s64(_i,2)));
       return(this);
    }
    public HSAILAssembler mad( Instruction _i, int _size){
       add( new HSAILInstructionSet.mad(currentFrame(), _i, stackReg_ref(_i, 1), stackReg_ref(_i, 1), stackReg_ref(_i), (long) _size));
       return(this);
    }
    public HSAILAssembler mov_var_ref( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_ref, StackReg_ref, ref, ref>(currentFrame(), _i, new VarReg_ref(_i, currentStackOffset()), stackReg_ref(_i)));
       return(this);
    }
    public HSAILAssembler mov_var_s32( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_s32, StackReg_s32, s32, s32>(currentFrame(), _i, new VarReg_s32(_i, currentStackOffset()), stackReg_s32(_i)));
       return(this);
    }
    public HSAILAssembler mov_var_f32( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_f32, StackReg_f32, f32, f32>(currentFrame(), _i, new VarReg_f32(_i, currentStackOffset()), stackReg_f32(_i)));
       return(this);
    }
    public HSAILAssembler mov_var_f64( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_f64, StackReg_f64, f64, f64>(currentFrame(), _i, new VarReg_f64(_i, currentStackOffset()), stackReg_f64(_i)));
       return(this);
    }
    public HSAILAssembler mov_var_s64( Instruction _i){
        add( new HSAILInstructionSet.mov<VarReg_s64, StackReg_s64, s64, s64>(currentFrame(), _i, new VarReg_s64(_i, currentStackOffset()), stackReg_s64(_i)));
       return(this);
    }

    public HSAILAssembler array_load_s32( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s32, s32>(currentFrame(), _i, stackReg_s32(_i), stackReg_ref(_i, 1)));
       return(this);
    }
    public HSAILAssembler array_load_f32( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_f32, f32>(currentFrame(), _i, stackReg_f32(_i), stackReg_ref(_i, 1)));
       return(this);
    }
    public HSAILAssembler array_load_u16( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_u16, u16>(currentFrame(), _i, stackReg_u16(_i), stackReg_ref(_i, 1)));
       return(this);
    }
    public HSAILAssembler array_load_s16( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s16, s16>(currentFrame(), _i, stackReg_s16(_i), stackReg_ref(_i, 1)));
       return(this);
    }
    public HSAILAssembler array_load_s64( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s64, s64>(currentFrame(), _i, stackReg_s64(_i), stackReg_ref(_i, 1)));
       return(this);
    }
    public HSAILAssembler array_load_f64( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_f64, f64>(currentFrame(), _i, stackReg_f64(_i), stackReg_ref(_i, 1)));
       return(this);
    }

    public HSAILAssembler array_load_s8( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_s8, s8>(currentFrame(), _i, stackReg_s8(_i), stackReg_ref(_i, 1)));
       return(this);
    }
    public HSAILAssembler array_load_ref( Instruction _i){
        add( new HSAILInstructionSet.array_load<StackReg_ref, ref>(currentFrame(), _i, stackReg_ref(_i), stackReg_ref(_i, 1)));
       return(this);
    }
    public HSAILAssembler mov_f64_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_f64, VarReg_f64, f64, f64>(currentFrame(), _i, stackReg_f64(_i), new VarReg_f64(_i, currentStackOffset())));
       return(this);
    }
    public HSAILAssembler mov_f32_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_f32, VarReg_f32, f32, f32>(currentFrame(), _i, stackReg_f32(_i), new VarReg_f32(_i, currentStackOffset())));
       return(this);
    }

    public HSAILAssembler mov_s64_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_s64, VarReg_s64, s64, s64>(currentFrame(), _i, stackReg_s64(_i), new VarReg_s64(_i, currentStackOffset())));
       return(this);
    }
    public HSAILAssembler mov_s32_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_s32, VarReg_s32, s32, s32>(currentFrame(), _i, stackReg_s32(_i), new VarReg_s32(_i, currentStackOffset())));
       return(this);
    }


    public HSAILAssembler mov_ref_var( Instruction _i){
        add( new HSAILInstructionSet.mov<StackReg_ref, VarReg_ref, ref, ref>(currentFrame(), _i, stackReg_ref(_i), new VarReg_ref(_i, currentStackOffset())));
       return(this);
    }
    public HSAILAssembler mov_s64_const( Instruction _i, long _value){
        add( new HSAILInstructionSet.mov_const<StackReg_s64, s64, Long>(currentFrame(), _i, stackReg_s64(_i), _value));
       return(this);
    }
    public HSAILAssembler mov_s32_const( Instruction _i, StackReg_s32 _dest, int _value){
        add( new HSAILInstructionSet.mov_const<StackReg_s32, s32, Integer>(currentFrame(), _i, _dest, _value));
       return(this);
    }

    public HSAILAssembler mov_f64_const( Instruction _i, double _value){
        add( new HSAILInstructionSet.mov_const<StackReg_f64, f64, Double>(currentFrame(), _i, stackReg_f64(_i), _value));
       return(this);
    }
    public HSAILAssembler mov_f32_const( Instruction _i, float _value){
        add( new HSAILInstructionSet.mov_const<StackReg_f32, f32, Float>(currentFrame(), _i, stackReg_f32(_i), _value));
       return(this);
    }
    public HSAILAssembler ld_arg_ref( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(currentFrame(), _i, new VarReg_ref(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_ref( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(currentFrame(), _i, new VarReg_ref(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_s32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(currentFrame(), _i, new VarReg_s32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_s32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(currentFrame(), _i, new VarReg_s32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_f32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(currentFrame(), _i, new VarReg_f32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_f32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(currentFrame(), _i, new VarReg_f32(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_f64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(currentFrame(), _i, new VarReg_f64(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_f64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(currentFrame(), _i, new VarReg_f64(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_arg_s64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_arg(currentFrame(), _i, new VarReg_s64(_varOffset)));
       return(this);
    }
    public HSAILAssembler ld_kernarg_s64( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.ld_kernarg(currentFrame(), _i, new VarReg_s64(_varOffset)));
       return(this);
    }
    public HSAILAssembler workitemabsid_u32( Instruction _i, int _varOffset){
        add( new HSAILInstructionSet.workitemabsid(currentFrame(), _i, new VarReg_s32(_varOffset)));
       return(this);
    }
    public HSAILAssembler gridsize_s32( Instruction _i, StackReg_s32 _dest){
        add( new HSAILInstructionSet.gridsize(currentFrame(), _i, _dest));
        return(this);
    }
    public HSAILAssembler countuplane_s32( Instruction _i, StackReg_s32 _dest){
        add( new HSAILInstructionSet.countuplane(currentFrame(), _i, _dest));
        return(this);
    }
    public HSAILAssembler masklane_s32( Instruction _i, StackReg_s32 _dest){
        add( new HSAILInstructionSet.masklane(currentFrame(), _i, _dest));
        return(this);
    }
    public HSAILAssembler laneid_s32( Instruction _i, StackReg_s32 _dest){
        add( new HSAILInstructionSet.laneid(currentFrame(), _i, _dest));
        return(this);
    }
    public HSAILAssembler cuid_s32( Instruction _i, StackReg_s32 _dest){
        add( new HSAILInstructionSet.cuid(currentFrame(), _i, _dest));
        return(this);
    }
    public HSAILAssembler clock_s64( Instruction _i, StackReg_s64 _dest){
        add( new HSAILInstructionSet.clock(currentFrame(), _i, _dest));
        return(this);
    }
    public HSAILAssembler workgroupid_s32( Instruction _i, StackReg_s32 _dest){
        add( new HSAILInstructionSet.workgroupid(currentFrame(), _i, _dest));
        return(this);
    }
    public HSAILAssembler workgroupsize_s32( Instruction _i, StackReg_s32 _dest){
        add( new HSAILInstructionSet.workgroupsize(currentFrame(), _i, _dest));
        return(this);
    }
    public HSAILAssembler currentworkgroupsize_s32( Instruction _i, StackReg_s32 _dest){
        add( new HSAILInstructionSet.currentworkgroupsize(currentFrame(), _i, _dest));
        return(this);
    }
    public HSAILAssembler workitemid_s32( Instruction _i, StackReg_s32 _dest){
        add( new HSAILInstructionSet.workitemid(currentFrame(), _i, _dest));
        return(this);
    }

    public HSAILAssembler nsqrt( Instruction _i, StackReg_f64 _reg){
       add( new HSAILInstructionSet.nsqrt(currentFrame(), _i, _reg));
        return(this);
    }
    public HSAILAssembler ncos( Instruction _i, StackReg_f64 _reg){
        add( new HSAILInstructionSet.ncos(currentFrame(), _i, _reg));
        return(this);
    }
    public HSAILAssembler nsin( Instruction _i, StackReg_f64 _reg){
        add( new HSAILInstructionSet.nsin(currentFrame(), _i, _reg));
        return(this);
    }

    public HSAILAssembler nyi( Instruction _i){
      add( new HSAILInstructionSet.nyi(currentFrame(), _i));
        return(this);
    }

    public HSAILAssembler array_len(Instruction _i){
       add( new HSAILInstructionSet.array_len(currentFrame(),_i, stackReg_s32(_i), stackReg_ref(_i)));
       return(this);
    }

    public HSAILAssembler nop( Instruction _i, String _label){
       add( new HSAILInstructionSet.nop(currentFrame(), _i, _label));
        return(this);
    }

    public HSAILAssembler barrier_fgroup( Instruction _i){
        add( new HSAILInstructionSet.barrier_fgroup(currentFrame(), _i));
        return(this);
    }
    public HSAILAssembler nop( Instruction _i){
        return(nop(_i, null));
    }
    public HSAILAssembler nopUniqueLabel( Instruction _i){
        return(nop(_i, currentFrame().getUniqueName()));
    }
    public <T extends StackReg>HSAILAssembler mov( Instruction _i, T _dest, T _source){

        add( new HSAILInstructionSet.mov(currentFrame(), _i, _dest, _source));
        return(this);
    }

    public HSAILAssembler returnBranchUniqueName( Instruction _i){
       add( new HSAILInstructionSet.returnBranch(currentFrame(), _i, currentFrame().getUniqueName()));
        return(this);
    }

    public HSAILAssembler cvt( Instruction _i, StackReg_u64 _dest, StackReg_s32 _source) {
        add(new HSAILInstructionSet.cvt(currentFrame(), _i, _dest, _source));
        return (this);
    }

    public <T extends StackReg> HSAILAssembler ld_global( Instruction _i,  T _dest, StackReg_ref _source, int _offset) {
        add(new HSAILInstructionSet.ld_global(currentFrame(), _i, _dest, _source, _offset));
        return(this);
    }

    public void addmov( Instruction _i, PrimitiveType _type, int _from, int _to) {
        if (_type.equals(PrimitiveType.ref) || _type.getHsaBits() == 32) {
            if (_type.equals(PrimitiveType.ref)) {
                mov(_i, stackReg_ref(_i, _to), stackReg_ref(_i, _from));
            } else if (_type.equals(PrimitiveType.s32)) {
                mov(_i, stackReg_s32(_i, _to), stackReg_s32(_i, _from));
            } else if (_type.equals(PrimitiveType.f32)) {

                mov(_i, stackReg_f32(_i, _to), stackReg_f32(_i, _from));
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
        HSAILRegister r = getRegOfLastWriteToIndex( stackIdx(_i) + _from);
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

        ParseState parseState = ParseState.NONE;

        boolean needsReturnLabel = false;
        for (Instruction i : method.getInstructions()) {

            switch (i.getByteCode()) {

                case ACONST_NULL:
                    nyi(i);
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
                    mov_s32_const(i, stackReg_s32(i), i.asIntegerConstant().getValue());
                    break;
                case LCONST_0:
                case LCONST_1:
                    mov_s64_const(i, i.asLongConstant().getValue());
                    break;
                case FCONST_0:
                case FCONST_1:
                case FCONST_2:
                    mov_f32_const(i, i.asFloatConstant().getValue());
                    break;
                case DCONST_0:
                case DCONST_1:
                    mov_f64_const(i, i.asDoubleConstant().getValue());
                    break;
                // case BIPUSH: moved up
                // case SIPUSH: moved up

                case LDC:
                case LDC_W:
                case LDC2_W: {
                    InstructionSet.ConstantPoolEntryConstant cpe = (InstructionSet.ConstantPoolEntryConstant) i;

                    ClassModel.ConstantPool.ConstantEntry e = (ClassModel.ConstantPool.ConstantEntry) cpe.getConstantPoolEntry();
                    if (e instanceof ClassModel.ConstantPool.DoubleEntry) {
                        mov_f64_const(i, ((ClassModel.ConstantPool.DoubleEntry) e).getValue());
                    } else if (e instanceof ClassModel.ConstantPool.FloatEntry) {
                        mov_f32_const(i, ((ClassModel.ConstantPool.FloatEntry) e).getValue());
                    } else if (e instanceof ClassModel.ConstantPool.IntegerEntry) {
                        mov_s32_const(i, stackReg_s32(i), ((ClassModel.ConstantPool.IntegerEntry) e).getValue());
                    } else if (e instanceof ClassModel.ConstantPool.LongEntry) {
                        mov_s64_const(i, ((ClassModel.ConstantPool.LongEntry) e).getValue());
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
                    mov_s32_var(i);

                    break;
                case LLOAD:
                case LLOAD_0:
                case LLOAD_1:
                case LLOAD_2:
                case LLOAD_3:
                    mov_s64_var(i);
                    break;
                case FLOAD:
                case FLOAD_0:
                case FLOAD_1:
                case FLOAD_2:
                case FLOAD_3:

                    mov_f32_var(i);
                    break;
                case DLOAD:
                case DLOAD_0:
                case DLOAD_1:
                case DLOAD_2:
                case DLOAD_3:

                    mov_f64_var(i);
                    break;
                case ALOAD:
                case ALOAD_0:
                case ALOAD_1:
                case ALOAD_2:
                case ALOAD_3:
                    mov_ref_var(i);

                    break;
                case IALOAD:
                    cvt_ref_s32_1(i).mad(i, PrimitiveType.s32.getHsaBytes()).array_load_s32(i);
                    break;
                case LALOAD:
                    cvt_ref_s32_1(i).mad(i, PrimitiveType.s64.getHsaBytes()).array_load_s64(i);
                    break;
                case FALOAD:
                    cvt_ref_s32_1(i).mad(i, PrimitiveType.f32.getHsaBytes()).array_load_f32(i);

                    break;
                case DALOAD:
                    cvt_ref_s32_1(i).mad(i, PrimitiveType.f64.getHsaBytes()).array_load_f64(i);

                    break;
                case AALOAD:
                    cvt_ref_s32_1(i).mad(i, PrimitiveType.ref.getHsaBytes()).array_load_ref(i);

                    break;
                case BALOAD:
                    cvt_ref_s32_1(i).mad(i, PrimitiveType.s8.getHsaBytes()).array_load_s8(i);

                    break;
                case CALOAD:
                    cvt_ref_s32_1(i).mad(i, PrimitiveType.u16.getHsaBytes()).array_load_u16(i);

                    break;
                case SALOAD:
                    cvt_ref_s32_1(i).mad(i, PrimitiveType.s16.getHsaBytes()).array_load_s16(i);
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
                    mov_var_s32(i);

                    break;
                case LSTORE:
                case LSTORE_0:
                case LSTORE_1:
                case LSTORE_2:
                case LSTORE_3:
                    mov_var_s64(i);

                    break;
                case FSTORE:
                case FSTORE_0:
                case FSTORE_1:
                case FSTORE_2:
                case FSTORE_3:
                    mov_var_f32(i);
                    break;
                case DSTORE:
                case DSTORE_0:
                case DSTORE_1:
                case DSTORE_2:
                case DSTORE_3:
                    mov_var_f64(i);
                    break;
                case ASTORE:
                case ASTORE_0:
                case ASTORE_1:
                case ASTORE_2:
                case ASTORE_3:
                    mov_var_ref(i);
                    break;
                case IASTORE:
                    cvt_ref_s32_1(i).mad(i, PrimitiveType.s32.getHsaBytes()).array_store_s32(i);
                    break;
                case LASTORE:
                    cvt_ref_s32_1(i).mad( i, PrimitiveType.s64.getHsaBytes()).array_store_s64(i);
                    break;
                case FASTORE:
                    cvt_ref_s32_1(i).mad( i, PrimitiveType.f32.getHsaBytes()).array_store_f32(i);

                    break;
                case DASTORE:
                    cvt_ref_s32_1(i).mad( i, PrimitiveType.f64.getHsaBytes()).array_store_f64(i);
                    break;
                case AASTORE:
                    cvt_ref_s32_1(i).mad( i, PrimitiveType.ref.getHsaBytes()).array_store_ref(i);
                    break;
                case BASTORE:
                    cvt_ref_s32_1(i).mad( i, PrimitiveType.s8.getHsaBytes()).array_store_s8(i);
                    break;
                case CASTORE:
                    cvt_ref_s32_1(i).mad( i, PrimitiveType.u16.getHsaBytes()).array_store_u16(i);
                    break;
                case SASTORE:
                    cvt_ref_s32_1(i).mad( i, PrimitiveType.s16.getHsaBytes()).array_store_s16(i);
                    break;
                case POP:
                    nyi(i);
                    break;
                case POP2:
                    nyi(i);
                    break;
                case DUP:
                    addmov(i, 0, 1);
                    break;
                case DUP_X1:
                    nyi(i);
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
                    nyi(i);
                    break;
                case DUP2_X2:
                    nyi(i);
                    break;
                case SWAP:
                    nyi(i);
                    break;
                case IADD:
                    add(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
                    break;
                case LADD:
                    add(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
                    break;
                case FADD:
                    add(i, stackReg_f32(i), stackReg_f32(i), stackReg_f32(i, 1));
                    break;
                case DADD:
                    add(i, stackReg_f64(i), stackReg_f64(i), stackReg_f64(i, 1));
                    break;
                case ISUB:
                    sub(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
                    break;
                case LSUB:
                    sub(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
                    break;
                case FSUB:
                    sub(i, stackReg_f32(i), stackReg_f32(i), stackReg_f32(i, 1));
                    break;
                case DSUB:
                    sub(i, stackReg_f64(i), stackReg_f64(i), stackReg_f64(i, 1));
                    break;
                case IMUL:
                    mul(i, stackReg_s32(i), stackReg_s32(i), stackReg_s32(i, 1));
                    break;
                case LMUL:
                    mul(i, stackReg_s64(i), stackReg_s64(i), stackReg_s64(i, 1));
                    break;
                case FMUL:
                    mul(i, stackReg_f32(i), stackReg_f32(i), stackReg_f32(i, 1));
                    break;
                case DMUL:
                    mul(i, stackReg_f64(i), stackReg_f64(i), stackReg_f64(i, 1));
                    break;
                case IDIV:
                    div_s32(i);
                    break;
                case LDIV:
                    div_s64(i);
                    break;
                case FDIV:
                    div_f32(i);
                    break;
                case DDIV:
                    div_f64(i);
                    break;
                case IREM:
                    rem_s32(i);
                    break;
                case LREM:
                    rem_s64(i);
                    break;
                case FREM:
                    rem_f32(i);
                    break;
                case DREM:
                    rem_f64(i);
                    break;
                case INEG:
                    neg_s32(i);
                    break;
                case LNEG:
                    neg_s64(i);
                    break;
                case FNEG:
                    neg_f32(i);
                    break;
                case DNEG:
                    neg_f64(i);
                    break;
                case ISHL:
                    shl_s32(i);
                    break;
                case LSHL:
                    shl_s64(i);
                    break;
                case ISHR:
                    shr_s32(i);
                    break;
                case LSHR:
                    shr_s64(i);
                    break;
                case IUSHR:
                    ushr_s32(i);
                    break;
                case LUSHR:
                    ushr_s64(i);
                    break;
                case IAND:
                    and_s32(i);
                    break;
                case LAND:
                    and_s64(i);
                    break;
                case IOR:
                    or_s32(i);
                    break;
                case LOR:
                    or_s64(i);
                    break;
                case IXOR:
                    xor_s32(i);
                    break;
                case LXOR:
                    xor_s64(i);
                    break;
                case IINC:
                    add_const_s32(i);
                    break;
                case I2L:
                    cvt_s64_s32(i);
                    break;
                case I2F:
                    cvt_f32_s32(i);
                    break;
                case I2D:
                    cvt_f64_s32(i);
                    break;
                case L2I:
                    cvt_s32_s64(i);
                    break;
                case L2F:
                    cvt_f32_s64(i);
                    break;
                case L2D:
                    cvt_f64_s64(i);
                    break;
                case F2I:
                    cvt_s32_f32(i);
                    break;
                case F2L:
                    cvt_s64_f32(i);
                    break;
                case F2D:
                    cvt_f64_f32(i);
                    break;
                case D2I:
                    cvt_s32_f64(i);
                    break;
                case D2L:
                    cvt_s64_f64(i);
                    break;
                case D2F:
                    cvt_f32_f64(i);
                    break;
                case I2B:
                    cvt_s8_s32(i);
                    break;
                case I2C:
                    cvt_u16_s32(i);
                    break;
                case I2S:
                    cvt_s16_s32(i);
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
                        cmp_f32_eq(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        cmp_f64_eq(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        cmp_s64_eq(i);
                        parseState = ParseState.NONE;
                    } else {
                        cmp_s32_eq_const_0(i);
                    }
                    cbr(i);
                    break;
                case IFNE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        cmp_f32_ne(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        cmp_f64_ne(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        cmp_s64_ne(i);
                        parseState = ParseState.NONE;
                    } else {
                        cmp_s32_ne_const_0(i);
                    }
                    cbr(i);
                    break;
                case IFLT:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        cmp_f32_lt(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        cmp_f64_lt(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        cmp_s64_lt(i);
                        parseState = ParseState.NONE;
                    } else {
                        cmp_s32_lt_const_0(i);

                    }
                    cbr(i);
                    break;
                case IFGE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        cmp_f32_ge(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        cmp_f64_ge(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        cmp_s64_ge(i);
                        parseState = ParseState.NONE;
                    } else {
                        cmp_s32_ge_const_0(i);

                    }
                    cbr(i);
                    break;
                case IFGT:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        cmp_f32_gt(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        cmp_f64_gt(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        cmp_s64_gt(i);
                        parseState = ParseState.NONE;
                    } else {
                        cmp_s32_gt_const_0(i);

                    }
                    cbr(i);
                    break;
                case IFLE:
                    if (parseState.equals(ParseState.COMPARE_F32)) {
                        cmp_f32_le(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_F64)) {
                        cmp_f64_le(i);
                        parseState = ParseState.NONE;
                    } else if (parseState.equals(ParseState.COMPARE_S64)) {
                        cmp_s64_le(i);
                        parseState = ParseState.NONE;
                    } else {
                        cmp_s32_le_const_0(i);


                    }
                    cbr(i);
                    break;
                case IF_ICMPEQ:

                    cmp_s32_eq(i).cbr(i);

                    break;
                case IF_ICMPNE:
                    cmp_s32_ne(i).cbr(i);
                    break;
                case IF_ICMPLT:
                    cmp_s32_lt(i).cbr(i);
                    break;
                case IF_ICMPGE:
                    cmp_s32_ge(i).cbr(i);
                    break;
                case IF_ICMPGT:
                    cmp_s32_gt(i).cbr(i);
                    break;
                case IF_ICMPLE:
                    cmp_s32_le(i).cbr(i);
                    break;
                case IF_ACMPEQ:
                    cmp_ref_eq(i).cbr(i);
                    break;
                case IF_ACMPNE:
                    cmp_ref_ne(i).cbr(i);
                    break;
                case GOTO:
                    brn(i);
                    break;
                case IFNULL:
                    branch(i);
                case IFNONNULL:
                    branch(i);
                case GOTO_W:
                    branch(i);
                    break;
                case JSR:
                    nyi(i);
                    break;
                case RET:
                    nyi(i);
                    break;
                case TABLESWITCH:
                    nyi(i);
                    break;
                case LOOKUPSWITCH:
                    nyi(i);
                    break;
                case IRETURN:
                case LRETURN:
                case FRETURN:
                case DRETURN:
                case ARETURN:
                    if (frames.size()>1){
                        int maxLocals=i.getMethod().getCodeEntry().getMaxLocals(); // hsailStackFrame.stackOffset -maxLocals is the slot for the return value

                        switch(i.getByteCode()){
                            case IRETURN: mov(i, stackReg_s32(i, -maxLocals), stackReg_s32(i));break;
                            case LRETURN: mov(i, stackReg_s64(i, -maxLocals), stackReg_s64(i));break;
                            case FRETURN: mov(i, stackReg_f32(i, -maxLocals), stackReg_f32(i));break;
                            case DRETURN: mov(i, stackReg_f64(i, -maxLocals), stackReg_f64(i));break;
                            case ARETURN: mov(i, stackReg_ref(i, -maxLocals), stackReg_ref(i));break;
                        }
                        if (i.isLastInstruction()){
                            if (needsReturnLabel){
                                nopUniqueLabel(i);
                            }
                        }else{
                            returnBranchUniqueName(i);

                            needsReturnLabel=true;
                        }
                    }else{
                        switch(i.getByteCode()){
                            case IRETURN:  ret_s32(i);break;
                            case LRETURN:  ret_s64(i);break;
                            case FRETURN:  ret_f32(i);break;
                            case DRETURN:  ret_s64(i);break;
                            case ARETURN:  ret_ref(i);break;

                        }

                    }
                    break;
                case RETURN:
                    if (frames.size()>1){
                        if (i.getNextPC()!=null){
                            returnBranchUniqueName(i);
                            needsReturnLabel=true;
                        }else{
                            if (i.isBranchTarget()){
                                nop(i);

                            }else if (needsReturnLabel){
                                nopUniqueLabel(i);
                            }
                        }
                    }else{
                        ret_void(i);
                    }
                    break;
                case GETSTATIC: {
                    TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

                    try {
                        Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                        Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());

                        if (!type.isPrimitive()) {
                            static_field_load_ref(i, f);
                        } else if (type.isInt()) {
                            static_field_load_s32(i, f);
                        } else if (type.isFloat()) {
                            static_field_load_f32(i, f);
                        } else if (type.isDouble()) {
                            static_field_load_f64(i, f);
                        } else if (type.isLong()) {
                            static_field_load_s64(i, f);
                        } else if (type.isChar()) {
                            static_field_load_u16(i, f);
                        } else if (type.isShort()) {
                            static_field_load_s16(i, f);
                        } else if (type.isChar()) {
                            static_field_load_s8(i, f);
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
                            field_load_ref(i, f);
                        } else if (f.getType().equals(int.class)) {
                            field_load_s32(i, f);
                        } else if (f.getType().equals(short.class)) {
                            field_load_s16(i, f);
                        } else if (f.getType().equals(char.class)) {
                            field_load_u16(i, f);
                        } else if (f.getType().equals(boolean.class)) {
                            field_load_s8(i, f);
                        } else if (f.getType().equals(float.class)) {
                            field_load_f32(i, f);
                        } else if (f.getType().equals(double.class)) {
                            field_load_f64(i, f);
                        } else if (f.getType().equals(long.class)) {
                            field_load_s64(i, f);
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
                    nyi(i);
                    break;
                case PUTFIELD: {
                    // TypeHelper.JavaType type = i.asFieldAccessor().getConstantPoolFieldEntry().getType();

                    try {
                        Class clazz = Class.forName(i.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());

                        Field f = clazz.getDeclaredField(i.asFieldAccessor().getFieldName());
                        if (!f.getType().isPrimitive()) {
                            field_store_ref(i, f);
                        } else if (f.getType().equals(int.class)) {
                            field_store_s32(i, f);
                        } else if (f.getType().equals(short.class)) {
                            field_store_s16(i, f);
                        } else if (f.getType().equals(char.class)) {
                            field_store_u16(i, f);
                        } else if (f.getType().equals(boolean.class)) {
                            field_store_s8(i, f);
                        } else if (f.getType().equals(float.class)) {
                            field_store_f32(i, f);
                        } else if (f.getType().equals(double.class)) {
                            field_store_f64(i, f);
                        } else if (f.getType().equals(long.class)) {
                            field_store_s64(i, f);
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
                            HSAILStackFrame newFrame = new HSAILStackFrame(currentFrame(),  calledMethod, i.getThisPC(), i.getPreStackBase()+i.getMethod().getCodeEntry().getMaxLocals()+currentStackOffset());
                            frames.push(newFrame);
                            frameSet.add(newFrame);
                            addInstructions(calledMethod);
                            frames.pop();
                        }catch (ClassParseException cpe){

                        }catch (ClassNotFoundException cnf){

                        }




                    }
                }
                break;
                case NEW:
                    nyi(i);
                    break;
                case NEWARRAY:
                    nyi(i);
                    break;
                case ANEWARRAY:
                    nyi(i);
                    break;
                case ARRAYLENGTH:
                    array_len(i);

                    break;
                case ATHROW:
                    nyi(i);
                    break;
                case CHECKCAST:
                    nyi(i);
                    break;
                case INSTANCEOF:
                    nyi(i);
                    break;
                case MONITORENTER:
                    nyi(i);
                    break;
                case MONITOREXIT:
                    nyi(i);
                    break;
                case WIDE:
                    nyi(i);
                    break;
                case MULTIANEWARRAY:
                    nyi(i);
                    break;
                case JSR_W:
                    nyi(i);
                    break;

            }
        }

    }
    
    int stackIdx(Instruction _i){
        return(currentFrame().stackIdx(_i));
    }
    HSAILStackFrame currentFrame(){
        return(frames.peek());
    }
    List<HSAILInstruction> getInstructions(){
        return(instructions);
    }
    List<HSAILStackFrame> getFrameSet(){
        return(frameSet);
    }
    int currentStackOffset(){
        return(currentFrame().stackOffset);
    }
    StackReg_u64 stackReg_u64(int _slot){
        return(new StackReg_u64(_slot));
    }
    StackReg_u64 stackReg_u64(Instruction _i, int _offset){
        return(stackReg_u64(stackIdx(_i) + _offset));
    }
    StackReg_u64 stackReg_u64(Instruction _i){
        return(stackReg_u64(_i, 0));
    }
    StackReg_s64 stackReg_s64(int _slot){
        return(new StackReg_s64(_slot));
    }
    StackReg_s64 stackReg_s64(Instruction _i, int _offset){
        return(stackReg_s64(stackIdx(_i)+_offset));
    }
    StackReg_s64 stackReg_s64(Instruction _i){
        return(stackReg_s64(_i, 0));
    }
    StackReg_f64 stackReg_f64(int _slot){
        return(new StackReg_f64(_slot));
    }
    StackReg_f64 stackReg_f64(Instruction _i, int _offset){
        return(stackReg_f64(stackIdx(_i) + _offset));
    }
    StackReg_f64 stackReg_f64(Instruction _i){
        return(stackReg_f64(_i, 0));
    }
    StackReg_s32 stackReg_s32(int _slot){
        return(new StackReg_s32(_slot));
    }
    StackReg_s32 stackReg_s32(Instruction _i, int _offset){
        return(stackReg_s32(stackIdx(_i) + _offset));
    }
    StackReg_s32 stackReg_s32(Instruction _i){
        return(stackReg_s32(_i, 0));
    }
    StackReg_f32 stackReg_f32(int _slot){
        return(new StackReg_f32(_slot));
    }
    StackReg_f32 stackReg_f32(Instruction _i, int _offset){
        return(stackReg_f32(stackIdx(_i) + _offset));
    }
    StackReg_f32 stackReg_f32(Instruction _i){
        return(stackReg_f32(_i, 0));
    }

    StackReg_s16 stackReg_s16(int _slot){
        return(new StackReg_s16(_slot));
    }
    StackReg_s16 stackReg_s16(Instruction _i, int _offset){
        return(stackReg_s16(stackIdx(_i) + _offset));
    }
    StackReg_s16 stackReg_s16(Instruction _i){
        return(stackReg_s16(_i, 0));
    }

    StackReg_u16 stackReg_u16(int _slot){
        return(new StackReg_u16(_slot));
    }
    StackReg_u16 stackReg_u16(Instruction _i, int _offset){
        return(stackReg_u16(stackIdx(_i) + _offset));
    }
    StackReg_u16 stackReg_u16(Instruction _i){
        return(stackReg_u16(_i, 0));
    }

    StackReg_s8 stackReg_s8(int _slot){
        return(new StackReg_s8(_slot));
    }
    StackReg_s8 stackReg_s8(Instruction _i, int _offset){
        return(stackReg_s8(stackIdx(_i) + _offset));
    }
    StackReg_s8 stackReg_s8(Instruction _i){
        return(stackReg_s8(_i, 0));
    }


    StackReg_ref stackReg_ref(int _slot){
        return(new StackReg_ref(_slot));
    }
    StackReg_ref stackReg_ref(Instruction _i, int _offset){
        return(stackReg_ref(stackIdx(_i) + _offset));
    }
    StackReg_ref stackReg_ref(Instruction _i){
        return(stackReg_ref(_i, 0));
    }
}

