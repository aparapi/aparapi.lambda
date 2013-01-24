#ifndef INSTRUCTION_H
#define INSTRUCTION_H

#include "classtools.h"

enum ByteCodeType{
   I_NOP,
   I_ACONST_NULL,
   I_ICONST_M1,
   I_ICONST_0,
   I_ICONST_1,
   I_ICONST_2,
   I_ICONST_3,
   I_ICONST_4,
   I_ICONST_5,
   I_LCONST_0,
   I_LCONST_1,
   I_FCONST_0,
   I_FCONST_1,
   I_FCONST_2,
   I_DCONST_0,
   I_DCONST_1,
   I_BIPUSH,
   I_SIPUSH,
   I_LDC,
   I_LDC_W,
   I_LDC2_W,
   I_ILOAD,
   I_LLOAD,
   I_FLOAD,
   I_DLOAD,
   I_ALOAD,
   I_ILOAD_0,
   I_ILOAD_1,
   I_ILOAD_2,
   I_ILOAD_3,
   I_LLOAD_0,
   I_LLOAD_1,
   I_LLOAD_2,
   I_LLOAD_3,
   I_FLOAD_0,
   I_FLOAD_1,
   I_FLOAD_2,
   I_FLOAD_3,
   I_DLOAD_0,
   I_DLOAD_1,
   I_DLOAD_2,
   I_DLOAD_3,
   I_ALOAD_0,
   I_ALOAD_1,
   I_ALOAD_2,
   I_ALOAD_3,
   I_IALOAD,
   I_LALOAD,
   I_FALOAD,
   I_DALOAD,
   I_AALOAD,
   I_BALOAD,
   I_CALOAD,
   I_SALOAD,
   I_ISTORE,
   I_LSTORE,
   I_FSTORE,
   I_DSTORE,
   I_ASTORE,
   I_ISTORE_0,
   I_ISTORE_1,
   I_ISTORE_2,
   I_ISTORE_3,
   I_LSTORE_0,
   I_LSTORE_1,
   I_LSTORE_2,
   I_LSTORE_3,
   I_FSTORE_0,
   I_FSTORE_1,
   I_FSTORE_2,
   I_FSTORE_3,
   I_DSTORE_0,
   I_DSTORE_1,
   I_DSTORE_2,
   I_DSTORE_3,
   I_ASTORE_0,
   I_ASTORE_1,
   I_ASTORE_2,
   I_ASTORE_3,
   I_IASTORE,
   I_LASTORE,
   I_FASTORE,
   I_DASTORE,
   I_AASTORE,
   I_BASTORE,
   I_CASTORE,
   I_SASTORE,
   I_POP,
   I_POP2,
   I_DUP,
   I_DUP_X1,
   I_DUP_X2,
   I_DUP2,
   I_DUP2_X1,
   I_DUP2_X2,
   I_SWAP,
   I_IADD,
   I_LADD,
   I_FADD,
   I_DADD,
   I_ISUB,
   I_LSUB,
   I_FSUB,
   I_DSUB,
   I_IMUL,
   I_LMUL,
   I_FMUL,
   I_DMUL,
   I_IDIV,
   I_LDIV,
   I_FDIV,
   I_DDIV,
   I_IREM,
   I_LREM,
   I_FREM,
   I_DREM,
   I_INEG,
   I_LNEG,
   I_FNEG,
   I_DNEG,
   I_ISHL,
   I_LSHL,
   I_ISHR,
   I_LSHR,
   I_IUSHR,
   I_LUSHR,
   I_IAND,
   I_LAND,
   I_IOR,
   I_LOR,
   I_IXOR,
   I_LXOR,
   I_IINC,
   I_I2L,
   I_I2F,
   I_I2D,
   I_L2I,
   I_L2F,
   I_L2D,
   I_F2I,
   I_F2L,
   I_F2D,
   I_D2I,
   I_D2L,
   I_D2F,
   I_I2B,
   I_I2C,
   I_I2S,
   I_LCMP,
   I_FCMPL,
   I_FCMPG,
   I_DCMPL,
   I_DCMPG,
   I_IFEQ,
   I_IFNE,
   I_IFLT,
   I_IFGE,
   I_IFGT,
   I_IFLE,
   I_IF_ICMPEQ,
   I_IF_ICMPNE,
   I_IF_ICMPLT,
   I_IF_ICMPGE,
   I_IF_ICMPGT,
   I_IF_ICMPLE,
   I_IF_ACMPEQ,
   I_IF_ACMPNE,
   I_GOTO,
   I_JSR,
   I_RET,
   I_TABLESWITCH,
   I_LOOKUPSWITCH,
   I_IRETURN,
   I_LRETURN,
   I_FRETURN,
   I_DRETURN,
   I_ARETURN,
   I_RETURN,
   I_GETSTATIC,
   I_PUTSTATIC,
   I_GETFIELD,
   I_PUTFIELD,
   I_INVOKEVIRTUAL,
   I_INVOKESPECIAL,
   I_INVOKESTATIC,
   I_INVOKEINTERFACE,
   I_INVOKEDYNAMIC,
   I_NEW,
   I_NEWARRAY,
   I_ANEWARRAY,
   I_ARRAYLENGTH,
   I_ATHROW,
   I_CHECKCAST,
   I_INSTANCEOF,
   I_MONITORENTER,
   I_MONITOREXIT,
   I_WIDE,
   I_MULTIANEWARRAY,
   I_IFNULL,
   I_IFNONNULL,
   I_GOTO_W,
   I_JSR_W
};


#define BITS_B  0x1
#define BITS_C  0x2
#define BITS_S  0x3
#define BITS_I  0x4
#define BITS_F  0x5
#define BITS_L  0x6
#define BITS_D  0x7
#define BITS_O  0x8
#define BITS_A  0x9
#define BITS_UN 0xA

#define LDSpec_NONE                    0x0
#define LDSpec_I                       BITS_I
#define LDSpec_L                       BITS_L
#define LDSpec_A                       BITS_A
#define LDSpec_F                       BITS_F
#define LDSpec_D                       BITS_D

enum ImmSpecType{
   ImmSpec_NONE,
   ImmSpec_Blvti,
   ImmSpec_Bcpci,
   ImmSpec_Scpci,
   ImmSpec_Bconst,
   ImmSpec_Sconst,
   ImmSpec_IorForS,
   ImmSpec_Spc,
   ImmSpec_Scpfi,
   ImmSpec_ScpmiBB,
   ImmSpec_BlvtiBconst,
   ImmSpec_Scpmi,
   ImmSpec_ScpciBdim,
   ImmSpec_Ipc,
   ImmSpec_UNKNOWN
};

#define STSpec_NONE                    0x00
#define STSpec_L                       BITS_L
#define STSpec_F                       BITS_F
#define STSpec_D                       BITS_D
#define STSpec_I                       BITS_I
#define STSpec_A                       BITS_A

enum PushSpecType{
PushSpec_NONE,
PushSpec_N,
PushSpec_I,
PushSpec_L,
PushSpec_F,
PushSpec_D,
PushSpec_O,
PushSpec_A,
PushSpec_RA,
PushSpec_IorForS,
PushSpec_LorD,
PushSpec_II,
PushSpec_III,
PushSpec_IIII,
PushSpec_IIIII,
PushSpec_IIIIII,
PushSpec_UNKNOWN
};

#define PopSpec_NONE                   0x000
#define PopSpec_COUNT_MASK             0x007
#define PopSpec_A                      0x011
#define PopSpec_AI                     0x022
#define PopSpec_AII                    0x033
#define PopSpec_AIL                    0x043
#define PopSpec_AIF                    0x053
#define PopSpec_AID                    0x063
#define PopSpec_AIO                    0x073
#define PopSpec_AIB                    0x083
#define PopSpec_AIC                    0x093
#define PopSpec_AIS                    0x0A3
#define PopSpec_II                     0x0B2
#define PopSpec_III                    0x0C3
#define PopSpec_IIII                   0x0D4
#define PopSpec_L                      0x0E1
#define PopSpec_LI                     0x0F2
#define PopSpec_LL                     0x102
#define PopSpec_F                      0x111
#define PopSpec_FF                     0x122
#define PopSpec_OO                     0x132
#define PopSpec_RA                     0x142
#define PopSpec_O                      0x151
#define PopSpec_I                      0x161
#define PopSpec_D                      0x171
#define PopSpec_DD                     0x182
#define PopSpec_OUNKNOWN               0x192
#define PopSpec_UNKNOWN                0x1A1
#define PopSpec_ARGS                   0x1B0
#define PopSpec_OARGS                  0x1C0


#define OpSpec_NONE                    0x000
#define OpSpec_CAST                    0x0040
#define OpSpec_ARITHMETIC              0x0080
#define OpSpec_BINARY                  0x0100
#define OpSpec_UNARY                   0x0200
#define OpSpec_BITWISE                 0x0400
#define OpSpec_COMPARE                 0x0800
#define OpSpec_Add                     0x0001 | OpSpec_ARITHMETIC  | OpSpec_BINARY
#define OpSpec_Sub                     0x0002 | OpSpec_ARITHMETIC | OpSpec_BINARY
#define OpSpec_Mul                     0x0003 | OpSpec_ARITHMETIC | OpSpec_BINARY
#define OpSpec_Div                     0x0004 | OpSpec_ARITHMETIC | OpSpec_BINARY
#define OpSpec_Rem                     0x0005 | OpSpec_ARITHMETIC | OpSpec_BINARY
#define OpSpec_Neg                     0x0006 | OpSpec_ARITHMETIC | OpSpec_UNARY
#define OpSpec_BitwiseOr               0x0007 | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_BitwiseAnd              0x0008 | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_BitwiseXor              0x0009 | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_LeftShift               0x000A | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_LogicalRightShift       0x000B | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_ArithmeticRightShift    0x000C | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_F2ICast                 0x000D | OpSpec_CAST 
#define OpSpec_F2LCast                 0x000E | OpSpec_CAST
#define OpSpec_F2DCast                 0x000F | OpSpec_CAST
#define OpSpec_D2ICast                 0x0010 | OpSpec_CAST
#define OpSpec_D2FCast                 0x0011 | OpSpec_CAST
#define OpSpec_D2LCast                 0x0012 | OpSpec_CAST
#define OpSpec_I2SCast                 0x0013 | OpSpec_CAST
#define OpSpec_I2BCast                 0x0014 | OpSpec_CAST
#define OpSpec_I2CCast                 0x0015 | OpSpec_CAST
#define OpSpec_I2LCast                 0x0016 | OpSpec_CAST
#define OpSpec_I2FCast                 0x0017 | OpSpec_CAST
#define OpSpec_I2DCast                 0x0018 | OpSpec_CAST
#define OpSpec_L2ICast                 0x0019 | OpSpec_CAST
#define OpSpec_L2FCast                 0x001A | OpSpec_CAST
#define OpSpec_L2DCast                 0x001B | OpSpec_CAST
#define OpSpec_GreaterThan             0x001C | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_GreaterThanOrEqual      0x001E | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_LessThan                0x001F | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_LessThanOrEqual         0x0020 | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_Equal                   0x0021 | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_EqualNULL               0x0022 | OpSpec_COMPARE | OpSpec_UNARY
#define OpSpec_NotEqual                0x0023 | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_NotEqualNULL            0x0024 | OpSpec_COMPARE | OpSpec_UNARY

struct ImmSpec_NONE_s{
};
struct ImmSpec_Blvti_s{
   u1_t lvti;
};
struct ImmSpec_Bcpci_s{
   u1_t cpci;
};
struct ImmSpec_Scpci_s{
   u2_t cpci;
};
struct ImmSpec_Bconst_s{
   u1_t value;
};
struct ImmSpec_Sconst_s{
   s2_t value;
};
struct ImmSpec_IorForS_s{
   union{
      s4_t i;
      f4_t f;
      s4_t s;
   };
};
struct ImmSpec_Spc_s{
   u2_t pc;
};
struct ImmSpec_Scpfi_s{
   u2_t cpfi;
};
struct ImmSpec_ScpmiBB_s{
   u2_t cpmi;
   u1_t b1;
   u1_t b2;
};
struct ImmSpec_BlvtiBconst_s{
   u1_t lvti;
   u1_t value;
};
struct ImmSpec_Scpmi_s{
   u2_t cpmi;
};
struct ImmSpec_ScpciBdim_s{
   u2_t cpci;
   u1_t dim;
};
struct ImmSpec_Ipc_s{
   s4_t pc;
};
struct ImmSpec_UNKNOWN_s{
};

class ByteCode{
   public:
      ByteCodeType bytecode;
      const char *name;
      u2_t ldSpec;
      u2_t stSpec;
      ImmSpecType immSpec;
      u4_t popSpec;
      PushSpecType pushSpec;
      u4_t opSpec;
};


#ifdef INSTRUCTION_CPP
ByteCode bytecode[] ={
   {I_NOP, "nop", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE}, 
   {I_ACONST_NULL, "aconst_null", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_N, OpSpec_NONE},
   {I_ICONST_M1, "iconst_m1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_0, "iconst_0", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_1, "iconst_1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_2, "iconst_2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_3, "iconst_3", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_4, "iconst_4", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_5, "iconst_5", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_LCONST_0, "lconst_0", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_LCONST_1, "lconst_1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_FCONST_0, "fconst_0", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_FCONST_1, "fconst_1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_FCONST_2, "fconst_2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_DCONST_0, "dconst_0", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_DCONST_1, "dconst_1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_BIPUSH, "bipush", LDSpec_NONE, STSpec_NONE, ImmSpec_Bconst, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_SIPUSH, "sipush", LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_LDC, "ldc", LDSpec_NONE, STSpec_NONE, ImmSpec_Bcpci, PopSpec_NONE, PushSpec_IorForS, OpSpec_NONE},
   {I_LDC_W, "ldc_w", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_IorForS, OpSpec_NONE},
   {I_LDC2_W, "ldc2_w", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_LorD, OpSpec_NONE},
   {I_ILOAD, "iload", LDSpec_I, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_LLOAD, "lload", LDSpec_L, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_FLOAD, "fload", LDSpec_F, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_DLOAD, "dload", LDSpec_F, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_ALOAD, "aload", LDSpec_A, STSpec_NONE, ImmSpec_Blvti, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_ILOAD_0, "iload_0", LDSpec_I, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ILOAD_1, "iload_1", LDSpec_I, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ILOAD_2, "iload_2", LDSpec_I, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ILOAD_3, "iload_3", LDSpec_I, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_LLOAD_0, "lload_0", LDSpec_L, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_LLOAD_1, "lload_1", LDSpec_L, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_LLOAD_2, "lload_2", LDSpec_L, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_LLOAD_3, "lload_3", LDSpec_L, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_FLOAD_0, "fload_0", LDSpec_F, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_FLOAD_1, "fload_1", LDSpec_F, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_FLOAD_2, "fload_2", LDSpec_F, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_FLOAD_3, "fload_3", LDSpec_F, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_DLOAD_0, "dload_0", LDSpec_D, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_DLOAD_1, "dload_1", LDSpec_D, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_DLOAD_2, "dload_2", LDSpec_D, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_DLOAD_3, "dload_3", LDSpec_D, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_ALOAD_0, "aload_0", LDSpec_A, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_ALOAD_1, "aload_1", LDSpec_A, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_ALOAD_2, "aload_2", LDSpec_A, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_ALOAD_3, "aload_3", LDSpec_A, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_IALOAD, "iaload", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
   {I_LALOAD, "laload", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_L, OpSpec_NONE},
   {I_FALOAD, "faload", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_F, OpSpec_NONE},
   {I_DALOAD, "daload", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_D, OpSpec_NONE},
   {I_AALOAD, "aaload", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_A, OpSpec_NONE},
   {I_BALOAD, "baload", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
   {I_CALOAD, "caload", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
   {I_SALOAD, "saload", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
   {I_ISTORE, "istore", LDSpec_NONE, STSpec_I, ImmSpec_Blvti, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_LSTORE, "lstore", LDSpec_NONE, STSpec_L, ImmSpec_Blvti, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_FSTORE, "fstore", LDSpec_NONE, STSpec_F, ImmSpec_Blvti, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_DSTORE, "dstore", LDSpec_NONE, STSpec_D, ImmSpec_Blvti, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_ASTORE, "astore", LDSpec_NONE, STSpec_A, ImmSpec_Blvti, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_ISTORE_0, "istore_0", LDSpec_NONE, STSpec_I, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_ISTORE_1, "istore_1", LDSpec_NONE, STSpec_I, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_ISTORE_2, "istore_2", LDSpec_NONE, STSpec_I, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_ISTORE_3, "istore_3", LDSpec_NONE, STSpec_I, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_LSTORE_0, "lstore_0", LDSpec_NONE, STSpec_L, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_LSTORE_1, "lstore_1", LDSpec_NONE, STSpec_L, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_LSTORE_2, "lstore_2", LDSpec_NONE, STSpec_L, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_LSTORE_3, "lstore_3", LDSpec_NONE, STSpec_L, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_FSTORE_0, "fstore_0", LDSpec_NONE, STSpec_F, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_FSTORE_1, "fstore_1", LDSpec_NONE, STSpec_F, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_FSTORE_2, "fstore_2", LDSpec_NONE, STSpec_F, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_FSTORE_3, "fstore_3", LDSpec_NONE, STSpec_F, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_DSTORE_0, "dstore_0", LDSpec_NONE, STSpec_D, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_DSTORE_1, "dstore_1", LDSpec_NONE, STSpec_D, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_DSTORE_2, "dstore_2", LDSpec_NONE, STSpec_D, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_DSTORE_3, "dstore_3", LDSpec_NONE, STSpec_D, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_ASTORE_0, "astore_0", LDSpec_NONE, STSpec_A, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_ASTORE_1, "astore_1", LDSpec_NONE, STSpec_A, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_ASTORE_2, "astore_2", LDSpec_NONE, STSpec_A, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_ASTORE_3, "astore_3", LDSpec_NONE, STSpec_A, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_IASTORE, "iastore", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AII, PushSpec_NONE, OpSpec_NONE},
   {I_LASTORE, "lastore", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIL, PushSpec_NONE, OpSpec_NONE},
   {I_FASTORE, "fastore", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIF, PushSpec_NONE, OpSpec_NONE},
   {I_DASTORE, "dastore", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AID, PushSpec_NONE, OpSpec_NONE},
   {I_AASTORE, "aastore", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIO, PushSpec_NONE, OpSpec_NONE},
   {I_BASTORE, "bastore", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIB, PushSpec_NONE, OpSpec_NONE},
   {I_CASTORE, "castore", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIC, PushSpec_NONE, OpSpec_NONE},
   {I_SASTORE, "sastore", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIS, PushSpec_NONE, OpSpec_NONE},
   {I_POP, "pop", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_POP2, "pop2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_NONE, OpSpec_NONE},
   {I_DUP, "dup", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_II, OpSpec_NONE},
   {I_DUP_X1, "dup_x1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_III, OpSpec_NONE},
   {I_DUP_X2, "dup_x2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_III, PushSpec_IIII, OpSpec_NONE},
   {I_DUP2, "dup2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_IIII, OpSpec_NONE},
   {I_DUP2_X1, "dup2_x1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_III, PushSpec_IIIII, OpSpec_NONE},
   {I_DUP2_X2, "dup2_x2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_IIII, PushSpec_IIIIII, OpSpec_NONE},
   {I_SWAP, "swap", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_II, OpSpec_NONE},
   {I_IADD, "iadd", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Add},
   {I_LADD, "ladd", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Add},
   {I_FADD, "fadd", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Add},
   {I_DADD, "dadd", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Add},
   {I_ISUB, "isub", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Sub},
   {I_LSUB, "lsub", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Sub},
   {I_FSUB, "fsub", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Sub},
   {I_DSUB, "dsub", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Sub},
   {I_IMUL, "imul", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Mul},
   {I_LMUL, "lmul", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Mul},
   {I_FMUL, "fmul", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Mul},
   {I_DMUL, "dmul", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Mul},
   {I_IDIV, "idiv", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Div},
   {I_LDIV, "ldiv", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Div},
   {I_FDIV, "fdiv", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Div},
   {I_DDIV, "ddiv", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Div},
   {I_IREM, "irem", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Rem},
   {I_LREM, "lrem", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Rem},
   {I_FREM, "frem", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Rem},
   {I_DREM, "drem", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Rem},
   {I_INEG, "ineg", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_Neg},
   {I_LNEG, "lneg", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_L, OpSpec_Neg},
   {I_FNEG, "fneg", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_F, OpSpec_Neg},
   {I_DNEG, "dneg", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_D, OpSpec_Neg},
   {I_ISHL, "ishl", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_LeftShift},
   {I_LSHL, "lshl", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_LeftShift},
   {I_ISHR, "ishr", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_LogicalRightShift},
   {I_LSHR, "lshr", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_LogicalRightShift},
   {I_IUSHR, "iushr", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_ArithmeticRightShift},
   {I_LUSHR, "lushr", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_ArithmeticRightShift},
   {I_IAND, "iand", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseAnd},
   {I_LAND, "land", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseAnd},
   {I_IOR, "ior", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseOr},
   {I_LOR, "lor", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseOr},
   {I_IXOR, "ixor", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseXor},
   {I_LXOR, "lxor", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseXor},
   {I_IINC, "iinc", LDSpec_NONE, STSpec_NONE, ImmSpec_BlvtiBconst, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE},
   {I_I2L, "i2l", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_L, OpSpec_I2LCast},
   {I_I2F, "i2f", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_F, OpSpec_I2FCast},
   {I_I2D, "i2d", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_D, OpSpec_I2DCast},
   {I_L2I, "l2i", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_I, OpSpec_L2ICast},
   {I_L2F, "l2f", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_F, OpSpec_L2FCast},
   {I_L2D, "l2d", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_D, OpSpec_L2DCast},
   {I_F2I, "f2i", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_I, OpSpec_F2ICast},
   {I_F2L, "f2l", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_L, OpSpec_F2LCast},
   {I_F2D, "f2d", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_D, OpSpec_F2DCast},
   {I_D2I, "d2i", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_I, OpSpec_D2ICast},
   {I_D2L, "d2l", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_L, OpSpec_D2LCast},
   {I_D2F, "d2f", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_F, OpSpec_D2FCast},
   {I_I2B, "i2b", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2BCast},
   {I_I2C, "i2c", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2CCast},
   {I_I2S, "i2s", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2SCast},
   {I_LCMP, "lcmp", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_I, OpSpec_Sub},
   {I_FCMPL, "fcmpl", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_I, OpSpec_LessThan},
   {I_FCMPG, "fcmpg", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_I, OpSpec_GreaterThan},
   {I_DCMPL, "dcmpl", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_I, OpSpec_LessThan},
   {I_DCMPG, "dcmpg", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_I, OpSpec_GreaterThan},
   {I_IFEQ, "ifeq", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_Equal},
   {I_IFNE, "ifne", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_NotEqual},
   {I_IFLT, "iflt", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_LessThan},
   {I_IFGE, "ifge", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_GreaterThanOrEqual},
   {I_IFGT, "ifgt", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_GreaterThan},
   {I_IFLE, "ifle", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_LessThanOrEqual},
   {I_IF_ICMPEQ, "if_icmpeq", LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_II, OpSpec_Equal},
   {I_IF_ICMPNE, "if_icmpne", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, OpSpec_NotEqual},
   {I_IF_ICMPLT, "if_icmplt", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, OpSpec_LessThan},
   {I_IF_ICMPGE, "if_icmpge", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, OpSpec_GreaterThanOrEqual},
   {I_IF_ICMPGT, "if_icmpgt", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, OpSpec_GreaterThan},
   {I_IF_ICMPLE, "if_icmple", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, OpSpec_LessThanOrEqual},
   {I_IF_ACMPEQ, "if_acmpeq", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_OO, OpSpec_Equal},
   {I_IF_ACMPNE, "if_acmpne", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_OO, OpSpec_NotEqual},
   {I_GOTO, "goto", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc},
   {I_JSR, "jsr", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_NONE, PushSpec_RA, OpSpec_NONE},
   {I_RET, "ret", LDSpec_NONE, STSpec_NONE, ImmSpec_Bconst},
   {I_TABLESWITCH, "tableswitch", LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_LOOKUPSWITCH, "lookupswitch", LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_IRETURN, "ireturn", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_LRETURN, "lreturn", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_FRETURN, "freturn", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_DRETURN, "dreturn", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_ARETURN, "areturn", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_RETURN, "return", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE},
   {I_GETSTATIC, "getstatic", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_NONE, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_PUTSTATIC, "putstatic", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_UNKNOWN, PushSpec_NONE, OpSpec_NONE},
   {I_GETFIELD, "getfield", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_O, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_PUTFIELD, "putfield", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_OUNKNOWN, PushSpec_NONE, OpSpec_NONE},
   {I_INVOKEVIRTUAL, "invokevirtual", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_OARGS, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_INVOKESPECIAL, "invokespecial", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_OARGS, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_INVOKESTATIC, "invokestatic", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_ARGS, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_INVOKEINTERFACE, "invokeinterface", LDSpec_NONE, STSpec_NONE, ImmSpec_ScpmiBB, PopSpec_OARGS, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_INVOKEDYNAMIC, "invokedynamic", LDSpec_NONE, STSpec_NONE, ImmSpec_ScpmiBB, PopSpec_OARGS, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_NEW, "new", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_NEWARRAY, "newarray", LDSpec_NONE, STSpec_NONE, ImmSpec_Bconst, PopSpec_I, PushSpec_A, OpSpec_NONE},
   {I_ANEWARRAY, "anewarray", LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_I, PushSpec_A, OpSpec_NONE},
   {I_ARRAYLENGTH, "arraylength", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_A, PushSpec_I, OpSpec_NONE},
   {I_ATHROW, "athrow", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_O, OpSpec_NONE},
   {I_CHECKCAST, "checkcast", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_O, PushSpec_O, OpSpec_NONE},
   {I_INSTANCEOF, "instanceof", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_O, PushSpec_I, OpSpec_NONE},
   {I_MONITORENTER, "monitorenter", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_MONITOREXIT, "monitorexit", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_WIDE, "wide", LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_UNKNOWN, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_MULTIANEWARRAY, "multianewarray", LDSpec_NONE, STSpec_NONE, ImmSpec_ScpciBdim, PopSpec_UNKNOWN, PushSpec_A, OpSpec_NONE},
   {I_IFNULL, "ifnull", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_O, PushSpec_NONE, OpSpec_EqualNULL},
   {I_IFNONNULL, "ifnonnull", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_O, PushSpec_NONE, OpSpec_NotEqualNULL},
   {I_GOTO_W, "goto_w", LDSpec_NONE, STSpec_NONE, ImmSpec_Ipc, PopSpec_O, PushSpec_NONE, OpSpec_NotEqualNULL},
   {I_JSR_W, "jsr_w", LDSpec_NONE, STSpec_NONE, ImmSpec_Ipc, PopSpec_NONE, PushSpec_RA, OpSpec_NONE}
};
#else
extern ByteCode bytecode[];
#endif


class Instruction{
   public:
      union  {
         ImmSpec_NONE_s immSpec_NONE;
         ImmSpec_Blvti_s immSpec_Blvti;
         ImmSpec_Bcpci_s immSpec_Bcpci;
         ImmSpec_Scpci_s immSpec_Scpci;
         ImmSpec_Bconst_s immSpec_Bconst;
         ImmSpec_Sconst_s immSpec_Sconst;
         ImmSpec_IorForS_s immSpec_IorForS;
         ImmSpec_Spc_s immSpec_Spc;
         ImmSpec_Scpfi_s immSpec_Scpfi;
         ImmSpec_ScpmiBB_s immSpec_ScpmiBB;
         ImmSpec_BlvtiBconst_s immSpec_BlvtiBconst;
         ImmSpec_Scpmi_s immSpec_Scpmi;
         ImmSpec_ScpciBdim_s immSpec_ScpciBdim;
         ImmSpec_Ipc_s immSpec_Ipc;
         ImmSpec_UNKNOWN_s immSpec_UNKNOWN;
      };
      ByteCode *byteCode;
      Instruction(ByteBuffer *_codeByteBuffer);
};


class Decoder{
   static void list(u1_t *buf, u4_t len);
};

#endif



